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
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>The embodiment of the adaptive LL(*), ALL(*), parsing strategy.</summary>
    /// <remarks>
    /// The embodiment of the adaptive LL(*), ALL(*), parsing strategy.
    /// <p>
    /// The basic complexity of the adaptive strategy makes it harder to understand.
    /// We begin with ATN simulation to build paths in a DFA. Subsequent prediction
    /// requests go through the DFA first. If they reach a state without an edge for
    /// the current symbol, the algorithm fails over to the ATN simulation to
    /// complete the DFA path for the current input (until it finds a conflict state
    /// or uniquely predicting state).</p>
    /// <p>
    /// All of that is done without using the outer context because we want to create
    /// a DFA that is not dependent upon the rule invocation stack when we do a
    /// prediction. One DFA works in all contexts. We avoid using context not
    /// necessarily because it's slower, although it can be, but because of the DFA
    /// caching problem. The closure routine only considers the rule invocation stack
    /// created during prediction beginning in the decision rule. For example, if
    /// prediction occurs without invoking another rule's ATN, there are no context
    /// stacks in the configurations. When lack of context leads to a conflict, we
    /// don't know if it's an ambiguity or a weakness in the strong LL(*) parsing
    /// strategy (versus full LL(*)).</p>
    /// <p>
    /// When SLL yields a configuration set with conflict, we rewind the input and
    /// retry the ATN simulation, this time using full outer context without adding
    /// to the DFA. Configuration context stacks will be the full invocation stacks
    /// from the start rule. If we get a conflict using full context, then we can
    /// definitively say we have a true ambiguity for that input sequence. If we
    /// don't get a conflict, it implies that the decision is sensitive to the outer
    /// context. (It is not context-sensitive in the sense of context-sensitive
    /// grammars.)</p>
    /// <p>
    /// The next time we reach this DFA state with an SLL conflict, through DFA
    /// simulation, we will again retry the ATN simulation using full context mode.
    /// This is slow because we can't save the results and have to "interpret" the
    /// ATN each time we get that input.</p>
    /// <p>
    /// <strong>CACHING FULL CONTEXT PREDICTIONS</strong></p>
    /// <p>
    /// We could cache results from full context to predicted alternative easily and
    /// that saves a lot of time but doesn't work in presence of predicates. The set
    /// of visible predicates from the ATN start state changes depending on the
    /// context, because closure can fall off the end of a rule. I tried to cache
    /// tuples (stack context, semantic context, predicted alt) but it was slower
    /// than interpreting and much more complicated. Also required a huge amount of
    /// memory. The goal is not to create the world's fastest parser anyway. I'd like
    /// to keep this algorithm simple. By launching multiple threads, we can improve
    /// the speed of parsing across a large number of files.</p>
    /// <p>
    /// There is no strict ordering between the amount of input used by SLL vs LL,
    /// which makes it really hard to build a cache for full context. Let's say that
    /// we have input A B C that leads to an SLL conflict with full context X. That
    /// implies that using X we might only use A B but we could also use A B C D to
    /// resolve conflict. Input A B C D could predict alternative 1 in one position
    /// in the input and A B C E could predict alternative 2 in another position in
    /// input. The conflicting SLL configurations could still be non-unique in the
    /// full context prediction, which would lead us to requiring more input than the
    /// original A B C.	To make a	prediction cache work, we have to track	the exact
    /// input	used during the previous prediction. That amounts to a cache that maps
    /// X to a specific DFA for that context.</p>
    /// <p>
    /// Something should be done for left-recursive expression predictions. They are
    /// likely LL(1) + pred eval. Easier to do the whole SLL unless error and retry
    /// with full LL thing Sam does.</p>
    /// <p>
    /// <strong>AVOIDING FULL CONTEXT PREDICTION</strong></p>
    /// <p>
    /// We avoid doing full context retry when the outer context is empty, we did not
    /// dip into the outer context by falling off the end of the decision state rule,
    /// or when we force SLL mode.</p>
    /// <p>
    /// As an example of the not dip into outer context case, consider as super
    /// constructor calls versus function calls. One grammar might look like
    /// this:</p>
    /// <pre>
    /// ctorBody
    /// : '{' superCall? stat* '}'
    /// ;
    /// </pre>
    /// <p>
    /// Or, you might see something like</p>
    /// <pre>
    /// stat
    /// : superCall ';'
    /// | expression ';'
    /// | ...
    /// ;
    /// </pre>
    /// <p>
    /// In both cases I believe that no closure operations will dip into the outer
    /// context. In the first case ctorBody in the worst case will stop at the '}'.
    /// In the 2nd case it should stop at the ';'. Both cases should stay within the
    /// entry rule and not dip into the outer context.</p>
    /// <p>
    /// <strong>PREDICATES</strong></p>
    /// <p>
    /// Predicates are always evaluated if present in either SLL or LL both. SLL and
    /// LL simulation deals with predicates differently. SLL collects predicates as
    /// it performs closure operations like ANTLR v3 did. It delays predicate
    /// evaluation until it reaches and accept state. This allows us to cache the SLL
    /// ATN simulation whereas, if we had evaluated predicates on-the-fly during
    /// closure, the DFA state configuration sets would be different and we couldn't
    /// build up a suitable DFA.</p>
    /// <p>
    /// When building a DFA accept state during ATN simulation, we evaluate any
    /// predicates and return the sole semantically valid alternative. If there is
    /// more than 1 alternative, we report an ambiguity. If there are 0 alternatives,
    /// we throw an exception. Alternatives without predicates act like they have
    /// true predicates. The simple way to think about it is to strip away all
    /// alternatives with false predicates and choose the minimum alternative that
    /// remains.</p>
    /// <p>
    /// When we start in the DFA and reach an accept state that's predicated, we test
    /// those and return the minimum semantically viable alternative. If no
    /// alternatives are viable, we throw an exception.</p>
    /// <p>
    /// During full LL ATN simulation, closure always evaluates predicates and
    /// on-the-fly. This is crucial to reducing the configuration set size during
    /// closure. It hits a landmine when parsing with the Java grammar, for example,
    /// without this on-the-fly evaluation.</p>
    /// <p>
    /// <strong>SHARING DFA</strong></p>
    /// <p>
    /// All instances of the same parser share the same decision DFAs through a
    /// static field. Each instance gets its own ATN simulator but they share the
    /// same
    /// <see cref="ATN.decisionToDFA"/>
    /// field. They also share a
    /// <see cref="PredictionContextCache"/>
    /// object that makes sure that all
    /// <see cref="PredictionContext"/>
    /// objects are shared among the DFA states. This makes
    /// a big size difference.</p>
    /// <p>
    /// <strong>THREAD SAFETY</strong></p>
    /// <p>
    /// The
    /// <see cref="ParserATNSimulator"/>
    /// locks on the
    /// <see cref="ATN.decisionToDFA"/>
    /// field when
    /// it adds a new DFA object to that array.
    /// <see cref="AddDFAEdge(Antlr4.Runtime.Dfa.DFAState, int, Antlr4.Runtime.Dfa.DFAState)"/>
    /// locks on the DFA for the current decision when setting the
    /// <see cref="DFAState.edges"/>
    /// field.
    /// <see cref="AddDFAState(Antlr4.Runtime.Dfa.DFA, ATNConfigSet, PredictionContextCache)"/>
    /// locks on
    /// the DFA for the current decision when looking up a DFA state to see if it
    /// already exists. We must make sure that all requests to add DFA states that
    /// are equivalent result in the same shared DFA object. This is because lots of
    /// threads will be trying to update the DFA at once. The
    /// <see cref="AddDFAState(Antlr4.Runtime.Dfa.DFA, ATNConfigSet, PredictionContextCache)"/>
    /// method also locks inside the DFA lock
    /// but this time on the shared context cache when it rebuilds the
    /// configurations'
    /// <see cref="PredictionContext"/>
    /// objects using cached
    /// subgraphs/nodes. No other locking occurs, even during DFA simulation. This is
    /// safe as long as we can guarantee that all threads referencing
    /// <c>s.edge[t]</c>
    /// get the same physical target
    /// <see cref="Antlr4.Runtime.Dfa.DFAState"/>
    /// , or
    /// <see langword="null"/>
    /// . Once into the DFA, the DFA simulation does not reference the
    /// <see cref="Antlr4.Runtime.Dfa.DFA.states"/>
    /// map. It follows the
    /// <see cref="DFAState.edges"/>
    /// field to new
    /// targets. The DFA simulator will either find
    /// <see cref="DFAState.edges"/>
    /// to be
    /// <see langword="null"/>
    /// , to be non-
    /// <see langword="null"/>
    /// and
    /// <c>dfa.edges[t]</c>
    /// null, or
    /// <c>dfa.edges[t]</c>
    /// to be non-null. The
    /// <see cref="AddDFAEdge(Antlr4.Runtime.Dfa.DFAState, int, Antlr4.Runtime.Dfa.DFAState)"/>
    /// method could be racing to set the field
    /// but in either case the DFA simulator works; if
    /// <see langword="null"/>
    /// , and requests ATN
    /// simulation. It could also race trying to get
    /// <c>dfa.edges[t]</c>
    /// , but either
    /// way it will work because it's not doing a test and set operation.</p>
    /// <p>
    /// <strong>Starting with SLL then failing to combined SLL/LL (Two-Stage
    /// Parsing)</strong></p>
    /// <p>
    /// Sam pointed out that if SLL does not give a syntax error, then there is no
    /// point in doing full LL, which is slower. We only have to try LL if we get a
    /// syntax error. For maximum speed, Sam starts the parser set to pure SLL
    /// mode with the
    /// <see cref="Antlr4.Runtime.BailErrorStrategy"/>
    /// :</p>
    /// <pre>
    /// parser.
    /// <see cref="Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}.Interpreter()">getInterpreter()</see>
    /// .
    /// <see cref="PredictionMode"/>
    /// <c>(</c>
    /// <see cref="Atn.PredictionMode.Sll"/>
    /// <c>)</c>
    /// ;
    /// parser.
    /// (new
    /// <see cref="Antlr4.Runtime.BailErrorStrategy"/>
    /// ());
    /// </pre>
    /// <p>
    /// If it does not get a syntax error, then we're done. If it does get a syntax
    /// error, we need to retry with the combined SLL/LL strategy.</p>
    /// <p>
    /// The reason this works is as follows. If there are no SLL conflicts, then the
    /// grammar is SLL (at least for that input set). If there is an SLL conflict,
    /// the full LL analysis must yield a set of viable alternatives which is a
    /// subset of the alternatives reported by SLL. If the LL set is a singleton,
    /// then the grammar is LL but not SLL. If the LL set is the same size as the SLL
    /// set, the decision is SLL. If the LL set has size &gt; 1, then that decision
    /// is truly ambiguous on the current input. If the LL set is smaller, then the
    /// SLL conflict resolution might choose an alternative that the full LL would
    /// rule out as a possibility based upon better context information. If that's
    /// the case, then the SLL parse will definitely get an error because the full LL
    /// analysis says it's not viable. If SLL conflict resolution chooses an
    /// alternative within the LL set, them both SLL and LL would choose the same
    /// alternative because they both choose the minimum of multiple conflicting
    /// alternatives.</p>
    /// <p>
    /// Let's say we have a set of SLL conflicting alternatives
    /// <c/>
    /// 
    /// 1, 2, 3}} and
    /// a smaller LL set called <em>s</em>. If <em>s</em> is
    /// <c/>
    /// 
    /// 2, 3}}, then SLL
    /// parsing will get an error because SLL will pursue alternative 1. If
    /// <em>s</em> is
    /// <c/>
    /// 
    /// 1, 2}} or
    /// <c/>
    /// 
    /// 1, 3}} then both SLL and LL will
    /// choose the same alternative because alternative one is the minimum of either
    /// set. If <em>s</em> is
    /// <c/>
    /// 
    /// 2}} or
    /// <c/>
    /// 
    /// 3}} then SLL will get a syntax
    /// error. If <em>s</em> is
    /// <c/>
    /// 
    /// 1}} then SLL will succeed.</p>
    /// <p>
    /// Of course, if the input is invalid, then we will get an error for sure in
    /// both SLL and LL parsing. Erroneous input will therefore require 2 passes over
    /// the input.</p>
    /// </remarks>
    public class ParserATNSimulator : ATNSimulator
    {
#pragma warning disable 0162 // Unreachable code detected
#if !PORTABLE
        public const bool debug = false;

        public const bool dfa_debug = false;

        public const bool retry_debug = false;
#endif

        [NotNull]
        private Antlr4.Runtime.Atn.PredictionMode predictionMode = Antlr4.Runtime.Atn.PredictionMode.Ll;

        public bool force_global_context = false;

        public bool always_try_local_context = true;

        /// <summary>Determines whether the DFA is used for full-context predictions.</summary>
        /// <remarks>
        /// Determines whether the DFA is used for full-context predictions. When
        /// <see langword="true"/>
        /// , the DFA stores transition information for both full-context
        /// and SLL parsing; otherwise, the DFA only stores SLL transition
        /// information.
        /// <p>
        /// For some grammars, enabling the full-context DFA can result in a
        /// substantial performance improvement. However, this improvement typically
        /// comes at the expense of memory used for storing the cached DFA states,
        /// configuration sets, and prediction contexts.</p>
        /// <p>
        /// The default value is
        /// <see langword="false"/>
        /// .</p>
        /// </remarks>
        public bool enable_global_context_dfa = false;

        public bool optimize_unique_closure = true;

        public bool optimize_ll1 = true;

        [System.ObsoleteAttribute(@"This flag is not currently used by the ATN simulator.")]
        public bool optimize_hidden_conflicted_configs = false;

        public bool optimize_tail_calls = true;

        public bool tail_call_preserves_sll = true;

        public bool treat_sllk1_conflict_as_ambiguity = false;

        [Nullable]
        private readonly Antlr4.Runtime.Parser _parser;

        /// <summary>
        /// When
        /// <see langword="true"/>
        /// , ambiguous alternatives are reported when they are
        /// encountered within
        /// <see cref="ExecATN(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.ITokenStream, int, SimulatorState)"/>
        /// . When
        /// <see langword="false"/>
        /// , these messages
        /// are suppressed. The default is
        /// <see langword="false"/>
        /// .
        /// <p/>
        /// When messages about ambiguous alternatives are not required, setting this
        /// to
        /// <see langword="false"/>
        /// enables additional internal optimizations which may lose
        /// this information.
        /// </summary>
		public bool reportAmbiguities = false;

        /// <summary>
        /// By default we do full context-sensitive LL(*) parsing not
        /// Strong LL(*) parsing.
        /// </summary>
        /// <remarks>
        /// By default we do full context-sensitive LL(*) parsing not
        /// Strong LL(*) parsing. If we fail with Strong LL(*) we
        /// try full LL(*). That means we rewind and use context information
        /// when closure operations fall off the end of the rule that
        /// holds the decision were evaluating.
        /// </remarks>
        protected internal bool userWantsCtxSensitive = true;

        private DFA dfa;

        /// <summary>Testing only!</summary>
        public ParserATNSimulator(ATN atn)
            : this(null, atn)
        {
        }

        public ParserATNSimulator(Antlr4.Runtime.Parser parser, ATN atn)
            : base(atn)
        {
            this._parser = parser;
        }

        public Antlr4.Runtime.Atn.PredictionMode PredictionMode
        {
            get
            {
                return predictionMode;
            }
            set
            {
                Antlr4.Runtime.Atn.PredictionMode predictionMode = value;
                this.predictionMode = predictionMode;
            }
        }

        public override void Reset()
        {
        }

        public virtual int AdaptivePredict(ITokenStream input, int decision, ParserRuleContext outerContext)
        {
            return AdaptivePredict(input, decision, outerContext, false);
        }

        public virtual int AdaptivePredict(ITokenStream input, int decision, ParserRuleContext outerContext, bool useContext)
        {
            DFA dfa = atn.decisionToDFA[decision];
            System.Diagnostics.Debug.Assert(dfa != null);
            if (optimize_ll1 && !dfa.IsPrecedenceDfa && !dfa.IsEmpty)
            {
                int ll_1 = input.La(1);
                if (ll_1 >= 0 && ll_1 <= short.MaxValue)
                {
                    int key = (decision << 16) + ll_1;
                    int alt;
                    if (atn.LL1Table.TryGetValue(key, out alt))
                    {
                        return alt;
                    }
                }
            }
            this.dfa = dfa;
            if (force_global_context)
            {
                useContext = true;
            }
            else
            {
                if (!always_try_local_context)
                {
                    useContext |= dfa.IsContextSensitive;
                }
            }
            userWantsCtxSensitive = useContext || (predictionMode != Antlr4.Runtime.Atn.PredictionMode.Sll && outerContext != null);
            if (outerContext == null)
            {
                outerContext = ParserRuleContext.EmptyContext;
            }
            SimulatorState state = null;
            if (!dfa.IsEmpty)
            {
                state = GetStartState(dfa, input, outerContext, useContext);
            }
            if (state == null)
            {
                if (outerContext == null)
                {
                    outerContext = ParserRuleContext.EmptyContext;
                }
                state = ComputeStartState(dfa, outerContext, useContext);
            }
            int m = input.Mark();
            int index = input.Index;
            try
            {
                int alt = ExecDFA(dfa, input, index, state);
                return alt;
            }
            finally
            {
                this.dfa = null;
                input.Seek(index);
                input.Release(m);
            }
        }

        protected internal virtual SimulatorState GetStartState(DFA dfa, ITokenStream input, ParserRuleContext outerContext, bool useContext)
        {
            if (!useContext)
            {
                if (dfa.IsPrecedenceDfa)
                {
                    // the start state for a precedence DFA depends on the current
                    // parser precedence, and is provided by a DFA method.
                    DFAState state = dfa.GetPrecedenceStartState(_parser.Precedence, false);
                    if (state == null)
                    {
                        return null;
                    }
                    return new SimulatorState(outerContext, state, false, outerContext);
                }
                else
                {
                    if (dfa.s0.Get() == null)
                    {
                        return null;
                    }
                    return new SimulatorState(outerContext, dfa.s0.Get(), false, outerContext);
                }
            }
            if (!enable_global_context_dfa)
            {
                return null;
            }
            ParserRuleContext remainingContext = outerContext;
            System.Diagnostics.Debug.Assert(outerContext != null);
            DFAState s0;
            if (dfa.IsPrecedenceDfa)
            {
                s0 = dfa.GetPrecedenceStartState(_parser.Precedence, true);
            }
            else
            {
                s0 = dfa.s0full.Get();
            }
            while (remainingContext != null && s0 != null && s0.IsContextSensitive)
            {
                remainingContext = SkipTailCalls(remainingContext);
                s0 = s0.GetContextTarget(GetReturnState(remainingContext));
                if (remainingContext.IsEmpty)
                {
                    System.Diagnostics.Debug.Assert(s0 == null || !s0.IsContextSensitive);
                }
                else
                {
                    remainingContext = ((ParserRuleContext)remainingContext.Parent);
                }
            }
            if (s0 == null)
            {
                return null;
            }
            return new SimulatorState(outerContext, s0, useContext, remainingContext);
        }

        protected internal virtual int ExecDFA(DFA dfa, ITokenStream input, int startIndex, SimulatorState state)
        {
            ParserRuleContext outerContext = state.outerContext;
            DFAState s = state.s0;
            int t = input.La(1);
            ParserRuleContext remainingOuterContext = state.remainingOuterContext;
            while (true)
            {
                if (state.useContext)
                {
                    while (s.IsContextSymbol(t))
                    {
                        DFAState next = null;
                        if (remainingOuterContext != null)
                        {
                            remainingOuterContext = SkipTailCalls(remainingOuterContext);
                            next = s.GetContextTarget(GetReturnState(remainingOuterContext));
                        }
                        if (next == null)
                        {
                            // fail over to ATN
                            SimulatorState initialState = new SimulatorState(state.outerContext, s, state.useContext, remainingOuterContext);
                            return ExecATN(dfa, input, startIndex, initialState);
                        }
                        System.Diagnostics.Debug.Assert(remainingOuterContext != null);
                        remainingOuterContext = ((ParserRuleContext)remainingOuterContext.Parent);
                        s = next;
                    }
                }
                if (IsAcceptState(s, state.useContext))
                {
                    if (s.predicates != null)
                    {
                    }
                    // keep going unless we're at EOF or state only has one alt number
                    // mentioned in configs; check if something else could match
                    // TODO: don't we always stop? only lexer would keep going
                    // TODO: v3 dfa don't do this.
                    break;
                }
                // t is not updated if one of these states is reached
                System.Diagnostics.Debug.Assert(!IsAcceptState(s, state.useContext));
                // if no edge, pop over to ATN interpreter, update DFA and return
                DFAState target = GetExistingTargetState(s, t);
                if (target == null)
                {
#if !PORTABLE
					#pragma warning disable 162, 429
					if (dfa_debug && t >= 0)
                    {
                        System.Console.Out.WriteLine("no edge for " + _parser.Vocabulary.GetDisplayName(t));
                    }
					#pragma warning restore 162, 429
#endif
                    int alt;
                    SimulatorState initialState = new SimulatorState(outerContext, s, state.useContext, remainingOuterContext);
                    alt = ExecATN(dfa, input, startIndex, initialState);
                    //dump(dfa);
                    // action already executed
                    return alt;
                }
                else
                {
                    // we've updated DFA, exec'd action, and have our deepest answer
                    if (target == Error)
                    {
                        SimulatorState errorState = new SimulatorState(outerContext, s, state.useContext, remainingOuterContext);
                        return HandleNoViableAlt(input, startIndex, errorState);
                    }
                }
                s = target;
                if (!IsAcceptState(s, state.useContext) && t != IntStreamConstants.Eof)
                {
                    input.Consume();
                    t = input.La(1);
                }
            }
            //		if ( acceptState==null ) {
            //			if ( debug ) System.out.println("!!! no viable alt in dfa");
            //			return -1;
            //		}
            if (!state.useContext && s.configs.ConflictInformation != null)
            {
                if (dfa.atnStartState is DecisionState)
                {
                    if (!userWantsCtxSensitive || (!s.configs.DipsIntoOuterContext && s.configs.IsExactConflict) || (treat_sllk1_conflict_as_ambiguity && input.Index == startIndex))
                    {
                    }
                    else
                    {
                        // we don't report the ambiguity again
                        //if ( !acceptState.configset.hasSemanticContext() ) {
                        //	reportAmbiguity(dfa, acceptState, startIndex, input.index(), acceptState.configset.getConflictingAlts(), acceptState.configset);
                        //}
                        System.Diagnostics.Debug.Assert(!state.useContext);
                        // Before attempting full context prediction, check to see if there are
                        // disambiguating or validating predicates to evaluate which allow an
                        // immediate decision
                        BitSet conflictingAlts = null;
                        DFAState.PredPrediction[] predicates = s.predicates;
                        if (predicates != null)
                        {
                            int conflictIndex = input.Index;
                            if (conflictIndex != startIndex)
                            {
                                input.Seek(startIndex);
                            }
                            conflictingAlts = EvalSemanticContext(predicates, outerContext, true);
                            if (conflictingAlts.Cardinality() == 1)
                            {
                                return conflictingAlts.NextSetBit(0);
                            }
                            if (conflictIndex != startIndex)
                            {
                                // restore the index so reporting the fallback to full
                                // context occurs with the index at the correct spot
                                input.Seek(conflictIndex);
                            }
                        }
                        if (reportAmbiguities)
                        {
                            SimulatorState conflictState = new SimulatorState(outerContext, s, state.useContext, remainingOuterContext);
                            ReportAttemptingFullContext(dfa, conflictingAlts, conflictState, startIndex, input.Index);
                        }
                        input.Seek(startIndex);
                        return AdaptivePredict(input, dfa.decision, outerContext, true);
                    }
                }
            }
            // Before jumping to prediction, check to see if there are
            // disambiguating or validating predicates to evaluate
            DFAState.PredPrediction[] predicates_1 = s.predicates;
            if (predicates_1 != null)
            {
                int stopIndex = input.Index;
                if (startIndex != stopIndex)
                {
                    input.Seek(startIndex);
                }
                BitSet alts = EvalSemanticContext(predicates_1, outerContext, reportAmbiguities && predictionMode == Antlr4.Runtime.Atn.PredictionMode.LlExactAmbigDetection);
                switch (alts.Cardinality())
                {
                    case 0:
                    {
                        throw NoViableAlt(input, outerContext, s.configs, startIndex);
                    }

                    case 1:
                    {
                        return alts.NextSetBit(0);
                    }

                    default:
                    {
                        // report ambiguity after predicate evaluation to make sure the correct
                        // set of ambig alts is reported.
                        if (startIndex != stopIndex)
                        {
                            input.Seek(stopIndex);
                        }
                        ReportAmbiguity(dfa, s, startIndex, stopIndex, s.configs.IsExactConflict, alts, s.configs);
                        return alts.NextSetBit(0);
                    }
                }
            }
            return s.Prediction;
        }

        /// <summary>
        /// Determines if a particular DFA state should be treated as an accept state
        /// for the current prediction mode.
        /// </summary>
        /// <remarks>
        /// Determines if a particular DFA state should be treated as an accept state
        /// for the current prediction mode. In addition to the
        /// <paramref name="useContext"/>
        /// parameter, the
        /// <see cref="PredictionMode()"/>
        /// method provides the
        /// prediction mode controlling the prediction algorithm as a whole.
        /// <p>
        /// The default implementation simply returns the value of
        /// <see cref="Antlr4.Runtime.Dfa.DFAState.IsAcceptState()"/>
        /// except for conflict states when
        /// <paramref name="useContext"/>
        /// is
        /// <see langword="true"/>
        /// and
        /// <see cref="PredictionMode()"/>
        /// is
        /// <see cref="Antlr4.Runtime.Atn.PredictionMode.LlExactAmbigDetection"/>
        /// . In that case, only
        /// conflict states where
        /// <see cref="ATNConfigSet.IsExactConflict()"/>
        /// is
        /// <see langword="true"/>
        /// are considered accept states.
        /// </p>
        /// </remarks>
        /// <param name="state">The DFA state to check.</param>
        /// <param name="useContext">
        /// 
        /// <see langword="true"/>
        /// if the prediction algorithm is currently
        /// considering the full parser context; otherwise,
        /// <see langword="false"/>
        /// if the
        /// algorithm is currently performing a local context prediction.
        /// </param>
        /// <returns>
        /// 
        /// <see langword="true"/>
        /// if the specified
        /// <paramref name="state"/>
        /// is an accept state;
        /// otherwise,
        /// <see langword="false"/>
        /// .
        /// </returns>
        protected internal virtual bool IsAcceptState(DFAState state, bool useContext)
        {
            if (!state.IsAcceptState)
            {
                return false;
            }
            if (state.configs.ConflictingAlts == null)
            {
                // unambiguous
                return true;
            }
            // More picky when we need exact conflicts
            if (useContext && predictionMode == Antlr4.Runtime.Atn.PredictionMode.LlExactAmbigDetection)
            {
                return state.configs.IsExactConflict;
            }
            return true;
        }

        /// <summary>
        /// Performs ATN simulation to compute a predicted alternative based
        /// upon the remaining input, but also updates the DFA cache to avoid
        /// having to traverse the ATN again for the same input sequence.
        /// </summary>
        /// <remarks>
        /// Performs ATN simulation to compute a predicted alternative based
        /// upon the remaining input, but also updates the DFA cache to avoid
        /// having to traverse the ATN again for the same input sequence.
        /// There are some key conditions we're looking for after computing a new
        /// set of ATN configs (proposed DFA state):
        /// if the set is empty, there is no viable alternative for current symbol
        /// does the state uniquely predict an alternative?
        /// does the state have a conflict that would prevent us from
        /// putting it on the work list?
        /// if in non-greedy decision is there a config at a rule stop state?
        /// We also have some key operations to do:
        /// add an edge from previous DFA state to potentially new DFA state, D,
        /// upon current symbol but only if adding to work list, which means in all
        /// cases except no viable alternative (and possibly non-greedy decisions?)
        /// collecting predicates and adding semantic context to DFA accept states
        /// adding rule context to context-sensitive DFA accept states
        /// consuming an input symbol
        /// reporting a conflict
        /// reporting an ambiguity
        /// reporting a context sensitivity
        /// reporting insufficient predicates
        /// We should isolate those operations, which are side-effecting, to the
        /// main work loop. We can isolate lots of code into other functions, but
        /// they should be side effect free. They can return package that
        /// indicates whether we should report something, whether we need to add a
        /// DFA edge, whether we need to augment accept state with semantic
        /// context or rule invocation context. Actually, it seems like we always
        /// add predicates if they exist, so that can simply be done in the main
        /// loop for any accept state creation or modification request.
        /// cover these cases:
        /// dead end
        /// single alt
        /// single alt + preds
        /// conflict
        /// conflict + preds
        /// TODO: greedy + those
        /// </remarks>
        protected internal virtual int ExecATN(DFA dfa, ITokenStream input, int startIndex, SimulatorState initialState)
        {
            ParserRuleContext outerContext = initialState.outerContext;
			bool useContext = initialState.useContext;
            int t = input.La(1);
            SimulatorState previous = initialState;
            PredictionContextCache contextCache = new PredictionContextCache();
            while (true)
            {
                // while more work
                SimulatorState nextState = ComputeReachSet(dfa, previous, t, contextCache);
                if (nextState == null)
                {
                    AddDFAEdge(previous.s0, input.La(1), Error);
                    return HandleNoViableAlt(input, startIndex, previous);
                }
                DFAState D = nextState.s0;
                // predicted alt => accept state
                System.Diagnostics.Debug.Assert(D.IsAcceptState || D.Prediction == ATN.InvalidAltNumber);
                // conflicted => accept state
                System.Diagnostics.Debug.Assert(D.IsAcceptState || D.configs.ConflictInformation == null);
                if (IsAcceptState(D, useContext))
                {
                    BitSet conflictingAlts = D.configs.ConflictingAlts;
                    int predictedAlt = conflictingAlts == null ? D.Prediction : ATN.InvalidAltNumber;
                    if (predictedAlt != ATN.InvalidAltNumber)
                    {
                        if (optimize_ll1 && input.Index == startIndex && !dfa.IsPrecedenceDfa && nextState.outerContext == nextState.remainingOuterContext && dfa.decision >= 0 && !D.configs.HasSemanticContext)
                        {
                            if (t >= 0 && t <= short.MaxValue)
                            {
                                int key = (dfa.decision << 16) + t;
                                atn.LL1Table[key] = predictedAlt;
                            }
                        }
                        if (useContext && always_try_local_context)
                        {
                            ReportContextSensitivity(dfa, predictedAlt, nextState, startIndex, input.Index);
                        }
                    }
                    predictedAlt = D.Prediction;
                    //				int k = input.index() - startIndex + 1; // how much input we used
                    //				System.out.println("used k="+k);
                    bool attemptFullContext = conflictingAlts != null && userWantsCtxSensitive;
                    if (attemptFullContext)
                    {
                        // Only exact conflicts are known to be ambiguous when local
                        // prediction does not step out of the decision rule.
                        attemptFullContext = !useContext && (D.configs.DipsIntoOuterContext || !D.configs.IsExactConflict) && (!treat_sllk1_conflict_as_ambiguity || input.Index != startIndex);
                    }
                    if (D.configs.HasSemanticContext)
                    {
                        DFAState.PredPrediction[] predPredictions = D.predicates;
                        if (predPredictions != null)
                        {
                            int conflictIndex = input.Index;
                            if (conflictIndex != startIndex)
                            {
                                input.Seek(startIndex);
                            }
                            // use complete evaluation here if we'll want to retry with full context if still ambiguous
                            conflictingAlts = EvalSemanticContext(predPredictions, outerContext, attemptFullContext || reportAmbiguities);
                            switch (conflictingAlts.Cardinality())
                            {
                                case 0:
                                {
                                    throw NoViableAlt(input, outerContext, D.configs, startIndex);
                                }

                                case 1:
                                {
                                    return conflictingAlts.NextSetBit(0);
                                }

                                default:
                                {
                                    break;
                                }
                            }
                            if (conflictIndex != startIndex)
                            {
                                // restore the index so reporting the fallback to full
                                // context occurs with the index at the correct spot
                                input.Seek(conflictIndex);
                            }
                        }
                    }
                    if (!attemptFullContext)
                    {
                        if (conflictingAlts != null)
                        {
                            if (reportAmbiguities && conflictingAlts.Cardinality() > 1)
                            {
                                ReportAmbiguity(dfa, D, startIndex, input.Index, D.configs.IsExactConflict, conflictingAlts, D.configs);
                            }
                            predictedAlt = conflictingAlts.NextSetBit(0);
                        }
                        return predictedAlt;
                    }
                    else
                    {
                        System.Diagnostics.Debug.Assert(!useContext);
                        System.Diagnostics.Debug.Assert(IsAcceptState(D, false));
                        SimulatorState fullContextState = ComputeStartState(dfa, outerContext, true);
                        if (reportAmbiguities)
                        {
                            ReportAttemptingFullContext(dfa, conflictingAlts, nextState, startIndex, input.Index);
                        }
                        input.Seek(startIndex);
                        return ExecATN(dfa, input, startIndex, fullContextState);
                    }
                }
                previous = nextState;
                if (t != IntStreamConstants.Eof)
                {
                    input.Consume();
                    t = input.La(1);
                }
            }
        }

        /// <summary>
        /// This method is used to improve the localization of error messages by
        /// choosing an alternative rather than throwing a
        /// <see cref="Antlr4.Runtime.NoViableAltException"/>
        /// in particular prediction scenarios where the
        /// <see cref="ATNSimulator.Error"/>
        /// state was reached during ATN simulation.
        /// <p>
        /// The default implementation of this method uses the following
        /// algorithm to identify an ATN configuration which successfully parsed the
        /// decision entry rule. Choosing such an alternative ensures that the
        /// <see cref="Antlr4.Runtime.ParserRuleContext"/>
        /// returned by the calling rule will be complete
        /// and valid, and the syntax error will be reported later at a more
        /// localized location.</p>
        /// <ul>
        /// <li>If no configuration in
        /// <c>configs</c>
        /// reached the end of the
        /// decision rule, return
        /// <see cref="ATN.InvalidAltNumber"/>
        /// .</li>
        /// <li>If all configurations in
        /// <c>configs</c>
        /// which reached the end of the
        /// decision rule predict the same alternative, return that alternative.</li>
        /// <li>If the configurations in
        /// <c>configs</c>
        /// which reached the end of the
        /// decision rule predict multiple alternatives (call this <em>S</em>),
        /// choose an alternative in the following order.
        /// <ol>
        /// <li>Filter the configurations in
        /// <c>configs</c>
        /// to only those
        /// configurations which remain viable after evaluating semantic predicates.
        /// If the set of these filtered configurations which also reached the end of
        /// the decision rule is not empty, return the minimum alternative
        /// represented in this set.</li>
        /// <li>Otherwise, choose the minimum alternative in <em>S</em>.</li>
        /// </ol>
        /// </li>
        /// </ul>
        /// <p>
        /// In some scenarios, the algorithm described above could predict an
        /// alternative which will result in a
        /// <see cref="Antlr4.Runtime.FailedPredicateException"/>
        /// in
        /// parser. Specifically, this could occur if the <em>only</em> configuration
        /// capable of successfully parsing to the end of the decision rule is
        /// blocked by a semantic predicate. By choosing this alternative within
        /// <see cref="AdaptivePredict(Antlr4.Runtime.ITokenStream, int, Antlr4.Runtime.ParserRuleContext)"/>
        /// instead of throwing a
        /// <see cref="Antlr4.Runtime.NoViableAltException"/>
        /// , the resulting
        /// <see cref="Antlr4.Runtime.FailedPredicateException"/>
        /// in the parser will identify the specific
        /// predicate which is preventing the parser from successfully parsing the
        /// decision rule, which helps developers identify and correct logic errors
        /// in semantic predicates.
        /// </p>
        /// </summary>
        /// <param name="input">
        /// The input
        /// <see cref="Antlr4.Runtime.ITokenStream"/>
        /// </param>
        /// <param name="startIndex">
        /// The start index for the current prediction, which is
        /// the input index where any semantic context in
        /// <c>configs</c>
        /// should be
        /// evaluated
        /// </param>
        /// <param name="previous">
        /// The ATN simulation state immediately before the
        /// <see cref="ATNSimulator.Error"/>
        /// state was reached
        /// </param>
        /// <returns>
        /// The value to return from
        /// <see cref="AdaptivePredict(Antlr4.Runtime.ITokenStream, int, Antlr4.Runtime.ParserRuleContext)"/>
        /// , or
        /// <see cref="ATN.InvalidAltNumber"/>
        /// if a suitable alternative was not
        /// identified and
        /// <see cref="AdaptivePredict(Antlr4.Runtime.ITokenStream, int, Antlr4.Runtime.ParserRuleContext)"/>
        /// should report an error instead.
        /// </returns>
        protected internal virtual int HandleNoViableAlt(ITokenStream input, int startIndex, SimulatorState previous)
        {
            if (previous.s0 != null)
            {
                BitSet alts = new BitSet();
                int maxAlt = 0;
                foreach (ATNConfig config in previous.s0.configs)
                {
                    if (config.ReachesIntoOuterContext || config.State is RuleStopState)
                    {
                        alts.Set(config.Alt);
                        maxAlt = Math.Max(maxAlt, config.Alt);
                    }
                }
                switch (alts.Cardinality())
                {
                    case 0:
                    {
                        break;
                    }

                    case 1:
                    {
                        return alts.NextSetBit(0);
                    }

                    default:
                    {
                        if (!previous.s0.configs.HasSemanticContext)
                        {
                            // configs doesn't contain any predicates, so the predicate
                            // filtering code below would be pointless
                            return alts.NextSetBit(0);
                        }
                        ATNConfigSet filteredConfigs = new ATNConfigSet();
                        foreach (ATNConfig config_1 in previous.s0.configs)
                        {
                            if (config_1.ReachesIntoOuterContext || config_1.State is RuleStopState)
                            {
                                filteredConfigs.Add(config_1);
                            }
                        }
                        SemanticContext[] altToPred = GetPredsForAmbigAlts(alts, filteredConfigs, maxAlt);
                        if (altToPred != null)
                        {
                            DFAState.PredPrediction[] predicates = GetPredicatePredictions(alts, altToPred);
                            if (predicates != null)
                            {
                                int stopIndex = input.Index;
                                try
                                {
                                    input.Seek(startIndex);
                                    BitSet filteredAlts = EvalSemanticContext(predicates, previous.outerContext, false);
                                    if (!filteredAlts.IsEmpty())
                                    {
                                        return filteredAlts.NextSetBit(0);
                                    }
                                }
                                finally
                                {
                                    input.Seek(stopIndex);
                                }
                            }
                        }
                        return alts.NextSetBit(0);
                    }
                }
            }
            throw NoViableAlt(input, previous.outerContext, previous.s0.configs, startIndex);
        }

        protected internal virtual SimulatorState ComputeReachSet(DFA dfa, SimulatorState previous, int t, PredictionContextCache contextCache)
        {
            bool useContext = previous.useContext;
            ParserRuleContext remainingGlobalContext = previous.remainingOuterContext;
            DFAState s = previous.s0;
            if (useContext)
            {
                while (s.IsContextSymbol(t))
                {
                    DFAState next = null;
                    if (remainingGlobalContext != null)
                    {
                        remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
                        next = s.GetContextTarget(GetReturnState(remainingGlobalContext));
                    }
                    if (next == null)
                    {
                        break;
                    }
                    System.Diagnostics.Debug.Assert(remainingGlobalContext != null);
                    remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.Parent);
                    s = next;
                }
            }
            System.Diagnostics.Debug.Assert(!IsAcceptState(s, useContext));
            if (IsAcceptState(s, useContext))
            {
                return new SimulatorState(previous.outerContext, s, useContext, remainingGlobalContext);
            }
            DFAState s0 = s;
            DFAState target = GetExistingTargetState(s0, t);
            if (target == null)
            {
                Tuple<DFAState, ParserRuleContext> result = ComputeTargetState(dfa, s0, remainingGlobalContext, t, useContext, contextCache);
                target = result.Item1;
                remainingGlobalContext = result.Item2;
            }
            if (target == Error)
            {
                return null;
            }
            System.Diagnostics.Debug.Assert(!useContext || !target.configs.DipsIntoOuterContext);
            return new SimulatorState(previous.outerContext, target, useContext, remainingGlobalContext);
        }

        /// <summary>Get an existing target state for an edge in the DFA.</summary>
        /// <remarks>
        /// Get an existing target state for an edge in the DFA. If the target state
        /// for the edge has not yet been computed or is otherwise not available,
        /// this method returns
        /// <see langword="null"/>
        /// .
        /// </remarks>
        /// <param name="s">The current DFA state</param>
        /// <param name="t">The next input symbol</param>
        /// <returns>
        /// The existing target DFA state for the given input symbol
        /// <paramref name="t"/>
        /// , or
        /// <see langword="null"/>
        /// if the target state for this edge is not
        /// already cached
        /// </returns>
        [return: Nullable]
        protected internal virtual DFAState GetExistingTargetState(DFAState s, int t)
        {
            return s.GetTarget(t);
        }

        /// <summary>
        /// Compute a target state for an edge in the DFA, and attempt to add the
        /// computed state and corresponding edge to the DFA.
        /// </summary>
        /// <remarks>
        /// Compute a target state for an edge in the DFA, and attempt to add the
        /// computed state and corresponding edge to the DFA.
        /// </remarks>
        /// <param name="dfa"/>
        /// <param name="s">The current DFA state</param>
        /// <param name="remainingGlobalContext"/>
        /// <param name="t">The next input symbol</param>
        /// <param name="useContext"/>
        /// <param name="contextCache"/>
        /// <returns>
        /// The computed target DFA state for the given input symbol
        /// <paramref name="t"/>
        /// . If
        /// <paramref name="t"/>
        /// does not lead to a valid DFA state, this method
        /// returns
        /// <see cref="ATNSimulator.Error"/>
        /// .
        /// </returns>
        [return: NotNull]
        protected internal virtual Tuple<DFAState, ParserRuleContext> ComputeTargetState(DFA dfa, DFAState s, ParserRuleContext remainingGlobalContext, int t, bool useContext, PredictionContextCache contextCache)
        {
            IList<ATNConfig> closureConfigs = new List<ATNConfig>(s.configs);
            List<int> contextElements = null;
            ATNConfigSet reach = new ATNConfigSet();
            bool stepIntoGlobal;
            do
            {
                bool hasMoreContext = !useContext || remainingGlobalContext != null;
                if (!hasMoreContext)
                {
                    reach.IsOutermostConfigSet = true;
                }
                ATNConfigSet reachIntermediate = new ATNConfigSet();
                IList<ATNConfig> skippedStopStates = null;
                foreach (ATNConfig c in closureConfigs)
                {
                    if (c.State is RuleStopState)
                    {
                        System.Diagnostics.Debug.Assert(c.Context.IsEmpty);
                        if (useContext && !c.ReachesIntoOuterContext || t == IntStreamConstants.Eof)
                        {
                            if (skippedStopStates == null)
                            {
                                skippedStopStates = new List<ATNConfig>();
                            }
                            skippedStopStates.Add(c);
                        }
                        continue;
                    }
                    int n = c.State.NumberOfOptimizedTransitions;
                    for (int ti = 0; ti < n; ti++)
                    {
                        // for each optimized transition
                        Transition trans = c.State.GetOptimizedTransition(ti);
                        ATNState target = GetReachableTarget(c, trans, t);
                        if (target != null)
                        {
                            reachIntermediate.Add(c.Transform(target, false), contextCache);
                        }
                    }
                }
                if (optimize_unique_closure && skippedStopStates == null && t != TokenConstants.Eof && reachIntermediate.UniqueAlt != ATN.InvalidAltNumber)
                {
                    reachIntermediate.IsOutermostConfigSet = reach.IsOutermostConfigSet;
                    reach = reachIntermediate;
                    break;
                }
                bool collectPredicates = false;
                bool treatEofAsEpsilon = t == TokenConstants.Eof;
                Closure(reachIntermediate, reach, collectPredicates, hasMoreContext, contextCache, treatEofAsEpsilon);
                stepIntoGlobal = reach.DipsIntoOuterContext;
                if (t == IntStreamConstants.Eof)
                {
                    reach = RemoveAllConfigsNotInRuleStopState(reach, contextCache);
                }
                if (skippedStopStates != null && (!useContext || !Antlr4.Runtime.Atn.PredictionMode.HasConfigInRuleStopState(reach)))
                {
                    System.Diagnostics.Debug.Assert(skippedStopStates.Count > 0);
                    foreach (ATNConfig c_1 in skippedStopStates)
                    {
                        reach.Add(c_1, contextCache);
                    }
                }
                if (useContext && stepIntoGlobal)
                {
                    reach.Clear();
                    remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
                    int nextContextElement = GetReturnState(remainingGlobalContext);
                    if (contextElements == null)
                    {
                        contextElements = new List<int>();
                    }
                    if (remainingGlobalContext.IsEmpty)
                    {
                        remainingGlobalContext = null;
                    }
                    else
                    {
                        remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.Parent);
                    }
                    contextElements.Add(nextContextElement);
                    if (nextContextElement != PredictionContext.EmptyFullStateKey)
                    {
                        for (int i = 0; i < closureConfigs.Count; i++)
                        {
                            closureConfigs[i] = closureConfigs[i].AppendContext(nextContextElement, contextCache);
                        }
                    }
                }
            }
            while (useContext && stepIntoGlobal);
            if (reach.IsEmpty())
            {
                AddDFAEdge(s, t, Error);
                return Tuple.Create(Error, remainingGlobalContext);
            }
            DFAState result = AddDFAEdge(dfa, s, t, contextElements, reach, contextCache);
            return Tuple.Create(result, remainingGlobalContext);
        }

        /// <summary>
        /// Return a configuration set containing only the configurations from
        /// <paramref name="configs"/>
        /// which are in a
        /// <see cref="RuleStopState"/>
        /// . If all
        /// configurations in
        /// <paramref name="configs"/>
        /// are already in a rule stop state, this
        /// method simply returns
        /// <paramref name="configs"/>
        /// .
        /// </summary>
        /// <param name="configs">the configuration set to update</param>
        /// <param name="contextCache">
        /// the
        /// <see cref="PredictionContext"/>
        /// cache
        /// </param>
        /// <returns>
        /// 
        /// <paramref name="configs"/>
        /// if all configurations in
        /// <paramref name="configs"/>
        /// are in a
        /// rule stop state, otherwise return a new configuration set containing only
        /// the configurations from
        /// <paramref name="configs"/>
        /// which are in a rule stop state
        /// </returns>
        [return: NotNull]
        protected internal virtual ATNConfigSet RemoveAllConfigsNotInRuleStopState(ATNConfigSet configs, PredictionContextCache contextCache)
        {
            if (Antlr4.Runtime.Atn.PredictionMode.AllConfigsInRuleStopStates(configs))
            {
                return configs;
            }
            ATNConfigSet result = new ATNConfigSet();
            foreach (ATNConfig config in configs)
            {
                if (!(config.State is RuleStopState))
                {
                    continue;
                }
                result.Add(config, contextCache);
            }
            return result;
        }

        [return: NotNull]
        protected internal virtual SimulatorState ComputeStartState(DFA dfa, ParserRuleContext globalContext, bool useContext)
        {
            DFAState s0 = dfa.IsPrecedenceDfa ? dfa.GetPrecedenceStartState(_parser.Precedence, useContext) : useContext ? dfa.s0full.Get() : dfa.s0.Get();
            if (s0 != null)
            {
                if (!useContext)
                {
                    return new SimulatorState(globalContext, s0, useContext, globalContext);
                }
                s0.SetContextSensitive(atn);
            }
            ATNState p = dfa.atnStartState;
            int previousContext = 0;
            ParserRuleContext remainingGlobalContext = globalContext;
            PredictionContext initialContext = useContext ? PredictionContext.EmptyFull : PredictionContext.EmptyLocal;
            // always at least the implicit call to start rule
            PredictionContextCache contextCache = new PredictionContextCache();
            if (useContext)
            {
                if (!enable_global_context_dfa)
                {
                    while (remainingGlobalContext != null)
                    {
                        if (remainingGlobalContext.IsEmpty)
                        {
                            previousContext = PredictionContext.EmptyFullStateKey;
                            remainingGlobalContext = null;
                        }
                        else
                        {
                            previousContext = GetReturnState(remainingGlobalContext);
                            initialContext = initialContext.AppendContext(previousContext, contextCache);
                            remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.Parent);
                        }
                    }
                }
                while (s0 != null && s0.IsContextSensitive && remainingGlobalContext != null)
                {
                    DFAState next;
                    remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
                    if (remainingGlobalContext.IsEmpty)
                    {
                        next = s0.GetContextTarget(PredictionContext.EmptyFullStateKey);
                        previousContext = PredictionContext.EmptyFullStateKey;
                        remainingGlobalContext = null;
                    }
                    else
                    {
                        previousContext = GetReturnState(remainingGlobalContext);
                        next = s0.GetContextTarget(previousContext);
                        initialContext = initialContext.AppendContext(previousContext, contextCache);
                        remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.Parent);
                    }
                    if (next == null)
                    {
                        break;
                    }
                    s0 = next;
                }
            }
            if (s0 != null && !s0.IsContextSensitive)
            {
                return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
            }
            ATNConfigSet configs = new ATNConfigSet();
            while (true)
            {
                ATNConfigSet reachIntermediate = new ATNConfigSet();
                int n = p.NumberOfTransitions;
                for (int ti = 0; ti < n; ti++)
                {
                    // for each transition
                    ATNState target = p.Transition(ti).target;
                    reachIntermediate.Add(ATNConfig.Create(target, ti + 1, initialContext));
                }
                bool hasMoreContext = remainingGlobalContext != null;
                if (!hasMoreContext)
                {
                    configs.IsOutermostConfigSet = true;
                }
                if (!useContext || enable_global_context_dfa)
                {
                    if (!dfa.IsPrecedenceDfa && dfa.atnStartState is StarLoopEntryState)
                    {
                        if (((StarLoopEntryState)dfa.atnStartState).precedenceRuleDecision)
                        {
                            dfa.IsPrecedenceDfa = true;
                        }
                    }
                }
                bool collectPredicates = true;
                Closure(reachIntermediate, configs, collectPredicates, hasMoreContext, contextCache, false);
                bool stepIntoGlobal = configs.DipsIntoOuterContext;
                DFAState next;
                if (useContext && !enable_global_context_dfa)
                {
                    s0 = AddDFAState(dfa, configs, contextCache);
                    break;
                }
                else
                {
                    if (s0 == null)
                    {
                        if (!dfa.IsPrecedenceDfa)
                        {
                            AtomicReference<DFAState> reference = useContext ? dfa.s0full : dfa.s0;
                            next = AddDFAState(dfa, configs, contextCache);
                            if (!reference.CompareAndSet(null, next))
                            {
                                next = reference.Get();
                            }
                        }
                        else
                        {
                            configs = ApplyPrecedenceFilter(configs, globalContext, contextCache);
                            next = AddDFAState(dfa, configs, contextCache);
                            dfa.SetPrecedenceStartState(_parser.Precedence, useContext, next);
                        }
                    }
                    else
                    {
                        if (dfa.IsPrecedenceDfa)
                        {
                            configs = ApplyPrecedenceFilter(configs, globalContext, contextCache);
                        }
                        next = AddDFAState(dfa, configs, contextCache);
                        s0.SetContextTarget(previousContext, next);
                    }
                }
                s0 = next;
                if (!useContext || !stepIntoGlobal)
                {
                    break;
                }
                // TODO: make sure it distinguishes empty stack states
                next.SetContextSensitive(atn);
                configs.Clear();
                remainingGlobalContext = SkipTailCalls(remainingGlobalContext);
                int nextContextElement = GetReturnState(remainingGlobalContext);
                if (remainingGlobalContext.IsEmpty)
                {
                    remainingGlobalContext = null;
                }
                else
                {
                    remainingGlobalContext = ((ParserRuleContext)remainingGlobalContext.Parent);
                }
                if (nextContextElement != PredictionContext.EmptyFullStateKey)
                {
                    initialContext = initialContext.AppendContext(nextContextElement, contextCache);
                }
                previousContext = nextContextElement;
            }
            return new SimulatorState(globalContext, s0, useContext, remainingGlobalContext);
        }

        /// <summary>
        /// This method transforms the start state computed by
        /// <see cref="ComputeStartState(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.ParserRuleContext, bool)"/>
        /// to the special start state used by a
        /// precedence DFA for a particular precedence value. The transformation
        /// process applies the following changes to the start state's configuration
        /// set.
        /// <ol>
        /// <li>Evaluate the precedence predicates for each configuration using
        /// <see cref="SemanticContext.EvalPrecedence{Symbol, ATNInterpreter}(Antlr4.Runtime.Recognizer{Symbol, ATNInterpreter}, Antlr4.Runtime.RuleContext)"/>
        /// .</li>
        /// <li>When
        /// <see cref="ATNConfig.PrecedenceFilterSuppressed()"/>
        /// is
        /// <see langword="false"/>
        /// ,
        /// remove all configurations which predict an alternative greater than 1,
        /// for which another configuration that predicts alternative 1 is in the
        /// same ATN state with the same prediction context. This transformation is
        /// valid for the following reasons:
        /// <ul>
        /// <li>The closure block cannot contain any epsilon transitions which bypass
        /// the body of the closure, so all states reachable via alternative 1 are
        /// part of the precedence alternatives of the transformed left-recursive
        /// rule.</li>
        /// <li>The "primary" portion of a left recursive rule cannot contain an
        /// epsilon transition, so the only way an alternative other than 1 can exist
        /// in a state that is also reachable via alternative 1 is by nesting calls
        /// to the left-recursive rule, with the outer calls not being at the
        /// preferred precedence level. The
        /// <see cref="ATNConfig.PrecedenceFilterSuppressed()"/>
        /// property marks ATN
        /// configurations which do not meet this condition, and therefore are not
        /// eligible for elimination during the filtering process.</li>
        /// </ul>
        /// </li>
        /// </ol>
        /// <p>
        /// The prediction context must be considered by this filter to address
        /// situations like the following.
        /// </p>
        /// <code>
        /// <pre>
        /// grammar TA;
        /// prog: statement* EOF;
        /// statement: letterA | statement letterA 'b' ;
        /// letterA: 'a';
        /// </pre>
        /// </code>
        /// <p>
        /// If the above grammar, the ATN state immediately before the token
        /// reference
        /// <c>'a'</c>
        /// in
        /// <c>letterA</c>
        /// is reachable from the left edge
        /// of both the primary and closure blocks of the left-recursive rule
        /// <c>statement</c>
        /// . The prediction context associated with each of these
        /// configurations distinguishes between them, and prevents the alternative
        /// which stepped out to
        /// <c>prog</c>
        /// (and then back in to
        /// <c>statement</c>
        /// from being eliminated by the filter.
        /// </p>
        /// </summary>
        /// <param name="configs">
        /// The configuration set computed by
        /// <see cref="ComputeStartState(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.ParserRuleContext, bool)"/>
        /// as the start state for the DFA.
        /// </param>
		/// <param name="globalContext">
		/// </param>
		/// <param name="contextCache">
		/// </param>
        /// <returns>
        /// The transformed configuration set representing the start state
        /// for a precedence DFA at a particular precedence level (determined by
        /// calling
        /// <see cref="Antlr4.Runtime.Parser.Precedence()"/>
        /// ).
        /// </returns>
        [return: NotNull]
        protected internal virtual ATNConfigSet ApplyPrecedenceFilter(ATNConfigSet configs, ParserRuleContext globalContext, PredictionContextCache contextCache)
        {
            Dictionary<int, PredictionContext> statesFromAlt1 = new Dictionary<int, PredictionContext>();
            ATNConfigSet configSet = new ATNConfigSet();
            foreach (ATNConfig config in configs)
            {
                // handle alt 1 first
                if (config.Alt != 1)
                {
                    continue;
                }
                SemanticContext updatedContext = config.SemanticContext.EvalPrecedence(_parser, globalContext);
                if (updatedContext == null)
                {
                    // the configuration was eliminated
                    continue;
                }
                statesFromAlt1[config.State.stateNumber] = config.Context;
                if (updatedContext != config.SemanticContext)
                {
                    configSet.Add(config.Transform(config.State, updatedContext, false), contextCache);
                }
                else
                {
                    configSet.Add(config, contextCache);
                }
            }
            foreach (ATNConfig config_1 in configs)
            {
                if (config_1.Alt == 1)
                {
                    // already handled
                    continue;
                }
				if (!config_1.PrecedenceFilterSuppressed)
				{
	                PredictionContext context;
	                if (statesFromAlt1.TryGetValue(config_1.State.stateNumber, out context) && context.Equals(config_1.Context))
	                {
	                    // eliminated
	                    continue;
	                }
				}
                configSet.Add(config_1, contextCache);
            }
            return configSet;
        }

        [return: Nullable]
        protected internal virtual ATNState GetReachableTarget(ATNConfig source, Transition trans, int ttype)
        {
            if (trans.Matches(ttype, 0, atn.maxTokenType))
            {
                return trans.target;
            }
            return null;
        }

        /// <summary>collect and set D's semantic context</summary>
        protected internal virtual DFAState.PredPrediction[] PredicateDFAState(DFAState D, ATNConfigSet configs, int nalts)
        {
            BitSet conflictingAlts = GetConflictingAltsFromConfigSet(configs);
            SemanticContext[] altToPred = GetPredsForAmbigAlts(conflictingAlts, configs, nalts);
            // altToPred[uniqueAlt] is now our validating predicate (if any)
            DFAState.PredPrediction[] predPredictions = null;
            if (altToPred != null)
            {
                // we have a validating predicate; test it
                // Update DFA so reach becomes accept state with predicate
                predPredictions = GetPredicatePredictions(conflictingAlts, altToPred);
                D.predicates = predPredictions;
            }
            return predPredictions;
        }

        protected internal virtual SemanticContext[] GetPredsForAmbigAlts(BitSet ambigAlts, ATNConfigSet configs, int nalts)
        {
            // REACH=[1|1|[]|0:0, 1|2|[]|0:1]
            SemanticContext[] altToPred = new SemanticContext[nalts + 1];
            int n = altToPred.Length;
            foreach (ATNConfig c in configs)
            {
                if (ambigAlts.Get(c.Alt))
                {
                    altToPred[c.Alt] = SemanticContext.OrOp(altToPred[c.Alt], c.SemanticContext);
                }
            }
            int nPredAlts = 0;
            for (int i = 0; i < n; i++)
            {
                if (altToPred[i] == null)
                {
                    altToPred[i] = SemanticContext.None;
                }
                else
                {
                    if (altToPred[i] != SemanticContext.None)
                    {
                        nPredAlts++;
                    }
                }
            }
            // nonambig alts are null in altToPred
            if (nPredAlts == 0)
            {
                altToPred = null;
            }
            return altToPred;
        }

        protected internal virtual DFAState.PredPrediction[] GetPredicatePredictions(BitSet ambigAlts, SemanticContext[] altToPred)
        {
            List<DFAState.PredPrediction> pairs = new List<DFAState.PredPrediction>();
            bool containsPredicate = false;
            for (int i = 1; i < altToPred.Length; i++)
            {
                SemanticContext pred = altToPred[i];
                // unpredicated is indicated by SemanticContext.NONE
                System.Diagnostics.Debug.Assert(pred != null);
                // find first unpredicated but ambig alternative, if any.
                // Only ambiguous alternatives will have SemanticContext.NONE.
                // Any unambig alts or ambig naked alts after first ambig naked are ignored
                // (null, i) means alt i is the default prediction
                // if no (null, i), then no default prediction.
                if (ambigAlts != null && ambigAlts.Get(i) && pred == SemanticContext.None)
                {
                    pairs.Add(new DFAState.PredPrediction(pred, i));
                }
                else
                {
                    if (pred != SemanticContext.None)
                    {
                        containsPredicate = true;
                        pairs.Add(new DFAState.PredPrediction(pred, i));
                    }
                }
            }
            if (!containsPredicate)
            {
                return null;
            }
            //		System.out.println(Arrays.toString(altToPred)+"->"+pairs);
            return pairs.ToArray();
        }

        /// <summary>
        /// Look through a list of predicate/alt pairs, returning alts for the
        /// pairs that win.
        /// </summary>
        /// <remarks>
        /// Look through a list of predicate/alt pairs, returning alts for the
        /// pairs that win. A
        /// <see langword="null"/>
        /// predicate indicates an alt containing an
        /// unpredicated config which behaves as "always true."
        /// </remarks>
        protected internal virtual BitSet EvalSemanticContext(DFAState.PredPrediction[] predPredictions, ParserRuleContext outerContext, bool complete)
        {
            BitSet predictions = new BitSet();
            foreach (DFAState.PredPrediction pair in predPredictions)
            {
                if (pair.pred == SemanticContext.None)
                {
                    predictions.Set(pair.alt);
                    if (!complete)
                    {
                        break;
                    }
                    continue;
                }
                bool evaluatedResult = EvalSemanticContext(pair.pred, outerContext, pair.alt);
#if !PORTABLE
				#pragma warning disable 162, 429
				if (debug || dfa_debug)
                {
                    System.Console.Out.WriteLine("eval pred " + pair + "=" + evaluatedResult);
                }
				#pragma warning restore 162, 429
#endif
                if (evaluatedResult)
                {
#if !PORTABLE
					#pragma warning disable 162, 429
					if (debug || dfa_debug)
                    {
                        System.Console.Out.WriteLine("PREDICT " + pair.alt);
                    }
					#pragma warning restore 162, 429
#endif
                    predictions.Set(pair.alt);
                    if (!complete)
                    {
                        break;
                    }
                }
            }
            return predictions;
        }

        /// <summary>Evaluate a semantic context within a specific parser context.</summary>
        /// <remarks>
        /// Evaluate a semantic context within a specific parser context.
        /// <p>
        /// This method might not be called for every semantic context evaluated
        /// during the prediction process. In particular, we currently do not
        /// evaluate the following but it may change in the future:</p>
        /// <ul>
        /// <li>Precedence predicates (represented by
        /// <see cref="SemanticContext.PrecedencePredicate"/>
        /// ) are not currently evaluated
        /// through this method.</li>
        /// <li>Operator predicates (represented by
        /// <see cref="SemanticContext.AND"/>
        /// and
        /// <see cref="SemanticContext.OR"/>
        /// ) are evaluated as a single semantic
        /// context, rather than evaluating the operands individually.
        /// Implementations which require evaluation results from individual
        /// predicates should override this method to explicitly handle evaluation of
        /// the operands within operator predicates.</li>
        /// </ul>
        /// </remarks>
        /// <param name="pred">The semantic context to evaluate</param>
        /// <param name="parserCallStack">
        /// The parser context in which to evaluate the
        /// semantic context
        /// </param>
        /// <param name="alt">
        /// The alternative which is guarded by
        /// <paramref name="pred"/>
        /// </param>
        /// <since>4.3</since>
        protected internal virtual bool EvalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt)
        {
            return pred.Eval(_parser, parserCallStack);
        }

        protected internal virtual void Closure(ATNConfigSet sourceConfigs, ATNConfigSet configs, bool collectPredicates, bool hasMoreContext, PredictionContextCache contextCache, bool treatEofAsEpsilon)
        {
            if (contextCache == null)
            {
                contextCache = PredictionContextCache.Uncached;
            }
            ATNConfigSet currentConfigs = sourceConfigs;
            HashSet<ATNConfig> closureBusy = new HashSet<ATNConfig>();
            while (currentConfigs.Count > 0)
            {
                ATNConfigSet intermediate = new ATNConfigSet();
                foreach (ATNConfig config in currentConfigs)
                {
                    Closure(config, configs, intermediate, closureBusy, collectPredicates, hasMoreContext, contextCache, 0, treatEofAsEpsilon);
                }
                currentConfigs = intermediate;
            }
        }

        protected internal virtual void Closure(ATNConfig config, ATNConfigSet configs, ATNConfigSet intermediate, HashSet<ATNConfig> closureBusy, bool collectPredicates, bool hasMoreContexts, PredictionContextCache contextCache, int depth, bool treatEofAsEpsilon)
        {
            if (config.State is RuleStopState)
            {
                // We hit rule end. If we have context info, use it
                if (!config.Context.IsEmpty)
                {
                    bool hasEmpty = config.Context.HasEmpty;
                    int nonEmptySize = config.Context.Size - (hasEmpty ? 1 : 0);
                    for (int i = 0; i < nonEmptySize; i++)
                    {
                        PredictionContext newContext = config.Context.GetParent(i);
                        // "pop" return state
                        ATNState returnState = atn.states[config.Context.GetReturnState(i)];
                        ATNConfig c = ATNConfig.Create(returnState, config.Alt, newContext, config.SemanticContext);
                        // While we have context to pop back from, we may have
                        // gotten that context AFTER having fallen off a rule.
                        // Make sure we track that we are now out of context.
                        c.OuterContextDepth = config.OuterContextDepth;
                        c.PrecedenceFilterSuppressed = config.PrecedenceFilterSuppressed;
                        System.Diagnostics.Debug.Assert(depth > int.MinValue);
                        Closure(c, configs, intermediate, closureBusy, collectPredicates, hasMoreContexts, contextCache, depth - 1, treatEofAsEpsilon);
                    }
                    if (!hasEmpty || !hasMoreContexts)
                    {
                        return;
                    }
                    config = config.Transform(config.State, PredictionContext.EmptyLocal, false);
                }
                else
                {
                    if (!hasMoreContexts)
                    {
                        configs.Add(config, contextCache);
                        return;
                    }
                    else
                    {
                        // else if we have no context info, just chase follow links (if greedy)
                        if (config.Context == PredictionContext.EmptyFull)
                        {
                            // no need to keep full context overhead when we step out
                            config = config.Transform(config.State, PredictionContext.EmptyLocal, false);
                        }
                        else
                        {
                            if (!config.ReachesIntoOuterContext && PredictionContext.IsEmptyLocal(config.Context))
                            {
                                // add stop state when leaving decision rule for the first time
                                configs.Add(config, contextCache);
                            }
                        }
                    }
                }
            }
            ATNState p = config.State;
            // optimization
            if (!p.OnlyHasEpsilonTransitions)
            {
                configs.Add(config, contextCache);
            }
            // make sure to not return here, because EOF transitions can act as
            // both epsilon transitions and non-epsilon transitions.
            for (int i_1 = 0; i_1 < p.NumberOfOptimizedTransitions; i_1++)
            {
                Transition t = p.GetOptimizedTransition(i_1);
                bool continueCollecting = !(t is Antlr4.Runtime.Atn.ActionTransition) && collectPredicates;
                ATNConfig c = GetEpsilonTarget(config, t, continueCollecting, depth == 0, contextCache, treatEofAsEpsilon);
                if (c != null)
                {
                    if (t is Antlr4.Runtime.Atn.RuleTransition)
                    {
                        if (intermediate != null && !collectPredicates)
                        {
                            intermediate.Add(c, contextCache);
                            continue;
                        }
                    }
                    if (!t.IsEpsilon && !closureBusy.Add(c))
                    {
                        // avoid infinite recursion for EOF* and EOF+
                        continue;
                    }
                    int newDepth = depth;
                    if (config.State is RuleStopState)
                    {
                        // target fell off end of rule; mark resulting c as having dipped into outer context
                        // We can't get here if incoming config was rule stop and we had context
                        // track how far we dip into outer context.  Might
                        // come in handy and we avoid evaluating context dependent
                        // preds if this is > 0.
                        if (!closureBusy.Add(c))
                        {
                            // avoid infinite recursion for right-recursive rules
                            continue;
                        }
                        if (dfa != null && dfa.IsPrecedenceDfa)
                        {
                            int outermostPrecedenceReturn = ((EpsilonTransition)t).OutermostPrecedenceReturn;
                            if (outermostPrecedenceReturn == dfa.atnStartState.ruleIndex)
                            {
                                c.PrecedenceFilterSuppressed = true;
                            }
                        }
                        c.OuterContextDepth = c.OuterContextDepth + 1;
                        System.Diagnostics.Debug.Assert(newDepth > int.MinValue);
                        newDepth--;
                    }
                    else
                    {
                        if (t is Antlr4.Runtime.Atn.RuleTransition)
                        {
                            if (optimize_tail_calls && ((Antlr4.Runtime.Atn.RuleTransition)t).optimizedTailCall && (!tail_call_preserves_sll || !PredictionContext.IsEmptyLocal(config.Context)))
                            {
                                System.Diagnostics.Debug.Assert(c.Context == config.Context);
                                if (newDepth == 0)
                                {
                                    // the pop/push of a tail call would keep the depth
                                    // constant, except we latch if it goes negative
                                    newDepth--;
                                    if (!tail_call_preserves_sll && PredictionContext.IsEmptyLocal(config.Context))
                                    {
                                        // make sure the SLL config "dips into the outer context" or prediction may not fall back to LL on conflict
                                        c.OuterContextDepth = c.OuterContextDepth + 1;
                                    }
                                }
                            }
                            else
                            {
                                // latch when newDepth goes negative - once we step out of the entry context we can't return
                                if (newDepth >= 0)
                                {
                                    newDepth++;
                                }
                            }
                        }
                    }
                    Closure(c, configs, intermediate, closureBusy, continueCollecting, hasMoreContexts, contextCache, newDepth, treatEofAsEpsilon);
                }
            }
        }

        [return: NotNull]
        public virtual string GetRuleName(int index)
        {
            if (_parser != null && index >= 0)
            {
                return _parser.RuleNames[index];
            }
            return "<rule " + index + ">";
        }

        [return: Nullable]
        protected internal virtual ATNConfig GetEpsilonTarget(ATNConfig config, Transition t, bool collectPredicates, bool inContext, PredictionContextCache contextCache, bool treatEofAsEpsilon)
        {
            switch (t.TransitionType)
            {
                case TransitionType.Rule:
                {
                    return RuleTransition(config, (Antlr4.Runtime.Atn.RuleTransition)t, contextCache);
                }

                case TransitionType.Precedence:
                {
                    return PrecedenceTransition(config, (PrecedencePredicateTransition)t, collectPredicates, inContext);
                }

                case TransitionType.Predicate:
                {
                    return PredTransition(config, (PredicateTransition)t, collectPredicates, inContext);
                }

                case TransitionType.Action:
                {
                    return ActionTransition(config, (Antlr4.Runtime.Atn.ActionTransition)t);
                }

                case TransitionType.Epsilon:
                {
                    return config.Transform(t.target, false);
                }

                case TransitionType.Atom:
                case TransitionType.Range:
                case TransitionType.Set:
                {
                    // EOF transitions act like epsilon transitions after the first EOF
                    // transition is traversed
                    if (treatEofAsEpsilon)
                    {
                        if (t.Matches(TokenConstants.Eof, 0, 1))
                        {
                            return config.Transform(t.target, false);
                        }
                    }
                    return null;
                }

                default:
                {
                    return null;
                }
            }
        }

        [return: NotNull]
        protected internal virtual ATNConfig ActionTransition(ATNConfig config, Antlr4.Runtime.Atn.ActionTransition t)
        {
            return config.Transform(t.target, false);
        }

        [return: Nullable]
        protected internal virtual ATNConfig PrecedenceTransition(ATNConfig config, PrecedencePredicateTransition pt, bool collectPredicates, bool inContext)
        {
            ATNConfig c;
            if (collectPredicates && inContext)
            {
                SemanticContext newSemCtx = SemanticContext.AndOp(config.SemanticContext, pt.Predicate);
                c = config.Transform(pt.target, newSemCtx, false);
            }
            else
            {
                c = config.Transform(pt.target, false);
            }
            return c;
        }

        [return: Nullable]
        protected internal virtual ATNConfig PredTransition(ATNConfig config, PredicateTransition pt, bool collectPredicates, bool inContext)
        {
            ATNConfig c;
            if (collectPredicates && (!pt.isCtxDependent || (pt.isCtxDependent && inContext)))
            {
                SemanticContext newSemCtx = SemanticContext.AndOp(config.SemanticContext, pt.Predicate);
                c = config.Transform(pt.target, newSemCtx, false);
            }
            else
            {
                c = config.Transform(pt.target, false);
            }
            return c;
        }

        [return: NotNull]
        protected internal virtual ATNConfig RuleTransition(ATNConfig config, Antlr4.Runtime.Atn.RuleTransition t, PredictionContextCache contextCache)
        {
            ATNState returnState = t.followState;
            PredictionContext newContext;
            if (optimize_tail_calls && t.optimizedTailCall && (!tail_call_preserves_sll || !PredictionContext.IsEmptyLocal(config.Context)))
            {
                newContext = config.Context;
            }
            else
            {
                if (contextCache != null)
                {
                    newContext = contextCache.GetChild(config.Context, returnState.stateNumber);
                }
                else
                {
                    newContext = config.Context.GetChild(returnState.stateNumber);
                }
            }
            return config.Transform(t.target, newContext, false);
        }

        private sealed class _IComparer_1996 : IComparer<ATNConfig>
        {
            public _IComparer_1996()
            {
            }

            public int Compare(ATNConfig o1, ATNConfig o2)
            {
                int diff = o1.State.NonStopStateNumber - o2.State.NonStopStateNumber;
                if (diff != 0)
                {
                    return diff;
                }
                diff = o1.Alt - o2.Alt;
                if (diff != 0)
                {
                    return diff;
                }
                return 0;
            }
        }

        private static readonly IComparer<ATNConfig> StateAltSortComparator = new _IComparer_1996();

        private ConflictInfo IsConflicted(ATNConfigSet configset, PredictionContextCache contextCache)
        {
            if (configset.UniqueAlt != ATN.InvalidAltNumber || configset.Count <= 1)
            {
                return null;
            }
            List<ATNConfig> configs = new List<ATNConfig>(configset);
            configs.Sort(StateAltSortComparator);
            bool exact = !configset.DipsIntoOuterContext;
            BitSet alts = new BitSet();
            int minAlt = configs[0].Alt;
            alts.Set(minAlt);
            // quick check 1 & 2 => if we assume #1 holds and check #2 against the
            // minAlt from the first state, #2 will fail if the assumption was
            // incorrect
            int currentState = configs[0].State.NonStopStateNumber;
            foreach (ATNConfig config in configs)
            {
                int stateNumber = config.State.NonStopStateNumber;
                if (stateNumber != currentState)
                {
                    if (config.Alt != minAlt)
                    {
                        return null;
                    }
                    currentState = stateNumber;
                }
            }
            BitSet representedAlts;
            if (exact)
            {
                currentState = configs[0].State.NonStopStateNumber;
                // get the represented alternatives of the first state
                representedAlts = new BitSet();
                int maxAlt = minAlt;
                foreach (ATNConfig config_1 in configs)
                {
                    if (config_1.State.NonStopStateNumber != currentState)
                    {
                        break;
                    }
                    int alt = config_1.Alt;
                    representedAlts.Set(alt);
                    maxAlt = alt;
                }
                // quick check #3:
                currentState = configs[0].State.NonStopStateNumber;
                int currentAlt = minAlt;
                foreach (ATNConfig config_2 in configs)
                {
                    int stateNumber = config_2.State.NonStopStateNumber;
                    int alt = config_2.Alt;
                    if (stateNumber != currentState)
                    {
                        if (currentAlt != maxAlt)
                        {
                            exact = false;
                            break;
                        }
                        currentState = stateNumber;
                        currentAlt = minAlt;
                    }
                    else
                    {
                        if (alt != currentAlt)
                        {
                            if (alt != representedAlts.NextSetBit(currentAlt + 1))
                            {
                                exact = false;
                                break;
                            }
                            currentAlt = alt;
                        }
                    }
                }
            }
            currentState = configs[0].State.NonStopStateNumber;
            int firstIndexCurrentState = 0;
            int lastIndexCurrentStateMinAlt = 0;
            PredictionContext joinedCheckContext = configs[0].Context;
            for (int i = 1; i < configs.Count; i++)
            {
                ATNConfig config_1 = configs[i];
                if (config_1.Alt != minAlt)
                {
                    break;
                }
                if (config_1.State.NonStopStateNumber != currentState)
                {
                    break;
                }
                lastIndexCurrentStateMinAlt = i;
                joinedCheckContext = contextCache.Join(joinedCheckContext, configs[i].Context);
            }
            for (int i_1 = lastIndexCurrentStateMinAlt + 1; i_1 < configs.Count; i_1++)
            {
                ATNConfig config_1 = configs[i_1];
                ATNState state = config_1.State;
                alts.Set(config_1.Alt);
                if (state.NonStopStateNumber != currentState)
                {
                    currentState = state.NonStopStateNumber;
                    firstIndexCurrentState = i_1;
                    lastIndexCurrentStateMinAlt = i_1;
                    joinedCheckContext = config_1.Context;
                    for (int j = firstIndexCurrentState + 1; j < configs.Count; j++)
                    {
                        ATNConfig config2 = configs[j];
                        if (config2.Alt != minAlt)
                        {
                            break;
                        }
                        if (config2.State.NonStopStateNumber != currentState)
                        {
                            break;
                        }
                        lastIndexCurrentStateMinAlt = j;
                        joinedCheckContext = contextCache.Join(joinedCheckContext, config2.Context);
                    }
                    i_1 = lastIndexCurrentStateMinAlt;
                    continue;
                }
                PredictionContext joinedCheckContext2 = config_1.Context;
                int currentAlt = config_1.Alt;
                int lastIndexCurrentStateCurrentAlt = i_1;
                for (int j_1 = lastIndexCurrentStateCurrentAlt + 1; j_1 < configs.Count; j_1++)
                {
                    ATNConfig config2 = configs[j_1];
                    if (config2.Alt != currentAlt)
                    {
                        break;
                    }
                    if (config2.State.NonStopStateNumber != currentState)
                    {
                        break;
                    }
                    lastIndexCurrentStateCurrentAlt = j_1;
                    joinedCheckContext2 = contextCache.Join(joinedCheckContext2, config2.Context);
                }
                i_1 = lastIndexCurrentStateCurrentAlt;
                PredictionContext check = contextCache.Join(joinedCheckContext, joinedCheckContext2);
                if (!joinedCheckContext.Equals(check))
                {
                    return null;
                }
                // update exact if necessary
                exact = exact && joinedCheckContext.Equals(joinedCheckContext2);
            }
            return new ConflictInfo(alts, exact);
        }

        protected internal virtual BitSet GetConflictingAltsFromConfigSet(ATNConfigSet configs)
        {
            BitSet conflictingAlts = configs.ConflictingAlts;
            if (conflictingAlts == null && configs.UniqueAlt != ATN.InvalidAltNumber)
            {
                conflictingAlts = new BitSet();
                conflictingAlts.Set(configs.UniqueAlt);
            }
            return conflictingAlts;
        }

        [return: NotNull]
        public virtual string GetTokenName(int t)
        {
            if (t == TokenConstants.Eof)
            {
                return "EOF";
            }
            IVocabulary vocabulary = _parser != null ? _parser.Vocabulary : Vocabulary.EmptyVocabulary;
            string displayName = vocabulary.GetDisplayName(t);
            if (displayName.Equals(t.ToString(), StringComparison.Ordinal))
            {
                return displayName;
            }
            return displayName + "<" + t + ">";
        }

        public virtual string GetLookaheadName(ITokenStream input)
        {
            return GetTokenName(input.La(1));
        }

#if !PORTABLE
        public virtual void DumpDeadEndConfigs(NoViableAltException nvae)
        {
            System.Console.Error.WriteLine("dead end configs: ");
            foreach (ATNConfig c in nvae.DeadEndConfigs)
            {
                string trans = "no edges";
                if (c.State.NumberOfOptimizedTransitions > 0)
                {
                    Transition t = c.State.GetOptimizedTransition(0);
                    if (t is AtomTransition)
                    {
                        AtomTransition at = (AtomTransition)t;
						trans = "Atom " + GetTokenName(at.token);
                    }
                    else
                    {
                        if (t is SetTransition)
                        {
                            SetTransition st = (SetTransition)t;
                            bool not = st is NotSetTransition;
                            trans = (not ? "~" : string.Empty) + "Set " + st.set.ToString();
                        }
                    }
                }
                System.Console.Error.WriteLine(c.ToString(_parser, true) + ":" + trans);
            }
        }
#endif

        [return: NotNull]
        protected internal virtual NoViableAltException NoViableAlt(ITokenStream input, ParserRuleContext outerContext, ATNConfigSet configs, int startIndex)
        {
            return new NoViableAltException(_parser, input, input.Get(startIndex), input.Lt(1), configs, outerContext);
        }

        protected internal virtual int GetUniqueAlt(IEnumerable<ATNConfig> configs)
        {
            int alt = ATN.InvalidAltNumber;
            foreach (ATNConfig c in configs)
            {
                if (alt == ATN.InvalidAltNumber)
                {
                    alt = c.Alt;
                }
                else
                {
                    // found first alt
                    if (c.Alt != alt)
                    {
                        return ATN.InvalidAltNumber;
                    }
                }
            }
            return alt;
        }

        protected internal virtual bool ConfigWithAltAtStopState(IEnumerable<ATNConfig> configs, int alt)
        {
            foreach (ATNConfig c in configs)
            {
                if (c.Alt == alt)
                {
                    if (c.State is RuleStopState)
                    {
                        return true;
                    }
                }
            }
            return false;
        }

        [return: NotNull]
        protected internal virtual DFAState AddDFAEdge(DFA dfa, DFAState fromState, int t, List<int> contextTransitions, ATNConfigSet toConfigs, PredictionContextCache contextCache)
        {
            System.Diagnostics.Debug.Assert(contextTransitions == null || contextTransitions.Count == 0 || dfa.IsContextSensitive);
            DFAState from = fromState;
            DFAState to = AddDFAState(dfa, toConfigs, contextCache);
            if (contextTransitions != null)
            {
                foreach (int context in contextTransitions.ToArray())
                {
                    if (context == PredictionContext.EmptyFullStateKey)
                    {
                        if (from.configs.IsOutermostConfigSet)
                        {
                            continue;
                        }
                    }
                    from.SetContextSensitive(atn);
                    from.SetContextSymbol(t);
                    DFAState next = from.GetContextTarget(context);
                    if (next != null)
                    {
                        from = next;
                        continue;
                    }
                    next = AddDFAContextState(dfa, from.configs, context, contextCache);
                    System.Diagnostics.Debug.Assert(context != PredictionContext.EmptyFullStateKey || next.configs.IsOutermostConfigSet);
                    from.SetContextTarget(context, next);
                    from = next;
                }
            }
            AddDFAEdge(from, t, to);
            return to;
        }

        protected internal virtual void AddDFAEdge(DFAState p, int t, DFAState q)
        {
            if (p != null)
            {
                p.SetTarget(t, q);
            }
        }

        /// <summary>See comment on LexerInterpreter.addDFAState.</summary>
        /// <remarks>See comment on LexerInterpreter.addDFAState.</remarks>
        [return: NotNull]
        protected internal virtual DFAState AddDFAContextState(DFA dfa, ATNConfigSet configs, int returnContext, PredictionContextCache contextCache)
        {
            if (returnContext != PredictionContext.EmptyFullStateKey)
            {
                ATNConfigSet contextConfigs = new ATNConfigSet();
                foreach (ATNConfig config in configs)
                {
                    contextConfigs.Add(config.AppendContext(returnContext, contextCache));
                }
                return AddDFAState(dfa, contextConfigs, contextCache);
            }
            else
            {
                System.Diagnostics.Debug.Assert(!configs.IsOutermostConfigSet, "Shouldn't be adding a duplicate edge.");
                configs = configs.Clone(true);
                configs.IsOutermostConfigSet = true;
                return AddDFAState(dfa, configs, contextCache);
            }
        }

        /// <summary>See comment on LexerInterpreter.addDFAState.</summary>
        /// <remarks>See comment on LexerInterpreter.addDFAState.</remarks>
        [return: NotNull]
        protected internal virtual DFAState AddDFAState(DFA dfa, ATNConfigSet configs, PredictionContextCache contextCache)
        {
            bool enableDfa = enable_global_context_dfa || !configs.IsOutermostConfigSet;
            if (enableDfa)
            {
                if (!configs.IsReadOnly)
                {
                    configs.OptimizeConfigs(this);
                }
                DFAState proposed = CreateDFAState(dfa, configs);
                DFAState existing;
                if (dfa.states.TryGetValue(proposed, out existing))
                {
                    return existing;
                }
            }
            if (!configs.IsReadOnly)
            {
                if (configs.ConflictInformation == null)
                {
                    configs.ConflictInformation = IsConflicted(configs, contextCache);
                }
            }
            DFAState newState = CreateDFAState(dfa, configs.Clone(true));
            DecisionState decisionState = atn.GetDecisionState(dfa.decision);
            int predictedAlt = GetUniqueAlt(configs);
            if (predictedAlt != ATN.InvalidAltNumber)
            {
                newState.AcceptStateInfo = new AcceptStateInfo(predictedAlt);
            }
            else
            {
                if (configs.ConflictingAlts != null)
                {
                    newState.AcceptStateInfo = new AcceptStateInfo(newState.configs.ConflictingAlts.NextSetBit(0));
                }
            }
            if (newState.IsAcceptState && configs.HasSemanticContext)
            {
                PredicateDFAState(newState, configs, decisionState.NumberOfTransitions);
            }
            if (!enableDfa)
            {
                return newState;
            }
            DFAState added = dfa.AddState(newState);
#if !PORTABLE
			#pragma warning disable 162, 429
			if (debug && added == newState)
            {
                System.Console.Out.WriteLine("adding new DFA state: " + newState);
            }
			#pragma warning restore 162, 429
#endif
            return added;
        }

        [return: NotNull]
        protected internal virtual DFAState CreateDFAState(DFA dfa, ATNConfigSet configs)
        {
            return new DFAState(dfa, configs);
        }

        protected internal virtual void ReportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, SimulatorState conflictState, int startIndex, int stopIndex)
        {
#if !PORTABLE
			#pragma warning disable 162, 429
			if (debug || retry_debug)
            {
                Interval interval = Interval.Of(startIndex, stopIndex);
                System.Console.Out.WriteLine("reportAttemptingFullContext decision=" + dfa.decision + ":" + conflictState.s0.configs + ", input=" + ((ITokenStream)_parser.InputStream).GetText(interval));
            }
			#pragma warning restore 162, 429
#endif
            if (_parser != null)
            {
                ((IParserErrorListener)_parser.ErrorListenerDispatch).ReportAttemptingFullContext(_parser, dfa, startIndex, stopIndex, conflictingAlts, conflictState);
			}
        }

        protected internal virtual void ReportContextSensitivity(DFA dfa, int prediction, SimulatorState acceptState, int startIndex, int stopIndex)
        {
#if !PORTABLE
			#pragma warning disable 162, 429
            if (debug || retry_debug)
            {
                Interval interval = Interval.Of(startIndex, stopIndex);
                System.Console.Out.WriteLine("reportContextSensitivity decision=" + dfa.decision + ":" + acceptState.s0.configs + ", input=" + ((ITokenStream)_parser.InputStream).GetText(interval));
            }
			#pragma warning restore 162, 429
#endif
            if (_parser != null)
            {
                ((IParserErrorListener)_parser.ErrorListenerDispatch).ReportContextSensitivity(_parser, dfa, startIndex, stopIndex, prediction, acceptState);
            }
        }

        /// <summary>If context sensitive parsing, we know it's ambiguity not conflict</summary>
        protected internal virtual void ReportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
        {
#if !PORTABLE
			#pragma warning disable 162, 429
            if (debug || retry_debug)
            {
                Interval interval = Interval.Of(startIndex, stopIndex);
                System.Console.Out.WriteLine("reportAmbiguity " + ambigAlts + ":" + configs + ", input=" + ((ITokenStream)_parser.InputStream).GetText(interval));
            }
			#pragma warning restore 162, 429
#endif
            if (_parser != null)
            {
                ((IParserErrorListener)_parser.ErrorListenerDispatch).ReportAmbiguity(_parser, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
            }
        }

        protected internal int GetReturnState(RuleContext context)
        {
            if (context.IsEmpty)
            {
                return PredictionContext.EmptyFullStateKey;
            }
            ATNState state = atn.states[context.invokingState];
            Antlr4.Runtime.Atn.RuleTransition transition = (Antlr4.Runtime.Atn.RuleTransition)state.Transition(0);
            return transition.followState.stateNumber;
        }

        protected internal ParserRuleContext SkipTailCalls(ParserRuleContext context)
        {
            if (!optimize_tail_calls)
            {
                return context;
            }
            while (!context.IsEmpty)
            {
                ATNState state = atn.states[context.invokingState];
                System.Diagnostics.Debug.Assert(state.NumberOfTransitions == 1 && state.Transition(0).TransitionType == TransitionType.Rule);
                Antlr4.Runtime.Atn.RuleTransition transition = (Antlr4.Runtime.Atn.RuleTransition)state.Transition(0);
                if (!transition.tailCall)
                {
                    break;
                }
                context = ((ParserRuleContext)context.Parent);
            }
            return context;
        }

        /// <since>4.3</since>
        public virtual Antlr4.Runtime.Parser Parser
        {
            get
            {
                return _parser;
            }
        }
    }
}
