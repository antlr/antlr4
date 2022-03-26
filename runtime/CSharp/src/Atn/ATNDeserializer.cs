/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Globalization;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class ATNDeserializer
    {
        public static readonly int SerializedVersion = 4;

        [NotNull]
        private readonly ATNDeserializationOptions deserializationOptions;

        public ATNDeserializer()
            : this(ATNDeserializationOptions.Default)
        {
        }

        public ATNDeserializer(ATNDeserializationOptions deserializationOptions)
        {
            if (deserializationOptions == null)
            {
                deserializationOptions = ATNDeserializationOptions.Default;
            }
            this.deserializationOptions = deserializationOptions;
        }

		int[] data;
		int p;

        public virtual ATN Deserialize(int[] data)
        {
	        this.data = data;
			CheckVersion ();
			ATN atn = ReadATN ();
			ReadStates (atn);
			ReadRules (atn);
			ReadModes (atn);
			IList<IntervalSet> sets = new List<IntervalSet>();
			ReadSets (atn, sets);
	        ReadEdges (atn, sets);
			ReadDecisions (atn);
			ReadLexerActions (atn);
            MarkPrecedenceDecisions(atn);
			if (deserializationOptions.VerifyAtn) {
				VerifyATN (atn);
			}
			if (deserializationOptions.GenerateRuleBypassTransitions && atn.grammarType == ATNType.Parser) {
				GenerateRuleBypassTransitions (atn);
			}
            if (deserializationOptions.Optimize)
            {
				OptimizeATN (atn);
            }
            IdentifyTailCalls(atn);
            return atn;
        }

		protected internal virtual void OptimizeATN(ATN atn)
		{
			while (true)
			{
				int optimizationCount = 0;
				optimizationCount += InlineSetRules(atn);
				optimizationCount += CombineChainedEpsilons(atn);
				bool preserveOrder = atn.grammarType == ATNType.Lexer;
				optimizationCount += OptimizeSets(atn, preserveOrder);
				if (optimizationCount == 0)
				{
					break;
				}
			}
			if (deserializationOptions.VerifyAtn)
			{
				// reverify after modification
				VerifyATN(atn);
			}
		}

		protected internal virtual void GenerateRuleBypassTransitions(ATN atn)
		{
			atn.ruleToTokenType = new int[atn.ruleToStartState.Length];
			for (int i_10 = 0; i_10 < atn.ruleToStartState.Length; i_10++)
			{
				atn.ruleToTokenType[i_10] = atn.maxTokenType + i_10 + 1;
			}
			for (int i_13 = 0; i_13 < atn.ruleToStartState.Length; i_13++)
			{
				BasicBlockStartState bypassStart = new BasicBlockStartState();
				bypassStart.ruleIndex = i_13;
				atn.AddState(bypassStart);
				BlockEndState bypassStop = new BlockEndState();
				bypassStop.ruleIndex = i_13;
				atn.AddState(bypassStop);
				bypassStart.endState = bypassStop;
				atn.DefineDecisionState(bypassStart);
				bypassStop.startState = bypassStart;
				ATNState endState;
				Transition excludeTransition = null;
				if (atn.ruleToStartState[i_13].isPrecedenceRule)
				{
					// wrap from the beginning of the rule to the StarLoopEntryState
					endState = null;
					foreach (ATNState state_3 in atn.states)
					{
						if (state_3.ruleIndex != i_13)
						{
							continue;
						}
						if (!(state_3 is StarLoopEntryState))
						{
							continue;
						}
						ATNState maybeLoopEndState = state_3.Transition(state_3.NumberOfTransitions - 1).target;
						if (!(maybeLoopEndState is LoopEndState))
						{
							continue;
						}
						if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.Transition(0).target is RuleStopState)
						{
							endState = state_3;
							break;
						}
					}
					if (endState == null)
					{
						throw new NotSupportedException("Couldn't identify final state of the precedence rule prefix section.");
					}
					excludeTransition = ((StarLoopEntryState)endState).loopBackState.Transition(0);
				}
				else
				{
					endState = atn.ruleToStopState[i_13];
				}
				// all non-excluded transitions that currently target end state need to target blockEnd instead
				foreach (ATNState state_4 in atn.states)
				{
					foreach (Transition transition in state_4.transitions)
					{
						if (transition == excludeTransition)
						{
							continue;
						}
						if (transition.target == endState)
						{
							transition.target = bypassStop;
						}
					}
				}
				// all transitions leaving the rule start state need to leave blockStart instead
				while (atn.ruleToStartState[i_13].NumberOfTransitions > 0)
				{
					Transition transition = atn.ruleToStartState[i_13].Transition(atn.ruleToStartState[i_13].NumberOfTransitions - 1);
					atn.ruleToStartState[i_13].RemoveTransition(atn.ruleToStartState[i_13].NumberOfTransitions - 1);
					bypassStart.AddTransition(transition);
				}
				// link the new states
				atn.ruleToStartState[i_13].AddTransition(new EpsilonTransition(bypassStart));
				bypassStop.AddTransition(new EpsilonTransition(endState));
				ATNState matchState = new BasicState();
				atn.AddState(matchState);
				matchState.AddTransition(new AtomTransition(bypassStop, atn.ruleToTokenType[i_13]));
				bypassStart.AddTransition(new EpsilonTransition(matchState));
			}
			if (deserializationOptions.VerifyAtn)
			{
				// reverify after modification
				VerifyATN(atn);
			}
		}

		protected internal virtual void ReadLexerActions(ATN atn)
		{
			//
			// LEXER ACTIONS
			//
			if (atn.grammarType == ATNType.Lexer)
			{
				atn.lexerActions = new ILexerAction[ReadInt()];
				for (int i_10 = 0; i_10 < atn.lexerActions.Length; i_10++)
				{
					LexerActionType actionType = (LexerActionType)ReadInt();
					int data1 = ReadInt();
					int data2 = ReadInt();
					ILexerAction lexerAction = LexerActionFactory(actionType, data1, data2);
					atn.lexerActions[i_10] = lexerAction;
				}
			}
		}

		protected internal virtual void ReadDecisions(ATN atn)
		{
			//
			// DECISIONS
			//
			int ndecisions = ReadInt();
			for (int i_11 = 0; i_11 < ndecisions; i_11++)
			{
				int s = ReadInt();
				DecisionState decState = (DecisionState)atn.states[s];
				atn.decisionToState.Add(decState);
				decState.decision = i_11;
			}
			atn.decisionToDFA = new DFA[ndecisions];
			for (int i_12 = 0; i_12 < ndecisions; i_12++)
			{
				atn.decisionToDFA[i_12] = new DFA(atn.decisionToState[i_12], i_12);
			}
		}

		protected internal virtual void ReadEdges(ATN atn, IList<IntervalSet> sets)
		{
			//
			// EDGES
			//
			int nedges = ReadInt();
			for (int i_9 = 0; i_9 < nedges; i_9++)
			{
				int src = ReadInt();
				int trg = ReadInt();
				TransitionType ttype = (TransitionType)ReadInt();
				int arg1 = ReadInt();
				int arg2 = ReadInt();
				int arg3 = ReadInt();
				Transition trans = EdgeFactory(atn, ttype, src, trg, arg1, arg2, arg3, sets);
				ATNState srcState = atn.states[src];
				srcState.AddTransition(trans);
			}
			// edges for rule stop states can be derived, so they aren't serialized
			foreach (ATNState state_1 in atn.states)
			{
				for (int i_10 = 0; i_10 < state_1.NumberOfTransitions; i_10++)
				{
					Transition t = state_1.Transition(i_10);
					if (!(t is RuleTransition))
					{
						continue;
					}
					RuleTransition ruleTransition = (RuleTransition)t;
					int outermostPrecedenceReturn = -1;
					if (atn.ruleToStartState[ruleTransition.target.ruleIndex].isPrecedenceRule)
					{
						if (ruleTransition.precedence == 0)
						{
							outermostPrecedenceReturn = ruleTransition.target.ruleIndex;
						}
					}
					EpsilonTransition returnTransition = new EpsilonTransition(ruleTransition.followState, outermostPrecedenceReturn);
					atn.ruleToStopState[ruleTransition.target.ruleIndex].AddTransition(returnTransition);
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
				else if (state_2 is PlusLoopbackState)
				{
					PlusLoopbackState loopbackState = (PlusLoopbackState)state_2;
					for (int i_10 = 0; i_10 < loopbackState.NumberOfTransitions; i_10++)
					{
						ATNState target = loopbackState.Transition(i_10).target;
						if (target is PlusBlockStartState)
						{
							((PlusBlockStartState)target).loopBackState = loopbackState;
						}
					}
				}
				else if (state_2 is StarLoopbackState)
				{
					StarLoopbackState loopbackState = (StarLoopbackState)state_2;
					for (int i_10 = 0; i_10 < loopbackState.NumberOfTransitions; i_10++)
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

		protected internal virtual void ReadSets(ATN atn, IList<IntervalSet> sets)
		{
			//
			// SETS
			//
			int nsets = ReadInt();
			for (int i_8 = 0; i_8 < nsets; i_8++)
			{
				IntervalSet set = new IntervalSet();
				sets.Add(set);
				int nintervals = ReadInt();
				bool containsEof = ReadInt() != 0;
				if (containsEof)
				{
					set.Add(-1);
				}
				for (int j = 0; j < nintervals; j++)
				{
					set.Add(ReadInt(), ReadInt());
				}
			}
		}

		protected internal virtual void ReadModes(ATN atn)
		{
			//
			// MODES
			//
			int nmodes = ReadInt();
			for (int i_6 = 0; i_6 < nmodes; i_6++)
			{
				int _i = ReadInt();
				atn.modeToStartState.Add((TokensStartState)atn.states[_i]);
			}
			// not in Java code
			atn.modeToDFA = new DFA[nmodes];
			for (int i_7 = 0; i_7 < nmodes; i_7++)
			{
				atn.modeToDFA[i_7] = new DFA(atn.modeToStartState[i_7]);
			}
		}

		protected internal virtual void ReadRules(ATN atn)
		{
			//
			// RULES
			//
			int nrules = ReadInt();
			if (atn.grammarType == ATNType.Lexer)
			{
				atn.ruleToTokenType = new int[nrules];
			}
			atn.ruleToStartState = new RuleStartState[nrules];
			for (int i_5 = 0; i_5 < nrules; i_5++)
			{
				int s = ReadInt();
				RuleStartState startState = (RuleStartState)atn.states[s];
				atn.ruleToStartState[i_5] = startState;
				if (atn.grammarType == ATNType.Lexer) {
					int tokenType = ReadInt ();
					atn.ruleToTokenType [i_5] = tokenType;
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
		}

		protected internal virtual void ReadStates(ATN atn)
		{
			//
			// STATES
			//
			IList<Tuple<LoopEndState, int>> loopBackStateNumbers = new List<Tuple<LoopEndState, int>>();
			IList<Tuple<BlockStartState, int>> endStateNumbers = new List<Tuple<BlockStartState, int>>();
			int nstates = ReadInt();
			for (int i_1 = 0; i_1 < nstates; i_1++)
			{
				StateType stype = (StateType)ReadInt();
				// ignore bad type of states
				if (stype == StateType.InvalidType)
				{
					atn.AddState(null);
					continue;
				}
				int ruleIndex = ReadInt();
				if (ruleIndex == char.MaxValue)
				{
					ruleIndex = -1;
				}
				ATNState s = StateFactory(stype, ruleIndex);
				if (stype == StateType.LoopEnd)
				{
					// special case
					int loopBackStateNumber = ReadInt();
					loopBackStateNumbers.Add(Tuple.Create((LoopEndState)s, loopBackStateNumber));
				}
				else
				{
					if (s is BlockStartState)
					{
						int endStateNumber = ReadInt();
						endStateNumbers.Add(Tuple.Create((BlockStartState)s, endStateNumber));
					}
				}
				atn.AddState(s);
			}
			// delay the assignment of loop back and end states until we know all the state instances have been initialized
			foreach (Tuple<LoopEndState, int> pair in loopBackStateNumbers)
			{
				pair.Item1.loopBackState = atn.states[pair.Item2];
			}
			foreach (Tuple<BlockStartState, int> pair_1 in endStateNumbers)
			{
				pair_1.Item1.endState = (BlockEndState)atn.states[pair_1.Item2];
			}
			int numNonGreedyStates = ReadInt();
			for (int i_2 = 0; i_2 < numNonGreedyStates; i_2++)
			{
				int stateNumber = ReadInt();
				((DecisionState)atn.states[stateNumber]).nonGreedy = true;
			}
			int numPrecedenceStates = ReadInt();
			for (int i_4 = 0; i_4 < numPrecedenceStates; i_4++)
			{
				int stateNumber = ReadInt();
				((RuleStartState)atn.states[stateNumber]).isPrecedenceRule = true;
			}
		}

		protected internal virtual ATN ReadATN()
		{
			ATNType grammarType = (ATNType)ReadInt();
			int maxTokenType = ReadInt();
			return new ATN(grammarType, maxTokenType);
		}

		protected internal virtual void CheckVersion()
		{
			int version = ReadInt();
			if (version != SerializedVersion)
			{
				string reason = string.Format(CultureInfo.CurrentCulture, "Could not deserialize ATN with version {0} (expected {1}).", version, SerializedVersion);
				throw new NotSupportedException(reason);
			}
		}

        /// <summary>
        /// Analyze the
        /// <see cref="StarLoopEntryState"/>
        /// states in the specified ATN to set
        /// the
        /// <see cref="StarLoopEntryState.isPrecedenceDecision"/>
        /// field to the
        /// correct value.
        /// </summary>
        /// <param name="atn">The ATN.</param>
        protected internal virtual void MarkPrecedenceDecisions(ATN atn)
        {
            foreach (ATNState state in atn.states)
            {
                if (!(state is StarLoopEntryState))
                {
                    continue;
                }
                if (atn.ruleToStartState[state.ruleIndex].isPrecedenceRule)
                {
                    ATNState maybeLoopEndState = state.Transition(state.NumberOfTransitions - 1).target;
                    if (maybeLoopEndState is LoopEndState)
                    {
                        if (maybeLoopEndState.epsilonOnlyTransitions && maybeLoopEndState.Transition(0).target is RuleStopState)
                        {
							((StarLoopEntryState)state).isPrecedenceDecision = true;
                        }
                    }
                }
            }
        }

        protected internal virtual void VerifyATN(ATN atn)
        {
            // verify assumptions
            foreach (ATNState state in atn.states)
            {
                if (state == null)
                {
                    continue;
                }
                CheckCondition(state.OnlyHasEpsilonTransitions || state.NumberOfTransitions <= 1);
                if (state is PlusBlockStartState)
                {
                    CheckCondition(((PlusBlockStartState)state).loopBackState != null);
                }
                if (state is StarLoopEntryState)
                {
                    StarLoopEntryState starLoopEntryState = (StarLoopEntryState)state;
                    CheckCondition(starLoopEntryState.loopBackState != null);
                    CheckCondition(starLoopEntryState.NumberOfTransitions == 2);
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
                    CheckCondition(state.NumberOfTransitions == 1);
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
                    CheckCondition(decisionState.NumberOfTransitions <= 1 || decisionState.decision >= 0);
                }
                else
                {
                    CheckCondition(state.NumberOfTransitions <= 1 || state is RuleStopState);
                }
            }
        }

        protected internal virtual void CheckCondition(bool condition)
        {
            CheckCondition(condition, null);
        }

        protected internal virtual void CheckCondition(bool condition, string message)
        {
            if (!condition)
            {
                throw new InvalidOperationException(message);
            }
        }

        private static int InlineSetRules(ATN atn)
        {
            int inlinedCalls = 0;
            Transition[] ruleToInlineTransition = new Transition[atn.ruleToStartState.Length];
            for (int i = 0; i < atn.ruleToStartState.Length; i++)
            {
                RuleStartState startState = atn.ruleToStartState[i];
                ATNState middleState = startState;
                while (middleState.OnlyHasEpsilonTransitions && middleState.NumberOfOptimizedTransitions == 1 && middleState.GetOptimizedTransition(0).TransitionType == TransitionType.EPSILON)
                {
                    middleState = middleState.GetOptimizedTransition(0).target;
                }
                if (middleState.NumberOfOptimizedTransitions != 1)
                {
                    continue;
                }
                Transition matchTransition = middleState.GetOptimizedTransition(0);
                ATNState matchTarget = matchTransition.target;
                if (matchTransition.IsEpsilon || !matchTarget.OnlyHasEpsilonTransitions || matchTarget.NumberOfOptimizedTransitions != 1 || !(matchTarget.GetOptimizedTransition(0).target is RuleStopState))
                {
                    continue;
                }
                switch (matchTransition.TransitionType)
                {
                    case TransitionType.ATOM:
                    case TransitionType.RANGE:
                    case TransitionType.SET:
                    {
                        ruleToInlineTransition[i] = matchTransition;
                        break;
                    }

                    case TransitionType.NOT_SET:
                    case TransitionType.WILDCARD:
                    {
                        // not implemented yet
                        continue;
                    }

                    default:
                    {
                        continue;
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
                for (int i_1 = 0; i_1 < state.NumberOfOptimizedTransitions; i_1++)
                {
                    Transition transition = state.GetOptimizedTransition(i_1);
                    if (!(transition is RuleTransition))
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.Add(transition);
                        }
                        continue;
                    }
                    RuleTransition ruleTransition = (RuleTransition)transition;
                    Transition effective = ruleToInlineTransition[ruleTransition.target.ruleIndex];
                    if (effective == null)
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.Add(transition);
                        }
                        continue;
                    }
                    if (optimizedTransitions == null)
                    {
                        optimizedTransitions = new List<Transition>();
                        for (int j = 0; j < i_1; j++)
                        {
                            optimizedTransitions.Add(state.GetOptimizedTransition(i_1));
                        }
                    }
                    inlinedCalls++;
                    ATNState target = ruleTransition.followState;
                    ATNState intermediateState = new BasicState();
                    intermediateState.SetRuleIndex(target.ruleIndex);
                    atn.AddState(intermediateState);
                    optimizedTransitions.Add(new EpsilonTransition(intermediateState));
                    switch (effective.TransitionType)
                    {
                        case TransitionType.ATOM:
                        {
							intermediateState.AddTransition(new AtomTransition(target, ((AtomTransition)effective).token));
                            break;
                        }

                        case TransitionType.RANGE:
                        {
                            intermediateState.AddTransition(new RangeTransition(target, ((RangeTransition)effective).from, ((RangeTransition)effective).to));
                            break;
                        }

                        case TransitionType.SET:
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
                    if (state.IsOptimized)
                    {
                        while (state.NumberOfOptimizedTransitions > 0)
                        {
                            state.RemoveOptimizedTransition(state.NumberOfOptimizedTransitions - 1);
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
                if (!state.OnlyHasEpsilonTransitions || state is RuleStopState)
                {
                    continue;
                }
                IList<Transition> optimizedTransitions = null;
                for (int i = 0; i < state.NumberOfOptimizedTransitions; i++)
                {
                    Transition transition = state.GetOptimizedTransition(i);
                    ATNState intermediate = transition.target;
                    if (transition.TransitionType != TransitionType.EPSILON || ((EpsilonTransition)transition).OutermostPrecedenceReturn != -1 || intermediate.StateType != StateType.Basic || !intermediate.OnlyHasEpsilonTransitions)
                    {
                        if (optimizedTransitions != null)
                        {
                            optimizedTransitions.Add(transition);
                        }
                        goto nextTransition_continue;
                    }
                    for (int j = 0; j < intermediate.NumberOfOptimizedTransitions; j++)
                    {
                        if (intermediate.GetOptimizedTransition(j).TransitionType != TransitionType.EPSILON || ((EpsilonTransition)intermediate.GetOptimizedTransition(j)).OutermostPrecedenceReturn != -1)
                        {
                            if (optimizedTransitions != null)
                            {
                                optimizedTransitions.Add(transition);
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
                            optimizedTransitions.Add(state.GetOptimizedTransition(j_1));
                        }
                    }
                    for (int j_2 = 0; j_2 < intermediate.NumberOfOptimizedTransitions; j_2++)
                    {
                        ATNState target = intermediate.GetOptimizedTransition(j_2).target;
                        optimizedTransitions.Add(new EpsilonTransition(target));
                    }
nextTransition_continue: ;
                }

                if (optimizedTransitions != null)
                {
                    if (state.IsOptimized)
                    {
                        while (state.NumberOfOptimizedTransitions > 0)
                        {
                            state.RemoveOptimizedTransition(state.NumberOfOptimizedTransitions - 1);
                        }
                    }
                    foreach (Transition transition in optimizedTransitions)
                    {
                        state.AddOptimizedTransition(transition);
                    }
                }
            }

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
                for (int i = 0; i < decision.NumberOfOptimizedTransitions; i++)
                {
                    Transition epsTransition = decision.GetOptimizedTransition(i);
                    if (!(epsTransition is EpsilonTransition))
                    {
                        continue;
                    }
                    if (epsTransition.target.NumberOfOptimizedTransitions != 1)
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
                    if (transition is AtomTransition || transition is RangeTransition || transition is SetTransition)
                    {
                        setTransitions.Add(i);
                    }
                }
                if (setTransitions.Count <= 1)
                {
                    continue;
                }
                IList<Transition> optimizedTransitions = new List<Transition>();
                for (int i_1 = 0; i_1 < decision.NumberOfOptimizedTransitions; i_1++)
                {
                    if (!setTransitions.Contains(i_1))
                    {
                        optimizedTransitions.Add(decision.GetOptimizedTransition(i_1));
                    }
                }
                ATNState blockEndState = decision.GetOptimizedTransition(setTransitions.MinElement).target.GetOptimizedTransition(0).target;
                IntervalSet matchSet = new IntervalSet();
                for (int i_2 = 0; i_2 < setTransitions.GetIntervals().Count; i_2++)
                {
                    Interval interval = setTransitions.GetIntervals()[i_2];
                    for (int j = interval.a; j <= interval.b; j++)
                    {
                        Transition matchTransition = decision.GetOptimizedTransition(j).target.GetOptimizedTransition(0);
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
                    if (matchSet.Count == 1)
                    {
                        newTransition = new AtomTransition(blockEndState, matchSet.MinElement);
                    }
                    else
                    {
                        Interval matchInterval = matchSet.GetIntervals()[0];
                        newTransition = new RangeTransition(blockEndState, matchInterval.a, matchInterval.b);
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
                optimizedTransitions.Add(new EpsilonTransition(setOptimizedState));
                removedPaths += decision.NumberOfOptimizedTransitions - optimizedTransitions.Count;
                if (decision.IsOptimized)
                {
                    while (decision.NumberOfOptimizedTransitions > 0)
                    {
                        decision.RemoveOptimizedTransition(decision.NumberOfOptimizedTransitions - 1);
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
                if (!state.IsOptimized)
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

        private static bool TestTailCall(ATN atn, RuleTransition transition, bool optimizedPath)
        {
            if (!optimizedPath && transition.tailCall)
            {
                return true;
            }
            if (optimizedPath && transition.optimizedTailCall)
            {
                return true;
            }
            BitSet reachable = new BitSet(atn.states.Count);
            Stack<ATNState> worklist = new Stack<ATNState>();
            worklist.Push(transition.followState);
            while (worklist.Count > 0)
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
                if (!state.OnlyHasEpsilonTransitions)
                {
                    return false;
                }
                IList<Transition> transitions = optimizedPath ? state.optimizedTransitions : state.transitions;
                foreach (Transition t in transitions)
                {
                    if (t.TransitionType != TransitionType.EPSILON)
                    {
                        return false;
                    }
                    worklist.Push(t.target);
                }
            }
            return true;
        }


        protected internal int ReadInt()
        {
			return data[p++];
        }

        [return: NotNull]
        protected internal virtual Transition EdgeFactory(ATN atn, TransitionType type, int src, int trg, int arg1, int arg2, int arg3, IList<IntervalSet> sets)
        {
            ATNState target = atn.states[trg];
            switch (type)
            {
                case TransitionType.EPSILON:
                {
                    return new EpsilonTransition(target);
                }

                case TransitionType.RANGE:
                {
                    if (arg3 != 0)
                    {
                        return new RangeTransition(target, TokenConstants.EOF, arg2);
                    }
                    else
                    {
                        return new RangeTransition(target, arg1, arg2);
                    }
                }

                case TransitionType.RULE:
                {
                    RuleTransition rt = new RuleTransition((RuleStartState)atn.states[arg1], arg2, arg3, target);
                    return rt;
                }

                case TransitionType.PREDICATE:
                {
                    PredicateTransition pt = new PredicateTransition(target, arg1, arg2, arg3 != 0);
                    return pt;
                }

                case TransitionType.PRECEDENCE:
                {
                    return new PrecedencePredicateTransition(target, arg1);
                }

                case TransitionType.ATOM:
                {
                    if (arg3 != 0)
                    {
                        return new AtomTransition(target, TokenConstants.EOF);
                    }
                    else
                    {
                        return new AtomTransition(target, arg1);
                    }
                }

                case TransitionType.ACTION:
                {
                    ActionTransition a = new ActionTransition(target, arg1, arg2, arg3 != 0);
                    return a;
                }

                case TransitionType.SET:
                {
                    return new SetTransition(target, sets[arg1]);
                }

                case TransitionType.NOT_SET:
                {
                    return new NotSetTransition(target, sets[arg1]);
                }

                case TransitionType.WILDCARD:
                {
                    return new WildcardTransition(target);
                }
            }
            throw new ArgumentException("The specified transition type is not valid.");
        }

        protected internal virtual ATNState StateFactory(StateType type, int ruleIndex)
        {
            ATNState s;
            switch (type)
            {
                case StateType.InvalidType:
                {
                    return null;
                }

                case StateType.Basic:
                {
                    s = new BasicState();
                    break;
                }

                case StateType.RuleStart:
                {
                    s = new RuleStartState();
                    break;
                }

                case StateType.BlockStart:
                {
                    s = new BasicBlockStartState();
                    break;
                }

                case StateType.PlusBlockStart:
                {
                    s = new PlusBlockStartState();
                    break;
                }

                case StateType.StarBlockStart:
                {
                    s = new StarBlockStartState();
                    break;
                }

                case StateType.TokenStart:
                {
                    s = new TokensStartState();
                    break;
                }

                case StateType.RuleStop:
                {
                    s = new RuleStopState();
                    break;
                }

                case StateType.BlockEnd:
                {
                    s = new BlockEndState();
                    break;
                }

                case StateType.StarLoopBack:
                {
                    s = new StarLoopbackState();
                    break;
                }

                case StateType.StarLoopEntry:
                {
                    s = new StarLoopEntryState();
                    break;
                }

                case StateType.PlusLoopBack:
                {
                    s = new PlusLoopbackState();
                    break;
                }

                case StateType.LoopEnd:
                {
                    s = new LoopEndState();
                    break;
                }

                default:
                {
                    string message = string.Format(CultureInfo.CurrentCulture, "The specified state type {0} is not valid.", type);
                    throw new ArgumentException(message);
                }
            }
            s.ruleIndex = ruleIndex;
            return s;
        }

        protected internal virtual ILexerAction LexerActionFactory(LexerActionType type, int data1, int data2)
        {
            switch (type)
            {
                case LexerActionType.Channel:
                {
                    return new LexerChannelAction(data1);
                }

                case LexerActionType.Custom:
                {
                    return new LexerCustomAction(data1, data2);
                }

                case LexerActionType.Mode:
                {
                    return new LexerModeAction(data1);
                }

                case LexerActionType.More:
                {
                    return LexerMoreAction.Instance;
                }

                case LexerActionType.PopMode:
                {
                    return LexerPopModeAction.Instance;
                }

                case LexerActionType.PushMode:
                {
                    return new LexerPushModeAction(data1);
                }

                case LexerActionType.Skip:
                {
                    return LexerSkipAction.Instance;
                }

                case LexerActionType.Type:
                {
                    return new LexerTypeAction(data1);
                }

                default:
                {
                    string message = string.Format(CultureInfo.CurrentCulture, "The specified lexer action type {0} is not valid.", type);
                    throw new ArgumentException(message);
                }
            }
        }
    }
}
