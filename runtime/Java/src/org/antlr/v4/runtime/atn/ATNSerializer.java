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

import java.io.InvalidClassException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ATNSerializer {
	public static ATN clone(ATN atn) {
		ByteBuffer serialized = new ATNSerializer(atn).serialize();
		return new ATNDeserializer().deserialize(serialized);
	}

	public final ATN atn;

	public ATNSerializer(ATN atn) {
		assert atn.grammarType != null;
		this.atn = atn;
	}

	/** Serialize state descriptors, edge descriptors, and decision&rarr;state map
	 *  into list of ints:
	 *
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
	public ByteBuffer serialize() {
		ATNDataWriter writer = new ATNDataWriter();

		writer.writeUInt16(ATNDeserializer.SERIALIZED_VERSION);
		writer.writeUUID(ATNDeserializer.SERIALIZED_UUID);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		writer.write(atn.grammarType.ordinal());
		writer.write(atn.maxTokenType);
		int nedges = 0;

		// Note that we use a LinkedHashMap as a set to
		// maintain insertion order while deduplicating
		// entries with the same key.
		Map<IntervalSet, Boolean> sets = new LinkedHashMap<>();

		// dump states, count edges and collect sets while doing so
		IntegerList nonGreedyStates = new IntegerList();
		IntegerList precedenceStates = new IntegerList();
		writer.write(atn.states.size());
		for (ATNState s : atn.states) {
			if ( s==null ) { // might be optimized away
				writer.write(ATNState.INVALID_TYPE);
				continue;
			}

			int stateType = s.getStateType();
			if (s instanceof DecisionState && ((DecisionState)s).nonGreedy) {
				nonGreedyStates.add(s.stateNumber);
			}

			if (s instanceof RuleStartState && ((RuleStartState)s).isLeftRecursiveRule) {
				precedenceStates.add(s.stateNumber);
			}

			writer.write(stateType);
			writer.write(s.ruleIndex);

			if ( s.getStateType() == ATNState.LOOP_END ) {
				writer.write(((LoopEndState)s).loopBackState.stateNumber);
			}
			else if ( s instanceof BlockStartState ) {
				writer.write(((BlockStartState)s).endState.stateNumber);
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
		writer.write(nonGreedyStates.size());
		for (int i = 0; i < nonGreedyStates.size(); i++) {
			writer.write(nonGreedyStates.get(i));
		}

		// precedence states
		writer.write(precedenceStates.size());
		for (int i = 0; i < precedenceStates.size(); i++) {
			writer.write(precedenceStates.get(i));
		}

		int nrules = atn.ruleToStartState.length;
		writer.write(nrules);
		for (int r=0; r<nrules; r++) {
			ATNState ruleStartState = atn.ruleToStartState[r];
			writer.write(ruleStartState.stateNumber);
			if (atn.grammarType == ATNType.LEXER) {
				writer.write(atn.ruleToTokenType[r]);
			}
		}

		int nmodes = atn.modeToStartState.size();
		writer.write(nmodes);
		if ( nmodes>0 ) {
			for (ATNState modeStartState : atn.modeToStartState) {
				writer.write(modeStartState.stateNumber);
			}
		}
		List<IntervalSet> bmpSets = new ArrayList<>();
		List<IntervalSet> smpSets = new ArrayList<>();
		for (IntervalSet set : sets.keySet()) {
			List<IntervalSet> localSets = !set.isNil() && set.getMaxElement() <= Character.MAX_VALUE ? bmpSets : smpSets;
			localSets.add(set);
		}
		serializeSets(writer, bmpSets, UnicodeSerializeMode.UNICODE_BMP);
		serializeSets(writer, smpSets, UnicodeSerializeMode.UNICODE_SMP);
		Map<IntervalSet, Integer> setIndices = new HashMap<>();
		int setIndex = 0;
		for (IntervalSet bmpSet : bmpSets) {
			setIndices.put(bmpSet, setIndex++);
		}
		for (IntervalSet smpSet : smpSets) {
			setIndices.put(smpSet, setIndex++);
		}

		writer.write(nedges);
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
						arg1 = t.target.stateNumber;
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
						arg3 = at.isCtxDependent ? 1 : 0 ;
						break;
					case Transition.SET :
					case Transition.NOT_SET :
						arg1 = setIndices.get(((SetTransition)t).set);
						break;
					case Transition.WILDCARD :
						break;
				}

				writer.write(src);
				writer.write(trg);
				writer.write(edgeType);
				writer.write(arg1);
				writer.write(arg2);
				writer.write(arg3);
			}
		}

		int ndecisions = atn.decisionToState.size();
		writer.write(ndecisions);
		for (DecisionState decStartState : atn.decisionToState) {
			writer.write(decStartState.stateNumber);
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			writer.write(atn.lexerActions.length);
			for (LexerAction action : atn.lexerActions) {
				writer.write(action.getActionType().ordinal());
				switch (action.getActionType()) {
				case CHANNEL:
					int channel = ((LexerChannelAction)action).getChannel();
					writer.write(channel);
					writer.write(0);
					break;

				case CUSTOM:
					int ruleIndex = ((LexerCustomAction)action).getRuleIndex();
					int actionIndex = ((LexerCustomAction)action).getActionIndex();
					writer.write(ruleIndex);
					writer.write(actionIndex);
					break;

				case MODE:
					int mode = ((LexerModeAction)action).getMode();
					writer.write(mode);
					writer.write(0);
					break;

				case MORE:
				case POP_MODE:
				case SKIP:
					writer.write(0);
					writer.write(0);
					break;

				case PUSH_MODE:
					mode = ((LexerPushModeAction)action).getMode();
					writer.write(mode);
					writer.write(0);
					break;

				case TYPE:
					int type = ((LexerTypeAction)action).getType();
					writer.write(type);
					writer.write(0);
					break;

				default:
					String message = String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", action.getActionType());
					throw new IllegalArgumentException(message);
				}
			}
		}

		return writer.getData();
	}

	private static void serializeSets(ATNDataWriter writer, Collection<IntervalSet> sets, UnicodeSerializeMode mode) {
		int nSets = sets.size();
		writer.write(nSets);

		for (IntervalSet set : sets) {
			boolean containsEof = set.contains(Token.EOF);
			int size = set.getIntervals().size();
			if (containsEof && set.getIntervals().get(0).b == Token.EOF) {
				size--;
			}
			writer.write(size);

			writer.write(containsEof ? 1 : 0);
			for (Interval I : set.getIntervals()) {
				int firstValue;
				if (I.a == Token.EOF) {
					if (I.b == Token.EOF) {
						continue;
					}
					else {
						firstValue = 0;
					}
				}
				else {
					firstValue = I.a;
				}

				if (mode == UnicodeSerializeMode.UNICODE_BMP) {
					writer.writeUInt16(firstValue);
					writer.writeUInt16(I.b);
				} else {
					writer.writeInt32(firstValue);
					writer.writeInt32(I.b);
				}
			}
		}
	}
}
