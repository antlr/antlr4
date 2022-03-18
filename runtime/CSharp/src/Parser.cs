/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.IO;
using System.Text;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Pattern;

namespace Antlr4.Runtime
{
    /// <summary>This is all the parsing support code essentially; most of it is error recovery stuff.</summary>
    /// <remarks>This is all the parsing support code essentially; most of it is error recovery stuff.</remarks>
    public abstract class Parser : Recognizer<IToken, ParserATNSimulator>
    {
        public class TraceListener : IParseTreeListener
        {

            public TraceListener(TextWriter output,Parser enclosing) {
                _output = output;
                _enclosing = enclosing;
            }

            public virtual void EnterEveryRule(ParserRuleContext ctx)
            {
                _output.WriteLine("enter   " + this._enclosing.RuleNames[ctx.RuleIndex] + ", LT(1)=" + this._enclosing._input.LT(1).Text);
            }

            public virtual void ExitEveryRule(ParserRuleContext ctx)
            {
                _output.WriteLine("exit    " + this._enclosing.RuleNames[ctx.RuleIndex] + ", LT(1)=" + this._enclosing._input.LT(1).Text);
            }

            public virtual void VisitErrorNode(IErrorNode node)
            {
            }

            public virtual void VisitTerminal(ITerminalNode node)
            {
                ParserRuleContext parent = (ParserRuleContext)((IRuleNode)node.Parent).RuleContext;
                IToken token = node.Symbol;
                _output.WriteLine("consume " + token + " rule " + this._enclosing.RuleNames[parent.RuleIndex]);
            }

            internal TraceListener(Parser _enclosing)
            {
                this._enclosing = _enclosing;
                _output = Console.Out;
            }

            private readonly Parser _enclosing;
            private readonly TextWriter _output;
        }

        public class TrimToSizeListener : IParseTreeListener
        {
            public static readonly Parser.TrimToSizeListener Instance = new Parser.TrimToSizeListener();

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
                if (ctx.children is List<IParseTree>)
                {
                    ((List<IParseTree>)ctx.children).TrimExcess();
                }
            }
        }

        /// <summary>
        /// This field maps from the serialized ATN string to the deserialized
        /// <see cref="Antlr4.Runtime.Atn.ATN"/>
        /// with
        /// bypass alternatives.
        /// </summary>
        /// <seealso cref="Antlr4.Runtime.Atn.ATNDeserializationOptions.GenerateRuleBypassTransitions()"/>
//        private static readonly IDictionary<string, ATN> bypassAltsAtnCache = new Dictionary<string, ATN>();
        private ATN bypassAltsAtnCache;

        /// <summary>The error handling strategy for the parser.</summary>
        /// <remarks>
        /// The error handling strategy for the parser. The default value is a new
        /// instance of
        /// <see cref="DefaultErrorStrategy"/>
        /// .
        /// </remarks>
        /// <seealso cref="ErrorHandler"/>
        [NotNull]
		private IAntlrErrorStrategy _errHandler = new DefaultErrorStrategy();

        /// <summary>The input stream.</summary>
        /// <remarks>The input stream.</remarks>
        /// <seealso cref="InputStream()"/>
    	private ITokenStream _input;

		private readonly List<int> _precedenceStack = new List<int> { 0 };

        /// <summary>
        /// The
        /// <see cref="ParserRuleContext"/>
        /// object for the currently executing rule.
        /// This is always non-null during the parsing process.
        /// </summary>
        private ParserRuleContext _ctx;

        /// <summary>
        /// Specifies whether or not the parser should construct a parse tree during
        /// the parsing process.
        /// </summary>
        /// <remarks>
        /// Specifies whether or not the parser should construct a parse tree during
        /// the parsing process. The default value is
        /// <see langword="true"/>
        /// .
        /// </remarks>
        /// <seealso cref="BuildParseTree"/>
        private bool _buildParseTrees = true;

        /// <summary>
        /// When
        /// <see cref="Trace"/>
        /// <c>(true)</c>
        /// is called, a reference to the
        /// <see cref="TraceListener"/>
        /// is stored here so it can be easily removed in a
        /// later call to
        /// <see cref="Trace"/>
        /// <c>(false)</c>
        /// . The listener itself is
        /// implemented as a parser listener so this field is not directly used by
        /// other parser methods.
        /// </summary>
        private Parser.TraceListener _tracer;

        /// <summary>
        /// The list of
        /// <see cref="Antlr4.Runtime.Tree.IParseTreeListener"/>
        /// listeners registered to receive
        /// events during the parse.
        /// </summary>
        /// <seealso cref="AddParseListener(Antlr4.Runtime.Tree.IParseTreeListener)"/>
        [Nullable]
        private IList<IParseTreeListener> _parseListeners;

        /// <summary>The number of syntax errors reported during parsing.</summary>
        /// <remarks>
        /// The number of syntax errors reported during parsing. This value is
        /// incremented each time
        /// <see cref="NotifyErrorListeners(string)"/>
        /// is called.
        /// </remarks>
        private int _syntaxErrors;

        protected readonly TextWriter Output;
        protected readonly TextWriter ErrorOutput;

        public Parser(ITokenStream input) : this(input, Console.Out, Console.Error) { }

        public Parser(ITokenStream input, TextWriter output, TextWriter errorOutput)
        {
            TokenStream = input;
            Output = output;
            ErrorOutput = errorOutput;
        }

        /// <summary>reset the parser's state</summary>
        public virtual void Reset()
        {
            if (((ITokenStream)InputStream) != null)
            {
                ((ITokenStream)InputStream).Seek(0);
            }
            _errHandler.Reset(this);
            _ctx = null;
            _syntaxErrors = 0;
            Trace = false;
            _precedenceStack.Clear();
            _precedenceStack.Add(0);
            ATNSimulator interpreter = Interpreter;
            if (interpreter != null)
            {
                interpreter.Reset();
            }
        }

        /// <summary>
        /// Match current input symbol against
        /// <paramref name="ttype"/>
        /// . If the symbol type
        /// matches,
        /// <see cref="IAntlrErrorStrategy.ReportMatch(Parser)"/>
        /// and
        /// <see cref="Consume()"/>
        /// are
        /// called to complete the match process.
        /// <p>If the symbol type does not match,
        /// <see cref="IAntlrErrorStrategy.RecoverInline(Parser)"/>
        /// is called on the current error
        /// strategy to attempt recovery. If
        /// <see cref="BuildParseTree()"/>
        /// is
        /// <see langword="true"/>
        /// and the token index of the symbol returned by
        /// <see cref="IAntlrErrorStrategy.RecoverInline(Parser)"/>
        /// is -1, the symbol is added to
        /// the parse tree by calling
        /// <see cref="ParserRuleContext.AddErrorNode(IToken)"/>
        /// .</p>
        /// </summary>
        /// <param name="ttype">the token type to match</param>
        /// <returns>the matched symbol</returns>
        /// <exception cref="RecognitionException">
        /// if the current input symbol did not match
        /// <paramref name="ttype"/>
        /// and the error strategy could not recover from the
        /// mismatched symbol
        /// </exception>
        /// <exception cref="Antlr4.Runtime.RecognitionException"/>
        [return: NotNull]
        public virtual IToken Match(int ttype)
        {
            IToken t = CurrentToken;
            if (t.Type == ttype)
            {
                _errHandler.ReportMatch(this);
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

        /// <summary>Match current input symbol as a wildcard.</summary>
        /// <remarks>
        /// Match current input symbol as a wildcard. If the symbol type matches
        /// (i.e. has a value greater than 0),
        /// <see cref="IAntlrErrorStrategy.ReportMatch(Parser)"/>
        /// and
        /// <see cref="Consume()"/>
        /// are called to complete the match process.
        /// <p>If the symbol type does not match,
        /// <see cref="IAntlrErrorStrategy.RecoverInline(Parser)"/>
        /// is called on the current error
        /// strategy to attempt recovery. If
        /// <see cref="BuildParseTree()"/>
        /// is
        /// <see langword="true"/>
        /// and the token index of the symbol returned by
        /// <see cref="IAntlrErrorStrategy.RecoverInline(Parser)"/>
        /// is -1, the symbol is added to
        /// the parse tree by calling
        /// <see cref="ParserRuleContext.AddErrorNode(IToken)"/>
        /// .</p>
        /// </remarks>
        /// <returns>the matched symbol</returns>
        /// <exception cref="RecognitionException">
        /// if the current input symbol did not match
        /// a wildcard and the error strategy could not recover from the mismatched
        /// symbol
        /// </exception>
        /// <exception cref="Antlr4.Runtime.RecognitionException"/>
        [return: NotNull]
        public virtual IToken MatchWildcard()
        {
            IToken t = CurrentToken;
            if (t.Type > 0)
            {
                _errHandler.ReportMatch(this);
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
        /// Track the
        /// <see cref="ParserRuleContext"/>
        /// objects during the parse and hook
        /// them up using the
        /// <see cref="ParserRuleContext.children"/>
        /// list so that it
        /// forms a parse tree. The
        /// <see cref="ParserRuleContext"/>
        /// returned from the start
        /// rule represents the root of the parse tree.
        /// <p>Note that if we are not building parse trees, rule contexts only point
        /// upwards. When a rule exits, it returns the context but that gets garbage
        /// collected if nobody holds a reference. It points upwards but nobody
        /// points at it.</p>
        /// <p>When we build parse trees, we are adding all of these contexts to
        /// <see cref="ParserRuleContext.children"/>
        /// list. Contexts are then not candidates
        /// for garbage collection.</p>
        /// </summary>
        /// <summary>
        /// Gets whether or not a complete parse tree will be constructed while
        /// parsing.
        /// </summary>
        /// <remarks>
        /// Gets whether or not a complete parse tree will be constructed while
        /// parsing. This property is
        /// <see langword="true"/>
        /// for a newly constructed parser.
        /// </remarks>
        /// <returns>
        ///
        /// <see langword="true"/>
        /// if a complete parse tree will be constructed while
        /// parsing, otherwise
        /// <see langword="false"/>
        /// </returns>
        public virtual bool BuildParseTree
        {
            get
            {
                return _buildParseTrees;
            }
            set
            {
				this._buildParseTrees = value;
            }
        }

        /// <summary>Trim the internal lists of the parse tree during parsing to conserve memory.</summary>
        /// <remarks>
        /// Trim the internal lists of the parse tree during parsing to conserve memory.
        /// This property is set to
        /// <see langword="false"/>
        /// by default for a newly constructed parser.
        /// </remarks>
        /// <value>
        ///
        /// <see langword="true"/>
        /// to trim the capacity of the
        /// <see cref="ParserRuleContext.children"/>
        /// list to its size after a rule is parsed.
        /// </value>
        /// <returns>
        ///
        /// <see langword="true"/>
        /// if the
        /// <see cref="ParserRuleContext.children"/>
        /// list is trimmed
        /// using the default
        /// <see cref="TrimToSizeListener"/>
        /// during the parse process.
        /// </returns>
        public virtual bool TrimParseTree
        {
            get
            {
                return ParseListeners.Contains(Parser.TrimToSizeListener.Instance);
            }
            set
            {
                bool trimParseTrees = value;
                if (trimParseTrees)
                {
                    if (TrimParseTree)
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
        }

        public virtual IList<IParseTreeListener> ParseListeners
        {
            get
            {
                IList<IParseTreeListener> listeners = _parseListeners;
                if (listeners == null)
                {
                    return Sharpen.Collections.EmptyList<IParseTreeListener>();
                }
                return listeners;
            }
        }

        /// <summary>
        /// Registers
        /// <paramref name="listener"/>
        /// to receive events during the parsing process.
        /// <p>To support output-preserving grammar transformations (including but not
        /// limited to left-recursion removal, automated left-factoring, and
        /// optimized code generation), calls to listener methods during the parse
        /// may differ substantially from calls made by
        /// <see cref="Antlr4.Runtime.Tree.ParseTreeWalker.Default"/>
        /// used after the parse is complete. In
        /// particular, rule entry and exit events may occur in a different order
        /// during the parse than after the parser. In addition, calls to certain
        /// rule entry methods may be omitted.</p>
        /// <p>With the following specific exceptions, calls to listener events are
        /// <em>deterministic</em>, i.e. for identical input the calls to listener
        /// methods will be the same.</p>
        /// <ul>
        /// <li>Alterations to the grammar used to generate code may change the
        /// behavior of the listener calls.</li>
        /// <li>Alterations to the command line options passed to ANTLR 4 when
        /// generating the parser may change the behavior of the listener calls.</li>
        /// <li>Changing the version of the ANTLR Tool used to generate the parser
        /// may change the behavior of the listener calls.</li>
        /// </ul>
        /// </summary>
        /// <param name="listener">the listener to add</param>
        /// <exception cref="System.ArgumentNullException">
        /// if
        /// <c/>
        /// listener is
        /// <see langword="null"/>
        /// </exception>
        public virtual void AddParseListener(IParseTreeListener listener)
        {
            if (listener == null)
            {
                throw new ArgumentNullException("listener");
            }
            if (_parseListeners == null)
            {
                _parseListeners = new List<IParseTreeListener>();
            }
            this._parseListeners.Add(listener);
        }

        /// <summary>
        /// Remove
        /// <paramref name="listener"/>
        /// from the list of parse listeners.
        /// <p>If
        /// <paramref name="listener"/>
        /// is
        /// <see langword="null"/>
        /// or has not been added as a parse
        /// listener, this method does nothing.</p>
        /// </summary>
        /// <seealso cref="AddParseListener(Antlr4.Runtime.Tree.IParseTreeListener)"/>
        /// <param name="listener">the listener to remove</param>
        public virtual void RemoveParseListener(IParseTreeListener listener)
        {
            if (_parseListeners != null)
            {
                if (_parseListeners.Remove(listener))
                {
                    if (_parseListeners.Count == 0)
                    {
                        _parseListeners = null;
                    }
                }
            }
        }

        /// <summary>Remove all parse listeners.</summary>
        /// <remarks>Remove all parse listeners.</remarks>
        /// <seealso cref="AddParseListener(Antlr4.Runtime.Tree.IParseTreeListener)"/>
        public virtual void RemoveParseListeners()
        {
            _parseListeners = null;
        }

        /// <summary>Notify any parse listeners of an enter rule event.</summary>
        /// <remarks>Notify any parse listeners of an enter rule event.</remarks>
        /// <seealso cref="AddParseListener(Antlr4.Runtime.Tree.IParseTreeListener)"/>
        protected internal virtual void TriggerEnterRuleEvent()
        {
            foreach (IParseTreeListener listener in _parseListeners)
            {
                listener.EnterEveryRule(_ctx);
                _ctx.EnterRule(listener);
            }
        }

        /// <summary>Notify any parse listeners of an exit rule event.</summary>
        /// <remarks>Notify any parse listeners of an exit rule event.</remarks>
        /// <seealso cref="AddParseListener(Antlr4.Runtime.Tree.IParseTreeListener)"/>
        protected internal virtual void TriggerExitRuleEvent()
        {
            // reverse order walk of listeners
			if (_parseListeners != null) {
				for (int i = _parseListeners.Count - 1; i >= 0; i--) {
					IParseTreeListener listener = _parseListeners [i];
					_ctx.ExitRule (listener);
					listener.ExitEveryRule (_ctx);
				}
			}
        }

        /// <summary>Gets the number of syntax errors reported during parsing.</summary>
        /// <remarks>
        /// Gets the number of syntax errors reported during parsing. This value is
        /// incremented each time
        /// <see cref="NotifyErrorListeners(string)"/>
        /// is called.
        /// </remarks>
        /// <seealso cref="NotifyErrorListeners(string)"/>
        public virtual int NumberOfSyntaxErrors
        {
            get
            {
                return _syntaxErrors;
            }
        }

        public virtual ITokenFactory TokenFactory
        {
            get
            {
                return _input.TokenSource.TokenFactory;
            }
        }

        /// <summary>
        /// The ATN with bypass alternatives is expensive to create so we create it
        /// lazily.
        /// </summary>
        /// <remarks>
        /// The ATN with bypass alternatives is expensive to create so we create it
        /// lazily.
        /// </remarks>
        /// <exception cref="System.NotSupportedException">
        /// if the current parser does not
        /// implement the
        /// <see cref="Recognizer{Symbol, ATNInterpreter}.SerializedAtn()"/>
        /// method.
        /// </exception>
        [return: NotNull]
        public virtual ATN GetATNWithBypassAlts()
        {
            int[] serializedAtn = SerializedAtn;
            if (serializedAtn == null)
            {
                throw new NotSupportedException("The current parser does not support an ATN with bypass alternatives.");
            }
            lock (this)
            {
                if ( bypassAltsAtnCache!=null ) {
                    return bypassAltsAtnCache;
                }
                ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
                deserializationOptions.GenerateRuleBypassTransitions = true;
                bypassAltsAtnCache = new ATNDeserializer(deserializationOptions).Deserialize(serializedAtn);
                return bypassAltsAtnCache;
            }
        }

        /// <summary>The preferred method of getting a tree pattern.</summary>
        /// <remarks>
        /// The preferred method of getting a tree pattern. For example, here's a
        /// sample use:
        /// <pre>
        /// ParseTree t = parser.expr();
        /// ParseTreePattern p = parser.compileParseTreePattern("&lt;ID&gt;+0", MyParser.RULE_expr);
        /// ParseTreeMatch m = p.match(t);
        /// String id = m.get("ID");
        /// </pre>
        /// </remarks>
        public virtual ParseTreePattern CompileParseTreePattern(string pattern, int patternRuleIndex)
        {
            if (((ITokenStream)InputStream) != null)
            {
                ITokenSource tokenSource = ((ITokenStream)InputStream).TokenSource;
                if (tokenSource is Lexer)
                {
                    Lexer lexer = (Lexer)tokenSource;
                    return CompileParseTreePattern(pattern, patternRuleIndex, lexer);
                }
            }
            throw new NotSupportedException("Parser can't discover a lexer to use");
        }

        /// <summary>
        /// The same as
        /// <see cref="CompileParseTreePattern(string, int)"/>
        /// but specify a
        /// <see cref="Lexer"/>
        /// rather than trying to deduce it from this parser.
        /// </summary>
        public virtual ParseTreePattern CompileParseTreePattern(string pattern, int patternRuleIndex, Lexer lexer)
        {
            ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
            return m.Compile(pattern, patternRuleIndex);
        }

        public virtual IAntlrErrorStrategy ErrorHandler
        {
            get
            {
                return _errHandler;
            }
            set
            {
                IAntlrErrorStrategy handler = value;
                this._errHandler = handler;
            }
        }

        public override IIntStream InputStream
		{
			get
			{
				return _input;
			}
		}

		public ITokenStream TokenStream
		{
			get
			{
				return _input;
			}
			set
			{
				this._input = null;
				Reset ();
				this._input = value;
			}
		}

        /// <summary>
        /// Match needs to return the current input symbol, which gets put
        /// into the label for the associated token ref; e.g., x=ID.
        /// </summary>
        /// <remarks>
        /// Match needs to return the current input symbol, which gets put
        /// into the label for the associated token ref; e.g., x=ID.
        /// </remarks>
        public virtual IToken CurrentToken
        {
            get
            {
                return _input.LT(1);
            }
        }

        public void NotifyErrorListeners(string msg)
        {
            NotifyErrorListeners(CurrentToken, msg, null);
        }

        public virtual void NotifyErrorListeners(IToken offendingToken, string msg, RecognitionException e)
        {
            _syntaxErrors++;
            int line = -1;
            int charPositionInLine = -1;
            if (offendingToken != null)
            {
                line = offendingToken.Line;
                charPositionInLine = offendingToken.Column;
            }
            IAntlrErrorListener<IToken> listener = ((IParserErrorListener)ErrorListenerDispatch);
            listener.SyntaxError(ErrorOutput, this, offendingToken, line, charPositionInLine, msg, e);
        }

        /// <summary>
        /// Consume and return the
        /// <linkplain>
        /// #getCurrentToken
        /// current symbol
        /// </linkplain>
        /// .
        /// <p>E.g., given the following input with
        /// <c>A</c>
        /// being the current
        /// lookahead symbol, this function moves the cursor to
        /// <c>B</c>
        /// and returns
        /// <c>A</c>
        /// .</p>
        /// <pre>
        /// A B
        /// ^
        /// </pre>
        /// If the parser is not in error recovery mode, the consumed symbol is added
        /// to the parse tree using
        /// <see cref="ParserRuleContext.AddChild(IToken)"/>
        /// , and
        /// <see cref="Antlr4.Runtime.Tree.IParseTreeListener.VisitTerminal(Antlr4.Runtime.Tree.ITerminalNode)"/>
        /// is called on any parse listeners.
        /// If the parser <em>is</em> in error recovery mode, the consumed symbol is
        /// added to the parse tree using
        /// <see cref="ParserRuleContext.AddErrorNode(IToken)"/>
        /// , and
        /// <see cref="Antlr4.Runtime.Tree.IParseTreeListener.VisitErrorNode(Antlr4.Runtime.Tree.IErrorNode)"/>
        /// is called on any parse
        /// listeners.
        /// </summary>
        public virtual IToken Consume()
        {
            IToken o = CurrentToken;
            if (o.Type != Eof)
            {
                ((ITokenStream)InputStream).Consume();
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
            ParserRuleContext parent = (ParserRuleContext)_ctx.Parent;
            // add current context to parent if we have a parent
            if (parent != null)
            {
                parent.AddChild(_ctx);
            }
        }

        /// <summary>Always called by generated parsers upon entry to a rule.</summary>
        /// <remarks>
        /// Always called by generated parsers upon entry to a rule. Access field
        /// <see cref="_ctx"/>
        /// get the current context.
        /// </remarks>
        public virtual void EnterRule(ParserRuleContext localctx, int state, int ruleIndex)
        {
            State = state;
            _ctx = localctx;
            _ctx.Start = _input.LT(1);
            if (_buildParseTrees)
            {
                AddContextToParseTree();
            }
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        public virtual void EnterLeftFactoredRule(ParserRuleContext localctx, int state, int ruleIndex)
        {
            State = state;
            if (_buildParseTrees)
            {
                ParserRuleContext factoredContext = (ParserRuleContext)_ctx.GetChild(_ctx.ChildCount - 1);
                _ctx.RemoveLastChild();
                factoredContext.Parent = localctx;
                localctx.AddChild(factoredContext);
            }
            _ctx = localctx;
            _ctx.Start = _input.LT(1);
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
            _ctx.Stop = _input.LT(-1);
            // trigger event on _ctx, before it reverts to parent
            if (_parseListeners != null)
            {
                TriggerExitRuleEvent();
            }
            State = _ctx.invokingState;
            _ctx = (ParserRuleContext)_ctx.Parent;
        }

        public virtual void EnterOuterAlt(ParserRuleContext localctx, int altNum)
        {
        	localctx.setAltNumber(altNum);
            // if we have new localctx, make sure we replace existing ctx
            // that is previous child of parse tree
            if (_buildParseTrees && _ctx != localctx)
            {
                ParserRuleContext parent = (ParserRuleContext)_ctx.Parent;
                if (parent != null)
                {
                    parent.RemoveLastChild();
                    parent.AddChild(localctx);
                }
            }
            _ctx = localctx;
        }

        /// <summary>Get the precedence level for the top-most precedence rule.</summary>
        /// <remarks>Get the precedence level for the top-most precedence rule.</remarks>
        /// <returns>
        /// The precedence level for the top-most precedence rule, or -1 if
        /// the parser context is not nested within a precedence rule.
        /// </returns>
        public int Precedence
        {
            get
            {
                if (_precedenceStack.Count == 0)
                {
                    return -1;
                }
                return _precedenceStack[_precedenceStack.Count - 1];
            }
        }

        [Obsolete(@"UseEnterRecursionRule(ParserRuleContext, int, int, int) instead.")]
        public virtual void EnterRecursionRule(ParserRuleContext localctx, int ruleIndex)
        {
            EnterRecursionRule(localctx, Atn.ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0);
        }

        public virtual void EnterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex, int precedence)
        {
            State = state;
            _precedenceStack.Add(precedence);
            _ctx = localctx;
            _ctx.Start = _input.LT(1);
            if (_parseListeners != null)
            {
                TriggerEnterRuleEvent();
            }
        }

        // simulates rule entry for left-recursive rules
        /// <summary>
        /// Like
        /// <see cref="EnterRule(ParserRuleContext, int, int)"/>
        /// but for recursive rules.
        /// </summary>
        public virtual void PushNewRecursionContext(ParserRuleContext localctx, int state, int ruleIndex)
        {
            ParserRuleContext previous = _ctx;
            previous.Parent = localctx;
            previous.invokingState = state;
            previous.Stop = _input.LT(-1);
            _ctx = localctx;
            _ctx.Start = previous.Start;
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
            _ctx.Stop = _input.LT(-1);
            ParserRuleContext retctx = _ctx;
            // save current ctx (return value)
            // unroll so _ctx is as it was before call to recursive method
            if (_parseListeners != null)
            {
                while (_ctx != _parentctx)
                {
                    TriggerExitRuleEvent();
                    _ctx = (ParserRuleContext)_ctx.Parent;
                }
            }
            else
            {
                _ctx = _parentctx;
            }
            // hook into tree
            retctx.Parent = _parentctx;
            if (_buildParseTrees && _parentctx != null)
            {
                // add return ctx into invoking rule's tree
                _parentctx.AddChild(retctx);
            }
        }

        public virtual ParserRuleContext GetInvokingContext(int ruleIndex)
        {
            ParserRuleContext p = _ctx;
            while (p != null)
            {
                if (p.RuleIndex == ruleIndex)
                {
                    return p;
                }
                p = (ParserRuleContext)p.Parent;
            }
            return null;
        }

        public virtual ParserRuleContext Context
        {
            get
            {
                return _ctx;
            }
            set
            {
                ParserRuleContext ctx = value;
                _ctx = ctx;
            }
        }

        public override bool Precpred(RuleContext localctx, int precedence)
        {
            return precedence >= _precedenceStack[_precedenceStack.Count - 1];
        }

        public new IParserErrorListener ErrorListenerDispatch
        {
            get
            {
                return new ProxyParserErrorListener(ErrorListeners);
            }
        }

        public virtual bool InContext(string context)
        {
            // TODO: useful in parser?
            return false;
        }

        /// <summary>
        /// Checks whether or not
        /// <paramref name="symbol"/>
        /// can follow the current state in the
        /// ATN. The behavior of this method is equivalent to the following, but is
        /// implemented such that the complete context-sensitive follow set does not
        /// need to be explicitly constructed.
        /// <pre>
        /// return getExpectedTokens().contains(symbol);
        /// </pre>
        /// </summary>
        /// <param name="symbol">the symbol type to check</param>
        /// <returns>
        ///
        /// <see langword="true"/>
        /// if
        /// <paramref name="symbol"/>
        /// can follow the current state in
        /// the ATN, otherwise
        /// <see langword="false"/>
        /// .
        /// </returns>
        public virtual bool IsExpectedToken(int symbol)
        {
            //   		return getInterpreter().atn.nextTokens(_ctx);
            ATN atn = Interpreter.atn;
            ParserRuleContext ctx = _ctx;
            ATNState s = atn.states[State];
            IntervalSet following = atn.NextTokens(s);
            if (following.Contains(symbol))
            {
                return true;
            }
            //        System.out.println("following "+s+"="+following);
            if (!following.Contains(TokenConstants.EPSILON))
            {
                return false;
            }
            while (ctx != null && ctx.invokingState >= 0 && following.Contains(TokenConstants.EPSILON))
            {
                ATNState invokingState = atn.states[ctx.invokingState];
                RuleTransition rt = (RuleTransition)invokingState.Transition(0);
                following = atn.NextTokens(rt.followState);
                if (following.Contains(symbol))
                {
                    return true;
                }
                ctx = (ParserRuleContext)ctx.Parent;
            }
            if (following.Contains(TokenConstants.EPSILON) && symbol == TokenConstants.EOF)
            {
                return true;
            }
            return false;
        }

        /// <summary>
        /// Computes the set of input symbols which could follow the current parser
        /// state and context, as given by
        /// <see cref="Recognizer{Symbol, ATNInterpreter}.State()"/>
        /// and
        /// <see cref="Context()"/>
        /// ,
        /// respectively.
        /// </summary>
        /// <seealso cref="Antlr4.Runtime.Atn.ATN.GetExpectedTokens(int, RuleContext)"/>
        [return: NotNull]
        public virtual IntervalSet GetExpectedTokens()
        {
            return Atn.GetExpectedTokens(State, Context);
        }

        [return: NotNull]
        public virtual IntervalSet GetExpectedTokensWithinCurrentRule()
        {
            ATN atn = Interpreter.atn;
            ATNState s = atn.states[State];
            return atn.NextTokens(s);
        }

        /// <summary>
        /// Get a rule's index (i.e.,
        /// <c>RULE_ruleName</c>
        /// field) or -1 if not found.
        /// </summary>
        public virtual int GetRuleIndex(string ruleName)
        {
            int ruleIndex;
            if (RuleIndexMap.TryGetValue(ruleName, out ruleIndex))
            {
                return ruleIndex;
            }
            return -1;
        }

        public virtual ParserRuleContext RuleContext
        {
            get
            {
                return _ctx;
            }
        }

        /// <summary>
        /// Return List&lt;String&gt; of the rule names in your parser instance
        /// leading up to a call to the current rule.
        /// </summary>
        /// <remarks>
        /// Return List&lt;String&gt; of the rule names in your parser instance
        /// leading up to a call to the current rule.  You could override if
        /// you want more details such as the file/line info of where
        /// in the ATN a rule is invoked.
        /// This is very useful for error messages.
        /// </remarks>
        public virtual IList<string> GetRuleInvocationStack()
        {
            return GetRuleInvocationStack(_ctx);
        }

		public virtual string GetRuleInvocationStackAsString()
		{
			StringBuilder sb = new StringBuilder ("[");
			foreach (string s in GetRuleInvocationStack()) {
				sb.Append (s);
				sb.Append (", ");
			}
			sb.Length = sb.Length - 2;
			sb.Append ("]");
			return sb.ToString ();
		}

        public virtual IList<string> GetRuleInvocationStack(RuleContext p)
        {
            string[] ruleNames = RuleNames;
            IList<string> stack = new List<string>();
            while (p != null)
            {
                // compute what follows who invoked us
                int ruleIndex = p.RuleIndex;
                if (ruleIndex < 0)
                {
                    stack.Add("n/a");
                }
                else
                {
                    stack.Add(ruleNames[ruleIndex]);
                }
                p = p.Parent;
            }
            return stack;
        }

        /// <summary>For debugging and other purposes.</summary>
        /// <remarks>For debugging and other purposes.</remarks>
        public virtual IList<string> GetDFAStrings()
        {
            IList<string> s = new List<string>();
            for (int d = 0; d < Interpreter.atn.decisionToDFA.Length; d++)
            {
				DFA dfa = Interpreter.atn.decisionToDFA[d];
                s.Add(dfa.ToString(Vocabulary));
            }
            return s;
        }

        /// <summary>For debugging and other purposes.</summary>
        /// <remarks>For debugging and other purposes.</remarks>
        public virtual void DumpDFA()
        {
            bool seenOne = false;
			for (int d = 0; d < Interpreter.decisionToDFA.Length; d++)
            {
				DFA dfa = Interpreter.decisionToDFA[d];
				if (dfa.states.Count>0)
                {
                    if (seenOne)
                    {
                        Output.WriteLine();
                    }
                    Output.WriteLine("Decision " + dfa.decision + ":");
                    Output.Write(dfa.ToString(Vocabulary));
                    seenOne = true;
                }
            }
        }

        public virtual string SourceName
        {
            get
            {
                return _input.SourceName;
            }
        }

        public override ParseInfo ParseInfo
        {
            get
            {
                ParserATNSimulator interp = Interpreter;
                if (interp is ProfilingATNSimulator)
                {
                    return new ParseInfo((ProfilingATNSimulator)interp);
                }
                return null;
            }
        }

        /// <since>4.3</since>
        public virtual bool Profile
        {
            set
            {
                bool profile = value;
                ParserATNSimulator interp = Interpreter;
                if (profile)
                {
                    if (!(interp is ProfilingATNSimulator))
                    {
                        Interpreter = new ProfilingATNSimulator(this);
                    }
                }
                else
                {
                    if (interp is ProfilingATNSimulator)
                    {
                        Interpreter = new ParserATNSimulator(this, Atn, null, null);
                    }
                }
            }
        }

        /// <summary>
        /// During a parse is sometimes useful to listen in on the rule entry and exit
        /// events as well as token matches.
        /// </summary>
        /// <remarks>
        /// During a parse is sometimes useful to listen in on the rule entry and exit
        /// events as well as token matches. This is for quick and dirty debugging.
        /// </remarks>
        public virtual bool Trace
        {
            get
            {
                foreach (object o in ParseListeners)
                {
                    if (o is Parser.TraceListener)
                    {
                        return true;
                    }
                }
                return false;
            }
            set
            {
                bool trace = value;
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
}
