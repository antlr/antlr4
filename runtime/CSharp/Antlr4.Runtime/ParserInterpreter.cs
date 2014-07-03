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
using System.Collections.Generic;
using System.Linq;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// A parser simulator that mimics what ANTLR's generated
    /// parser code does.
    /// </summary>
    /// <remarks>
    /// A parser simulator that mimics what ANTLR's generated
    /// parser code does. A ParserATNSimulator is used to make
    /// predictions via adaptivePredict but this class moves a pointer through the
    /// ATN to simulate parsing. ParserATNSimulator just
    /// makes us efficient rather than having to backtrack, for example.
    /// This properly creates parse trees even for left recursive rules.
    /// We rely on the left recursive rule invocation and special predicate
    /// transitions to make left recursive rules work.
    /// See TestParserInterpreter for examples.
    /// </remarks>
    public class ParserInterpreter : Parser
    {
        protected internal readonly string grammarFileName;

        protected internal readonly ATN atn;

        protected internal readonly BitSet pushRecursionContextStates;

        protected internal readonly string[] tokenNames;

        protected internal readonly string[] ruleNames;

        protected internal readonly Stack<Tuple<ParserRuleContext, int>> _parentContextStack = new Stack<Tuple<ParserRuleContext, int>>();

        public ParserInterpreter(string grammarFileName, IEnumerable<string> tokenNames, IEnumerable<string> ruleNames, ATN atn, ITokenStream input)
            : base(input)
        {
            this.grammarFileName = grammarFileName;
            this.atn = atn;
            this.tokenNames = tokenNames.ToArray();
            this.ruleNames = ruleNames.ToArray();
            // identify the ATN states where pushNewRecursionContext must be called
            this.pushRecursionContextStates = new BitSet(atn.states.Count);
            foreach (ATNState state in atn.states)
            {
                if (!(state is StarLoopEntryState))
                {
                    continue;
                }
                if (((StarLoopEntryState)state).precedenceRuleDecision)
                {
                    this.pushRecursionContextStates.Set(state.stateNumber);
                }
            }
            // get atn simulator that knows how to do predictions
            Interpreter = new ParserATNSimulator(this, atn);
        }

        public override ATN Atn
        {
            get
            {
                return atn;
            }
        }

        public override string[] TokenNames
        {
            get
            {
                return tokenNames;
            }
        }

        public override string[] RuleNames
        {
            get
            {
                return ruleNames;
            }
        }

        public override string GrammarFileName
        {
            get
            {
                return grammarFileName;
            }
        }

        /// <summary>Begin parsing at startRuleIndex</summary>
        public virtual ParserRuleContext Parse(int startRuleIndex)
        {
            RuleStartState startRuleStartState = atn.ruleToStartState[startRuleIndex];
            InterpreterRuleContext rootContext = new InterpreterRuleContext(null, ATNState.InvalidStateNumber, startRuleIndex);
            if (startRuleStartState.isPrecedenceRule)
            {
                EnterRecursionRule(rootContext, startRuleStartState.stateNumber, startRuleIndex, 0);
            }
            else
            {
                EnterRule(rootContext, startRuleStartState.stateNumber, startRuleIndex);
            }
            while (true)
            {
                ATNState p = AtnState;
                switch (p.StateType)
                {
                    case StateType.RuleStop:
                    {
                        // pop; return from rule
                        if (_ctx.IsEmpty)
                        {
                            if (startRuleStartState.isPrecedenceRule)
                            {
                                ParserRuleContext result = _ctx;
                                Tuple<ParserRuleContext, int> parentContext = _parentContextStack.Pop();
                                UnrollRecursionContexts(parentContext.Item1);
                                return result;
                            }
                            else
                            {
                                ExitRule();
                                return rootContext;
                            }
                        }
                        VisitRuleStopState(p);
                        break;
                    }

                    default:
                    {
                        try
                        {
                            VisitState(p);
                        }
                        catch (RecognitionException e)
                        {
                            State = atn.ruleToStopState[p.ruleIndex].stateNumber;
                            Context.exception = e;
                            ErrorHandler.ReportError(this, e);
                            ErrorHandler.Recover(this, e);
                        }
                        break;
                    }
                }
            }
        }

        public override void EnterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence)
        {
            _parentContextStack.Push(Tuple.Create(_ctx, localctx.invokingState));
            base.EnterRecursionRule(localctx, state, ruleIndex, precedence);
        }

        protected internal virtual ATNState AtnState
        {
            get
            {
                return atn.states[State];
            }
        }

        protected internal virtual void VisitState(ATNState p)
        {
            int edge;
            if (p.NumberOfTransitions > 1)
            {
                ErrorHandler.Sync(this);
                edge = Interpreter.AdaptivePredict(_input, ((DecisionState)p).decision, _ctx);
            }
            else
            {
                edge = 1;
            }
            Transition transition = p.Transition(edge - 1);
            switch (transition.TransitionType)
            {
                case TransitionType.Epsilon:
                {
                    if (pushRecursionContextStates.Get(p.stateNumber) && !(transition.target is LoopEndState))
                    {
                        InterpreterRuleContext ctx = new InterpreterRuleContext(_parentContextStack.Peek().Item1, _parentContextStack.Peek().Item2, _ctx.RuleIndex);
                        PushNewRecursionContext(ctx, atn.ruleToStartState[p.ruleIndex].stateNumber, _ctx.RuleIndex);
                    }
                    break;
                }

                case TransitionType.Atom:
                {
                    Match(((AtomTransition)transition).label);
                    break;
                }

                case TransitionType.Range:
                case TransitionType.Set:
                case TransitionType.NotSet:
                {
                    if (!transition.Matches(_input.La(1), TokenConstants.MinUserTokenType, 65535))
                    {
                        _errHandler.RecoverInline(this);
                    }
                    MatchWildcard();
                    break;
                }

                case TransitionType.Wildcard:
                {
                    MatchWildcard();
                    break;
                }

                case TransitionType.Rule:
                {
                    RuleStartState ruleStartState = (RuleStartState)transition.target;
                    int ruleIndex = ruleStartState.ruleIndex;
                    InterpreterRuleContext ctx_1 = new InterpreterRuleContext(_ctx, p.stateNumber, ruleIndex);
                    if (ruleStartState.isPrecedenceRule)
                    {
                        EnterRecursionRule(ctx_1, ruleStartState.stateNumber, ruleIndex, ((RuleTransition)transition).precedence);
                    }
                    else
                    {
                        EnterRule(ctx_1, transition.target.stateNumber, ruleIndex);
                    }
                    break;
                }

                case TransitionType.Predicate:
                {
                    PredicateTransition predicateTransition = (PredicateTransition)transition;
                    if (!Sempred(_ctx, predicateTransition.ruleIndex, predicateTransition.predIndex))
                    {
                        throw new FailedPredicateException(this);
                    }
                    break;
                }

                case TransitionType.Action:
                {
                    ActionTransition actionTransition = (ActionTransition)transition;
                    Action(_ctx, actionTransition.ruleIndex, actionTransition.actionIndex);
                    break;
                }

                case TransitionType.Precedence:
                {
                    if (!Precpred(_ctx, ((PrecedencePredicateTransition)transition).precedence))
                    {
                        throw new FailedPredicateException(this, string.Format("precpred(_ctx, {0})", ((PrecedencePredicateTransition)transition).precedence));
                    }
                    break;
                }

                default:
                {
                    throw new NotSupportedException("Unrecognized ATN transition type.");
                }
            }
            State = transition.target.stateNumber;
        }

        protected internal virtual void VisitRuleStopState(ATNState p)
        {
            RuleStartState ruleStartState = atn.ruleToStartState[p.ruleIndex];
            if (ruleStartState.isPrecedenceRule)
            {
                Tuple<ParserRuleContext, int> parentContext = _parentContextStack.Pop();
                UnrollRecursionContexts(parentContext.Item1);
                State = parentContext.Item2;
            }
            else
            {
                ExitRule();
            }
            RuleTransition ruleTransition = (RuleTransition)atn.states[State].Transition(0);
            State = ruleTransition.followState.stateNumber;
        }
    }
}
