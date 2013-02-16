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
using System.Collections;
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Tree;
using Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>This is all the parsing support code essentially; most of it is error recovery stuff.
    ///     </summary>
    /// <remarks>This is all the parsing support code essentially; most of it is error recovery stuff.
    ///     </remarks>
    public abstract class Parser : Recognizer<IToken, ParserATNSimulator>
    {
        public class TraceListener : IParseTreeListener
        {
            public virtual void EnterEveryRule(ParserRuleContext ctx)
            {
                System.Console.Out.WriteLine("enter   " + this._enclosing.GetRuleNames()[ctx.GetRuleIndex
                    ()] + ", LT(1)=" + this._enclosing._input.Lt(1).Text);
            }

            public virtual void ExitEveryRule(ParserRuleContext ctx)
            {
                System.Console.Out.WriteLine("exit    " + this._enclosing.GetRuleNames()[ctx.GetRuleIndex
                    ()] + ", LT(1)=" + this._enclosing._input.Lt(1).Text);
            }

            public virtual void VisitErrorNode(IErrorNode node)
            {
            }

            public virtual void VisitTerminal(ITerminalNode node)
            {
                ParserRuleContext parent = (ParserRuleContext)((IRuleNode)node.Parent).RuleContext;
                IToken token = node.Symbol;
                System.Console.Out.WriteLine("consume " + token + " rule " + this._enclosing.GetRuleNames
                    ()[parent.GetRuleIndex()] + " alt=" + parent.altNum);
            }

            internal TraceListener(Parser _enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly Parser _enclosing;
        }

        public class TrimToSizeListener : IParseTreeListener
        {
            public static readonly Parser.TrimToSizeListener Instance = new Parser.TrimToSizeListener
                ();

            public virtual void VisitTerminal(ITerminalNode node)
            {
            }

            public virtual void VisitErrorNode(IErrorNode node)
            {
            }

            public virtual void EnterEveryRule(ParserRuleContext ctx)
            {
            }

            public virtual void ExitEveryRule(ParserRuleContext ctx)
            {
                if (ctx.children is ArrayList)
                {
                    ((List<object>)ctx.children).TrimExcess();
                }
            }
        }

        protected internal IAntlrErrorStrategy _errHandler = new DefaultErrorStrategy();

        protected internal ITokenStream _input;

        protected internal readonly List<int> _precedenceStack = new List<int> { 0 };

        /// <summary>The RuleContext object for the currently executing rule.</summary>
        /// <remarks>
        /// The RuleContext object for the currently executing rule. This
        /// must be non-null during parsing, but is initially null.
        /// When somebody calls the start rule, this gets set to the
        /// root context.
        /// </remarks>
        protected internal ParserRuleContext _ctx;

        protected internal bool _buildParseTrees = true;

        protected internal Parser.TraceListener _tracer;

        /// <summary>
        /// If the listener is non-null, trigger enter and exit rule events
        /// *during* the parse.
        /// </summary>
        /// <remarks>
        /// If the listener is non-null, trigger enter and exit rule events
        /// *during* the parse. This is typically done only when not building
        /// parse trees for later visiting. We either trigger events during
        /// the parse or during tree walks later. Both could be done.
        /// Not intended for average user!!!  Most people should use
        /// ParseTreeListener with ParseTreeWalker.
        /// </remarks>
        /// <seealso cref="Antlr4.Runtime.Tree.ParseTreeWalker">Antlr4.Runtime.Tree.ParseTreeWalker
        ///     </seealso>
        protected internal IList<IParseTreeListener> _parseListeners;

        /// <summary>Did the recognizer encounter a syntax error?  Track how many.</summary>
        /// <remarks>Did the recognizer encounter a syntax error?  Track how many.</remarks>
        protected internal int _syntaxErrors = 0;

        public Parser(ITokenStream input)
        {
            SetInputStream(input);
        }

        /// <summary>reset the parser's state</summary>
        public virtual void Reset()
        {
            if (((ITokenStream)GetInputStream()) != null)
            {
                ((ITokenStream)GetInputStream()).Seek(0);
            }
            _errHandler.EndErrorCondition(this);
            _ctx = null;
            _syntaxErrors = 0;
            _tracer = null;
            _precedenceStack.Clear();
            _precedenceStack.Add(0);
            ATNSimulator interpreter = GetInterpreter();
            if (interpreter != null)
            {
                interpreter.Reset();
            }
        }

        /// <summary>Match current input symbol against ttype.</summary>
        /// <remarks>
        /// Match current input symbol against ttype.  Attempt
        /// single token insertion or deletion error recovery.  If
        /// that fails, throw MismatchedTokenException.
        /// </remarks>
        /// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
        public virtual IToken Match(int ttype)
        {
            IToken t = GetCurrentToken();
            if (t.Type == ttype)
            {
                _errHandler.EndErrorCondition(this);
                Consume();
            }
            else
            {
                t = _errHandler.RecoverInline(this);
                if (_buildParseTrees && t.TokenIndex == -1)
                {
                    // we must have conjured up a new token during single token insertion
                    // if it's not the current symbol
                    _ctx.AddErrorNode(t);
                }
            }
            return t;
        }

        /// <exception cref="Antlr4.Runtime.RecognitionException"></exception>
        public virtual IToken MatchWildcard()
        {
            IToken t = GetCurrentToken();
            if (t.Type > 0)
            {
                _errHandler.EndErrorCondition(this);
                Consume();
            }
            else
            {
                t = _errHandler.RecoverInline(this);
                if (_buildParseTrees && t.TokenIndex == -1)
                {
                    // we must have conjured up a new token during single token insertion
                    // if it's not the current symbol
                    _ctx.AddErrorNode(t);
                }
            }
            return t;
        }

        /// <summary>
        /// Track the RuleContext objects during the parse and hook them up
        /// using the children list so that it forms a parse tree.
        /// </summary>
        /// <remarks>
        /// Track the RuleContext objects during the parse and hook them up
        /// using the children list so that it forms a parse tree.
        /// The RuleContext returned from the start rule represents the root
        /// of the parse tree.
        /// To built parse trees, all we have to do is put a hook in setState()
        /// and enterRule(). In setState(), we add tokens to the current context
        /// as children. By the time we get to enterRule(), we are already
        /// in an invoked rule so we add this context as a child of the parent
        /// (invoking) context. Simple and effective.
        /// Note that if we are not building parse trees, rule contexts
        /// only point upwards. When a rule exits, it returns the context
        /// but that gets garbage collected if nobody holds a reference.
        /// It points upwards but nobody points at it.
        /// When we build parse trees, we are adding all of these contexts to
        /// somebody's children list. Contexts are then not candidates
        /// for garbage collection.
        /// </remarks>
        public virtual void SetBuildParseTree(bool buildParseTrees)
        {
            this._buildParseTrees = buildParseTrees;
        }

        public virtual bool GetBuildParseTree()
        {
            return _buildParseTrees;
        }

        /// <summary>Trim the internal lists of the parse tree during parsing to conserve memory.
        ///     </summary>
        /// <remarks>
        /// Trim the internal lists of the parse tree during parsing to conserve memory.
        /// This property is set to
        /// <code>false</code>
        /// by default for a newly constructed parser.
        /// </remarks>
        /// <param name="trimParseTrees">
        /// 
        /// <code>true</code>
        /// to trim the capacity of the
        /// <see cref="ParserRuleContext.children">ParserRuleContext.children</see>
        /// list to its size after a rule is parsed.
        /// </param>
        public virtual void SetTrimParseTree(bool trimParseTrees)
        {
            if (trimParseTrees)
            {
                if (GetTrimParseTree())
                {
                    return;
                }
                AddParseListener(Parser.TrimToSizeListener.Instance);
            }
            else
            {
                RemoveParseListener(Parser.TrimToSizeListener.Instance);
            }
        }

        /// <returns>
        /// 
        /// <code>true</code>
        /// if the
        /// <see cref="ParserRuleContext.children">ParserRuleContext.children</see>
        /// list is trimmed
        /// using the default
        /// <see cref="TrimToSizeListener">TrimToSizeListener</see>
        /// during the parse process.
        /// </returns>
        public virtual bool GetTrimParseTree()
        {
            if (_parseListeners == null)
            {
                return false;
            }
            return _parseListeners.Contains(Parser.TrimToSizeListener.Instance);
        }

        //	public void setTraceATNStates(boolean traceATNStates) {
        //		this.traceATNStates = traceATNStates;
        //	}
        //
        //	public boolean getTraceATNStates() {
        //		return traceATNStates;
        //	}
        public virtual IList<IParseTreeListener> GetParseListeners()
        {
            return _parseListeners;
        }

        /// <summary>
        /// Provide a listener that gets notified about token matches,
        /// and rule entry/exit events DURING the parse.
        /// </summary>
        /// <remarks>
        /// Provide a listener that gets notified about token matches,
        /// and rule entry/exit events DURING the parse. It's a little bit
        /// weird for left recursive rule entry events but it's
        /// deterministic.
        /// THIS IS ONLY FOR ADVANCED USERS. Please give your
        /// ParseTreeListener to a ParseTreeWalker instead of giving it to
        /// the parser!!!!
        /// </remarks>
        public virtual void AddParseListener(IParseTreeListener listener)
        {
            if (listener == null)
            {
                return;
            }
            if (_parseListeners == null)
            {
                _parseListeners = new List<IParseTreeListener>();
            }
            this._parseListeners.Add(listener);
        }

        public virtual void RemoveParseListener(IParseTreeListener l)
        {
            if (l == null)
            {
                return;
            }
            if (_parseListeners != null)
            {
                _parseListeners.Remove(l);
                if (_parseListeners.Count == 0)
                {
                    _parseListeners = null;
                }
            }
        }

        public virtual void RemoveParseListeners()
        {
            _parseListeners = null;
        }

        /// <summary>
        /// Notify any parse listeners (implemented as ParseTreeListener's)
        /// of an enter rule event.
        /// </summary>
        /// <remarks>
        /// Notify any parse listeners (implemented as ParseTreeListener's)
        /// of an enter rule event. This is not involved with
        /// parse tree walking in any way; it's just reusing the
        /// ParseTreeListener interface. This is not for the average user.
        /// </remarks>
        public virtual void TriggerEnterRuleEvent()
        {
            foreach (IParseTreeListener l in _parseListeners)
            {
                l.EnterEveryRule(_ctx);
                _ctx.EnterRule(l);
            }
        }

        /// <summary>
        /// Notify any parse listeners (implemented as ParseTreeListener's)
        /// of an exit rule event.
        /// </summary>
        /// <remarks>
        /// Notify any parse listeners (implemented as ParseTreeListener's)
        /// of an exit rule event. This is not involved with
        /// parse tree walking in any way; it's just reusing the
        /// ParseTreeListener interface. This is not for the average user.
        /// </remarks>
        public virtual void TriggerExitRuleEvent()
        {
            // reverse order walk of listeners
            for (int i = _parseListeners.Count - 1; i >= 0; i--)
            {
                IParseTreeListener l = _parseListeners[i];
                _ctx.ExitRule(l);
                l.ExitEveryRule(_ctx);
            }
        }

        /// <summary>Get number of recognition errors (lexer, parser, tree parser).</summary>
        /// <remarks>
        /// Get number of recognition errors (lexer, parser, tree parser).  Each
        /// recognizer tracks its own number.  So parser and lexer each have
        /// separate count.  Does not count the spurious errors found between
        /// an error and next valid token match
        /// See also reportError()
        /// </remarks>
        public virtual int GetNumberOfSyntaxErrors()
        {
            return _syntaxErrors;
        }

        public virtual IAntlrErrorStrategy GetErrorHandler()
        {
            return _errHandler;
        }

        public virtual void SetErrorHandler(IAntlrErrorStrategy handler)
        {
            this._errHandler = handler;
        }

        public override IIntStream GetInputStream()
        {
            return _input;
        }

        /// <summary>Set the token stream and reset the parser</summary>
        public virtual void SetInputStream(ITokenStream input)
        {
            this._input = null;
            Reset();
            this._input = input;
        }

        /// <summary>
        /// Match needs to return the current input symbol, which gets put
        /// into the label for the associated token ref; e.g., x=ID.
        /// </summary>
        /// <remarks>
        /// Match needs to return the current input symbol, which gets put
        /// into the label for the associated token ref; e.g., x=ID.
        /// </remarks>
        public virtual IToken GetCurrentToken()
        {
            return _input.Lt(1);
        }

        public virtual void NotifyErrorListeners(string msg)
        {
            NotifyErrorListeners(GetCurrentToken(), msg, null);
        }

        public virtual void NotifyErrorListeners(IToken offendingToken, string msg, RecognitionException
             e)
        {
            int line = -1;
            int charPositionInLine = -1;
            if (offendingToken != null)
            {
                line = offendingToken.Line;
                charPositionInLine = offendingToken.Column;
            }
            IAntlrErrorListener<IToken> listener = ((IParserErrorListener)GetErrorListenerDispatch
                ());
            listener.SyntaxError(this, offendingToken, line, charPositionInLine, msg, e);
        }

        /// <summary>Consume the current symbol and return it.</summary>
        /// <remarks>
        /// Consume the current symbol and return it. E.g., given the following
        /// input with A being the current lookahead symbol:
        /// A B
        /// ^
        /// this function moves the cursor to B and returns A.
        /// If the parser is creating parse trees, the current symbol
        /// would also be added as a child to the current context (node).
        /// Trigger listener events if there's a listener.
        /// </remarks>
        public virtual IToken Consume()
        {
            IToken o = GetCurrentToken();
            if (o.Type != Eof)
            {
                ((ITokenStream)GetInputStream()).Consume();
            }
            bool hasListener = _parseListeners != null && _parseListeners.Count != 0;
            if (_buildParseTrees || hasListener)
            {
                if (_errHandler.InErrorRecoveryMode(this))
                {
                    IErrorNode node = _ctx.AddErrorNode(o);
                    if (_parseListeners != null)
                    {
                        foreach (IParseTreeListener listener in _parseListeners)
                        {
                            listener.VisitErrorNode(node);
                        }
                    }
                }
                else
                {
                    ITerminalNode node = _ctx.AddChild(o);
                    if (_parseListeners != null)
                    {
                        foreach (IParseTreeListener listener in _parseListeners)
                        {
                            listener.VisitTerminal(node);
                        }
                    }
                }
            }
            return o;
        }

        protected internal virtual void AddContextToParseTree()
        {
            ParserRuleContext parent = (ParserRuleContext)_ctx.parent;
            // add current context to parent if we have a parent
            if (parent != null)
            {
                parent.AddChild(_ctx);
            }
        }

        /// <summary>Always called by generated parsers upon entry to a rule.</summary>
        /// <remarks>
        /// Always called by generated parsers upon entry to a rule.
        /// This occurs after the new context has been pushed. Access field
        /// _ctx get the current context.
        /// This is flexible because users do not have to regenerate parsers
        /// to get trace facilities.
        /// </remarks>
        public virtual void EnterRule(ParserRuleContext localctx, int state, int ruleIndex
            )
        {
            SetState(state);
            _ctx = localctx;
            _ctx.start = _input.Lt(1);
            if (_buildParseTrees)
            {
                AddContextToParseTree();
            }
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        public virtual void EnterLeftFactoredRule(ParserRuleContext localctx, int state, 
            int ruleIndex)
        {
            SetState(state);
            if (_buildParseTrees)
            {
                ParserRuleContext factoredContext = (ParserRuleContext)_ctx.GetChild(_ctx.ChildCount
                     - 1);
                _ctx.RemoveLastChild();
                factoredContext.parent = localctx;
                localctx.AddChild(factoredContext);
            }
            _ctx = localctx;
            _ctx.start = _input.Lt(1);
            if (_buildParseTrees)
            {
                AddContextToParseTree();
            }
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        public virtual void ExitRule()
        {
            _ctx.stop = _input.Lt(-1);
            // trigger event on _ctx, before it reverts to parent
            if (_parseListeners != null)
            {
                TriggerExitRuleEvent();
            }
            SetState(_ctx.invokingState);
            _ctx = (ParserRuleContext)_ctx.parent;
        }

        public virtual void EnterOuterAlt(ParserRuleContext localctx, int altNum)
        {
            // if we have new localctx, make sure we replace existing ctx
            // that is previous child of parse tree
            if (_buildParseTrees && _ctx != localctx)
            {
                ParserRuleContext parent = (ParserRuleContext)_ctx.parent;
                if (parent != null)
                {
                    parent.RemoveLastChild();
                    parent.AddChild(localctx);
                }
            }
            _ctx = localctx;
            _ctx.altNum = altNum;
        }

        public virtual void EnterRecursionRule(ParserRuleContext localctx, int ruleIndex, 
            int precedence)
        {
            _precedenceStack.Add(precedence);
            _ctx = localctx;
            _ctx.start = _input.Lt(1);
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        // simulates rule entry for left-recursive rules
        public virtual void PushNewRecursionContext(ParserRuleContext localctx, int state
            , int ruleIndex)
        {
            ParserRuleContext previous = _ctx;
            previous.parent = localctx;
            previous.invokingState = state;
            previous.stop = _input.Lt(-1);
            _ctx = localctx;
            _ctx.start = previous.start;
            if (_buildParseTrees)
            {
                _ctx.AddChild(previous);
            }
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        // simulates rule entry for left-recursive rules
        public virtual void UnrollRecursionContexts(ParserRuleContext _parentctx)
        {
            _precedenceStack.RemoveAt(_precedenceStack.Count - 1);
            _ctx.stop = _input.Lt(-1);
            ParserRuleContext retctx = _ctx;
            // save current ctx (return value)
            // unroll so _ctx is as it was before call to recursive method
            if (_parseListeners != null)
            {
                while (_ctx != _parentctx)
                {
                    TriggerExitRuleEvent();
                    _ctx = (ParserRuleContext)_ctx.parent;
                }
            }
            else
            {
                _ctx = _parentctx;
            }
            // hook into tree
            retctx.parent = _parentctx;
            if (_buildParseTrees)
            {
                _parentctx.AddChild(retctx);
            }
        }

        // add return ctx into invoking rule's tree
        public virtual ParserRuleContext GetInvokingContext(int ruleIndex)
        {
            ParserRuleContext p = _ctx;
            while (p != null)
            {
                if (p.GetRuleIndex() == ruleIndex)
                {
                    return p;
                }
                p = (ParserRuleContext)p.parent;
            }
            return null;
        }

        public virtual ParserRuleContext GetContext()
        {
            return _ctx;
        }

        public override bool Precpred(RuleContext localctx, int precedence)
        {
            return precedence >= _precedenceStack[_precedenceStack.Count - 1];
        }

        public override IAntlrErrorListener<IToken> GetErrorListenerDispatch()
        {
            return new ProxyParserErrorListener(GetErrorListeners());
        }

        public virtual bool InContext(string context)
        {
            // TODO: useful in parser?
            return false;
        }

        public virtual bool IsExpectedToken(int symbol)
        {
            //   		return getInterpreter().atn.nextTokens(_ctx);
            ATN atn = GetInterpreter().atn;
            ParserRuleContext ctx = _ctx;
            ATNState s = atn.states[GetState()];
            IntervalSet following = atn.NextTokens(s);
            if (following.Contains(symbol))
            {
                return true;
            }
            //        System.out.println("following "+s+"="+following);
            if (!following.Contains(IToken.Epsilon))
            {
                return false;
            }
            while (ctx != null && ctx.invokingState >= 0 && following.Contains(IToken.Epsilon
                ))
            {
                ATNState invokingState = atn.states[ctx.invokingState];
                RuleTransition rt = (RuleTransition)invokingState.Transition(0);
                following = atn.NextTokens(rt.followState);
                if (following.Contains(symbol))
                {
                    return true;
                }
                ctx = (ParserRuleContext)ctx.parent;
            }
            if (following.Contains(IToken.Epsilon) && symbol == IToken.Eof)
            {
                return true;
            }
            return false;
        }

        /// <summary>
        /// Compute the set of valid tokens reachable from the current
        /// position in the parse.
        /// </summary>
        /// <remarks>
        /// Compute the set of valid tokens reachable from the current
        /// position in the parse.
        /// </remarks>
        public virtual IntervalSet GetExpectedTokens()
        {
            ATN atn = GetInterpreter().atn;
            ParserRuleContext ctx = _ctx;
            ATNState s = atn.states[GetState()];
            IntervalSet following = atn.NextTokens(s);
            //        System.out.println("following "+s+"="+following);
            if (!following.Contains(IToken.Epsilon))
            {
                return following;
            }
            IntervalSet expected = new IntervalSet();
            expected.AddAll(following);
            expected.Remove(IToken.Epsilon);
            while (ctx != null && ctx.invokingState >= 0 && following.Contains(IToken.Epsilon
                ))
            {
                ATNState invokingState = atn.states[ctx.invokingState];
                RuleTransition rt = (RuleTransition)invokingState.Transition(0);
                following = atn.NextTokens(rt.followState);
                expected.AddAll(following);
                expected.Remove(IToken.Epsilon);
                ctx = (ParserRuleContext)ctx.parent;
            }
            if (following.Contains(IToken.Epsilon))
            {
                expected.Add(IToken.Eof);
            }
            return expected;
        }

        public virtual IntervalSet GetExpectedTokensWithinCurrentRule()
        {
            ATN atn = GetInterpreter().atn;
            ATNState s = atn.states[GetState()];
            return atn.NextTokens(s);
        }

        //	/** Compute the set of valid tokens reachable from the current
        //	 *  position in the parse.
        //	 */
        //	public IntervalSet nextTokens(@NotNull RuleContext ctx) {
        //		ATN atn = getInterpreter().atn;
        //		ATNState s = atn.states.get(ctx.s);
        //		if ( s == null ) return null;
        //		return atn.nextTokens(s, ctx);
        //	}
        public virtual ParserRuleContext GetRuleContext()
        {
            return _ctx;
        }

        /// <summary>
        /// Return List<String> of the rule names in your parser instance
        /// leading up to a call to the current rule.
        /// </summary>
        /// <remarks>
        /// Return List<String> of the rule names in your parser instance
        /// leading up to a call to the current rule.  You could override if
        /// you want more details such as the file/line info of where
        /// in the ATN a rule is invoked.
        /// This is very useful for error messages.
        /// </remarks>
        public virtual IList<string> GetRuleInvocationStack()
        {
            return GetRuleInvocationStack(_ctx);
        }

        public virtual IList<string> GetRuleInvocationStack(RuleContext p)
        {
            string[] ruleNames = GetRuleNames();
            IList<string> stack = new List<string>();
            while (p != null)
            {
                // compute what follows who invoked us
                int ruleIndex = p.GetRuleIndex();
                if (ruleIndex < 0)
                {
                    stack.Add("n/a");
                }
                else
                {
                    stack.Add(ruleNames[ruleIndex]);
                }
                p = p.parent;
            }
            return stack;
        }

        /// <summary>For debugging and other purposes</summary>
        public virtual IList<string> GetDFAStrings()
        {
            IList<string> s = new List<string>();
            for (int d = 0; d < _interp.atn.decisionToDFA.Length; d++)
            {
                DFA dfa = _interp.atn.decisionToDFA[d];
                s.Add(dfa.ToString(GetTokenNames(), GetRuleNames()));
            }
            return s;
        }

        /// <summary>For debugging and other purposes</summary>
        public virtual void DumpDFA()
        {
            bool seenOne = false;
            for (int d = 0; d < _interp.atn.decisionToDFA.Length; d++)
            {
                DFA dfa = _interp.atn.decisionToDFA[d];
                if (!dfa.IsEmpty())
                {
                    if (seenOne)
                    {
                        System.Console.Out.WriteLine();
                    }
                    System.Console.Out.WriteLine("Decision " + dfa.decision + ":");
                    System.Console.Out.Write(dfa.ToString(GetTokenNames(), GetRuleNames()));
                    seenOne = true;
                }
            }
        }

        public virtual string GetSourceName()
        {
            return _input.SourceName;
        }

        /// <summary>A convenience method for use most often with template rewrites.</summary>
        /// <remarks>
        /// A convenience method for use most often with template rewrites.
        /// Convert a List<Token> to List<String>
        /// </remarks>
        public virtual IList<string> ToStrings<_T0>(IList<_T0> tokens) where _T0:IToken
        {
            if (tokens == null)
            {
                return null;
            }
            IList<string> strings = new List<string>(tokens.Count);
            for (int i = 0; i < tokens.Count; i++)
            {
                strings.Add(tokens[i].Text);
            }
            return strings;
        }

        /// <summary>
        /// During a parse is sometimes useful to listen in on the rule entry and exit
        /// events as well as token matches.
        /// </summary>
        /// <remarks>
        /// During a parse is sometimes useful to listen in on the rule entry and exit
        /// events as well as token matches. This is for quick and dirty debugging.
        /// </remarks>
        public virtual void SetTrace(bool trace)
        {
            if (!trace)
            {
                RemoveParseListener(_tracer);
                _tracer = null;
            }
            else
            {
                if (_tracer != null)
                {
                    RemoveParseListener(_tracer);
                }
                else
                {
                    _tracer = new Parser.TraceListener(this);
                }
                AddParseListener(_tracer);
            }
        }
    }
}
