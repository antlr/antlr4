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

import java.io.InvalidClassException;
import java.util.*;

public class ATNSerializer {
	public final ATN atn;
	private final List<String> tokenNames;

	public ATNSerializer(ATN atn) {
		assert atn.grammarType != null;
		this.atn = atn;
		this.tokenNames = null;
	}

	public ATNSerializer(ATN atn, List<String> tokenNames) {
		assert atn.grammarType != null;
		this.atn = atn;
		this.tokenNames = tokenNames;
	}

	/** Serialize state descriptors, edge descriptors, and decision&rarr;state map
	 *  into list of ints:
	 *
	 *      SERIALIZED_VERSION
	 *      UUID (2 longs)
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
		ATNDataWriter writer = new ATNDataWriter(data, language);

		writer.writeUInt16(ATNDeserializer.SERIALIZED_VERSION, false);

		// convert grammar type to ATN const to avoid dependence on ANTLRParser
		writer.write(atn.grammarType.ordinal());
		writer.write(atn.maxTokenType);
		int nedges = 0;

		Set<IntervalSet> sets = new HashSet<>();

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
					sets.add(st.set);
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

		serializeSets(writer, sets);

		Map<IntervalSet, Integer> setIndices = new HashMap<>();
		int setIndex = 0;
		for (IntervalSet set : sets) {
			setIndices.put(set, setIndex++);
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

		return data;
	}

	private static void serializeSets(ATNDataWriter writer, Set<IntervalSet> sets) {
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

				writer.write(firstValue);
				writer.write(I.b);
			}
		}
	}

	public String decode(char[] data) {
		ATNDataReader reader = new ATNDataReader(data);
		StringBuilder buf = new StringBuilder();
		int version = reader.readUInt16(false);
		if (version != ATNDeserializer.SERIALIZED_VERSION) {
			String reason = String.format("Could not deserialize ATN with version %d (expected %d).", version, ATNDeserializer.SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		reader.read(); // skip grammarType
		int maxType = reader.read();
		buf.append("max type ").append(maxType).append("\n");
		int nstates = reader.read();
		for (int i=0; i<nstates; i++) {
			int stype = reader.read();
			if ( stype==ATNState.INVALID_TYPE ) continue; // ignore bad type of states
			int ruleIndex = reader.read();

			String arg = "";
			if ( stype == ATNState.LOOP_END ) {
				int loopBackStateNumber = reader.read();
				arg = " "+loopBackStateNumber;
			}
			else if ( stype == ATNState.PLUS_BLOCK_START || stype == ATNState.STAR_BLOCK_START || stype == ATNState.BLOCK_START ) {
				int endStateNumber = reader.read();
				arg = " "+endStateNumber;
			}
			buf.append(i).append(":")
				.append(ATNState.serializationNames.get(stype)).append(" ")
				.append(ruleIndex).append(arg).append("\n");
		}
		// this code is meant to model the form of ATNDeserializer.deserialize,
		// since both need to be updated together whenever a change is made to
		// the serialization format. The "dead" code is only used in debugging
		// and testing scenarios, so the form you see here was kept for
		// improved maintainability.
		// start
		int numNonGreedyStates = reader.read();
		for (int i = 0; i < numNonGreedyStates; i++) {
			reader.read(); // Skip stateNumber
		}
		int numPrecedenceStates = reader.read();
		for (int i = 0; i < numPrecedenceStates; i++) {
			reader.read(); // Skip stateNumber
		}
		// finish
		int nrules = reader.read();
		for (int i=0; i<nrules; i++) {
			int s = reader.read();
			buf.append("rule ").append(i).append(":").append(s);
			if (atn.grammarType == ATNType.LEXER) {
				buf.append(" ").append(reader.read());
			}
			buf.append('\n');
		}
		int nmodes = reader.read();
		for (int i=0; i<nmodes; i++) {
			int s = reader.read();
			buf.append("mode ").append(i).append(":").append(s).append('\n');
		}
		appendSets(buf, reader);
		int nedges = reader.read();
		for (int i=0; i<nedges; i++) {
			int src = reader.read();
			int trg = reader.read();
			int ttype = reader.read();
			int arg1 = reader.read();
			int arg2 = reader.read();
			int arg3 = reader.read();
			buf.append(src).append("->").append(trg)
				.append(" ").append(Transition.serializationNames.get(ttype))
				.append(" ").append(arg1).append(",").append(arg2).append(",").append(arg3)
				.append("\n");
		}
		int ndecisions = reader.read();
		for (int i=0; i<ndecisions; i++) {
			int s = reader.read();
			buf.append(i).append(":").append(s).append("\n");
		}
		if (atn.grammarType == ATNType.LEXER) {
			// this code is meant to model the form of ATNDeserializer.deserialize,
			// since both need to be updated together whenever a change is made to
			// the serialization format. The "dead" code is only used in debugging
			// and testing scenarios, so the form you see here was kept for
			// improved maintainability.
			int lexerActionCount = reader.read();
			for (int i = 0; i < lexerActionCount; i++) {
				reader.read(); // Skip actionType
				reader.read();
				reader.read();
			}
		}
		return buf.toString();
	}

	private void appendSets(StringBuilder buf, ATNDataReader reader) {
		int nsets = reader.read();
		for (int i = 0; i < nsets; i++) {
			int nintervals = reader.read();
			buf.append(i).append(":");
			boolean containsEof = reader.read() != 0;
			if (containsEof) {
				buf.append(getTokenName(Token.EOF));
			}

			for (int j=0; j<nintervals; j++) {
				if ( containsEof || j>0 ) {
					buf.append(", ");
				}

				int a = reader.read();
				int b = reader.read();
				buf.append(getTokenName(a)).append("..").append(getTokenName(b));
			}
			buf.append("\n");
		}
	}

	public String getTokenName(int t) {
		if ( t==-1 ) return "EOF";

		if ( atn.grammarType == ATNType.LEXER &&
			 t >= Character.MIN_VALUE && t <= Character.MAX_VALUE )
		{
			switch (t) {
			case '\n':
				return "'\\n'";
			case '\r':
				return "'\\r'";
			case '\t':
				return "'\\t'";
			case '\b':
				return "'\\b'";
			case '\f':
				return "'\\f'";
			case '\\':
				return "'\\\\'";
			case '\'':
				return "'\\''";
			default:
				char c = (char)t;
				if (Character.UnicodeBlock.of(c)==Character.UnicodeBlock.BASIC_LATIN && !Character.isISOControl(c)) {
					return '\'' + Character.toString(c) + '\'';
				}
				// turn on the bit above max "\uFFFF" value so that we pad with zeros
				// then only take last 4 digits
				return String.format("'\\u%04X'", t);
			}
		}

		if (tokenNames != null && t >= 0 && t < tokenNames.size()) {
			return tokenNames.get(t);
		}

		return String.valueOf(t);
	}

	public static IntegerList getSerialized(ATN atn, String language) {
		return new ATNSerializer(atn).serialize(language);
	}

	public static char[] getSerializedAsChars(ATN atn, String language) {
		return Utils.toCharArray(getSerialized(atn, language));
	}
}
