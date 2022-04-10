/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import 'atn/atn.dart';
import 'error/error.dart';
import 'input_stream.dart';
import 'interval_set.dart';
import 'lexer.dart';
import 'parser_rule_context.dart';
import 'recognizer.dart';
import 'rule_context.dart';
import 'token.dart';
import 'token_factory.dart';
import 'token_stream.dart';
import 'tree/tree.dart';

import 'util/platform_stub.dart'
    if (dart.library.io) 'util/platform_io.dart'
    if (dart.library.html) 'util/platform_html.dart';

/// This is all the parsing support code essentially; most of it is error recovery stuff. */
abstract class Parser extends Recognizer<ParserATNSimulator> {
  /// This field maps from the serialized ATN string to the deserialized [ATN] with
  /// bypass alternatives.
  ///
  /// @see ATNDeserializationOptions#isGenerateRuleBypassTransitions()
  ATN? bypassAltsAtnCache;

  /// The error handling strategy for the parser. The default value is a new
  /// instance of [DefaultErrorStrategy].
  ///
  /// @see #getErrorHandler
  /// @see #setErrorHandler

  ErrorStrategy errorHandler = DefaultErrorStrategy();

  /// The input stream.
  ///
  /// @see #getInputStream
  /// @see #setInputStream
  TokenStream _input;

  final List<int> _precedenceStack = [0];

  /// The [ParserRuleContext] object for the currently executing rule.
  /// This is always non-null during the parsing process.
  ParserRuleContext? context;

  /// Specifies whether or not the parser should construct a parse tree during
  /// the parsing process. The default value is [true].
  bool buildParseTree = true;

  /// When {@link #setTrace}{@code (true)} is called, a reference to the
  /// [TraceListener] is stored here so it can be easily removed in a
  /// later call to {@link #setTrace}{@code (false)}. The listener itself is
  /// implemented as a parser listener so this field is not directly used by
  /// other parser methods.
  TraceListener? _tracer;

  /// The list of [ParseTreeListener] listeners registered to receive
  /// events during the parse.
  ///
  /// @see #addParseListener
  List<ParseTreeListener>? _parseListeners;

  /// The number of syntax errors reported during parsing. This value is
  /// incremented each time {@link #notifyErrorListeners} is called.
  int _syntaxErrors = 0;

  /// Indicates parser has match()ed EOF token. See {@link #exitRule()}. */
  bool matchedEOF = false;

  Parser(this._input) {
    reset(false);
  }

  /// reset the parser's state */
  void reset([bool resetInput = true]) {
    if (resetInput) inputStream.seek(0);
    errorHandler.reset(this);
    context = null;
    _syntaxErrors = 0;
    matchedEOF = false;
    setTrace(false);
    _precedenceStack.clear();
    _precedenceStack.add(0);
    interpreter?.reset();
  }

  /// Match current input symbol against [ttype]. If the symbol type
  /// matches, {@link ANTLRErrorStrategy#reportMatch} and {@link #consume} are
  /// called to complete the match process.
  ///
  /// <p>If the symbol type does not match,
  /// {@link ANTLRErrorStrategy#recoverInline} is called on the current error
  /// strategy to attempt recovery. If {@link #getBuildParseTree} is
  /// [true] and the token index of the symbol returned by
  /// {@link ANTLRErrorStrategy#recoverInline} is -1, the symbol is added to
  /// the parse tree by calling {@link #createErrorNode(ParserRuleContext, Token)} then
  /// {@link ParserRuleContext#addErrorNode(ErrorNode)}.</p>
  ///
  /// @param ttype the token type to match
  /// @return the matched symbol
  /// @throws RecognitionException if the current input symbol did not match
  /// [ttype] and the error strategy could not recover from the
  /// mismatched symbol
  Token match(int ttype) {
    var t = currentToken;
    if (t.type == ttype) {
      if (ttype == Token.EOF) {
        matchedEOF = true;
      }
      errorHandler.reportMatch(this);
      consume();
    } else {
      t = errorHandler.recoverInline(this);
      if (buildParseTree && t.tokenIndex == -1) {
        // we must have conjured up a new token during single token insertion
        // if it's not the current symbol
        context!.addErrorNode(createErrorNode(context!, t));
      }
    }
    return t;
  }

  /// Match current input symbol as a wildcard. If the symbol type matches
  /// (i.e. has a value greater than 0), {@link ANTLRErrorStrategy#reportMatch}
  /// and {@link #consume} are called to complete the match process.
  ///
  /// <p>If the symbol type does not match,
  /// {@link ANTLRErrorStrategy#recoverInline} is called on the current error
  /// strategy to attempt recovery. If {@link #getBuildParseTree} is
  /// [true] and the token index of the symbol returned by
  /// {@link ANTLRErrorStrategy#recoverInline} is -1, the symbol is added to
  /// the parse tree by calling {@link Parser#createErrorNode(ParserRuleContext, Token)}. then
  /// {@link ParserRuleContext#addErrorNode(ErrorNode)}</p>
  ///
  /// @return the matched symbol
  /// @throws RecognitionException if the current input symbol did not match
  /// a wildcard and the error strategy could not recover from the mismatched
  /// symbol
  Token matchWildcard() {
    var t = currentToken;
    if (t.type > 0) {
      errorHandler.reportMatch(this);
      consume();
    } else {
      t = errorHandler.recoverInline(this);
      if (buildParseTree && t.tokenIndex == -1) {
        // we must have conjured up a new token during single token insertion
        // if it's not the current symbol
        context!.addErrorNode(createErrorNode(context!, t));
      }
    }

    return t;
  }

  /// Trim the internal lists of the parse tree during parsing to conserve memory.
  /// This property is set to [false] by default for a newly constructed parser.
  ///
  /// @param trimParseTrees [true] to trim the capacity of the {@link ParserRuleContext#children}
  /// list to its size after a rule is parsed.
  set trimParseTree(bool trimParseTrees) {
    if (trimParseTrees) {
      if (trimParseTree) return;
      addParseListener(TrimToSizeListener.INSTANCE);
    } else {
      removeParseListener(TrimToSizeListener.INSTANCE);
    }
  }

  /// @return [true] if the {@link ParserRuleContext#children} list is trimmed
  /// using the default {@link Parser.TrimToSizeListener} during the parse process.
  bool get trimParseTree {
    return parseListeners?.contains(TrimToSizeListener.INSTANCE) ?? false;
  }

  List<ParseTreeListener>? get parseListeners => _parseListeners;

  /// Registers [listener] to receive events during the parsing process.
  ///
  /// <p>To support output-preserving grammar transformations (including but not
  /// limited to left-recursion removal, automated left-factoring, and
  /// optimized code generation), calls to listener methods during the parse
  /// may differ substantially from calls made by
  /// {@link ParseTreeWalker#DEFAULT} used after the parse is complete. In
  /// particular, rule entry and exit events may occur in a different order
  /// during the parse than after the parser. In addition, calls to certain
  /// rule entry methods may be omitted.</p>
  ///
  /// <p>With the following specific exceptions, calls to listener events are
  /// <em>deterministic</em>, i.e. for identical input the calls to listener
  /// methods will be the same.</p>
  ///
  /// <ul>
  /// <li>Alterations to the grammar used to generate code may change the
  /// behavior of the listener calls.</li>
  /// <li>Alterations to the command line options passed to ANTLR 4 when
  /// generating the parser may change the behavior of the listener calls.</li>
  /// <li>Changing the version of the ANTLR Tool used to generate the parser
  /// may change the behavior of the listener calls.</li>
  /// </ul>
  ///
  /// @param listener the listener to add
  ///
  /// @throws NullPointerException if {@code} listener is null
  void addParseListener(
    ParseTreeListener listener,
  ) {
    _parseListeners ??= [];

    _parseListeners!.add(listener);
  }

  /// Remove [listener] from the list of parse listeners.
  ///
  /// <p>If [listener] is null or has not been added as a parse
  /// listener, this method does nothing.</p>
  ///
  /// @see #addParseListener
  ///
  /// @param listener the listener to remove
  void removeParseListener(ParseTreeListener? listener) {
    if (_parseListeners != null) {
      if (_parseListeners!.remove(listener)) {
        if (_parseListeners!.isEmpty) {
          _parseListeners = null;
        }
      }
    }
  }

  /// Remove all parse listeners.
  ///
  /// @see #addParseListener
  void removeParseListeners() {
    _parseListeners = null;
  }

  /// Notify any parse listeners of an enter rule event.
  ///
  /// @see #addParseListener
  void triggerEnterRuleEvent() {
    if (_parseListeners == null) return;
    for (var listener in _parseListeners!) {
      listener.enterEveryRule(context!);
      context!.enterRule(listener);
    }
  }

  /// Notify any parse listeners of an exit rule event.
  ///
  /// @see #addParseListener
  void triggerExitRuleEvent() {
    if (_parseListeners == null) return;
    // reverse order walk of listeners
    for (var i = _parseListeners!.length - 1; i >= 0; i--) {
      final listener = _parseListeners![i];
      context!.exitRule(listener);
      listener.exitEveryRule(context!);
    }
  }

  /// Gets the number of syntax errors reported during parsing. This value is
  /// incremented each time {@link #notifyErrorListeners} is called.
  ///
  /// @see #notifyErrorListeners
  int get numberOfSyntaxErrors {
    return _syntaxErrors;
  }

  @override
  TokenFactory get tokenFactory {
    return _input.tokenSource.tokenFactory;
  }

  /// Tell our token source and error strategy about a new way to create tokens. */
  @override
  set tokenFactory(TokenFactory factory) {
    _input.tokenSource.tokenFactory = factory;
  }

  /// The ATN with bypass alternatives is expensive to create so we create it
  /// lazily.
  ///
  /// @throws UnsupportedOperationException if the current parser does not
  /// implement the {@link #getSerializedATN()} method.
  ATN get ATNWithBypassAlts {
    if (serializedATN == null) {
      throw UnsupportedError(
          'The current parser does not support an ATN with bypass alternatives.');
    }

    if (bypassAltsAtnCache == null) {
      final deserializationOptions = ATNDeserializationOptions(false);
      deserializationOptions.setGenerateRuleBypassTransitions(true);
      bypassAltsAtnCache = ATNDeserializer(deserializationOptions).deserialize(serializedATN);
    }

    return bypassAltsAtnCache!;
  }

  /// The preferred method of getting a tree pattern. For example, here's a
  /// sample use:
  ///
  /// <pre>
  /// ParseTree t = parser.expr();
  /// ParseTreePattern p = parser.compileParseTreePattern("&lt;ID&gt;+0", MyParser.RULE_expr);
  /// ParseTreeMatch m = p.match(t);
  /// String id = m.get("ID");
  /// </pre>
  ParseTreePattern compileParseTreePattern(
    String pattern,
    int patternRuleIndex, [
    Lexer? lexer,
  ]) {
    if (lexer == null) {
      final tokenSource = tokenStream.tokenSource;
      if (tokenSource is! Lexer) {
        throw UnsupportedError("Parser can't discover a lexer to use");
      }
      lexer = tokenSource;
    }

    final m = ParseTreePatternMatcher(lexer, this);
    return m.compile(pattern, patternRuleIndex);
  }

  @override
  TokenStream get inputStream => tokenStream;

  @override
  set inputStream(TokenStream input) {
    setTokenStream(input);
  }

  TokenStream get tokenStream => _input;

  /// Set the token stream and reset the parser. */
  void setTokenStream(TokenStream input) {
    reset(false);
    _input = input;
  }

  /// Match needs to return the current input symbol, which gets put
  ///  into the label for the associated token ref; e.g., x=ID.

  Token get currentToken {
    return _input.LT(1)!;
  }

  void notifyErrorListeners(
    String msg, [
    Token? offendingToken,
    RecognitionException? e,
  ]) {
    offendingToken = offendingToken ?? currentToken;
    _syntaxErrors++;
    int? line = -1;
    var charPositionInLine = -1;
    line = offendingToken.line;
    charPositionInLine = offendingToken.charPositionInLine;

    final listener = errorListenerDispatch;
    listener.syntaxError(
      this,
      offendingToken,
      line,
      charPositionInLine,
      msg,
      e,
    );
  }

  /// Consume and return the {@linkplain #getCurrentToken current symbol}.
  ///
  /// <p>E.g., given the following input with [A] being the current
  /// lookahead symbol, this function moves the cursor to [B] and returns
  /// [A].</p>
  ///
  /// <pre>
  ///  A B
  ///  ^
  /// </pre>
  ///
  /// If the parser is not in error recovery mode, the consumed symbol is added
  /// to the parse tree using {@link ParserRuleContext#addChild}, and
  /// {@link ParseTreeListener#visitTerminal} is called on any parse listeners.
  /// If the parser <em>is</em> in error recovery mode, the consumed symbol is
  /// added to the parse tree using {@link #createErrorNode(ParserRuleContext, Token)} then
  /// {@link ParserRuleContext#addErrorNode(ErrorNode)} and
  /// {@link ParseTreeListener#visitErrorNode} is called on any parse
  /// listeners.
  Token consume() {
    final o = currentToken;
    if (o.type != IntStream.EOF) {
      inputStream.consume();
    }
    final hasListener = _parseListeners != null && _parseListeners!.isNotEmpty;
    if (buildParseTree || hasListener) {
      if (errorHandler.inErrorRecoveryMode(this)) {
        final node = context!.addErrorNode(createErrorNode(context!, o));
        if (_parseListeners != null) {
          for (var listener in _parseListeners!) {
            listener.visitErrorNode(node);
          }
        }
      } else {
        final node = context!.addChild(createTerminalNode(context!, o));
        if (_parseListeners != null) {
          for (var listener in _parseListeners!) {
            listener.visitTerminal(node);
          }
        }
      }
    }
    return o;
  }

  /// How to create a token leaf node associated with a parent.
  ///  Typically, the terminal node to create is not a function of the parent.
  ///
  /// @since 4.7
  TerminalNode createTerminalNode(ParserRuleContext parent, Token t) {
    return TerminalNodeImpl(t);
  }

  /// How to create an error node, given a token, associated with a parent.
  ///  Typically, the error node to create is not a function of the parent.
  ///
  /// @since 4.7
  ErrorNode createErrorNode(ParserRuleContext parent, Token t) {
    return ErrorNodeImpl(t);
  }

  void addContextToParseTree() {
    final parent = context?.parent;
    // add current context to parent if we have a parent
    if (parent != null) {
      parent.addAnyChild(context!);
    }
  }

  /// Always called by generated parsers upon entry to a rule. Access field
  /// {@link #_ctx} get the current context.
  void enterRule(ParserRuleContext localctx, int state, int ruleIndex) {
    this.state = state;
    context = localctx;
    context!.start = _input.LT(1)!;
    if (buildParseTree) addContextToParseTree();
    if (_parseListeners != null) triggerEnterRuleEvent();
  }

  void exitRule() {
    assert(context != null);
    if (matchedEOF) {
      // if we have matched EOF, it cannot consume past EOF so we use LT(1) here
      context!.stop = _input.LT(1); // LT(1) will be end of file
    } else {
      context!.stop = _input.LT(-1); // stop node is what we just matched
    }
    // trigger event on _ctx, before it reverts to parent
    if (_parseListeners != null) triggerExitRuleEvent();
    state = context!.invokingState;
    context = context?.parent;
  }

  void enterOuterAlt(ParserRuleContext localctx, int altNum) {
    assert(context != null);
    localctx.altNumber = altNum;
    // if we have new localctx, make sure we replace existing ctx
    // that is previous child of parse tree
    if (buildParseTree && context != localctx) {
      final parent = context!.parent;
      if (parent != null) {
        parent.removeLastChild();
        parent.addAnyChild(localctx);
      }
    }
    context = localctx;
  }

  /// Get the precedence level for the top-most precedence rule.
  ///
  /// @return The precedence level for the top-most precedence rule, or -1 if
  /// the parser context is not nested within a precedence rule.
  int get precedence {
    if (_precedenceStack.isEmpty) {
      return -1;
    }

    return _precedenceStack.last;
  }

  void enterRecursionRule(
      ParserRuleContext localctx, int state, int ruleIndex, int precedence) {
    this.state = state;
    _precedenceStack.add(precedence);
    context = localctx;
    context!.start = _input.LT(1)!;
    if (_parseListeners != null) {
      triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
    }
  }

  /// Like {@link #enterRule} but for recursive rules.
  ///  Make the current context the child of the incoming localctx.
  void pushNewRecursionContext(
    ParserRuleContext localctx,
    int state,
    int? ruleIndex,
  ) {
    assert(context != null);
    final previous = context!;
    previous.parent = localctx;
    previous.invokingState = state;
    previous.stop = _input.LT(-1);

    context = localctx;
    context!.start = previous.start;
    if (buildParseTree) {
      context!.addAnyChild(previous);
    }

    if (_parseListeners != null) {
      triggerEnterRuleEvent(); // simulates rule entry for left-recursive rules
    }
  }

  void unrollRecursionContexts(ParserRuleContext? _parentctx) {
    assert(context != null);
    _precedenceStack.removeLast();
    context!.stop = _input.LT(-1);
    final retctx = context!; // save current ctx (return value)

    // unroll so _ctx is as it was before call to recursive method
    if (_parseListeners != null) {
      while (context != _parentctx) {
        triggerExitRuleEvent();
        context = context!.parent;
      }
    } else {
      context = _parentctx;
    }

    // hook into tree
    retctx.parent = _parentctx;

    if (buildParseTree && _parentctx != null) {
      // add return ctx into invoking rule's tree
      _parentctx.addAnyChild(retctx);
    }
  }

  ParserRuleContext? getInvokingContext(int ruleIndex) {
    var p = context;
    while (p != null) {
      if (p.ruleIndex == ruleIndex) return p;
      p = p.parent;
    }
    return null;
  }

  @override
  bool precpred(RuleContext? localctx, int precedence) {
    return precedence >= _precedenceStack.last;
  }

  bool inContext(String context) {
    // TODO: useful in parser?
    return false;
  }

  /// Checks whether or not [symbol] can follow the current state in the
  /// ATN. The behavior of this method is equivalent to the following, but is
  /// implemented such that the complete context-sensitive follow set does not
  /// need to be explicitly constructed.
  ///
  /// <pre>
  /// return expectedTokens.contains(symbol);
  /// </pre>
  ///
  /// @param symbol the symbol type to check
  /// @return [true] if [symbol] can follow the current state in
  /// the ATN, otherwise [false].
  bool isExpectedToken(int symbol) {
//   		return interpreter!.atn.nextTokens(_ctx);
    final atn = interpreter!.atn;
    var ctx = context;
    final s = atn.states[state];
    var following = atn.nextTokens(s!);
    if (following.contains(symbol)) {
      return true;
    }
//        log("following "+s+"="+following);
    if (!following.contains(Token.EPSILON)) return false;

    while (ctx != null &&
        ctx.invokingState >= 0 &&
        following.contains(Token.EPSILON)) {
      final invokingState = atn.states[ctx.invokingState]!;
      final rt = invokingState.transition(0) as RuleTransition;
      following = atn.nextTokens(rt.followState);
      if (following.contains(symbol)) {
        return true;
      }

      ctx = ctx.parent;
    }

    if (following.contains(Token.EPSILON) && symbol == Token.EOF) {
      return true;
    }

    return false;
  }

  bool isMatchedEOF() {
    return matchedEOF;
  }

  /// Computes the set of input symbols which could follow the current parser
  /// state and context, as given by {@link #getState} and {@link #getContext},
  /// respectively.
  ///
  /// @see ATN#getExpectedTokens(int, RuleContext)
  IntervalSet get expectedTokens {
    return getATN().getExpectedTokens(state, context);
  }

  IntervalSet get expectedTokensWithinCurrentRule {
    final atn = interpreter!.atn;
    final s = atn.states[state]!;
    return atn.nextTokens(s);
  }

  /// Get a rule's index (i.e., {@code RULE_ruleName} field) or -1 if not found. */
  int getRuleIndex(String ruleName) {
    final ruleIndex = ruleIndexMap[ruleName];
    if (ruleIndex != null) return ruleIndex;
    return -1;
  }

  ParserRuleContext get ruleContext {
    assert(context != null);
    return context!;
  }

  List<String> get ruleInvocationStack => getRuleInvocationStack();

  /// Return List&lt;String&gt; of the rule names in your parser instance
  ///  leading up to a call to the current rule.  You could override if
  ///  you want more details such as the file/line info of where
  ///  in the ATN a rule is invoked.
  ///
  ///  This is very useful for error messages.
  List<String> getRuleInvocationStack([RuleContext? p]) {
    p = p ?? context;
    final _ruleNames = ruleNames;
    final stack = <String>[];
    while (p != null) {
      // compute what follows who invoked us
      final ruleIndex = p.ruleIndex;
      if (ruleIndex < 0) {
        stack.add('n/a');
      } else {
        stack.add(_ruleNames[ruleIndex]);
      }
      p = p.parent;
    }
    return stack;
  }

  /// For debugging and other purposes. */
  List<String> get dfaStrings {
    final s = <String>[];
    for (var d = 0; d < interpreter!.decisionToDFA.length; d++) {
      final dfa = interpreter!.decisionToDFA[d];
      s.add(dfa.toString(vocabulary));
    }
    return s;
  }

  /// For debugging and other purposes. */
  void dumpDFA() {
    var seenOne = false;
    for (var d = 0; d < interpreter!.decisionToDFA.length; d++) {
      final dfa = interpreter!.decisionToDFA[d];
      if (dfa.states.isNotEmpty) {
        if (seenOne) print('');
        print('Decision ${dfa.decision}:');
        stdoutWrite(dfa.toString(vocabulary));
        seenOne = true;
      }
    }
  }

  String get sourceName {
    return _input.sourceName;
  }

  @override
  ParseInfo? get parseInfo {
    final interp = interpreter;
    if (interp is ProfilingATNSimulator) {
      return ParseInfo(interp);
    }
    return null;
  }

  /// @since 4.3
  void setProfile(bool profile) {
    final interp = interpreter!;
    final saveMode = interp.predictionMode;
    if (profile) {
      if (interp is! ProfilingATNSimulator) {
        interpreter = ProfilingATNSimulator(this);
      }
    } else if (interp is ProfilingATNSimulator) {
      final sim = ParserATNSimulator(
        this,
        getATN(),
        interp.decisionToDFA,
        interp.sharedContextCache,
      );
      interpreter = sim;
    }
    interpreter!.predictionMode = saveMode;
  }

  /// During a parse is sometimes useful to listen in on the rule entry and exit
  ///  events as well as token matches. This is for quick and dirty debugging.
  void setTrace(bool trace) {
    if (!trace) {
      removeParseListener(_tracer);
      _tracer = null;
    } else {
      if (_tracer != null) {
        removeParseListener(_tracer);
      } else {
        _tracer = TraceListener(this);
      }
      addParseListener(_tracer!);
    }
  }

  /// Gets whether a [TraceListener] is registered as a parse listener
  /// for the parser.
  ///
  /// @see #setTrace(bool)
  bool isTrace() {
    return _tracer != null;
  }
}
