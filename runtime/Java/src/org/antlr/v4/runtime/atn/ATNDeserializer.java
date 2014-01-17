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
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Tuple;
import org.antlr.v4.runtime.misc.Tuple2;

import java.io.InvalidClassException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
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
		BASE_SERIALIZED_UUID = UUID.fromString("E4178468-DF95-44D0-AD87-F22A5D5FB6D3");
		ADDED_LEXER_ACTIONS = UUID.fromString("AB35191A-1603-487E-B75A-479B831EAF6D");

		SUPPORTED_UUIDS = new ArrayList<UUID>();
		SUPPORTED_UUIDS.add(BASE_SERIALIZED_UUID);
		SUPPORTED_UUIDS.add(ADDED_LEXER_ACTIONS);

		SERIALIZED_UUID = ADDED_LEXER_ACTIONS;
	}

	@NotNull
	private final ATNDeserializationOptions deserializationOptions;

	public ATNDeserializer() {
		this(ATNDeserializationOptions.getDefaultOptions());
	}

	public ATNDeserializer(@Nullable ATNDeserializationOptions deserializationOptions) {
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
	public ATN deserialize(@NotNull char[] data) {
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

		boolean supportsLexerActions = isFeatureSupported(ADDED_LEXER_ACTIONS, uuid);

		ATNType grammarType = ATNType.values()[toInt(data[p++])];
		int maxTokenType = toInt(data[p++]);
		ATN atn = new ATN(grammarType, maxTokenType);

		//
		// STATES
		//
		List<Tuple2<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Tuple2<LoopEndState, Integer>>();
		List<Tuple2<BlockStartState, Integer>> endStateNumbers = new ArrayList<Tuple2<BlockStartState, Integer>>();
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
				loopBackStateNumbers.add(Tuple.create((LoopEndState)s, loopBackStateNumber));
			}
			else if (s instanceof BlockStartState) {
				int endStateNumber = toInt(data[p++]);
				endStateNumbers.add(Tuple.create((BlockStartState)s, endStateNumber));
			}
			atn.addState(s);
		}

		// delay the assignment of loop back and end states until we know all the state instances have been initialized
		for (Tuple2<LoopEndState, Integer> pair : loopBackStateNumbers) {
			pair.getItem1().loopBackState = atn.states.get(pair.getItem2());
		}

		for (Tuple2<BlockStartState, Integer> pair : endStateNumbers) {
			pair.getItem1().endState = (BlockEndState)atn.states.get(pair.getItem2());
		}

		int numNonGreedyStates = toInt(data[p++]);
		for (int i = 0; i < numNonGreedyStates; i++) {
			int stateNumber = toInt(data[p++]);
			((DecisionState)atn.states.get(stateNumber)).nonGreedy = true;
		}

		int numSllDecisions = toInt(data[p++]);
		for (int i = 0; i < numSllDecisions; i++) {
			int stateNumber = toInt(data[p++]);
			((DecisionState)atn.states.get(stateNumber)).sll = true;
		}

		int numPrecedenceStates = toInt(data[p++]);
		for (int i = 0; i < numPrecedenceStates; i++) {
			int stateNumber = toInt(data[p++]);
			((RuleStartState)atn.states.get(stateNumber)).isPrecedenceRule = true;
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
			startState.leftFactored = toInt(data[p++]) != 0;
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
					if (actionIndexIgnored == 0xFFFF) {
						actionIndexIgnored = -1;
					}
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

		atn.modeToDFA = new DFA[nmodes];
		for (int i = 0; i < nmodes; i++) {
			atn.modeToDFA[i] = new DFA(atn.modeToStartState.get(i));
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
			boolean returningToLeftFactored = state.ruleIndex >= 0 && atn.ruleToStartState[state.ruleIndex].leftFactored;
			for (int i = 0; i < state.getNumberOfTransitions(); i++) {
				Transition t = state.transition(i);
				if (!(t instanceof RuleTransition)) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)t;
				boolean returningFromLeftFactored = atn.ruleToStartState[ruleTransition.target.ruleIndex].leftFactored;
				if (!returningFromLeftFactored && returningToLeftFactored) {
					continue;
				}

				atn.ruleToStopState[ruleTransition.target.ruleIndex].addTransition(new EpsilonTransition(ruleTransition.followState));
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
					int data2 = toInt(data[p++]);
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

		atn.decisionToDFA = new DFA[ndecisions];
		for (int i = 0; i < ndecisions; i++) {
			atn.decisionToDFA[i] = new DFA(atn.decisionToState.get(i), i);
		}

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
				if (atn.ruleToStartState[i].isPrecedenceRule) {
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

		if (deserializationOptions.isOptimize()) {
			while (true) {
				int optimizationCount = 0;
				optimizationCount += inlineSetRules(atn);
				optimizationCount += combineChainedEpsilons(atn);
				boolean preserveOrder = atn.grammarType == ATNType.LEXER;
				optimizationCount += optimizeSets(atn, preserveOrder);
				if (optimizationCount == 0) {
					break;
				}
			}

			if (deserializationOptions.isVerifyATN()) {
				// reverify after modification
				verifyATN(atn);
			}
		}

		identifyTailCalls(atn);

		return atn;
	}

	/**
	 * Analyze the {@link StarLoopEntryState} states in the specified ATN to set
	 * the {@link StarLoopEntryState#precedenceRuleDecision} field to the
	 * correct value.
	 *
	 * @param atn The ATN.
	 */
	protected void markPrecedenceDecisions(@NotNull ATN atn) {
		for (ATNState state : atn.states) {
			if (!(state instanceof StarLoopEntryState)) {
				continue;
			}

			/* We analyze the ATN to determine if this ATN decision state is the
			 * decision for the closure block that determines whether a
			 * precedence rule should continue or complete.
			 */
			if (atn.ruleToStartState[state.ruleIndex].isPrecedenceRule) {
				ATNState maybeLoopEndState = state.transition(state.getNumberOfTransitions() - 1).target;
				if (maybeLoopEndState instanceof LoopEndState) {
					if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.transition(0).target instanceof RuleStopState) {
						((StarLoopEntryState)state).precedenceRuleDecision = true;
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

	private static int inlineSetRules(ATN atn) {
		int inlinedCalls = 0;

		Transition[] ruleToInlineTransition = new Transition[atn.ruleToStartState.length];
		for (int i = 0; i < atn.ruleToStartState.length; i++) {
			RuleStartState startState = atn.ruleToStartState[i];
			ATNState middleState = startState;
			while (middleState.onlyHasEpsilonTransitions()
				&& middleState.getNumberOfOptimizedTransitions() == 1
				&& middleState.getOptimizedTransition(0).getSerializationType() == Transition.EPSILON)
			{
				middleState = middleState.getOptimizedTransition(0).target;
			}

			if (middleState.getNumberOfOptimizedTransitions() != 1) {
				continue;
			}

			Transition matchTransition = middleState.getOptimizedTransition(0);
			ATNState matchTarget = matchTransition.target;
			if (matchTransition.isEpsilon()
				|| !matchTarget.onlyHasEpsilonTransitions()
				|| matchTarget.getNumberOfOptimizedTransitions() != 1
				|| !(matchTarget.getOptimizedTransition(0).target instanceof RuleStopState))
			{
				continue;
			}

			switch (matchTransition.getSerializationType()) {
			case Transition.ATOM:
			case Transition.RANGE:
			case Transition.SET:
				ruleToInlineTransition[i] = matchTransition;
				break;

			case Transition.NOT_SET:
			case Transition.WILDCARD:
				// not implemented yet
				continue;

			default:
				continue;
			}
		}

		for (int stateNumber = 0; stateNumber < atn.states.size(); stateNumber++) {
			ATNState state = atn.states.get(stateNumber);
			if (state.ruleIndex < 0) {
				continue;
			}

			List<Transition> optimizedTransitions = null;
			for (int i = 0; i < state.getNumberOfOptimizedTransitions(); i++) {
				Transition transition = state.getOptimizedTransition(i);
				if (!(transition instanceof RuleTransition)) {
					if (optimizedTransitions != null) {
						optimizedTransitions.add(transition);
					}

					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)transition;
				Transition effective = ruleToInlineTransition[ruleTransition.target.ruleIndex];
				if (effective == null) {
					if (optimizedTransitions != null) {
						optimizedTransitions.add(transition);
					}

					continue;
				}

				if (optimizedTransitions == null) {
					optimizedTransitions = new ArrayList<Transition>();
					for (int j = 0; j < i; j++) {
						optimizedTransitions.add(state.getOptimizedTransition(i));
					}
				}

				inlinedCalls++;
				ATNState target = ruleTransition.followState;
				ATNState intermediateState = new BasicState();
				intermediateState.setRuleIndex(target.ruleIndex);
				atn.addState(intermediateState);
				optimizedTransitions.add(new EpsilonTransition(intermediateState));

				switch (effective.getSerializationType()) {
				case Transition.ATOM:
					intermediateState.addTransition(new AtomTransition(target, ((AtomTransition)effective).label));
					break;

				case Transition.RANGE:
					intermediateState.addTransition(new RangeTransition(target, ((RangeTransition)effective).from, ((RangeTransition)effective).to));
					break;

				case Transition.SET:
					intermediateState.addTransition(new SetTransition(target, effective.label()));
					break;

				default:
					throw new UnsupportedOperationException();
				}
			}

			if (optimizedTransitions != null) {
				if (state.isOptimized()) {
					while (state.getNumberOfOptimizedTransitions() > 0) {
						state.removeOptimizedTransition(state.getNumberOfOptimizedTransitions() - 1);
					}
				}

				for (Transition transition : optimizedTransitions) {
					state.addOptimizedTransition(transition);
				}
			}
		}

		if (ParserATNSimulator.debug) {
			System.out.println("ATN runtime optimizer removed " + inlinedCalls + " rule invocations by inlining sets.");
		}

		return inlinedCalls;
	}

	private static int combineChainedEpsilons(ATN atn) {
		int removedEdges = 0;

		nextState:
		for (ATNState state : atn.states) {
			if (!state.onlyHasEpsilonTransitions() || state instanceof RuleStopState) {
				continue;
			}

			List<Transition> optimizedTransitions = null;
			nextTransition:
			for (int i = 0; i < state.getNumberOfOptimizedTransitions(); i++) {
				Transition transition = state.getOptimizedTransition(i);
				ATNState intermediate = transition.target;
				if (transition.getSerializationType() != Transition.EPSILON
					|| intermediate.getStateType() != ATNState.BASIC
					|| !intermediate.onlyHasEpsilonTransitions())
				{
					if (optimizedTransitions != null) {
						optimizedTransitions.add(transition);
					}

					continue nextTransition;
				}

				for (int j = 0; j < intermediate.getNumberOfOptimizedTransitions(); j++) {
					if (intermediate.getOptimizedTransition(j).getSerializationType() != Transition.EPSILON) {
						if (optimizedTransitions != null) {
							optimizedTransitions.add(transition);
						}

						continue nextTransition;
					}
				}

				removedEdges++;
				if (optimizedTransitions == null) {
					optimizedTransitions = new ArrayList<Transition>();
					for (int j = 0; j < i; j++) {
						optimizedTransitions.add(state.getOptimizedTransition(j));
					}
				}

				for (int j = 0; j < intermediate.getNumberOfOptimizedTransitions(); j++) {
					ATNState target = intermediate.getOptimizedTransition(j).target;
					optimizedTransitions.add(new EpsilonTransition(target));
				}
			}

			if (optimizedTransitions != null) {
				if (state.isOptimized()) {
					while (state.getNumberOfOptimizedTransitions() > 0) {
						state.removeOptimizedTransition(state.getNumberOfOptimizedTransitions() - 1);
					}
				}

				for (Transition transition : optimizedTransitions) {
					state.addOptimizedTransition(transition);
				}
			}
		}

		if (ParserATNSimulator.debug) {
			System.out.println("ATN runtime optimizer removed " + removedEdges + " transitions by combining chained epsilon transitions.");
		}

		return removedEdges;
	}

	private static int optimizeSets(ATN atn, boolean preserveOrder) {
		if (preserveOrder) {
			// this optimization currently doesn't preserve edge order.
			return 0;
		}

		int removedPaths = 0;
		List<DecisionState> decisions = atn.decisionToState;
		for (DecisionState decision : decisions) {
			IntervalSet setTransitions = new IntervalSet();
			for (int i = 0; i < decision.getNumberOfOptimizedTransitions(); i++) {
				Transition epsTransition = decision.getOptimizedTransition(i);
				if (!(epsTransition instanceof EpsilonTransition)) {
					continue;
				}

				if (epsTransition.target.getNumberOfOptimizedTransitions() != 1) {
					continue;
				}

				Transition transition = epsTransition.target.getOptimizedTransition(0);
				if (!(transition.target instanceof BlockEndState)) {
					continue;
				}

				if (transition instanceof NotSetTransition) {
					// TODO: not yet implemented
					continue;
				}

				if (transition instanceof AtomTransition
					|| transition instanceof RangeTransition
					|| transition instanceof SetTransition)
				{
					setTransitions.add(i);
				}
			}

			if (setTransitions.size() <= 1) {
				continue;
			}

			List<Transition> optimizedTransitions = new ArrayList<Transition>();
			for (int i = 0; i < decision.getNumberOfOptimizedTransitions(); i++) {
				if (!setTransitions.contains(i)) {
					optimizedTransitions.add(decision.getOptimizedTransition(i));
				}
			}

			ATNState blockEndState = decision.getOptimizedTransition(setTransitions.getMinElement()).target.getOptimizedTransition(0).target;
			IntervalSet matchSet = new IntervalSet();
			for (int i = 0; i < setTransitions.getIntervals().size(); i++) {
				Interval interval = setTransitions.getIntervals().get(i);
				for (int j = interval.a; j <= interval.b; j++) {
					Transition matchTransition = decision.getOptimizedTransition(j).target.getOptimizedTransition(0);
					if (matchTransition instanceof NotSetTransition) {
						throw new UnsupportedOperationException("Not yet implemented.");
					} else {
						matchSet.addAll(matchTransition.label());
					}
				}
			}

			Transition newTransition;
			if (matchSet.getIntervals().size() == 1) {
				if (matchSet.size() == 1) {
					newTransition = new AtomTransition(blockEndState, matchSet.getMinElement());
				} else {
					Interval matchInterval = matchSet.getIntervals().get(0);
					newTransition = new RangeTransition(blockEndState, matchInterval.a, matchInterval.b);
				}
			} else {
				newTransition = new SetTransition(blockEndState, matchSet);
			}

			ATNState setOptimizedState = new BasicState();
			setOptimizedState.setRuleIndex(decision.ruleIndex);
			atn.addState(setOptimizedState);

			setOptimizedState.addTransition(newTransition);
			optimizedTransitions.add(new EpsilonTransition(setOptimizedState));

			removedPaths += decision.getNumberOfOptimizedTransitions() - optimizedTransitions.size();

			if (decision.isOptimized()) {
				while (decision.getNumberOfOptimizedTransitions() > 0) {
					decision.removeOptimizedTransition(decision.getNumberOfOptimizedTransitions() - 1);
				}
			}

			for (Transition transition : optimizedTransitions) {
				decision.addOptimizedTransition(transition);
			}
		}

		if (ParserATNSimulator.debug) {
			System.out.println("ATN runtime optimizer removed " + removedPaths + " paths by collapsing sets.");
		}

		return removedPaths;
	}

	private static void identifyTailCalls(ATN atn) {
		for (ATNState state : atn.states) {
			for (Transition transition : state.transitions) {
				if (!(transition instanceof RuleTransition)) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)transition;
				ruleTransition.tailCall = testTailCall(atn, ruleTransition, false);
				ruleTransition.optimizedTailCall = testTailCall(atn, ruleTransition, true);
			}

			if (!state.isOptimized()) {
				continue;
			}

			for (Transition transition : state.optimizedTransitions) {
				if (!(transition instanceof RuleTransition)) {
					continue;
				}

				RuleTransition ruleTransition = (RuleTransition)transition;
				ruleTransition.tailCall = testTailCall(atn, ruleTransition, false);
				ruleTransition.optimizedTailCall = testTailCall(atn, ruleTransition, true);
			}
		}
	}

	private static boolean testTailCall(ATN atn, RuleTransition transition, boolean optimizedPath) {
		if (!optimizedPath && transition.tailCall) {
			return true;
		}
		if (optimizedPath && transition.optimizedTailCall) {
			return true;
		}

		BitSet reachable = new BitSet(atn.states.size());
		Deque<ATNState> worklist = new ArrayDeque<ATNState>();
		worklist.add(transition.followState);
		while (!worklist.isEmpty()) {
			ATNState state = worklist.pop();
			if (reachable.get(state.stateNumber)) {
				continue;
			}

			if (state instanceof RuleStopState) {
				continue;
			}

			if (!state.onlyHasEpsilonTransitions()) {
				return false;
			}

			List<Transition> transitions = optimizedPath ? state.optimizedTransitions : state.transitions;
			for (Transition t : transitions) {
				if (t.getSerializationType() != Transition.EPSILON) {
					return false;
				}

				worklist.add(t.target);
			}
		}

		return true;
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

	@NotNull
	protected Transition edgeFactory(@NotNull ATN atn,
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
