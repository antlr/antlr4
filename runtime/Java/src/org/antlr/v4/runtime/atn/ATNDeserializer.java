/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Pair;

import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 *
 * @author Sam Harwell
 */
public class ATNDeserializer {
	public static final int SERIALIZED_VERSION;
	static {
		/* This value should never change. Updates following this version are
		 * reflected as change in the unique ID SERIALIZED_UUID.
		 */
		SERIALIZED_VERSION = 3;
	}

	/**
	 * This is the earliest supported serialized UUID.
	 */
	private static final UUID BASE_SERIALIZED_UUID;
	/**
	 * This UUID indicates an extension of {@link BASE_SERIALIZED_UUID} for the
	 * addition of precedence predicates.
	 */
	private static final UUID ADDED_PRECEDENCE_TRANSITIONS;
	/**
	 * This UUID indicates an extension of {@link #ADDED_PRECEDENCE_TRANSITIONS}
	 * for the addition of lexer actions encoded as a sequence of
	 * {@link LexerAction} instances.
	 */
	private static final UUID ADDED_LEXER_ACTIONS;
	/**
	 * This list contains all of the currently supported UUIDs, ordered by when
	 * the feature first appeared in this branch.
	 */
	private static final List<UUID> SUPPORTED_UUIDS;

	/**
	 * This is the current serialized UUID.
	 */
	public static final UUID SERIALIZED_UUID;
	static {
		/* WARNING: DO NOT MERGE THESE LINES. If UUIDs differ during a merge,
		 * resolve the conflict by generating a new ID!
		 */
		BASE_SERIALIZED_UUID = UUID.fromString("33761B2D-78BB-4A43-8B0B-4F5BEE8AACF3");
		ADDED_PRECEDENCE_TRANSITIONS = UUID.fromString("1DA0C57D-6C06-438A-9B27-10BCB3CE0F61");
		ADDED_LEXER_ACTIONS = UUID.fromString("AADB8D7E-AEEF-4415-AD2B-8204D6CF042E");

		SUPPORTED_UUIDS = new ArrayList<UUID>();
		SUPPORTED_UUIDS.add(BASE_SERIALIZED_UUID);
		SUPPORTED_UUIDS.add(ADDED_PRECEDENCE_TRANSITIONS);
		SUPPORTED_UUIDS.add(ADDED_LEXER_ACTIONS);

		SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
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

	/**
	 * Determines if a particular serialized representation of an ATN supports
	 * a particular feature, identified by the {@link UUID} used for serializing
	 * the ATN at the time the feature was first introduced.
	 *
	 * @param feature The {@link UUID} marking the first time the feature was
	 * supported in the serialized ATN.
	 * @param actualUuid The {@link UUID} of the actual serialized ATN which is
	 * currently being deserialized.
	 * @return {@code true} if the {@code actualUuid} value represents a
	 * serialized ATN at or after the feature identified by {@code feature} was
	 * introduced; otherwise, {@code false}.
	 */
	protected boolean isFeatureSupported(UUID feature, UUID actualUuid) {
		int featureIndex = SUPPORTED_UUIDS.indexOf(feature);
		if (featureIndex < 0) {
			return false;
		}

		return SUPPORTED_UUIDS.indexOf(actualUuid) >= featureIndex;
	}

	@SuppressWarnings("deprecation")
	public ATN deserialize(char[] data) {
		data = data.clone();
		// don't adjust the first value since that's the version number
		for (int i = 1; i < data.length; i++) {
			data[i] = (char)(data[i] - 2);
		}

		int p = 0;
		int version = toInt(data[p++]);
		if (version != SERIALIZED_VERSION) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with version %d (expected %d).", version, SERIALIZED_VERSION);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		UUID uuid = toUUID(data, p);
		p += 8;
		if (!SUPPORTED_UUIDS.contains(uuid)) {
			String reason = String.format(Locale.getDefault(), "Could not deserialize ATN with UUID %s (expected %s or a legacy UUID).", uuid, SERIALIZED_UUID);
			throw new UnsupportedOperationException(new InvalidClassException(ATN.class.getName(), reason));
		}

		boolean supportsPrecedencePredicates = isFeatureSupported(ADDED_PRECEDENCE_TRANSITIONS, uuid);
		boolean supportsLexerActions = isFeatureSupported(ADDED_LEXER_ACTIONS, uuid);

		ATNType grammarType = ATNType.values()[toInt(data[p++])];
		int maxTokenType = toInt(data[p++]);
		ATN atn = new ATN(grammarType, maxTokenType);

		//
		// STATES
		//
		List<Pair<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Pair<LoopEndState, Integer>>();
		List<Pair<BlockStartState, Integer>> endStateNumbers = new ArrayList<Pair<BlockStartState, Integer>>();
		int nstates = toInt(data[p++]);
		for (int i=0; i<nstates; i++) {
			int stype = toInt(data[p++]);
			// ignore bad type of states
			if ( stype==ATNState.INVALID_TYPE ) {
				atn.addState(null);
				continue;
			}

			int ruleIndex = toInt(data[p++]);
			if (ruleIndex == Character.MAX_VALUE) {
				ruleIndex = -1;
			}

			ATNState s = stateFactory(stype, ruleIndex);
			if ( stype == ATNState.LOOP_END ) { // special case
				int loopBackStateNumber = toInt(data[p++]);
				loopBackStateNumbers.add(new Pair<LoopEndState, Integer>((LoopEndState)s, loopBackStateNumber));
			}
			else if (s instanceof BlockStartState) {
				int endStateNumber = toInt(data[p++]);
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

		int numNonGreedyStates = toInt(data[p++]);
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = toInt(data[p++]);
			((DecisionState)atn.states.get(stateNumber)).nonGreedy = true;
		}

		if (supportsPrecedencePredicates) {
			int numPrecedenceStates = toInt(data[p++]);
			for (int i = 0; i < numPrecedenceStates; i++) {
				int stateNumber = toInt(data[p++]);
				((RuleStartState)atn.states.get(stateNumber)).isLeftRecursiveRule = true;
			}
		}

		//
		// RULES
		//
		int nrules = toInt(data[p++]);
		if ( atn.grammarType == ATNType.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
		}

		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = toInt(data[p++]);
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATNType.LEXER ) {
				int tokenType = toInt(data[p++]);
				if (tokenType == 0xFFFF) {
					tokenType = Token.EOF;
				}

				atn.ruleToTokenType[i] = tokenType;

				if (!isFeatureSupported(ADDED_LEXER_ACTIONS, uuid)) {
					// this piece of unused metadata was serialized prior to the
					// addition of LexerAction
					int actionIndexIgnored = toInt(data[p++]);
				}
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
		int nmodes = toInt(data[p++]);
		for (int i=0; i<nmodes; i++) {
			int s = toInt(data[p++]);
			atn.modeToStartState.add((TokensStartState)atn.states.get(s));
		}

		//
		// SETS
		//
		List<IntervalSet> sets = new ArrayList<IntervalSet>();
		int nsets = toInt(data[p++]);
		for (int i=0; i<nsets; i++) {
			int nintervals = toInt(data[p]);
			p++;
			IntervalSet set = new IntervalSet();
			sets.add(set);

			boolean containsEof = toInt(data[p++]) != 0;
			if (containsEof) {
				set.add(-1);
			}

			for (int j=0; j<nintervals; j++) {
				set.add(toInt(data[p]), toInt(data[p + 1]));
				p += 2;
			}
		}

		//
		// EDGES
		//
		int nedges = toInt(data[p++]);
		for (int i=0; i<nedges; i++) {
			int src = toInt(data[p]);
			int trg = toInt(data[p+1]);
			int ttype = toInt(data[p+2]);
			int arg1 = toInt(data[p+3]);
			int arg2 = toInt(data[p+4]);
			int arg3 = toInt(data[p+5]);
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
		int ndecisions = toInt(data[p++]);
		for (int i=1; i<=ndecisions; i++) {
			int s = toInt(data[p++]);
			DecisionState decState = (DecisionState)atn.states.get(s);
			atn.decisionToState.add(decState);
			decState.decision = i-1;
		}

		//
		// LEXER ACTIONS
		//
		if (atn.grammarType == ATNType.LEXER) {
			if (supportsLexerActions) {
				atn.lexerActions = new LexerAction[toInt(data[p++])];
				for (int i = 0; i < atn.lexerActions.length; i++) {
					LexerActionType actionType = LexerActionType.values()[toInt(data[p++])];
					int data1 = toInt(data[p++]);
					if (data1 == 0xFFFF) {
						data1 = -1;
					}

					int data2 = toInt(data[p++]);
					if (data2 == 0xFFFF) {
						data2 = -1;
					}

					LexerAction lexerAction = lexerActionFactory(actionType, data1, data2);

					atn.lexerActions[i] = lexerAction;
				}
			}
			else {
				// for compatibility with older serialized ATNs, convert the old
				// serialized action index for action transitions to the new
				// form, which is the index of a LexerCustomAction
				List<LexerAction> legacyLexerActions = new ArrayList<LexerAction>();
				for (ATNState state : atn.states) {
					for (int i = 0; i < state.getNumberOfTransitions(); i++) {
						Transition transition = state.transition(i);
						if (!(transition instanceof ActionTransition)) {
							continue;
						}

						int ruleIndex = ((ActionTransition)transition).ruleIndex;
						int actionIndex = ((ActionTransition)transition).actionIndex;
						LexerCustomAction lexerAction = new LexerCustomAction(ruleIndex, actionIndex);
						state.setTransition(i, new ActionTransition(transition.target, ruleIndex, legacyLexerActions.size(), false));
						legacyLexerActions.add(lexerAction);
					}
				}

				atn.lexerActions = legacyLexerActions.toArray(new LexerAction[legacyLexerActions.size()]);
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

	protected static long toLong(char[] data, int offset) {
		long lowOrder = toInt32(data, offset) & 0x00000000FFFFFFFFL;
		return lowOrder | ((long)toInt32(data, offset + 2) << 32);
	}

	protected static UUID toUUID(char[] data, int offset) {
		long leastSigBits = toLong(data, offset);
		long mostSigBits = toLong(data, offset + 4);
		return new UUID(mostSigBits, leastSigBits);
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
			String message = String.format(Locale.getDefault(), "The specified lexer action type %d is not valid.", type);
			throw new IllegalArgumentException(message);
		}
	}
}
