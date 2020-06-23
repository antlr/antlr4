/*
 * Copyright (c) 2012-2018 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.Parser;

import antlr.v4.runtime.ANTLRErrorListener;
import antlr.v4.runtime.ANTLRErrorStrategy;
import antlr.v4.runtime.CommonToken;
import antlr.v4.runtime.DefaultErrorStrategy;
import antlr.v4.runtime.IntStream;
import antlr.v4.runtime.InterfaceParser;
import antlr.v4.runtime.InterfaceRuleContext;
import antlr.v4.runtime.Lexer;
import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.RecognitionException;
import antlr.v4.runtime.Recognizer;
import antlr.v4.runtime.RuleContext;
import antlr.v4.runtime.Token;
import antlr.v4.runtime.TokenConstantDefinition;
import antlr.v4.runtime.TokenFactory;
import antlr.v4.runtime.TokenSource;
import antlr.v4.runtime.TokenStream;
import antlr.v4.runtime.UnsupportedOperationException;
import antlr.v4.runtime.atn.ATN;
import antlr.v4.runtime.atn.ATNDeserializationOptions;
import antlr.v4.runtime.atn.ATNDeserializer;
import antlr.v4.runtime.atn.ATNSimulator;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.ParseInfo;
import antlr.v4.runtime.atn.ParserATNSimulator;
import antlr.v4.runtime.atn.PredictionMode;
import antlr.v4.runtime.atn.ProfilingATNSimulator;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.misc;
import antlr.v4.runtime.tree.ErrorNode;
import antlr.v4.runtime.tree.ParseTreeListener;
import antlr.v4.runtime.tree.TerminalNode;
import antlr.v4.runtime.tree.pattern.ParseTreePattern;
import antlr.v4.runtime.tree.pattern.ParseTreePatternMatcher;
import std.algorithm;
import std.conv;
import std.stdio;

/**
 * TODO add class description
 */
abstract class Parser : Recognizer!(Token, ParserATNSimulator), InterfaceParser
{
    // Class TraceListener
    /**
     * TODO add class description
     */
    class TraceListener : ParseTreeListener
    {

        public void enterEveryRule(ParserRuleContext ctx)
        {
            writeln("enter   " ~ getRuleNames()[ctx.getRuleIndex()] ~
                    ", LT(1)=" ~ to!string(_input.LT(1).getText));
        }

        public void visitTerminal(TerminalNode node)
        {
            writeln("consume " ~ to!string(node.getSymbol.getText) ~ " rule " ~
                    getRuleNames()[ctx_.getRuleIndex()]);
        }

        public void visitErrorNode(ErrorNode node)
        {
        }

        /**
         * @uml
         * @override
         */
        public override void exitEveryRule(ParserRuleContext ctx)
        {
            writeln("exit   " ~ getRuleNames()[ctx.getRuleIndex()] ~
                    ", LT(1)=" ~ to!string(_input.LT(1).getText));
        }

    }

    // Singleton TrimToSizeListener
    /**
     * TODO add class description
     */
    static class TrimToSizeListener : ParseTreeListener
    {

        /**
         * The single instance of TrimToSizeListener.
         */
        private static __gshared Parser.TrimToSizeListener instance_;

        public void enterEveryRule(ParserRuleContext ctx)
        {
        }

        public void visitTerminal(TerminalNode node)
        {
        }

        public void visitErrorNode(ErrorNode node)
        {
        }

        public void exitEveryRule(ParserRuleContext ctx)
        {
            // if (ctx.children.classinfo == ArrayList.classinfo) {
            //     ((ArrayList<?>)ctx.children).trimToSize();
            // }
        }

        /**
         * Creates the single instance of TrimToSizeListener.
         */
        private shared static this()
        {
            instance_ = new TrimToSizeListener;
        }

        /**
         * Returns: A single instance of TrimToSizeListener.
         */
        public static TrimToSizeListener instance()
        {
            return instance_;
        }

    }

    /**
     * @uml
     * This field maps from the serialized ATN string to the deserialized {@link ATN} with
     * bypass alternatives.
     *
     * @see ATNDeserializationOptions#isGenerateRuleBypassTransitions()
     */
    private ATN[wstring] bypassAltsAtnCache;

    protected ANTLRErrorStrategy _errHandler;

    protected TokenStream _input;

    public IntegerStack _precedenceStack;

    /**
     * @uml
     * The {@link ParserRuleContext} object for the currently executing rule.
     * This is always non-null during the parsing process.
     * @read
     * @write
     */
    public ParserRuleContext ctx_;

    /**
     * @uml
     * Specifies whether or not the parser should construct a parse tree during
     * the parsing process. The default value is {@code true}.
     *
     * @see #getBuildParseTree
     * @see #setBuildParseTree
     */
    protected bool _buildParseTrees = true;

    public TraceListener _tracer;

    /**
     * @uml
     * The list of {@link ParseTreeListener} listeners registered to receive
     * events during the parse.
     *
     *  @see #addParseListener
     */
    public ParseTreeListener[] _parseListeners;

    /**
     * @uml
     * The number of syntax errors reported during parsing. This value is
     * incremented each time {@link #notifyErrorListeners} is called.
     * @read
     */
    private int numberOfSyntaxErrors_;

    /**
     * @uml
     * Indicates parser has match()ed EOF token. See {@link #exitRule()}.
     */
    public bool matchedEOF;

    public this()
    {
    }

    public this(TokenStream input)
    {
        setInputStream(input);
    }

    /**
     * @uml
     * reset the parser's state
     */
    public void reset()
    {
        if (getInputStream() !is null)
            getInputStream().seek(0);
        _errHandler = new DefaultErrorStrategy;
        _errHandler.reset(this);
        ctx_ = null;
        numberOfSyntaxErrors_ = 0;
        matchedEOF = false;
        _precedenceStack = new IntegerStack();
        _precedenceStack.clear;
        _precedenceStack.push(0);
        ATNSimulator interpreter = getInterpreter();
        if (interpreter !is null) {
            interpreter.reset();
        }
    }

    /**
     * @uml
     * Match current input symbol against {@code ttype}. If the symbol type
     * matches, {@link ANTLRErrorStrategy#reportMatch} and {@link #consume} are
     * called to complete the match process.
     *
     * <p>If the symbol type does not match,
     * {@link ANTLRErrorStrategy#recoverInline} is called on the current error
     * strategy to attempt recovery. If {@link #getBuildParseTree} is
     * {@code true} and the token index of the symbol returned by
     * {@link ANTLRErrorStrategy#recoverInline} is -1, the symbol is added to
     * the parse tree by calling {@link ParserRuleContext#addErrorNode}.</p>
     *
     *  @param ttype the token type to match
     *  @return the matched symbol
     *  @throws RecognitionException if the current input symbol did not match
     *  {@code ttype} and the error strategy could not recover from the
     *  mismatched symbol
     */
    public Token match(int ttype)
    {
        Token t = getCurrentToken;

        debug(Parser) {
            import std.stdio;
            writefln("Parser: match  %s, currentToken = %s", ttype, t);
        }

        if (t.getType == ttype) {
            if (ttype == TokenConstantDefinition.EOF)
                {
                    matchedEOF = true;
                }
            _errHandler.reportMatch(this);
            consume();
        }
        else {
            t = _errHandler.recoverInline(this);
            if (_buildParseTrees && t.getTokenIndex == -1) {
                // we must have conjured up a new token during single token insertion
                // if it's not the current symbol
                ctx_.addErrorNode(t);
            }
        }
        return t;
    }

    /**
     * @uml
     * Match current input symbol as a wildcard. If the symbol type matches
     * (i.e. has a value greater than 0), {@link ANTLRErrorStrategy#reportMatch}
     * and {@link #consume} are called to complete the match process.
     *
     * <p>If the symbol type does not match,
     * {@link ANTLRErrorStrategy#recoverInline} is called on the current error
     * strategy to attempt recovery. If {@link #getBuildParseTree} is
     * {@code true} and the token index of the symbol returned by
     * {@link ANTLRErrorStrategy#recoverInline} is -1, the symbol is added to
     * the parse tree by calling {@link ParserRuleContext#addErrorNode}.</p>
     *
     *  @return the matched symbol
     *  @throws RecognitionException if the current input symbol did not match
     *  a wildcard and the error strategy could not recover from the mismatched
     *  symbol
     */
    public Token matchWildcard()
    {
        Token t = getCurrentToken();
        if (t.getType() > 0) {
            _errHandler.reportMatch(this);
            consume();
        }
        else {
            t = _errHandler.recoverInline(this);
            if (_buildParseTrees && t.getTokenIndex() == -1) {
                // we must have conjured up a new token during single token insertion
                // if it's not the current symbol
                ctx_.addErrorNode(t);
            }
        }
        return t;
    }

    /**
     * @uml
     * Track the {@link ParserRuleContext} objects during the parse and hook
     * them up using the {@link ParserRuleContext#children} list so that it
     * forms a parse tree. The {@link ParserRuleContext} returned from the start
     * rule represents the root of the parse tree.
     *
     * <p>Note that if we are not building parse trees, rule contexts only point
     * upwards. When a rule exits, it returns the context but that gets garbage
     * collected if nobody holds a reference. It points upwards but nobody
     * points at it.</p>
     *
     * <p>When we build parse trees, we are adding all of these contexts to
     * {@link ParserRuleContext#children} list. Contexts are then not candidates
     * for garbage collection.</p>
     */
    public void setBuildParseTree(bool buildParseTrees)
    {
        this._buildParseTrees = buildParseTrees;
    }

    /**
     * @uml
     * Gets whether or not a complete parse tree will be constructed while
     * parsing. This property is {@code true} for a newly constructed parser.
     *
     *  @return {@code true} if a complete parse tree will be constructed while
     *  parsing, otherwise {@code false}
     */
    public bool getBuildParseTree()
    {
        return _buildParseTrees;
    }

    /**
     * @uml
     * Trim the internal lists of the parse tree during parsing to conserve memory.
     * This property is set to {@code false} by default for a newly constructed parser.
     *
     *  @param trimParseTrees {@code true} to trim the capacity of the {@link ParserRuleContext#children}
     *  list to its size after a rule is parsed.
     */
    public void setTrimParseTree(bool trimParseTrees)
    {
        if (trimParseTrees) {
            if (getTrimParseTree()) return;
            addParseListener(TrimToSizeListener.instance);
        }
        else {
            removeParseListener(TrimToSizeListener.instance);
        }
    }

    /**
     * @uml
     * The @return {@code true} if the {@link ParserRuleContext#children} list is trimed
     *  using the default {@link Parser.TrimToSizeListener} during the parse process.
     */
    public bool getTrimParseTree()
    {
        return canFind(getParseListeners(), TrimToSizeListener.instance);
    }

    public ParseTreeListener[] getParseListeners()
    {
        ParseTreeListener[] listeners = _parseListeners;
        if (listeners is null) {
            return [];
        }

        return listeners;
    }

    /**
     * @uml
     * Registers {@code listener} to receive events during the parsing process.
     *
     * <p>To support output-preserving grammar transformations (including but not
     * limited to left-recursion removal, automated left-factoring, and
     * optimized code generation), calls to listener methods during the parse
     * may differ substantially from calls made by
     * {@link ParseTreeWalker#DEFAULT} used after the parse is complete. In
     * particular, rule entry and exit events may occur in a different order
     * during the parse than after the parser. In addition, calls to certain
     * rule entry methods may be omitted.</p>
     *
     * <p>With the following specific exceptions, calls to listener events are
     * <em>deterministic</em>, i.e. for identical input the calls to listener
     * methods will be the same.</p>
     *
     * <ul>
     * <li>Alterations to the grammar used to generate code may change the
     * behavior of the listener calls.</li>
     * <li>Alterations to the command line options passed to ANTLR 4 when
     * generating the parser may change the behavior of the listener calls.</li>
     * <li>Changing the version of the ANTLR Tool used to generate the parser
     * may change the behavior of the listener calls.</li>
     * </ul>
     *
     *  @param listener the listener to add
     *
     *  @throws NullPointerException if {@code} listener is {@code null}
     */
    public void addParseListener(ParseTreeListener listener)
    {
        assert (listener !is null, "NullPointerException(listener)");
        _parseListeners ~= listener;
    }

    public void removeParseListener(ParseTreeListener listener)
    {
        ParseTreeListener[] new_parseListeners;
        foreach (li; _parseListeners) {
            if ( li != listener) new_parseListeners ~= li;
        }
        _parseListeners = new_parseListeners;
    }

    /**
     * @uml
     * Remove all parse listeners.
     *
     *  @see #addParseListener
     */
    public void removeParseListeners()
    {
        _parseListeners.length = 0;
    }

    /**
     * @uml
     * Notify any parse listeners of an enter rule event.
     *
     *  @see #addParseListener
     */
    protected void triggerEnterRuleEvent()
    {
        foreach (listener; _parseListeners) {
            listener.enterEveryRule(ctx_);
            ctx_.enterRule(listener);
        }
    }

    /**
     * @uml
     * Notify any parse listeners of an exit rule event.
     *
     *  @see #addParseListener
     */
    protected void triggerExitRuleEvent()
    {
        // reverse order walk of listeners
        for (auto i = _parseListeners.length-1; i >= 0; i--) {
            ParseTreeListener listener = _parseListeners[i];
            ctx_.exitRule(listener);
            listener.exitEveryRule(ctx_);
        }
    }

    /**
     * @uml
     * @override
     */
    public override TokenFactory!CommonToken tokenFactory()
    {
        return _input.getTokenSource().tokenFactory();
    }

    /**
     * Tell our token source and error strategy about a new way to create tokens.
     * @uml
     * @override
     */
    public override void tokenFactory(TokenFactory!CommonToken factory)
    {
        _input.getTokenSource().tokenFactory(factory);
    }

    /**
     * @uml
     * The ATN with bypass alternatives is expensive to create so we create it
     * lazily.
     *
     *  @throws UnsupportedOperationException if the current parser does not
     * implement the {@link #getSerializedATN()} method.
     */
    public ATN getATNWithBypassAlts()
    {
        wstring serializedAtn = getSerializedATN();
        if (serializedAtn is null) {
            throw new UnsupportedOperationException("The current parser does not support an ATN with bypass alternatives.");
        }
        if (serializedAtn in bypassAltsAtnCache) {
            return bypassAltsAtnCache[serializedAtn];
        }
        ATN result;
        ATNDeserializationOptions deserializationOptions = new ATNDeserializationOptions();
        deserializationOptions.generateRuleBypassTransitions(true);
        result = new ATNDeserializer(deserializationOptions).deserialize(serializedAtn);
        bypassAltsAtnCache[serializedAtn] = result;
        return result;
    }

    public ParseTreePattern compileParseTreePattern(string pattern, int patternRuleIndex)
    {
        if (getTokenStream() !is null) {
            TokenSource tokenSource = getTokenStream().getTokenSource();
            if (tokenSource.classinfo == Lexer.classinfo) {
                Lexer lexer = cast(Lexer)tokenSource;
                return compileParseTreePattern(pattern, patternRuleIndex, lexer);
            }
        }
        throw new UnsupportedOperationException("Parser can't discover a lexer to use");
    }

    /**
     * @uml
     * The same as {@link #compileParseTreePattern(String, int)} but specify a
     * {@link Lexer} rather than trying to deduce it from this parser.
     */
    public ParseTreePattern compileParseTreePattern(string pattern, int patternRuleIndex,
                                                    Lexer lexer)
    {
        ParseTreePatternMatcher m = new ParseTreePatternMatcher(lexer, this);
        return m.compile(pattern, patternRuleIndex);
    }

    public auto getErrorHandler()
    {
        return _errHandler;
    }

    public void setErrorHandler(ANTLRErrorStrategy handler)
    {
    }

    /**
     * @uml
     * @override
     */
    public override TokenStream getInputStream()
    {
        return getTokenStream();
    }

    /**
     * @uml
     * @override
     */
    public override void setInputStream(IntStream input)
    {
        setTokenStream(cast(TokenStream)input);
    }

    public TokenStream getTokenStream()
    {
        return _input;
    }

    /**
     * @uml
     * Set the token stream and reset the parser.
     */
    public void setTokenStream(TokenStream input)
    {
        this._input = null;
        reset();
        this._input = input;
    }

    /**
     * @uml
     * Match needs to return the current input symbol, which gets put
     * into the label for the associated token ref; e.g., x=ID.
     */
    public Token getCurrentToken()
    {
        return _input.LT(1);
    }

    /**
     * @uml
     * @final
     */
    public final void notifyErrorListeners(string msg)
    {
        notifyErrorListeners(getCurrentToken(), msg, null);
    }

    public void notifyErrorListeners(Token offendingToken, string msg, RecognitionException e)
    {
        numberOfSyntaxErrors_++;
        int line = offendingToken.getLine();
        int charPositionInLine = offendingToken.getCharPositionInLine();
        ANTLRErrorListener!(Token, ParserATNSimulator) listener = getErrorListenerDispatch();
        listener.syntaxError(this, cast(Object)offendingToken, line, charPositionInLine, msg, e);
    }

    /**
     * @uml
     * Consume and return the {@linkplain #getCurrentToken current symbol}.
     *
     * <p>E.g., given the following input with {@code A} being the current
     * lookahead symbol, this function moves the cursor to {@code B} and returns
     * {@code A}.</p>
     *
     * <pre>
     *  A B
     *  ^
     * </pre>
     *
     * If the parser is not in error recovery mode, the consumed symbol is added
     * to the parse tree using {@link ParserRuleContext#addChild(Token)}, and
     * {@link ParseTreeListener#visitTerminal} is called on any parse listeners.
     * If the parser <em>is</em> in error recovery mode, the consumed symbol is
     * added to the parse tree using
     * {@link ParserRuleContext#addErrorNode(Token)}, and
     * {@link ParseTreeListener#visitErrorNode} is called on any parse
     * listeners.
     */
    public Token consume()
    {
        Token o = getCurrentToken();
        if (o.getType() != EOF) {
            getInputStream().consume();
        }

        bool hasListener = _parseListeners !is null && _parseListeners.length;
        if (_buildParseTrees || hasListener) {
            if (_errHandler.inErrorRecoveryMode(this)) {
                ErrorNode node = ctx_.addErrorNode(o);
                if (_parseListeners !is null) {
                    foreach (ParseTreeListener listener; _parseListeners) {
                        listener.visitErrorNode(node);
                    }
                }
            }
            else {
                TerminalNode node = ctx_.addChild(o);
                if (_parseListeners !is null) {
                    foreach (ParseTreeListener listener; _parseListeners) {
                        listener.visitTerminal(node);
                    }
                }
            }
        }
        return o;
    }

    protected void addContextToParseTree()
    {
        ParserRuleContext parent = cast(ParserRuleContext)ctx_.parent;
        // add current context to parent if we have a parent
        if (parent !is null) {
            parent.addChild(ctx_);
        }
    }

    public void enterRule(ParserRuleContext localctx, int state, int ruleIndex)
    {
        setState(state);
        ctx_ = localctx;
        ctx_.start = _input.LT(1);
        if (_buildParseTrees) addContextToParseTree();
        if (_parseListeners !is null)
            triggerEnterRuleEvent();
    }

    public void exitRule()
    {
        if (matchedEOF) {
            // if we have matched EOF, it cannot consume past EOF so we use LT(1) here
            ctx_.stop = _input.LT(1); // LT(1) will be end of file
        }
        else {
            ctx_.stop = _input.LT(-1); // stop node is what we just matched
        }
        // trigger event on ctx_, before it reverts to parent
        if (_parseListeners !is null)
            triggerExitRuleEvent();
        setState(ctx_.invokingState);
        ctx_ = cast(ParserRuleContext)ctx_.parent;
    }

    public void enterOuterAlt(ParserRuleContext localctx, int altNum)
    {
        localctx.setAltNumber(altNum);
        // if we have new localctx, make sure we replace existing ctx
        // that is previous child of parse tree
        if (_buildParseTrees && ctx_ != localctx) {
            ParserRuleContext parent = cast(ParserRuleContext)ctx_.parent;
            if (parent !is null)
                {
                    parent.removeLastChild();
                    parent.addChild(localctx);
                }
        }
        ctx_ = localctx;
    }

    /**
     * @uml
     * Get the precedence level for the top-most precedence rule.
     *
     *  @return The precedence level for the top-most precedence rule, or -1 if
     * the parser context is not nested within a precedence rule.
     * @final
     */
    public final int getPrecedence()
    {
        if (_precedenceStack.isEmpty) {
            return -1;
        }
        return _precedenceStack.peek();
    }

    /**
     * @uml
     * . @deprecated Use
     * {@link #enterRecursionRule(ParserRuleContext, int, int, int)} instead.
     */
    public void enterRecursionRule(ParserRuleContext localctx, int ruleIndex)
    {
        enterRecursionRule(localctx, getATN().ruleToStartState[ruleIndex].stateNumber, ruleIndex, 0);
    }

    public void enterRecursionRule(ParserRuleContext localctx, int state, int ruleIndex,
                                   int precedence)
    {
        setState(state);
        _precedenceStack.push(precedence);
        ctx_ = localctx;
        ctx_.start = _input.LT(1);
        if(_parseListeners !is null) {
            triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
        }
    }

    /**
     * @uml
     * Like {@link #enterRule} but for recursive rules.
     * Make the current context the child of the incoming localctx.
     */
    public void pushNewRecursionContext(ParserRuleContext localctx, int state, size_t ruleIndex)
    {
        ParserRuleContext previous = ctx_;
        previous.parent = localctx;
        previous.invokingState = state;
        previous.stop = _input.LT(-1);

        ctx_ = localctx;
        ctx_.start = previous.start;
        if (_buildParseTrees) {
            ctx_.addChild(previous);
        }

        if (_parseListeners !is null) {
            triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
        }
    }

    public void unrollRecursionContexts(ParserRuleContext _parentctx)
    {
        _precedenceStack.pop();
        ctx_.stop = _input.LT(-1);
        ParserRuleContext retctx = ctx_; // save current ctx (return value)

        // unroll so ctx_ is as it was before call to recursive method
        if (_parseListeners !is null) {
            while (ctx_ !is _parentctx) {
                triggerExitRuleEvent();
                ctx_ = cast(ParserRuleContext)ctx_.parent;
            }
        }
        else {
            ctx_ = _parentctx;
        }

        // hook into tree
        retctx.parent = _parentctx;

        if (_buildParseTrees && _parentctx !is null) {
            // add return ctx into invoking rule's tree
            _parentctx.addChild(retctx);
        }
    }

    public ParserRuleContext getInvokingContext(int ruleIndex)
    {
        ParserRuleContext p = ctx_;
        while (p !is null ) {
            if ( p.getRuleIndex() == ruleIndex ) return p;
            p = cast(ParserRuleContext)p.parent;
        }
        return null;
    }

    /**
     * @uml
     * @override
     */
    public override bool precpred(InterfaceRuleContext localctx, int precedence)
    {
        return precedence >= _precedenceStack.peek();
    }

    public bool inContext(string context)
    {
    // TODO: useful in parser?
        return false;
    }

    /**
     * @uml
     * Checks whether or not {@code symbol} can follow the current state in the
     * ATN. The behavior of this method is equivalent to the following, but is
     * implemented such that the complete context-sensitive follow set does not
     * need to be explicitly constructed.
     *
     * <pre>
     * return getExpectedTokens().contains(symbol);
     * </pre>
     *
     *  @param symbol the symbol type to check
     *  @return {@code true} if {@code symbol} can follow the current state in
     * the ATN, otherwise {@code false}.
     */
    public bool isExpectedToken(int symbol)
    {
        ATN atn = getInterpreter.atn;
        ParserRuleContext ctx = ctx_;
        ATNState s = atn.states[getState];
        IntervalSet following = atn.nextTokens(s);
        if (following.contains(symbol)) {
            return true;
        }
        //        System.out.println("following "+s+"="+following);
        if (!following.contains(TokenConstantDefinition.EPSILON))
            return false;

        while (ctx !is null && ctx.invokingState>=0 && following.contains(TokenConstantDefinition.EPSILON) ) {
            ATNState invokingState = atn.states[ctx.invokingState];
            RuleTransition rt = cast(RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            if (following.contains(symbol)) {
                return true;
            }

            ctx = cast(ParserRuleContext)ctx.parent;
        }
        if (following.contains(TokenConstantDefinition.EPSILON) && symbol == TokenConstantDefinition.EOF) {
            return true;
        }

        return false;
    }

    public bool isMatchedEOF()
    {
        return matchedEOF;
    }

    /**
     * @uml
     * Computes the set of input symbols which could follow the current parser
     * state and context, as given by {@link #getState} and {@link #getContext},
     * espectively.
     *
     *  @see ATN#getExpectedTokens(int, RuleContext)
     */
    public IntervalSet getExpectedTokens()
    {
        return getATN().getExpectedTokens(getState(), ctx_);
    }

    public IntervalSet getExpectedTokensWithinCurrentRule()
    {
        ATN atn = getInterpreter.atn;
        ATNState s = atn.states[getState];
        return atn.nextTokens(s);
    }

    /**
     * @uml
     * Get a rule's index (i.e., {@code RULE_ruleName} field) or -1 if not found.
     */
    public int getRuleIndex(string ruleName)
    {
        if (ruleName in getRuleIndexMap)
            return getRuleIndexMap[ruleName];
        return -1;
    }

    public ParserRuleContext getRuleContext()
    {
        return ctx_;
    }

    /**
     * @uml
     * Return List&lt;String&gt; of the rule names in your parser instance
     * leading up to a call to the current rule.  You could override if
     * you want more details such as the file/line info of where
     * in the ATN a rule is invoked.
     *
     * This is very useful for error messages.
     */
    public string[] getRuleInvocationStack()
    {
        return getRuleInvocationStack(ctx_);
    }

    public string[] getRuleInvocationStack(RuleContext p)
    {
        string[] ruleNames = getRuleNames();
        string[] stack;
        while (p) {
            // compute what follows who invoked us
            auto ruleIndex = p.getRuleIndex();
            if (ruleIndex < 0)
                stack ~= "n/a";
            else
                stack ~= ruleNames[ruleIndex];
            p = p.getParent;
        }
        return stack;
    }

    /**
     * @uml
     * For debugging and other purposes.
     */
    public string[] getDFAStrings()
    {
        string[] s;
        for (int d = 0; d < _interp.decisionToDFA.length; d++) {
            DFA dfa = _interp.decisionToDFA[d];
            s ~= dfa.toString(getVocabulary());
        }
        return s;
    }

    /**
     * @uml
     * For debugging and other purposes.
     */
    public void dumpDFA()
    {
        bool seenOne = false;
        for (int d = 0; d < _interp.decisionToDFA.length; d++) {
            DFA dfa = _interp.decisionToDFA[d];
            if (dfa.states.length) {
                if (seenOne)
                    writeln();
                writefln!"Decision %1$s:"(dfa.decision);
                write(dfa.toString(getVocabulary));
                seenOne = true;
            }
        }
    }

    public string getSourceName()
    {
        return _input.getSourceName();
    }

    /**
     * @uml
     * @override
     */
    public override ParseInfo getParseInfo()
    {
        ParserATNSimulator interp = getInterpreter;
        if (interp.classinfo == ProfilingATNSimulator.classinfo) {
            return new ParseInfo(cast(ProfilingATNSimulator)interp);
        }
        return null;
    }

    public void setProfile(bool profile)
    {
        ParserATNSimulator interp = getInterpreter();
        auto saveMode = interp.getPredictionMode();
        if (profile) {
            if (interp.classinfo != ProfilingATNSimulator.classinfo) {
                setInterpreter(new ProfilingATNSimulator(this));
            }
        }
        else if (interp.classinfo == ProfilingATNSimulator.classinfo) {
            ParserATNSimulator sim =
                new ParserATNSimulator(this, getATN(), interp.decisionToDFA, interp.getSharedContextCache());
            setInterpreter(sim);
        }
        getInterpreter.setPredictionMode(saveMode);
    }

    public void setTrace(bool trace)
    {
        if (!trace) {
            removeParseListener(_tracer);
            _tracer = null;
        }
        else {
            if (_tracer !is null ) removeParseListener(_tracer);
            else _tracer = new TraceListener();
            addParseListener(_tracer);
        }
    }

    /**
     * @uml
     * Gets whether a {@link TraceListener} is registered as a parse listener
     * for the parser.
     *
     *  @see #setTrace(boolean)
     */
    public bool isTrace()
    {
        return _tracer !is null;
    }

    public final ParserRuleContext ctx()
    {
        return this.ctx_;
    }

    public final void ctx(ParserRuleContext ctx)
    {
        this.ctx_ = ctx;
    }

    public final int numberOfSyntaxErrors()
    {
        return this.numberOfSyntaxErrors_;
    }

}
