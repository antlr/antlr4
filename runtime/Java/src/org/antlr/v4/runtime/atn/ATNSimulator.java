/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Tuple;
import org.antlr.v4.runtime.misc.Tuple2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.List;

public abstract class ATNSimulator {
	public static final int SERIALIZED_NON_GREEDY_MASK = 0x8000;
	public static final int SERIALIZED_STATE_TYPE_MASK = 0x7FFF;

	/** Must distinguish between missing edge and edge we know leads nowhere */
	@NotNull
	public static final DFAState ERROR;
	@NotNull
	public final ATN atn;

	static {
		ERROR = new DFAState(new ATNConfigSet(), 0, 0);
		ERROR.stateNumber = Integer.MAX_VALUE;
	}

	public ATNSimulator(@NotNull ATN atn) {
		this.atn = atn;
	}

	public abstract void reset();

	public static ATN deserialize(@NotNull char[] data) {
		ATN atn = new ATN();
		List<IntervalSet> sets = new ArrayList<IntervalSet>();
		int p = 0;
		atn.grammarType = toInt(data[p++]);
		atn.maxTokenType = toInt(data[p++]);

		//
		// STATES
		//
		List<Tuple2<LoopEndState, Integer>> loopBackStateNumbers = new ArrayList<Tuple2<LoopEndState, Integer>>();
		List<Tuple2<BlockStartState, Integer>> endStateNumbers = new ArrayList<Tuple2<BlockStartState, Integer>>();
		int nstates = toInt(data[p++]);
		for (int i=1; i<=nstates; i++) {
			int stype = toInt(data[p++]);
			// ignore bad type of states
			if ( stype==ATNState.INVALID_TYPE ) {
				atn.addState(null);
				continue;
			}

			boolean nonGreedy = (stype & SERIALIZED_NON_GREEDY_MASK) != 0;
			stype &= SERIALIZED_STATE_TYPE_MASK;
			ATNState s = stateFactory(stype, i);
			if (s instanceof DecisionState) {
				((DecisionState)s).nonGreedy = nonGreedy;
			}
			s.ruleIndex = toInt(data[p++]);
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

		//
		// RULES
		//
		int nrules = toInt(data[p++]);
		if ( atn.grammarType == ATN.LEXER ) {
			atn.ruleToTokenType = new int[nrules];
			atn.ruleToActionIndex = new int[nrules];
		}
		atn.ruleToStartState = new RuleStartState[nrules];
		for (int i=0; i<nrules; i++) {
			int s = toInt(data[p++]);
			RuleStartState startState = (RuleStartState)atn.states.get(s);
			atn.ruleToStartState[i] = startState;
			if ( atn.grammarType == ATN.LEXER ) {
				int tokenType = toInt(data[p++]);
				atn.ruleToTokenType[i] = tokenType;
				int actionIndex = toInt(data[p++]);
				atn.ruleToActionIndex[i] = actionIndex;
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
		int nsets = toInt(data[p++]);
		for (int i=1; i<=nsets; i++) {
			int nintervals = toInt(data[p]);
			p++;
			IntervalSet set = new IntervalSet();
			sets.add(set);
			for (int j=1; j<=nintervals; j++) {
				set.add(toInt(data[p]), toInt(data[p + 1]));
				p += 2;
			}
		}

		//
		// EDGES
		//
		int nedges = toInt(data[p++]);
		for (int i=1; i<=nedges; i++) {
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

		atn.decisionToDFA = new DFA[ndecisions];
		for (int i = 0; i < ndecisions; i++) {
			atn.decisionToDFA[i] = new DFA(atn.decisionToState.get(i), i);
		}

		while (true) {
			int optimizationCount = 0;
			optimizationCount += inlineSetRules(atn);
			optimizationCount += combineChainedEpsilons(atn);
			boolean preserveOrder = atn.grammarType == ATN.LEXER;
			optimizationCount += optimizeSets(atn, preserveOrder);
			if (optimizationCount == 0) {
				break;
			}
		}

		identifyTailCalls(atn);

		verifyATN(atn);
		return atn;
	}

	private static void verifyATN(ATN atn) {
		// verify assumptions
		for (ATNState state : atn.states) {
			if (state == null) {
				continue;
			}

			if (state instanceof PlusBlockStartState) {
				if (((PlusBlockStartState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof StarLoopEntryState) {
				if (((StarLoopEntryState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof LoopEndState) {
				if (((LoopEndState)state).loopBackState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof RuleStartState) {
				if (((RuleStartState)state).stopState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof BlockStartState) {
				if (((BlockStartState)state).endState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof BlockEndState) {
				if (((BlockEndState)state).startState == null) {
					throw new IllegalStateException();
				}
			}

			if (state instanceof DecisionState) {
				DecisionState decisionState = (DecisionState)state;
				if (decisionState.getNumberOfTransitions() > 1 && decisionState.decision < 0) {
					throw new IllegalStateException();
				}
			}
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
				ATNState intermediateState = new ATNState();
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

			ATNState setOptimizedState = new ATNState();
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

	public static int toInt(char c) {
		return c==65535 ? -1 : c;
	}

	public static Transition edgeFactory(@NotNull ATN atn,
										 int type, int src, int trg,
										 int arg1, int arg2, int arg3,
										 List<IntervalSet> sets)
	{
		ATNState target = atn.states.get(trg);
		switch (type) {
			case Transition.EPSILON : return new EpsilonTransition(target);
			case Transition.RANGE : return new RangeTransition(target, arg1, arg2);
			case Transition.RULE :
				RuleTransition rt = new RuleTransition((RuleStartState)atn.states.get(arg1), arg2, target);
				return rt;
			case Transition.PREDICATE :
				PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
				return pt;
			case Transition.ATOM : return new AtomTransition(target, arg1);
			case Transition.ACTION :
				ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
				return a;
			case Transition.SET : return new SetTransition(target, sets.get(arg1));
			case Transition.NOT_SET : return new NotSetTransition(target, sets.get(arg1));
			case Transition.WILDCARD : return new WildcardTransition(target);
		}
		return null;
	}

	public static ATNState stateFactory(int type, int stateNumber) {
		ATNState s = null;
		switch (type) {
			case ATNState.INVALID_TYPE: return null;
			case ATNState.BASIC : s = new ATNState(); break;
			case ATNState.RULE_START : s = new RuleStartState(); break;
			case ATNState.BLOCK_START : s = new BlockStartState(); break;
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
                System.err.println("invalid state type in ATN deserialization: "+type+" for state "+stateNumber);
                break;
		}
		s.stateNumber = stateNumber;
		return s;
	}

/*
	public static void dump(DFA dfa, Grammar g) {
		DOTGenerator dot = new DOTGenerator(g);
		String output = dot.getDOT(dfa, false);
		System.out.println(output);
	}

	public static void dump(DFA dfa) {
		dump(dfa, null);
	}
	 */
}
