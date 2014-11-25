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
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <since>4.3</since>
    public class ProfilingATNSimulator : ParserATNSimulator
    {
        protected internal readonly Antlr4.Runtime.Atn.DecisionInfo[] decisions;

        protected internal int numDecisions;

        private ITokenStream _input;

		private int _startIndex;

		private int _sllStopIndex;

		private int _llStopIndex;

        protected internal int currentDecision;

        protected internal SimulatorState currentState;

        /// <summary>
        /// At the point of LL failover, we record how SLL would resolve the conflict so that
        /// we can determine whether or not a decision / input pair is context-sensitive.
        /// </summary>
        /// <remarks>
        /// At the point of LL failover, we record how SLL would resolve the conflict so that
        /// we can determine whether or not a decision / input pair is context-sensitive.
        /// If LL gives a different result than SLL's predicted alternative, we have a
        /// context sensitivity for sure. The converse is not necessarily true, however.
        /// It's possible that after conflict resolution chooses minimum alternatives,
        /// SLL could get the same answer as LL. Regardless of whether or not the result indicates
        /// an ambiguity, it is not treated as a context sensitivity because LL prediction
        /// was not required in order to produce a correct prediction for this decision and input sequence.
        /// It may in fact still be a context sensitivity but we don't know by looking at the
        /// minimum alternatives for the current input.
        /// </remarks>
        protected internal int conflictingAltResolvedBySLL;

        public ProfilingATNSimulator(Parser parser)
            : base(parser, parser.Interpreter.atn)
        {
            optimize_ll1 = false;
            reportAmbiguities = true;
            numDecisions = atn.decisionToState.Count;
            decisions = new Antlr4.Runtime.Atn.DecisionInfo[numDecisions];
            for (int i = 0; i < numDecisions; i++)
            {
                decisions[i] = new Antlr4.Runtime.Atn.DecisionInfo(i);
            }
        }

        public override int AdaptivePredict(ITokenStream input, int decision, ParserRuleContext outerContext)
        {
            try
            {
                this._input = input;
                this._startIndex = input.Index;
                // it's possible for SLL to reach a conflict state without consuming any input
                this._sllStopIndex = _startIndex - 1;
                this._llStopIndex = -1;
                this.currentDecision = decision;
                this.currentState = null;
                this.conflictingAltResolvedBySLL = ATN.InvalidAltNumber;
                // expensive but useful info
                int alt = base.AdaptivePredict(input, decision, outerContext);
                decisions[decision].invocations++;
                int SLL_k = _sllStopIndex - _startIndex + 1;
                decisions[decision].SLL_TotalLook += SLL_k;
                decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook == 0 ? SLL_k : Math.Min(decisions[decision].SLL_MinLook, SLL_k);
                if (SLL_k > decisions[decision].SLL_MaxLook)
                {
                    decisions[decision].SLL_MaxLook = SLL_k;
                    decisions[decision].SLL_MaxLookEvent = new LookaheadEventInfo(decision, null, input, _startIndex, _sllStopIndex, false);
                }
                if (_llStopIndex >= 0)
                {
                    int LL_k = _llStopIndex - _startIndex + 1;
                    decisions[decision].LL_TotalLook += LL_k;
                    decisions[decision].LL_MinLook = decisions[decision].LL_MinLook == 0 ? LL_k : Math.Min(decisions[decision].LL_MinLook, LL_k);
                    if (LL_k > decisions[decision].LL_MaxLook)
                    {
                        decisions[decision].LL_MaxLook = LL_k;
                        decisions[decision].LL_MaxLookEvent = new LookaheadEventInfo(decision, null, input, _startIndex, _llStopIndex, true);
                    }
                }
                return alt;
            }
            finally
            {
                this._input = null;
                this.currentDecision = -1;
            }
        }

        protected internal override SimulatorState GetStartState(DFA dfa, ITokenStream input, ParserRuleContext outerContext, bool useContext)
        {
            SimulatorState state = base.GetStartState(dfa, input, outerContext, useContext);
            currentState = state;
            return state;
        }

        protected internal override SimulatorState ComputeStartState(DFA dfa, ParserRuleContext globalContext, bool useContext)
        {
            SimulatorState state = base.ComputeStartState(dfa, globalContext, useContext);
            currentState = state;
            return state;
        }

        protected internal override SimulatorState ComputeReachSet(DFA dfa, SimulatorState previous, int t, PredictionContextCache contextCache)
        {
            SimulatorState reachState = base.ComputeReachSet(dfa, previous, t, contextCache);
            if (reachState == null)
            {
                // no reach on current lookahead symbol. ERROR.
                decisions[currentDecision].errors.Add(new ErrorInfo(currentDecision, previous, _input, _startIndex, _input.Index));
            }
            currentState = reachState;
            return reachState;
        }

        protected internal override DFAState GetExistingTargetState(DFAState previousD, int t)
        {
            // this method is called after each time the input position advances
            if (currentState.useContext)
            {
                _llStopIndex = _input.Index;
            }
            else
            {
                _sllStopIndex = _input.Index;
            }
            DFAState existingTargetState = base.GetExistingTargetState(previousD, t);
            if (existingTargetState != null)
            {
                // this method is directly called by execDFA; must construct a SimulatorState
                // to represent the current state for this case
                currentState = new SimulatorState(currentState.outerContext, existingTargetState, currentState.useContext, currentState.remainingOuterContext);
                if (currentState.useContext)
                {
                    decisions[currentDecision].LL_DFATransitions++;
                }
                else
                {
                    decisions[currentDecision].SLL_DFATransitions++;
                }
                // count only if we transition over a DFA state
                if (existingTargetState == Error)
                {
                    SimulatorState state = new SimulatorState(currentState.outerContext, previousD, currentState.useContext, currentState.remainingOuterContext);
                    decisions[currentDecision].errors.Add(new ErrorInfo(currentDecision, state, _input, _startIndex, _input.Index));
                }
            }
            return existingTargetState;
        }

        protected internal override Tuple<DFAState, ParserRuleContext> ComputeTargetState(DFA dfa, DFAState s, ParserRuleContext remainingGlobalContext, int t, bool useContext, PredictionContextCache contextCache)
        {
            Tuple<DFAState, ParserRuleContext> targetState = base.ComputeTargetState(dfa, s, remainingGlobalContext, t, useContext, contextCache);
            if (useContext)
            {
                decisions[currentDecision].LL_ATNTransitions++;
            }
            else
            {
                decisions[currentDecision].SLL_ATNTransitions++;
            }
            return targetState;
        }

        protected internal override bool EvalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt)
        {
            bool result = base.EvalSemanticContext(pred, parserCallStack, alt);
            if (!(pred is SemanticContext.PrecedencePredicate))
            {
                bool fullContext = _llStopIndex >= 0;
                int stopIndex = fullContext ? _llStopIndex : _sllStopIndex;
                decisions[currentDecision].predicateEvals.Add(new PredicateEvalInfo(currentState, currentDecision, _input, _startIndex, stopIndex, pred, result, alt));
            }
            return result;
        }

        protected internal override void ReportContextSensitivity(DFA dfa, int prediction, SimulatorState acceptState, int startIndex, int stopIndex)
        {
            if (prediction != conflictingAltResolvedBySLL)
            {
                decisions[currentDecision].contextSensitivities.Add(new ContextSensitivityInfo(currentDecision, acceptState, _input, startIndex, stopIndex));
            }
            base.ReportContextSensitivity(dfa, prediction, acceptState, startIndex, stopIndex);
        }

        protected internal override void ReportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, SimulatorState conflictState, int startIndex, int stopIndex)
        {
            if (conflictingAlts != null)
            {
                conflictingAltResolvedBySLL = conflictingAlts.NextSetBit(0);
            }
            else
            {
                conflictingAltResolvedBySLL = conflictState.s0.configs.RepresentedAlternatives.NextSetBit(0);
            }
            decisions[currentDecision].LL_Fallback++;
            base.ReportAttemptingFullContext(dfa, conflictingAlts, conflictState, startIndex, stopIndex);
        }

        protected internal override void ReportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
        {
            int prediction;
            if (ambigAlts != null)
            {
                prediction = ambigAlts.NextSetBit(0);
            }
            else
            {
                prediction = configs.RepresentedAlternatives.NextSetBit(0);
            }
            if (conflictingAltResolvedBySLL != ATN.InvalidAltNumber && prediction != conflictingAltResolvedBySLL)
            {
                // Even though this is an ambiguity we are reporting, we can
                // still detect some context sensitivities.  Both SLL and LL
                // are showing a conflict, hence an ambiguity, but if they resolve
                // to different minimum alternatives we have also identified a
                // context sensitivity.
                decisions[currentDecision].contextSensitivities.Add(new ContextSensitivityInfo(currentDecision, currentState, _input, startIndex, stopIndex));
            }
            decisions[currentDecision].ambiguities.Add(new AmbiguityInfo(currentDecision, currentState, _input, startIndex, stopIndex));
            base.ReportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
        }

        public virtual Antlr4.Runtime.Atn.DecisionInfo[] DecisionInfo
        {
            get
            {
                // ---------------------------------------------------------------------
                return decisions;
            }
        }
    }
}
