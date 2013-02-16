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
using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Atn
{
    public abstract class ATNSimulator
    {
        public static readonly int SerializedVersion = 5;

        public const char RuleVariantDelimiter = '$';

        public static readonly string RuleLfVariantMarker = "$lf$";

        public static readonly string RuleNolfVariantMarker = "$nolf$";

        /// <summary>Must distinguish between missing edge and edge we know leads nowhere</summary>
        [NotNull]
        public static readonly DFAState Error;

        [NotNull]
        public readonly ATN atn;

        static ATNSimulator()
        {
            Error = new DFAState(new ATNConfigSet(), 0, 0);
            Error.stateNumber = int.MaxValue;
        }

        public ATNSimulator(ATN atn)
        {
            this.atn = atn;
        }

        public abstract void Reset();

        public static ATN Deserialize(char[] data)
        {
            return Deserialize(data, true);
        }

        public static ATN Deserialize(char[] data, bool optimize)
        {
            data = data.Clone();
            // don't adjust the first value since that's the version number
            for (int i = 1; i < data.Length; i++)
            {
                data[i] = (char)(data[i] - 2);
            }
            ATN atn = new ATN();
            IList<IntervalSet> sets = new List<IntervalSet>();
            int p = 0;
            int version = ToInt(data[p++]);
            if (version != SerializedVersion)
            {
                string reason = string.Format("Could not deserialize ATN with version %d (expected %d)."
                    , version, SerializedVersion);
                throw new NotSupportedException(new InvalidClassException(typeof(ATN).FullName, reason
                    ));
            }
            atn.grammarType = ToInt(data[p++]);
            atn.maxTokenType = ToInt(data[p++]);
            //
            // STATES
            //
            IList<Tuple<LoopEndState, int>> loopBackStateNumbers = new List<Tuple<LoopEndState
                , int>>();
            IList<Tuple<BlockStartState, int>> endStateNumbers = new List<Tuple<BlockStartState
                , int>>();
            int nstates = ToInt(data[p++]);
            for (int i_1 = 1; i_1 <= nstates; i_1++)
            {
                int stype = ToInt(data[p++]);
                // ignore bad type of states
                if (stype == ATNState.InvalidType)
                {
                    atn.AddState(null);
                    continue;
                }
                int ruleIndex = ToInt(data[p++]);
                ATNState s = StateFactory(stype, ruleIndex);
                if (stype == ATNState.LoopEnd)
                {
                    // special case
                    int loopBackStateNumber = ToInt(data[p++]);
                    loopBackStateNumbers.AddItem(Tuple.Create((LoopEndState)s, loopBackStateNumber));
                }
                else
                {
                    if (s is BlockStartState)
                    {
                        int endStateNumber = ToInt(data[p++]);
                        endStateNumbers.AddItem(Tuple.Create((BlockStartState)s, endStateNumber));
                    }
                }
                atn.AddState(s);
            }
            // delay the assignment of loop back and end states until we know all the state instances have been initialized
            foreach (Tuple<LoopEndState, int> pair in loopBackStateNumbers)
            {
                pair.GetItem1().loopBackState = atn.states[pair.GetItem2()];
            }
            foreach (Tuple<BlockStartState, int> pair_1 in endStateNumbers)
            {
                pair_1.GetItem1().endState = (BlockEndState)atn.states[pair_1.GetItem2()];
            }
            int numNonGreedyStates = ToInt(data[p++]);
            for (int i_2 = 0; i_2 < numNonGreedyStates; i_2++)
            {
                int stateNumber = ToInt(data[p++]);
                ((DecisionState)atn.states[stateNumber]).nonGreedy = true;
            }
            int numSllDecisions = ToInt(data[p++]);
            for (int i_3 = 0; i_3 < numSllDecisions; i_3++)
            {
                int stateNumber = ToInt(data[p++]);
                ((DecisionState)atn.states[stateNumber]).sll = true;
            }
            int numPrecedenceStates = ToInt(data[p++]);
            for (int i_4 = 0; i_4 < numPrecedenceStates; i_4++)
            {
                int stateNumber = ToInt(data[p++]);
                ((RuleStartState)atn.states[stateNumber]).isPrecedenceRule = true;
            }
            //
            // RULES
            //
            int nrules = ToInt(data[p++]);
            if (atn.grammarType == ATN.Lexer)
            {
                atn.ruleToTokenType = new int[nrules];
                atn.ruleToActionIndex = new int[nrules];
            }
            atn.ruleToStartState = new RuleStartState[nrules];
            for (int i_5 = 0; i_5 < nrules; i_5++)
            {
                int s = ToInt(data[p++]);
                RuleStartState startState = (RuleStartState)atn.states[s];
                startState.leftFactored = ToInt(data[p++]) != 0;
                atn.ruleToStartState[i_5] = startState;
                if (atn.grammarType == ATN.Lexer)
                {
                    int tokenType = ToInt(data[p++]);
                    atn.ruleToTokenType[i_5] = tokenType;
                    int actionIndex = ToInt(data[p++]);
                    atn.ruleToActionIndex[i_5] = actionIndex;
                }
            }
            atn.ruleToStopState = new RuleStopState[nrules];
            foreach (ATNState state in atn.states)
            {
                if (!(state is RuleStopState))
                {
                    continue;
                }
                RuleStopState stopState = (RuleStopState)state;
                atn.ruleToStopState[state.ruleIndex] = stopState;
                atn.ruleToStartState[state.ruleIndex].stopState = stopState;
            }
            //
            // MODES
            //
            int nmodes = ToInt(data[p++]);
            for (int i_6 = 0; i_6 < nmodes; i_6++)
            {
                int s = ToInt(data[p++]);
                atn.modeToStartState.AddItem((TokensStartState)atn.states[s]);
            }
            atn.modeToDFA = new DFA[nmodes];
            for (int i_7 = 0; i_7 < nmodes; i_7++)
            {
                atn.modeToDFA[i_7] = new DFA(atn.modeToStartState[i_7]);
            }
            //
            // SETS
            //
            int nsets = ToInt(data[p++]);
            for (int i_8 = 1; i_8 <= nsets; i_8++)
            {
                int nintervals = ToInt(data[p]);
                p++;
                IntervalSet set = new IntervalSet();
                sets.AddItem(set);
                for (int j = 1; j <= nintervals; j++)
                {
                    set.Add(ToInt(data[p]), ToInt(data[p + 1]));
                    p += 2;
                }
            }
            //
            // EDGES
            //
            int nedges = ToInt(data[p++]);
            for (int i_9 = 1; i_9 <= nedges; i_9++)
            {
                int src = ToInt(data[p]);
                int trg = ToInt(data[p + 1]);
                int ttype = ToInt(data[p + 2]);
                int arg1 = ToInt(data[p + 3]);
                int arg2 = ToInt(data[p + 4]);
                int arg3 = ToInt(data[p + 5]);
                Transition trans = EdgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
                //			System.out.println("EDGE "+trans.getClass().getSimpleName()+" "+
                //							   src+"->"+trg+
                //					   " "+Transition.serializationNames[ttype]+
                //					   " "+arg1+","+arg2+","+arg3);
                ATNState srcState = atn.states[src];
                srcState.AddTransition(trans);
                p += 6;
            }
            // edges for rule stop states can be derived, so they aren't serialized
            foreach (ATNState state_1 in atn.states)
            {
                bool returningToLeftFactored = state_1.ruleIndex >= 0 && atn.ruleToStartState[state_1
                    .ruleIndex].leftFactored;
                for (int i_10 = 0; i_10 < state_1.GetNumberOfTransitions(); i_10++)
                {
                    Transition t = state_1.Transition(i_10);
                    if (!(t is RuleTransition))
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)t;
                    bool returningFromLeftFactored = atn.ruleToStartState[ruleTransition.target.ruleIndex
                        ].leftFactored;
                    if (!returningFromLeftFactored && returningToLeftFactored)
                    {
                        continue;
                    }
                    atn.ruleToStopState[ruleTransition.target.ruleIndex].AddTransition(new EpsilonTransition
                        (ruleTransition.followState));
                }
            }
            foreach (ATNState state_2 in atn.states)
            {
                if (state_2 is BlockStartState)
                {
                    // we need to know the end state to set its start state
                    if (((BlockStartState)state_2).endState == null)
                    {
                        throw new InvalidOperationException();
                    }
                    // block end states can only be associated to a single block start state
                    if (((BlockStartState)state_2).endState.startState != null)
                    {
                        throw new InvalidOperationException();
                    }
                    ((BlockStartState)state_2).endState.startState = (BlockStartState)state_2;
                }
                if (state_2 is PlusLoopbackState)
                {
                    PlusLoopbackState loopbackState = (PlusLoopbackState)state_2;
                    for (int i_10 = 0; i_10 < loopbackState.GetNumberOfTransitions(); i_10++)
                    {
                        ATNState target = loopbackState.Transition(i_10).target;
                        if (target is PlusBlockStartState)
                        {
                            ((PlusBlockStartState)target).loopBackState = loopbackState;
                        }
                    }
                }
                else
                {
                    if (state_2 is StarLoopbackState)
                    {
                        StarLoopbackState loopbackState = (StarLoopbackState)state_2;
                        for (int i_10 = 0; i_10 < loopbackState.GetNumberOfTransitions(); i_10++)
                        {
                            ATNState target = loopbackState.Transition(i_10).target;
                            if (target is StarLoopEntryState)
                            {
                                ((StarLoopEntryState)target).loopBackState = loopbackState;
                            }
                        }
                    }
                }
            }
            //
            // DECISIONS
            //
            int ndecisions = ToInt(data[p++]);
            for (int i_11 = 1; i_11 <= ndecisions; i_11++)
            {
                int s = ToInt(data[p++]);
                DecisionState decState = (DecisionState)atn.states[s];
                atn.decisionToState.AddItem(decState);
                decState.decision = i_11 - 1;
            }
            atn.decisionToDFA = new DFA[ndecisions];
            for (int i_12 = 0; i_12 < ndecisions; i_12++)
            {
                atn.decisionToDFA[i_12] = new DFA(atn.decisionToState[i_12], i_12);
            }
            if (optimize)
            {
                while (true)
                {
                    int optimizationCount = 0;
                    optimizationCount += InlineSetRules(atn);
                    optimizationCount += CombineChainedEpsilons(atn);
                    bool preserveOrder = atn.grammarType == ATN.Lexer;
                    optimizationCount += OptimizeSets(atn, preserveOrder);
                    if (optimizationCount == 0)
                    {
                        break;
                    }
                }
            }
            IdentifyTailCalls(atn);
            VerifyATN(atn);
            return atn;
        }

        private static void VerifyATN(ATN atn)
        {
            // verify assumptions
            foreach (ATNState state in atn.states)
            {
                if (state == null)
                {
                    continue;
                }
                CheckCondition(state.OnlyHasEpsilonTransitions() || state.GetNumberOfTransitions(
                    ) <= 1);
                if (state is PlusBlockStartState)
                {
                    CheckCondition(((PlusBlockStartState)state).loopBackState != null);
                }
                if (state is StarLoopEntryState)
                {
                    StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
                    CheckCondition(starLoopEntryState.loopBackState != null);
                    CheckCondition(starLoopEntryState.GetNumberOfTransitions() == 2);
                    if (starLoopEntryState.Transition(0).target is StarBlockStartState)
                    {
                        CheckCondition(starLoopEntryState.Transition(1).target is LoopEndState);
                        CheckCondition(!starLoopEntryState.nonGreedy);
                    }
                    else
                    {
                        if (starLoopEntryState.Transition(0).target is LoopEndState)
                        {
                            CheckCondition(starLoopEntryState.Transition(1).target is StarBlockStartState);
                            CheckCondition(starLoopEntryState.nonGreedy);
                        }
                        else
                        {
                            throw new InvalidOperationException();
                        }
                    }
                }
                if (state is StarLoopbackState)
                {
                    CheckCondition(state.GetNumberOfTransitions() == 1);
                    CheckCondition(state.Transition(0).target is StarLoopEntryState);
                }
                if (state is LoopEndState)
                {
                    CheckCondition(((LoopEndState)state).loopBackState != null);
                }
                if (state is RuleStartState)
                {
                    CheckCondition(((RuleStartState)state).stopState != null);
                }
                if (state is BlockStartState)
                {
                    CheckCondition(((BlockStartState)state).endState != null);
                }
                if (state is BlockEndState)
                {
                    CheckCondition(((BlockEndState)state).startState != null);
                }
                if (state is DecisionState)
                {
                    DecisionState decisionState = (DecisionState)state;
                    CheckCondition(decisionState.GetNumberOfTransitions() <= 1 || decisionState.decision
                         >= 0);
                }
                else
                {
                    CheckCondition(state.GetNumberOfTransitions() <= 1 || state is RuleStopState);
                }
            }
        }

        public static void CheckCondition(bool condition)
        {
            CheckCondition(condition, null);
        }

        public static void CheckCondition(bool condition, string message)
        {
            if (!condition)
            {
                throw new InvalidOperationException(message);
            }
        }

        private static int InlineSetRules(ATN atn)
        {
            int inlinedCalls = 0;
            Transition[] ruleToInlineTransition = new Transition[atn.ruleToStartState.Length]
                ;
            for (int i = 0; i < atn.ruleToStartState.Length; i++)
            {
                RuleStartState startState = atn.ruleToStartState[i];
                ATNState middleState = startState;
                while (middleState.OnlyHasEpsilonTransitions() && middleState.GetNumberOfOptimizedTransitions
                    () == 1 && middleState.GetOptimizedTransition(0).SerializationType == Transition
                    .Epsilon)
                {
                    middleState = middleState.GetOptimizedTransition(0).target;
                }
                if (middleState.GetNumberOfOptimizedTransitions() != 1)
                {
                    continue;
                }
                Transition matchTransition = middleState.GetOptimizedTransition(0);
                ATNState matchTarget = matchTransition.target;
                if (matchTransition.IsEpsilon || !matchTarget.OnlyHasEpsilonTransitions() || matchTarget
                    .GetNumberOfOptimizedTransitions() != 1 || !(matchTarget.GetOptimizedTransition
                    (0).target is RuleStopState))
                {
                    continue;
                }
                switch (matchTransition.SerializationType)
                {
                    case Transition.Atom:
                    case Transition.Range:
                    case Transition.Set:
                    {
                        ruleToInlineTransition[i] = matchTransition;
                        break;
                    }

                    case Transition.NotSet:
                    case Transition.Wildcard:
                    {
                        // not implemented yet
                        continue;
                        goto default;
                    }

                    default:
                    {
                        continue;
                        break;
                    }
                }
            }
            for (int stateNumber = 0; stateNumber < atn.states.Count; stateNumber++)
            {
                ATNState state = atn.states[stateNumber];
                if (state.ruleIndex < 0)
                {
                    continue;
                }
                IList<Transition> optimizedTransitions = null;
                for (int i_1 = 0; i_1 < state.GetNumberOfOptimizedTransitions(); i_1++)
                {
                    Transition transition = state.GetOptimizedTransition(i_1);
                    if (!(transition is RuleTransition))
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.AddItem(transition);
                        }
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    Transition effective = ruleToInlineTransition[ruleTransition.target.ruleIndex];
                    if (effective == null)
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.AddItem(transition);
                        }
                        continue;
                    }
                    if (optimizedTransitions == null)
                    {
                        optimizedTransitions = new List<Transition>();
                        for (int j = 0; j < i_1; j++)
                        {
                            optimizedTransitions.AddItem(state.GetOptimizedTransition(i_1));
                        }
                    }
                    inlinedCalls++;
                    ATNState target = ruleTransition.followState;
                    ATNState intermediateState = new BasicState();
                    intermediateState.SetRuleIndex(target.ruleIndex);
                    atn.AddState(intermediateState);
                    optimizedTransitions.AddItem(new EpsilonTransition(intermediateState));
                    switch (effective.SerializationType)
                    {
                        case Transition.Atom:
                        {
                            intermediateState.AddTransition(new AtomTransition(target, ((AtomTransition)effective
                                ).label));
                            break;
                        }

                        case Transition.Range:
                        {
                            intermediateState.AddTransition(new RangeTransition(target, ((RangeTransition)effective
                                ).from, ((RangeTransition)effective).to));
                            break;
                        }

                        case Transition.Set:
                        {
                            intermediateState.AddTransition(new SetTransition(target, effective.Label));
                            break;
                        }

                        default:
                        {
                            throw new NotSupportedException();
                        }
                    }
                }
                if (optimizedTransitions != null)
                {
                    if (state.IsOptimized())
                    {
                        while (state.GetNumberOfOptimizedTransitions() > 0)
                        {
                            state.RemoveOptimizedTransition(state.GetNumberOfOptimizedTransitions() - 1);
                        }
                    }
                    foreach (Transition transition in optimizedTransitions)
                    {
                        state.AddOptimizedTransition(transition);
                    }
                }
            }
            return inlinedCalls;
        }

        private static int CombineChainedEpsilons(ATN atn)
        {
            int removedEdges = 0;
            foreach (ATNState state in atn.states)
            {
                if (!state.OnlyHasEpsilonTransitions() || state is RuleStopState)
                {
                    continue;
                }
                IList<Transition> optimizedTransitions = null;
                for (int i = 0; i < state.GetNumberOfOptimizedTransitions(); i++)
                {
                    Transition transition = state.GetOptimizedTransition(i);
                    ATNState intermediate = transition.target;
                    if (transition.SerializationType != Transition.Epsilon || intermediate.GetStateType
                        () != ATNState.Basic || !intermediate.OnlyHasEpsilonTransitions())
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.AddItem(transition);
                        }
                        goto nextTransition_continue;
                    }
                    for (int j = 0; j < intermediate.GetNumberOfOptimizedTransitions(); j++)
                    {
                        if (intermediate.GetOptimizedTransition(j).SerializationType != Transition.Epsilon)
                        {
                            if (optimizedTransitions != null)
                            {
                                optimizedTransitions.AddItem(transition);
                            }
                            goto nextTransition_continue;
                        }
                    }
                    removedEdges++;
                    if (optimizedTransitions == null)
                    {
                        optimizedTransitions = new List<Transition>();
                        for (int j_1 = 0; j_1 < i; j_1++)
                        {
                            optimizedTransitions.AddItem(state.GetOptimizedTransition(j_1));
                        }
                    }
                    for (int j_2 = 0; j_2 < intermediate.GetNumberOfOptimizedTransitions(); j_2++)
                    {
                        ATNState target = intermediate.GetOptimizedTransition(j_2).target;
                        optimizedTransitions.AddItem(new EpsilonTransition(target));
                    }
nextTransition_continue: ;
                }
nextTransition_break: ;
                if (optimizedTransitions != null)
                {
                    if (state.IsOptimized())
                    {
                        while (state.GetNumberOfOptimizedTransitions() > 0)
                        {
                            state.RemoveOptimizedTransition(state.GetNumberOfOptimizedTransitions() - 1);
                        }
                    }
                    foreach (Transition transition in optimizedTransitions)
                    {
                        state.AddOptimizedTransition(transition);
                    }
                }
            }
nextState_break: ;
            return removedEdges;
        }

        private static int OptimizeSets(ATN atn, bool preserveOrder)
        {
            if (preserveOrder)
            {
                // this optimization currently doesn't preserve edge order.
                return 0;
            }
            int removedPaths = 0;
            IList<DecisionState> decisions = atn.decisionToState;
            foreach (DecisionState decision in decisions)
            {
                IntervalSet setTransitions = new IntervalSet();
                for (int i = 0; i < decision.GetNumberOfOptimizedTransitions(); i++)
                {
                    Transition epsTransition = decision.GetOptimizedTransition(i);
                    if (!(epsTransition is EpsilonTransition))
                    {
                        continue;
                    }
                    if (epsTransition.target.GetNumberOfOptimizedTransitions() != 1)
                    {
                        continue;
                    }
                    Transition transition = epsTransition.target.GetOptimizedTransition(0);
                    if (!(transition.target is BlockEndState))
                    {
                        continue;
                    }
                    if (transition is NotSetTransition)
                    {
                        // TODO: not yet implemented
                        continue;
                    }
                    if (transition is AtomTransition || transition is RangeTransition || transition is
                         SetTransition)
                    {
                        setTransitions.Add(i);
                    }
                }
                if (setTransitions.Size() <= 1)
                {
                    continue;
                }
                IList<Transition> optimizedTransitions = new List<Transition>();
                for (int i_1 = 0; i_1 < decision.GetNumberOfOptimizedTransitions(); i_1++)
                {
                    if (!setTransitions.Contains(i_1))
                    {
                        optimizedTransitions.AddItem(decision.GetOptimizedTransition(i_1));
                    }
                }
                ATNState blockEndState = decision.GetOptimizedTransition(setTransitions.GetMinElement
                    ()).target.GetOptimizedTransition(0).target;
                IntervalSet matchSet = new IntervalSet();
                for (int i_2 = 0; i_2 < setTransitions.GetIntervals().Count; i_2++)
                {
                    Interval interval = setTransitions.GetIntervals()[i_2];
                    for (int j = interval.a; j <= interval.b; j++)
                    {
                        Transition matchTransition = decision.GetOptimizedTransition(j).target.GetOptimizedTransition
                            (0);
                        if (matchTransition is NotSetTransition)
                        {
                            throw new NotSupportedException("Not yet implemented.");
                        }
                        else
                        {
                            matchSet.AddAll(matchTransition.Label);
                        }
                    }
                }
                Transition newTransition;
                if (matchSet.GetIntervals().Count == 1)
                {
                    if (matchSet.Size() == 1)
                    {
                        newTransition = new AtomTransition(blockEndState, matchSet.GetMinElement());
                    }
                    else
                    {
                        Interval matchInterval = matchSet.GetIntervals()[0];
                        newTransition = new RangeTransition(blockEndState, matchInterval.a, matchInterval
                            .b);
                    }
                }
                else
                {
                    newTransition = new SetTransition(blockEndState, matchSet);
                }
                ATNState setOptimizedState = new BasicState();
                setOptimizedState.SetRuleIndex(decision.ruleIndex);
                atn.AddState(setOptimizedState);
                setOptimizedState.AddTransition(newTransition);
                optimizedTransitions.AddItem(new EpsilonTransition(setOptimizedState));
                removedPaths += decision.GetNumberOfOptimizedTransitions() - optimizedTransitions
                    .Count;
                if (decision.IsOptimized())
                {
                    while (decision.GetNumberOfOptimizedTransitions() > 0)
                    {
                        decision.RemoveOptimizedTransition(decision.GetNumberOfOptimizedTransitions() - 1
                            );
                    }
                }
                foreach (Transition transition_1 in optimizedTransitions)
                {
                    decision.AddOptimizedTransition(transition_1);
                }
            }
            return removedPaths;
        }

        private static void IdentifyTailCalls(ATN atn)
        {
            foreach (ATNState state in atn.states)
            {
                foreach (Transition transition in state.transitions)
                {
                    if (!(transition is RuleTransition))
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    ruleTransition.tailCall = TestTailCall(atn, ruleTransition, false);
                    ruleTransition.optimizedTailCall = TestTailCall(atn, ruleTransition, true);
                }
                if (!state.IsOptimized())
                {
                    continue;
                }
                foreach (Transition transition_1 in state.optimizedTransitions)
                {
                    if (!(transition_1 is RuleTransition))
                    {
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition_1;
                    ruleTransition.tailCall = TestTailCall(atn, ruleTransition, false);
                    ruleTransition.optimizedTailCall = TestTailCall(atn, ruleTransition, true);
                }
            }
        }

        private static bool TestTailCall(ATN atn, RuleTransition transition, bool optimizedPath
            )
        {
            if (!optimizedPath && transition.tailCall)
            {
                return true;
            }
            if (optimizedPath && transition.optimizedTailCall)
            {
                return true;
            }
            BitArray reachable = new BitArray(atn.states.Count);
            IDeque<ATNState> worklist = new ArrayDeque<ATNState>();
            worklist.AddItem(transition.followState);
            while (!worklist.IsEmpty())
            {
                ATNState state = worklist.Pop();
                if (reachable.Get(state.stateNumber))
                {
                    continue;
                }
                if (state is RuleStopState)
                {
                    continue;
                }
                if (!state.OnlyHasEpsilonTransitions())
                {
                    return false;
                }
                IList<Transition> transitions = optimizedPath ? state.optimizedTransitions : state
                    .transitions;
                foreach (Transition t in transitions)
                {
                    if (t.SerializationType != Transition.Epsilon)
                    {
                        return false;
                    }
                    worklist.AddItem(t.target);
                }
            }
            return true;
        }

        public static int ToInt(char c)
        {
            return c == 65535 ? -1 : c;
        }

        [NotNull]
        public static Transition EdgeFactory(ATN atn, int type, int src, int trg, int arg1
            , int arg2, int arg3, IList<IntervalSet> sets)
        {
            ATNState target = atn.states[trg];
            switch (type)
            {
                case Transition.Epsilon:
                {
                    return new EpsilonTransition(target);
                }

                case Transition.Range:
                {
                    return new RangeTransition(target, arg1, arg2);
                }

                case Transition.Rule:
                {
                    RuleTransition rt = new RuleTransition((RuleStartState)atn.states[arg1], arg2, arg3
                        , target);
                    return rt;
                }

                case Transition.Predicate:
                {
                    PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                    return pt;
                }

                case Transition.Precedence:
                {
                    return new PrecedencePredicateTransition(target, arg1);
                }

                case Transition.Atom:
                {
                    return new AtomTransition(target, arg1);
                }

                case Transition.Action:
                {
                    ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                    return a;
                }

                case Transition.Set:
                {
                    return new SetTransition(target, sets[arg1]);
                }

                case Transition.NotSet:
                {
                    return new NotSetTransition(target, sets[arg1]);
                }

                case Transition.Wildcard:
                {
                    return new WildcardTransition(target);
                }
            }
            throw new ArgumentException("The specified transition type is not valid.");
        }

        public static ATNState StateFactory(int type, int ruleIndex)
        {
            ATNState s;
            switch (type)
            {
                case ATNState.InvalidType:
                {
                    return null;
                }

                case ATNState.Basic:
                {
                    s = new BasicState();
                    break;
                }

                case ATNState.RuleStart:
                {
                    s = new RuleStartState();
                    break;
                }

                case ATNState.BlockStart:
                {
                    s = new BasicBlockStartState();
                    break;
                }

                case ATNState.PlusBlockStart:
                {
                    s = new PlusBlockStartState();
                    break;
                }

                case ATNState.StarBlockStart:
                {
                    s = new StarBlockStartState();
                    break;
                }

                case ATNState.TokenStart:
                {
                    s = new TokensStartState();
                    break;
                }

                case ATNState.RuleStop:
                {
                    s = new RuleStopState();
                    break;
                }

                case ATNState.BlockEnd:
                {
                    s = new BlockEndState();
                    break;
                }

                case ATNState.StarLoopBack:
                {
                    s = new StarLoopbackState();
                    break;
                }

                case ATNState.StarLoopEntry:
                {
                    s = new StarLoopEntryState();
                    break;
                }

                case ATNState.PlusLoopBack:
                {
                    s = new PlusLoopbackState();
                    break;
                }

                case ATNState.LoopEnd:
                {
                    s = new LoopEndState();
                    break;
                }

                default:
                {
                    string message = string.Format("The specified state type %d is not valid.", type);
                    throw new ArgumentException(message);
                }
            }
            s.ruleIndex = ruleIndex;
            return s;
        }
    }
}
