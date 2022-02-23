/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ATNSerializer {
	public final ATN atn;

	private interface CodePointSerializer {
		void serializeCodePoint(IntegerList data, int cp);
	}

	public ATNSerializer(ATN atn) {
		assert atn.grammarType != null;
		this.atn = atn;
	}

	/** Serialize state descriptors, edge descriptors, and decision&rarr;state map
	 *  into list of ints:
	 *
	 *      SERIALIZED_VERSION
	 * 		grammar-type, (ANTLRParser.LEXER, ...)
	 *  	max token type,
	 *  	num states,
	 *  	state-0-type ruleIndex, state-1-type ruleIndex, ... state-i-type ruleIndex optional-arg ...
	 *  	num rules,
	 *  	rule-1-start-state rule-1-args, rule-2-start-state  rule-2-args, ...
	 *  	(args are token type,actionIndex in lexer else 0,0)
	 *      num modes,
	 *      mode-0-start-state, mode-1-start-state, ... (parser has 0 modes)
	 *      num unicode-bmp-sets
	 *      bmp-set-0-interval-count intervals, bmp-set-1-interval-count intervals, ...
	 *      num unicode-smp-sets
	 *      smp-set-0-interval-count intervals, smp-set-1-interval-count intervals, ...
	 *	num total edges,
	 *      src, trg, edge-type, edge arg1, optional edge arg2 (present always), ...
	 *      num decisions,
	 *      decision-0-start-state, decision-1-start-state, ...
	 *
	 *  Convenient to pack into unsigned shorts to make as Java string.
	 */
	public IntegerList serialize(String language) {
		IntegerList data = new IntegerList();
		data.add(ATNDeserializer.SERIALIZED_VERSION);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		data.add(atn.grammarType.ordinal());
		data.add(atn.maxTokenType);
		int nedges = 0;

		// Note that we use a LinkedHashMap as a set to
		// maintain insertion order while deduplicating
		// entries with the same key.
		Map<IntervalSet, Boolean> sets = new LinkedHashMap<>();

		// dump states, count edges and collect sets while doing so
		IntegerList nonGreedyStates = new IntegerList();
		IntegerList precedenceStates = new IntegerList();
		data.add(atn.states.size());
		for (ATNState s : atn.states) {
			if ( s==null ) { // might be optimized away
				data.add(ATNState.INVALID_TYPE);
				continue;
			}

			int stateType = s.getStateType();
			if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
				nonGreedyStates.add(s.stateNumber);
			}

			if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
				precedenceStates.add(s.stateNumber);
			}

			data.add(stateType);

			if (s.ruleIndex == -1) {
				data.add(Character.MAX_VALUE);
			}
			else {
				data.add(s.ruleIndex);
			}

			if ( s.getStateType() == ATNState.LOOP_END ) {
				data.add(((LoopEndState)s).loopBackState.stateNumber);
			}
			else if ( s instanceof BlockStartState ) {
				data.add(((BlockStartState)s).endState.stateNumber);
			}

			if (s.getStateType() != ATNState.RULE_STOP) {
				// the deserializer can trivially derive these edges, so there's no need to serialize them
				nedges += s.getNumberOfTransitions();
			}

			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);
				int edgeType = Transition.serializationTypes.get(t.getClass());
				if ( edgeType == Transition.SET || edgeType == Transition.NOT_SET ) {
					SetTransition st = (SetTransition)t;
					sets.put(st.set, true);
				}
			}
		}

		// non-greedy states
		data.add(nonGreedyStates.size());
		for (int i = 0; i < nonGreedyStates.size(); i++) {
			data.add(nonGreedyStates.get(i));
		}

		// precedence states
		data.add(precedenceStates.size());
		for (int i = 0; i < precedenceStates.size(); i++) {
			data.add(precedenceStates.get(i));
		}

		int nrules = atn.ruleToStartState.length;
		data.add(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			data.add(ruleStartState.stateNumber);
			if (atn.grammarType == ATNType.LEXER) {
				if (atn.ruleToTokenType[r] == Token.EOF) {
					data.add(Character.MAX_VALUE);
				}
				else {
					data.add(atn.ruleToTokenType[r]);
				}
			}
		}

		int nmodes = atn.modeToStartState.size();
		data.add(nmodes);
		if ( nmodes>0 ) {
			for (ATNState modeStartState : atn.modeToStartState) {
				data.add(modeStartState.stateNumber);
			}
		}
		List<IntervalSet> bmpSets = new ArrayList<>();
		List<IntervalSet> smpSets = new ArrayList<>();
		for (IntervalSet set : sets.keySet()) {
			if (!set.isNil() && set.getMaxElement() <= Character.MAX_VALUE) {
				bmpSets.add(set);
			}
			else {
				smpSets.add(set);
			}
		}
		serializeSets(
			data,
			bmpSets,
			new CodePointSerializer() {
				@Override
				public void serializeCodePoint(IntegerList data, int cp) {
					data.add(cp);
				}
			});
		serializeSets(
			data,
			smpSets,
			new CodePointSerializer() {
				@Override
				public void serializeCodePoint(IntegerList data, int cp) {
					serializeInt(data, cp);
				}
			});
		Map<IntervalSet, Integer> setIndices = new HashMap<>();
		int setIndex = 0;
		for (IntervalSet bmpSet : bmpSets) {
			setIndices.put(bmpSet, setIndex++);
		}
		for (IntervalSet smpSet : smpSets) {
			setIndices.put(smpSet, setIndex++);
		}

		data.add(nedges);
		for (ATNState s : atn.states) {
			if ( s==null ) {
				// might be optimized away
				continue;
			}

			if (s.getStateType() == ATNState.RULE_STOP) {
				continue;
			}

			for (int i=0; i<s.getNumberOfTransitions(); i++) {
				Transition t = s.transition(i);

				if (atn.states.get(t.target.stateNumber) == null) {
					throw new IllegalStateException("Cannot serialize a transition to a removed state.");
				}

				int src = s.stateNumber;
				int trg = t.target.stateNumber;
				int edgeType = Transition.serializationTypes.get(t.getClass());
				int arg1 = 0;
				int arg2 = 0;
				int arg3 = 0;
				switch ( edgeType ) {
					case Transition.RULE :
						trg = ((RuleTransition)t).followState.stateNumber;
						arg1 = ((RuleTransition)t).target.stateNumber;
						arg2 = ((RuleTransition)t).ruleIndex;
						arg3 = ((RuleTransition)t).precedence;
						break;
					case Transition.PRECEDENCE:
						PrecedencePredicateTransition ppt = (PrecedencePredicateTransition)t;
						arg1 = ppt.precedence;
						break;
					case Transition.PREDICATE :
						PredicateTransition pt = (PredicateTransition)t;
						arg1 = pt.ruleIndex;
						arg2 = pt.predIndex;
						arg3 = pt.isCtxDependent ? 1 : 0 ;
						break;
					case Transition.RANGE :
						arg1 = ((RangeTransition)t).from;
						arg2 = ((RangeTransition)t).to;
						if (arg1 == Token.EOF) {
							arg1 = 0;
							arg3 = 1;
						}

						break;
					case Transition.ATOM :
						arg1 = ((AtomTransition)t).label;
						if (arg1 == Token.EOF) {
							arg1 = 0;
							arg3 = 1;
						}

						break;
					case Transition.ACTION :
						ActionTransition at = (ActionTransition)t;
						arg1 = at.ruleIndex;
						arg2 = at.actionIndex;
						if (arg2 == -1) {
							arg2 = 0xFFFF;
						}

						arg3 = at.isCtxDependent ? 1 : 0 ;
						break;
					case Transition.SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.NOT_SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.WILDCARD :
						break;
				}

				data.add(src);
				data.add(trg);
				data.add(edgeType);
				data.add(arg1);
				data.add(arg2);
				data.add(arg3);
			}
		}

		int ndecisions = atn.decisionToState.size();
		data.add(ndecisions);
		for (DecisionState decStartState : atn.decisionToState) {
			data.add(decStartState.stateNumber);
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			data.add(atn.lexerActions.length);
			for (LexerAction action : atn.lexerActions) {
				data.add(action.getActionType().ordinal());
				switch (action.getActionType()) {
				case CHANNEL:
					int channel = ((LexerChannelAction)action).getChannel();
					data.add(channel != -1 ? channel : 0xFFFF);
					data.add(0);
					break;

				case CUSTOM:
					int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
					int actionIndex = ((LexerCustomAction)action).getActionIndex();
					data.add(ruleIndex != -1 ? ruleIndex : 0xFFFF);
					data.add(actionIndex != -1 ? actionIndex : 0xFFFF);
					break;

				case MODE:
					int mode = ((LexerModeAction)action).getMode();
					data.add(mode != -1 ? mode : 0xFFFF);
					data.add(0);
					break;

				case MORE:
					data.add(0);
					data.add(0);
					break;

				case POP_MODE:
					data.add(0);
					data.add(0);
					break;

				case PUSH_MODE:
					mode = ((LexerPushModeAction)action).getMode();
					data.add(mode != -1 ? mode : 0xFFFF);
					data.add(0);
					break;

				case SKIP:
					data.add(0);
					data.add(0);
					break;

				case TYPE:
					int type = ((LexerTypeAction)action).getType();
					data.add(type != -1 ? type : 0xFFFF);
					data.add(0);
					break;

				default:
					String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
					throw new IllegalArgumentException(message);
				}
			}
		}

		boolean isJava = language.equals("Java");
		for (int i = 1; i < data.size(); i++) {
			int value = data.get(i);
			if (value < Character.MIN_VALUE || value > Character.MAX_VALUE) {
				throw new UnsupportedOperationException("Serialized ATN data element " +
						value + " element " + i + " out of range " + (int) Character.MIN_VALUE + ".." + (int) Character.MAX_VALUE);
			}

			data.set(i, isJava ? (value + 2) & 0xFFFF : value);
		}

		return data;
	}

	private static void serializeSets(
			IntegerList data,
			Collection<IntervalSet> sets,
			CodePointSerializer codePointSerializer)
	{
		int nSets = sets.size();
		data.add(nSets);

		for (IntervalSet set : sets) {
			boolean containsEof = set.contains(Token.EOF);
			if (containsEof && set.getIntervals().get(0).b == Token.EOF) {
				data.add(set.getIntervals().size() - 1);
			}
			else {
				data.add(set.getIntervals().size());
			}

			data.add(containsEof ? 1 : 0);
			for (Interval I : set.getIntervals()) {
				if (I.a == Token.EOF) {
					if (I.b == Token.EOF) {
						continue;
					}
					else {
						codePointSerializer.serializeCodePoint(data, 0);
					}
				}
				else {
					codePointSerializer.serializeCodePoint(data, I.a);
				}

				codePointSerializer.serializeCodePoint(data, I.b);
			}
		}
	}

	private void serializeInt(IntegerList data, int value) {
		data.add((char)value);
		data.add((char)(value >> 16));
	}

	public static IntegerList getSerialized(ATN atn, String language) {
		return new ATNSerializer(atn).serialize(language);
	}

	public static char[] getSerializedAsChars(ATN atn, String language) {
		return Utils.toCharArray(getSerialized(atn, language));
	}
}
