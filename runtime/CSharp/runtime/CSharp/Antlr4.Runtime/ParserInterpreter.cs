/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Linq;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
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
        private readonly string _grammarFileName;

        private readonly ATN _atn;
		
        private readonly Dfa.DFA[] _decisionToDFA;

        protected internal readonly BitSet pushRecursionContextStates;

		private readonly string[] _ruleNames;

        [NotNull]
        private readonly IVocabulary vocabulary;

        private readonly Stack<Tuple<ParserRuleContext, int>> _parentContextStack = new Stack<Tuple<ParserRuleContext, int>>();

        public ParserInterpreter(string grammarFileName, IVocabulary vocabulary, IEnumerable<string> ruleNames, ATN atn, ITokenStream input)
            : base(input)
        {
            this._grammarFileName = grammarFileName;
            this._atn = atn;
            this._ruleNames = ruleNames.ToArray();
            this.vocabulary = vocabulary;
            // identify the ATN states where pushNewRecursionContext must be called
            this.pushRecursionContextStates = new BitSet(atn.states.Count);
            foreach (ATNState state in atn.states)
            {
                if (!(state is StarLoopEntryState))
                {
                    continue;
                }
				if (((StarLoopEntryState)state).isPrecedenceDecision)
                {
                    this.pushRecursionContextStates.Set(state.stateNumber);
                }
            }
			
            //init decision DFA
            int numberofDecisions = atn.NumberOfDecisions;
            this._decisionToDFA = new Dfa.DFA[numberofDecisions];
            for (int i = 0; i < numberofDecisions; i++)
            {
                DecisionState decisionState = atn.GetDecisionState(i);
                _decisionToDFA[i] = new Dfa.DFA(decisionState, i);
            }
             // get atn simulator that knows how to do predictions
            Interpreter = new ParserATNSimulator(this, atn, _decisionToDFA, null);
        }

        public override ATN Atn
        {
            get
            {
                return _atn;
            }
        }

        public override IVocabulary Vocabulary
        {
            get
            {
                return vocabulary;
            }
        }

        public override string[] RuleNames
        {
            get
            {
                return _ruleNames;
            }
        }

        public override string GrammarFileName
        {
            get
            {
                return _grammarFileName;
            }
        }

        /// <summary>Begin parsing at startRuleIndex</summary>
        public virtual ParserRuleContext Parse(int startRuleIndex)
        {
            RuleStartState startRuleStartState = _atn.ruleToStartState[startRuleIndex];
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
						if (RuleContext.IsEmpty)
                        {
                            if (startRuleStartState.isPrecedenceRule)
                            {
								ParserRuleContext result = RuleContext;
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
                            State = _atn.ruleToStopState[p.ruleIndex].stateNumber;
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
			_parentContextStack.Push(Tuple.Create(RuleContext, localctx.invokingState));
            base.EnterRecursionRule(localctx, state, ruleIndex, precedence);
        }

        protected internal virtual ATNState AtnState
        {
            get
            {
                return _atn.states[State];
            }
        }

        protected internal virtual void VisitState(ATNState p)
        {
            int edge;
            if (p.NumberOfTransitions > 1)
            {
                ErrorHandler.Sync(this);
				edge = Interpreter.AdaptivePredict(TokenStream, ((DecisionState)p).decision, RuleContext);
            }
            else
            {
                edge = 1;
            }
            Transition transition = p.Transition(edge - 1);
            switch (transition.TransitionType)
            {
                case TransitionType.EPSILON:
                {
                    if (pushRecursionContextStates.Get(p.stateNumber) && !(transition.target is LoopEndState))
                    {
						InterpreterRuleContext ctx = new InterpreterRuleContext(_parentContextStack.Peek().Item1, _parentContextStack.Peek().Item2, RuleContext.RuleIndex);
						PushNewRecursionContext(ctx, _atn.ruleToStartState[p.ruleIndex].stateNumber, RuleContext.RuleIndex);
                    }
                    break;
                }

                case TransitionType.ATOM:
                {
					Match(((AtomTransition)transition).token);
                    break;
                }

                case TransitionType.RANGE:
                case TransitionType.SET:
                case TransitionType.NOT_SET:
                {
                    if (!transition.Matches(TokenStream.LA(1), TokenConstants.MinUserTokenType, 65535))
                    {
						ErrorHandler.RecoverInline(this);
                    }
                    MatchWildcard();
                    break;
                }

                case TransitionType.WILDCARD:
                {
                    MatchWildcard();
                    break;
                }

                case TransitionType.RULE:
                {
                    RuleStartState ruleStartState = (RuleStartState)transition.target;
                    int ruleIndex = ruleStartState.ruleIndex;
					InterpreterRuleContext ctx_1 = new InterpreterRuleContext(RuleContext, p.stateNumber, ruleIndex);
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

                case TransitionType.PREDICATE:
                {
                    PredicateTransition predicateTransition = (PredicateTransition)transition;
					if (!Sempred(RuleContext, predicateTransition.ruleIndex, predicateTransition.predIndex))
                    {
                        throw new FailedPredicateException(this);
                    }
                    break;
                }

                case TransitionType.ACTION:
                {
                    ActionTransition actionTransition = (ActionTransition)transition;
					Action(RuleContext, actionTransition.ruleIndex, actionTransition.actionIndex);
                    break;
                }

                case TransitionType.PRECEDENCE:
                {
					if (!Precpred(RuleContext, ((PrecedencePredicateTransition)transition).precedence))
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
            RuleStartState ruleStartState = _atn.ruleToStartState[p.ruleIndex];
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
            RuleTransition ruleTransition = (RuleTransition)_atn.states[State].Transition(0);
            State = ruleTransition.followState.stateNumber;
        }
    }
}
