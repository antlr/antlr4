/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntegerList;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/** Deserialize ATNs for JavaTarget; it's complicated by the fact that java requires
 *  that we serialize the list of integers as 16 bit characters in a string. Other
 *  targets will have an array of ints generated and can simply decode the ints
 *  back into an ATN.
 *
 * @author Sam Harwell
 */
public class ATNDeserializer {
	public static final int SERIALIZED_VERSION;
	static {
		SERIALIZED_VERSION = 4;
	}

	private final ATNDeserializationOptions deserializationOptions;

	public ATNDeserializer() {
		this(ATNDeserializationOptions.getDefaultOptions());
	}

	public ATNDeserializer(ATNDeserializationOptions deserializationOptions) {
		if (deserializationOptions == null) {
			deserializationOptions = ATNDeserializationOptions.getDefaultOptions();
		}

		this.deserializationOptions = deserializationOptions;
	}

	public ATN deserialize(char[] data) {
		return deserialize(decodeIntsEncodedAs16BitWords(data));
	}

	public ATN deserialize(int[] data) {
		int p = 0;
		int version = data[p++];
		if (version != SERIALIZED_VERSION) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		ATNType grammarType = ATNType.values()[data[p++]];
		int maxTokenType = data[p++];
		ATN atn = new ATN(grammarType, maxTokenType);

		//
		// STATES
		//
		List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
		List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
		int nstates = data[p++];
		for (int i=0; i<nstates; i++) {
			int stype = data[p++];
			// ignore bad type of states
			if ( stype==ATNState.INVALID_TYPE ) {
				atn.addState(null);
				continue;
			}

			int ruleIndex = data[p++];
			ATNState s = stateFactory(stype, ruleIndex);
			if ( stype == ATNState.LOOP_END ) { // special case
				int loopBackStateNumber = data[p++];
				loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
			}
			else if (s instanceof BlockStartState) {
				int endStateNumber = data[p++];
				endStateNumbers.add(new Pair<BlockStartState, Integer>((BlockStartState)s, endStateNumber));
			}
			atn.addState(s);
		}

		// delay the assignment of loop back and end states until we know all the state instances have been initialized
		for (Pair<LoopEndState, Integer> pair : loopBackStateNumbers) {
			pair.a.loopBackState = atn.states.get(pair.b);
		}

		for (Pair<BlockStartState, Integer> pair : endStateNumbers) {
			pair.a.endState = (BlockEndState)atn.states.get(pair.b);
		}

		int numNonGreedyStates = data[p++];
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = data[p++];
			((DecisionState)atn.states.get(stateNumber)).nonGreedy = true;
		}

		int numPrecedenceStates = data[p++];
		for (int i = 0; i < numPrecedenceStates; i++) {
			int stateNumber = data[p++];
			((RuleStartState)atn.states.get(stateNumber)).isLeftRecursiveRule = true;
		}

		//
		// RULES
		//
		int nrules = data[p++];
		if ( atn.grammarType == ATNType.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
		}

		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = data[p++];
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATNType.LEXER ) {
				int tokenType = data[p++];
				atn.ruleToTokenType[i] = tokenType;
			}
		}

		atn.ruleToStopState = new RuleStopState[nrules];
		for (ATNState state : atn.states) {
			if (!(state instanceof RuleStopState)) {
				continue;
			}

			RuleStopState stopState = (RuleStopState)state;
			atn.ruleToStopState[state.ruleIndex] = stopState;
			atn.ruleToStartState[state.ruleIndex].stopState = stopState;
		}

		//
		// MODES
		//
		int nmodes = data[p++];
		for (int i=0; i<nmodes; i++) {
			int s = data[p++];
			atn.modeToStartState.add((TokensStartState)atn.states.get(s));
		}

		//
		// SETS
		//
		List<IntervalSet> sets = new ArrayList<IntervalSet>();
		p = deserializeSets(data, p, sets);

		//
		// EDGES
		//
		int nedges = data[p++];
		for (int i=0; i<nedges; i++) {
			int src = data[p];
			int trg = data[p+1];
			int ttype = data[p+2];
			int arg1 = data[p+3];
			int arg2 = data[p+4];
			int arg3 = data[p+5];
			Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
//			System.out.println("EDGE "+trans.getClass().getSimpleName()+" "+
//							   src+"->"+trg+
//					   " "+Transition.serializationNames[ttype]+
//					   " "+arg1+","+arg2+","+arg3);
			ATNState srcState = atn.states.get(src);
			srcState.addTransition(trans);
			p += 6;
		}

		// edges for rule stop states can be derived, so they aren't serialized
		for (ATNState state : atn.states) {
			for (int i = 0; i < state.getNumberOfTransitions(); i++) {
				Transition t = state.transition(i);
				if (!(t instanceof RuleTransition)) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)t;
				int outermostPrecedenceReturn = -1;
				if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isLeftRecursiveRule) {
					if (ruleTransition.precedence == 0) {
						outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
					}
				}

				EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
				atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(returnTransition);
			}
		}

		for (ATNState state : atn.states) {
			if (state instanceof BlockStartState) {
				// we need to know the end state to set its start state
				if (((BlockStartState)state).endState == null) {
					throw new IllegalStateException();
				}

				// block end states can only be associated to a single block start state
				if (((BlockStartState)state).endState.startState != null) {
					throw new IllegalStateException();
				}

				((BlockStartState)state).endState.startState = (BlockStartState)state;
			}

			if (state instanceof PlusLoopbackState) {
				PlusLoopbackState loopbackState = (PlusLoopbackState)state;
				for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
					ATNState target = loopbackState.transition(i).target;
					if (target instanceof PlusBlockStartState) {
						((PlusBlockStartState)target).loopBackState = loopbackState;
					}
				}
			}
			else if (state instanceof StarLoopbackState) {
				StarLoopbackState loopbackState = (StarLoopbackState)state;
				for (int i = 0; i < loopbackState.getNumberOfTransitions(); i++) {
					ATNState target = loopbackState.transition(i).target;
					if (target instanceof StarLoopEntryState) {
						((StarLoopEntryState)target).loopBackState = loopbackState;
					}
				}
			}
		}

		//
		// DECISIONS
		//
		int ndecisions = data[p++];
		for (int i=1; i<=ndecisions; i++) {
			int s = data[p++];
			DecisionState decState = (DecisionState)atn.states.get(s);
			atn.decisionToState.add(decState);
			decState.decision = i-1;
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			atn.lexerActions = new LexerAction[data[p++]];
			for (int i = 0; i < atn.lexerActions.length; i++) {
				LexerActionType actionType = LexerActionType.values()[data[p++]];
				int data1 = data[p++];
				int data2 = data[p++];

				LexerAction lexerAction = lexerActionFactory(actionType, data1, data2);

				atn.lexerActions[i] = lexerAction;
			}
		}

		markPrecedenceDecisions(atn);

		if (deserializationOptions.isVerifyATN()) {
			verifyATN(atn);
		}

		if (deserializationOptions.isGenerateRuleBypassTransitions() && atn.grammarType == ATNType.PARSER) {
			atn.ruleToTokenType = new int[atn.ruleToStartState.length];
			for (int i = 0; i < atn.ruleToStartState.length; i++) {
				atn.ruleToTokenType[i] = atn.maxTokenType + i + 1;
			}

			for (int i = 0; i < atn.ruleToStartState.length; i++) {
				BasicBlockStartState bypassStart = new BasicBlockStartState();
				bypassStart.ruleIndex = i;
				atn.addState(bypassStart);

				BlockEndState bypassStop = new BlockEndState();
				bypassStop.ruleIndex = i;
				atn.addState(bypassStop);

				bypassStart.endState = bypassStop;
				atn.defineDecisionState(bypassStart);

				bypassStop.startState = bypassStart;

				ATNState endState;
				Transition excludeTransition = null;
				if (atn.ruleToStartState[i].isLeftRecursiveRule) {
					// wrap from the beginning of the rule to the StarLoopEntryState
					endState = null;
					for (ATNState state : atn.states) {
						if (state.ruleIndex != i) {
							continue;
						}

						if (!(state instanceof StarLoopEntryState)) {
							continue;
						}

						ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
						if (!(maybeLoopEndState instanceof LoopEndState)) {
							continue;
						}

						if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
							endState = state;
							break;
						}
					}

					if (endState == null) {
						throw new UnsupportedOperationException("Couldn't identify final state of the precedence rule prefix section.");
					}

					excludeTransition = ((StarLoopEntryState)endState).loopBackState.transition(0);
				}
				else {
					endState = atn.ruleToStopState[i];
				}

				// all non-excluded transitions that currently target end state need to target blockEnd instead
				for (ATNState state : atn.states) {
					for (Transition transition : state.transitions) {
						if (transition == excludeTransition) {
							continue;
						}

						if (transition.target == endState) {
							transition.target = bypassStop;
						}
					}
				}

				// all transitions leaving the rule start state need to leave blockStart instead
				while (atn.ruleToStartState[i].getNumberOfTransitions() > 0) {
					Transition transition = atn.ruleToStartState[i].removeTransition(atn.ruleToStartState[i].getNumberOfTransitions() - 1);
					bypassStart.addTransition(transition);
				}

				// link the new states
				atn.ruleToStartState[i].addTransition(new EpsilonTransition(bypassStart));
				bypassStop.addTransition(new EpsilonTransition(endState));

				ATNState matchState = new BasicState();
				atn.addState(matchState);
				matchState.addTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i]));
				bypassStart.addTransition(new EpsilonTransition(matchState));
			}

			if (deserializationOptions.isVerifyATN()) {
				// reverify after modification
				verifyATN(atn);
			}
		}

		return atn;
	}

	private int deserializeSets(int[] data, int p, List<IntervalSet> sets) {
		int nsets = data[p++];
		for (int i=0; i<nsets; i++) {
			int nintervals = data[p];
			p++;
			IntervalSet set = new IntervalSet();
			sets.add(set);

			boolean containsEof = data[p++] != 0;
			if (containsEof) {
				set.add(-1);
			}

			for (int j=0; j<nintervals; j++) {
				int a = data[p++];
				int b = data[p++];
				set.add(a, b);
			}
		}
		return p;
	}

	/**
	 * Analyze the {@link StarLoopEntryState} states in the specified ATN to set
	 * the {@link StarLoopEntryState#isPrecedenceDecision} field to the
	 * correct value.
	 *
	 * @param atn The ATN.
	 */
	protected void markPrecedenceDecisions(ATN atn) {
		for (ATNState state : atn.states) {
			if (!(state instanceof StarLoopEntryState)) {
				continue;
			}

			/* We analyze the ATN to determine if this ATN decision state is the
			 * decision for the closure block that determines whether a
			 * precedence rule should continue or complete.
			 */
			if (atn.ruleToStartState[state.ruleIndex].isLeftRecursiveRule) {
				ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
				if (maybeLoopEndState instanceof LoopEndState) {
					if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
						((StarLoopEntryState)state).isPrecedenceDecision = true;
					}
				}
			}
		}
	}

	protected void verifyATN(ATN atn) {
		// verify assumptions
		for (ATNState state : atn.states) {
			if (state == null) {
				continue;
			}

			checkCondition(state.onlyHasEpsilonTransitions() || state.getNumberOfTransitions() <= 1);

			if (state instanceof PlusBlockStartState) {
				checkCondition(((PlusBlockStartState)state).loopBackState != null);
			}

			if (state instanceof StarLoopEntryState) {
				StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
				checkCondition(starLoopEntryState.loopBackState != null);
				checkCondition(starLoopEntryState.getNumberOfTransitions() == 2);

				if (starLoopEntryState.transition(0).target instanceof StarBlockStartState) {
					checkCondition(starLoopEntryState.transition(1).target instanceof LoopEndState);
					checkCondition(!starLoopEntryState.nonGreedy);
				}
				else if (starLoopEntryState.transition(0).target instanceof LoopEndState) {
					checkCondition(starLoopEntryState.transition(1).target instanceof StarBlockStartState);
					checkCondition(starLoopEntryState.nonGreedy);
				}
				else {
					throw new IllegalStateException();
				}
			}

			if (state instanceof StarLoopbackState) {
				checkCondition(state.getNumberOfTransitions() == 1);
				checkCondition(state.transition(0).target instanceof StarLoopEntryState);
			}

			if (state instanceof LoopEndState) {
				checkCondition(((LoopEndState)state).loopBackState != null);
			}

			if (state instanceof RuleStartState) {
				checkCondition(((RuleStartState)state).stopState != null);
			}

			if (state instanceof BlockStartState) {
				checkCondition(((BlockStartState)state).endState != null);
			}

			if (state instanceof BlockEndState) {
				checkCondition(((BlockEndState)state).startState != null);
			}

			if (state instanceof DecisionState) {
				DecisionState decisionState = (DecisionState)state;
				checkCondition(decisionState.getNumberOfTransitions() <= 1 || decisionState.decision >= 0);
			}
			else {
				checkCondition(state.getNumberOfTransitions() <= 1 || state instanceof RuleStopState);
			}
		}
	}

	protected void checkCondition(boolean condition) {
		checkCondition(condition, null);
	}

	protected void checkCondition(boolean condition, String message) {
		if (!condition) {
			throw new IllegalStateException(message);
		}
	}

	protected static int toInt(char c) {
		return c;
	}

	protected static int toInt32(char[] data, int offset) {
		return (int)data[offset] | ((int)data[offset + 1] << 16);
	}

	protected static int toInt32(int[] data, int offset) {
		return data[offset] | (data[offset + 1] << 16);
	}

	protected Transition edgeFactory(ATN atn,
										 int type, int src, int trg,
										 int arg1, int arg2, int arg3,
										 List<IntervalSet> sets)
	{
		ATNState target = atn.states.get(trg);
		switch (type) {
			case Transition.EPSILON : return new EpsilonTransition(target);
			case Transition.RANGE :
				if (arg3 != 0) {
					return new RangeTransition(target, Token.EOF, arg2);
				}
				else {
					return new RangeTransition(target, arg1, arg2);
				}
			case Transition.RULE :
				RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
				return rt;
			case Transition.PREDICATE :
				PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
				return pt;
			case Transition.PRECEDENCE:
				return new PrecedencePredicateTransition(target, arg1);
			case Transition.ATOM :
				if (arg3 != 0) {
					return new AtomTransition(target, Token.EOF);
				}
				else {
					return new AtomTransition(target, arg1);
				}
			case Transition.ACTION :
				ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
				return a;
			case Transition.SET : return new SetTransition(target, sets.get(arg1));
			case Transition.NOT_SET : return new NotSetTransition(target, sets.get(arg1));
			case Transition.WILDCARD : return new WildcardTransition(target);
		}

		throw new IllegalArgumentException("The specified transition type is not valid.");
	}

	protected ATNState stateFactory(int type, int ruleIndex) {
		ATNState s;
		switch (type) {
			case ATNState.INVALID_TYPE: return null;
			case ATNState.BASIC : s = new BasicState(); break;
			case ATNState.RULE_START : s = new RuleStartState(); break;
			case ATNState.BLOCK_START : s = new BasicBlockStartState(); break;
			case ATNState.PLUS_BLOCK_START : s = new PlusBlockStartState(); break;
			case ATNState.STAR_BLOCK_START : s = new StarBlockStartState(); break;
			case ATNState.TOKEN_START : s = new TokensStartState(); break;
			case ATNState.RULE_STOP : s = new RuleStopState(); break;
			case ATNState.BLOCK_END : s = new BlockEndState(); break;
			case ATNState.STAR_LOOP_BACK : s = new StarLoopbackState(); break;
			case ATNState.STAR_LOOP_ENTRY : s = new StarLoopEntryState(); break;
			case ATNState.PLUS_LOOP_BACK : s = new PlusLoopbackState(); break;
			case ATNState.LOOP_END : s = new LoopEndState(); break;
			default :
				String message = String.format(Locale.getDefault(), "The specified state type %d is not valid.", type);
				throw new IllegalArgumentException(message);
		}

		s.ruleIndex = ruleIndex;
		return s;
	}

	protected LexerAction lexerActionFactory(LexerActionType type, int data1, int data2) {
		switch (type) {
		case CHANNEL:
			return new LexerChannelAction(data1);

		case CUSTOM:
			return new LexerCustomAction(data1, data2);

		case MODE:
			return new LexerModeAction(data1);

		case MORE:
			return LexerMoreAction.INSTANCE;

		case POP_MODE:
			return LexerPopModeAction.INSTANCE;

		case PUSH_MODE:
			return new LexerPushModeAction(data1);

		case SKIP:
			return LexerSkipAction.INSTANCE;

		case TYPE:
			return new LexerTypeAction(data1);

		default:
			throw new IllegalArgumentException(String.format(Locale.getDefault(), "The specified lexer action type %s is not valid.", type));
		}
	}

	/** Given a list of integers representing a serialized ATN, encode values too large to fit into 15 bits
	 *  as two 16bit values. We use the high bit (0x8000_0000) to indicate values requiring two 16 bit words.
	 *  If the high bit is set, we grab the next value and combine them to get a 31-bit value. The possible
	 *  input int values are [-1,0x7FFF_FFFF].
	 *
	 * 		| compression/encoding                         | uint16 count | type            |
	 * 		| -------------------------------------------- | ------------ | --------------- |
	 * 		| 0xxxxxxx xxxxxxxx                            | 1            | uint (15 bit)   |
	 * 		| 1xxxxxxx xxxxxxxx yyyyyyyy yyyyyyyy          | 2            | uint (16+ bits) |
	 * 		| 11111111 11111111 11111111 11111111          | 2            | int value -1    |
	 *
	 * 	This is only used (other than for testing) by {@link org.antlr.v4.codegen.model.SerializedJavaATN}
	 * 	to encode ints as char values for the java target, but it is convenient to combine it with the
	 * 	#decodeIntsEncodedAs16BitWords that follows as they are a pair (I did not want to introduce a new class
	 * 	into the runtime). Used only for Java Target.
	 */
	public static IntegerList encodeIntsWith16BitWords(IntegerList data) {
		IntegerList data16 = new IntegerList((int)(data.size()*1.5));
		for (int i = 0; i < data.size(); i++) {
			int v = data.get(i);
			if ( v==-1 ) { // use two max uint16 for -1
				data16.add(0xFFFF);
				data16.add(0xFFFF);
			}
			else if (v <= 0x7FFF) {
				data16.add(v);
			}
			else { // v > 0x7FFF
				if ( v>=0x7FFF_FFFF ) { // too big to fit in 15 bits + 16 bits? (+1 would be 8000_0000 which is bad encoding)
					throw new UnsupportedOperationException("Serialized ATN data element["+i+"] = "+v+" doesn't fit in 31 bits");
				}
				v = v & 0x7FFF_FFFF;					// strip high bit (sentinel) if set
				data16.add((v >> 16) | 0x8000);   // store high 15-bit word first and set high bit to say word follows
				data16.add((v & 0xFFFF)); 		// then store lower 16-bit word
			}
		}
		return data16;
	}

	public static int[] decodeIntsEncodedAs16BitWords(char[] data16) {
		return decodeIntsEncodedAs16BitWords(data16, false);
	}

	/** Convert a list of chars (16 uint) that represent a serialized and compressed list of ints for an ATN.
	 *  This method pairs with {@link #encodeIntsWith16BitWords(IntegerList)} above. Used only for Java Target.
	 */
	public static int[] decodeIntsEncodedAs16BitWords(char[] data16, boolean trimToSize) {
		// will be strictly smaller but we waste bit of space to avoid copying during initialization of parsers
		int[] data = new int[data16.length];
		int i = 0;
		int i2 = 0;
		while ( i < data16.length ) {
			char v = data16[i++];
			if ( (v & 0x8000) == 0 ) { // hi bit not set? Implies 1-word value
				data[i2++] = v; // 7 bit int
			}
			else { // hi bit set. Implies 2-word value
				char vnext = data16[i++];
				if ( v==0xFFFF && vnext == 0xFFFF ) { // is it -1?
					data[i2++] = -1;
				}
				else { // 31-bit int
					data[i2++] = (v & 0x7FFF) << 16 | (vnext & 0xFFFF);
				}
			}
		}
		if ( trimToSize ) {
			return Arrays.copyOf(data, i2);
		}
		return data;
	}
}
