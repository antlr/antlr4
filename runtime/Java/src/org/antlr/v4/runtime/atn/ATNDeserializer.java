/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author Sam Harwell
 */
public class ATNDeserializer {
	public static final int SERIALIZED_VERSION = 4;

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
		ATNDataReader reader = new ATNDataReader(data);

		int version = reader.readUInt16(false);
		if (version != SERIALIZED_VERSION) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		ATNType grammarType = ATNType.values()[reader.readUInt16()];
		int maxTokenType = reader.readUInt16();
		ATN atn = new ATN(grammarType, maxTokenType);

		//
		// STATES
		//
		List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<>();
		List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<>();
		int nstates = reader.readUInt16();
		for (int i=0; i<nstates; i++) {
			int stype = reader.readUInt16();
			// ignore bad type of states
			if ( stype==ATNState.INVALID_TYPE ) {
				atn.addState(null);
				continue;
			}

			int ruleIndex = reader.readUInt16();
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			ATNState s = stateFactory(stype, ruleIndex);
			if ( stype == ATNState.LOOP_END ) { // special case
				int loopBackStateNumber = reader.readUInt16();
				loopBackStateNumbers.add(new Pair<>((LoopEndState) s, loopBackStateNumber));
			}
			else if (s instanceof BlockStartState) {
				int endStateNumber = reader.readUInt16();
				endStateNumbers.add(new Pair<>((BlockStartState) s, endStateNumber));
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

		int numNonGreedyStates = reader.readUInt16();
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = reader.readUInt16();
			((DecisionState)atn.states.get(stateNumber)).nonGreedy = true;
		}

		int numPrecedenceStates = reader.readUInt16();
		for (int i = 0; i < numPrecedenceStates; i++) {
			int stateNumber = reader.readUInt16();
			((RuleStartState)atn.states.get(stateNumber)).isLeftRecursiveRule = true;
		}

		//
		// RULES
		//
		int nrules = reader.readUInt16();
		if ( atn.grammarType == ATNType.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
		}

		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = reader.readUInt16();
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATNType.LEXER ) {
				int tokenType = reader.readUInt16();
				if (tokenType == 0xFFFF) {
					tokenType = Token.EOF;
				}

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
		int nmodes = reader.readUInt16();
		for (int i=0; i < nmodes; i++) {
			int s = reader.readUInt16();
			atn.modeToStartState.add((TokensStartState)atn.states.get(s));
		}

		//
		// SETS
		//
		List<IntervalSet> sets = new ArrayList<>();

		// First, read all sets with 16-bit Unicode code points <= U+FFFF.
		deserializeSets(reader, sets, ATNSerializer.UnicodeSerializeMode.UNICODE_BMP);

		// Next, deserialize sets with 32-bit arguments <= U+10FFFF.
		deserializeSets(reader, sets, ATNSerializer.UnicodeSerializeMode.UNICODE_SMP);

		//
		// EDGES
		//
		int nedges = reader.readUInt16();
		for (int i=0; i<nedges; i++) {
			int src = reader.readUInt16();
			int trg = reader.readUInt16();
			int ttype = reader.readUInt16();
			int arg1 = reader.readUInt16();
			int arg2 = reader.readUInt16();
			int arg3 = reader.readUInt16();
			Transition trans = edgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
			ATNState srcState = atn.states.get(src);
			srcState.addTransition(trans);
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
				BlockStartState blockStartState = (BlockStartState) state;
				// we need to know the end state to set its start state
				// block end states can only be associated to a single block start state
				if (blockStartState.endState == null || blockStartState.endState.startState != null) {
					throw new IllegalStateException();
				}

				blockStartState.endState.startState = blockStartState;
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
		int ndecisions = reader.readUInt16();
		for (int i=1; i<=ndecisions; i++) {
			int s = reader.readUInt16();
			DecisionState decState = (DecisionState)atn.states.get(s);
			atn.decisionToState.add(decState);
			decState.decision = i-1;
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			atn.lexerActions = new LexerAction[reader.readUInt16()];
			for (int i = 0; i < atn.lexerActions.length; i++) {
				LexerActionType actionType = LexerActionType.values()[reader.readUInt16()];
				int data1 = reader.readUInt16();
				if (data1 == 0xFFFF) {
					data1 = -1;
				}

				int data2 = reader.readUInt16();
				if (data2 == 0xFFFF) {
					data2 = -1;
				}

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

	private void deserializeSets(ATNDataReader reader, List<IntervalSet> sets, ATNSerializer.UnicodeSerializeMode mode) {
		int nsets = reader.readUInt16();
		for (int i=0; i<nsets; i++) {
			int nintervals = reader.readUInt16();
			IntervalSet set = new IntervalSet();
			sets.add(set);

			boolean containsEof = reader.readUInt16() != 0;
			if (containsEof) {
				set.add(-1);
			}

			for (int j=0; j<nintervals; j++) {
				int a, b;
				if (mode == ATNSerializer.UnicodeSerializeMode.UNICODE_BMP) {
					a = reader.readUInt16();
					b = reader.readUInt16();
				} else {
					a = reader.readUInt32();
					b = reader.readUInt32();
				}
				set.add(a, b);
			}
		}
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
				return new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, arg3, target);
			case Transition.PREDICATE :
				return new PredicateTransition(target, arg1, arg2, arg3 != 0);
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
				return new ActionTransition(target, arg1, arg2, arg3 != 0);
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
}
