/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 

/// 
/// This is the default implementation of _org.antlr.v4.runtime.ANTLRErrorStrategy_ used for
/// error reporting and recovery in ANTLR parsers.
/// 

import Foundation

open class DefaultErrorStrategy: ANTLRErrorStrategy {
    /// 
    /// Indicates whether the error strategy is currently "recovering from an
    /// error". This is used to suppress reporting multiple error messages while
    /// attempting to recover from a detected syntax error.
    /// 
    /// - seealso: #inErrorRecoveryMode
    /// 
    open var errorRecoveryMode = false

    /// 
    /// The index into the input stream where the last error occurred.
    /// This is used to prevent infinite loops where an error is found
    /// but no token is consumed during recovery...another error is found,
    /// ad nauseum.  This is a failsafe mechanism to guarantee that at least
    /// one token/tree node is consumed for two errors.
    /// 
    open var lastErrorIndex = -1

    open var lastErrorStates: IntervalSet?

    /**
     * This field is used to propagate information about the lookahead following
     * the previous match. Since prediction prefers completing the current rule
     * to error recovery efforts, error reporting may occur later than the
     * original point where it was discoverable. The original context is used to
     * compute the true expected sets as though the reporting occurred as early
     * as possible.
     */
    open var nextTokensContext: ParserRuleContext?

    /**
     * @see #nextTokensContext
     */
    open var nextTokensState = ATNState.INVALID_STATE_NUMBER


    public init() {
    }

    /// 
    /// The default implementation simply calls _#endErrorCondition_ to
    /// ensure that the handler is not in error recovery mode.
    /// 
    open func reset(_ recognizer: Parser) {
        endErrorCondition(recognizer)
    }

    /// 
    /// This method is called to enter error recovery mode when a recognition
    /// exception is reported.
    /// 
    /// - parameter recognizer: the parser instance
    /// 
    open func beginErrorCondition(_ recognizer: Parser) {
        errorRecoveryMode = true
    }

    open func inErrorRecoveryMode(_ recognizer: Parser) -> Bool {
        return errorRecoveryMode
    }

    /// 
    /// This method is called to leave error recovery mode after recovering from
    /// a recognition exception.
    /// 
    /// - parameter recognizer:
    /// 
    open func endErrorCondition(_ recognizer: Parser) {
        errorRecoveryMode = false
        lastErrorStates = nil
        lastErrorIndex = -1
    }

    /// 
    /// The default implementation simply calls _#endErrorCondition_.
    /// 
    open func reportMatch(_ recognizer: Parser) {
        endErrorCondition(recognizer)
    }

    /// 
    /// 
    /// The default implementation returns immediately if the handler is already
    /// in error recovery mode. Otherwise, it calls _#beginErrorCondition_
    /// and dispatches the reporting task based on the runtime type of `e`
    /// according to the following table.
    /// 
    /// * _org.antlr.v4.runtime.NoViableAltException_: Dispatches the call to
    /// _#reportNoViableAlternative_
    /// * _org.antlr.v4.runtime.InputMismatchException_: Dispatches the call to
    /// _#reportInputMismatch_
    /// * _org.antlr.v4.runtime.FailedPredicateException_: Dispatches the call to
    /// _#reportFailedPredicate_
    /// * All other types: calls _org.antlr.v4.runtime.Parser#notifyErrorListeners_ to report
    /// the exception
    /// 
    open func reportError(_ recognizer: Parser, _ e: RecognitionException) {
        // if we've already reported an error and have not matched a token
        // yet successfully, don't report any errors.
        if inErrorRecoveryMode(recognizer) {

            return // don't report spurious errors
        }
        beginErrorCondition(recognizer)
        if let nvae = e as? NoViableAltException {
            reportNoViableAlternative(recognizer, nvae)
        }
        else if let ime = e as? InputMismatchException {
            reportInputMismatch(recognizer, ime)
        }
        else if let fpe = e as? FailedPredicateException {
            reportFailedPredicate(recognizer, fpe)
        }
        else {
            errPrint("unknown recognition error type: " + String(describing: type(of: e)))
            recognizer.notifyErrorListeners(e.getOffendingToken(), e.message ?? "", e)
        }
    }

    /// 
    /// The default implementation resynchronizes the parser by consuming tokens
    /// until we find one in the resynchronization set--loosely the set of tokens
    /// that can follow the current rule.
    /// 
    open func recover(_ recognizer: Parser, _ e: RecognitionException) throws {
//		print("recover in "+recognizer.getRuleInvocationStack()+
//						   " index="+getTokenStream(recognizer).index()+
//						   ", lastErrorIndex="+
//						   lastErrorIndex+
//						   ", states="+lastErrorStates);
        if let lastErrorStates = lastErrorStates ,
          lastErrorIndex == getTokenStream(recognizer).index() &&
          lastErrorStates.contains(recognizer.getState()) {
            // uh oh, another error at same token index and previously-visited
            // state in ATN; must be a case where LT(1) is in the recovery
            // token set so nothing got consumed. Consume a single token
            // at least to prevent an infinite loop; this is a failsafe.
//			errPrint("seen error condition before index="+
//							   lastErrorIndex+", states="+lastErrorStates);
//			errPrint("FAILSAFE consumes "+recognizer.getTokenNames()[getTokenStream(recognizer).LA(1)]);
            try recognizer.consume()
        }
        lastErrorIndex = getTokenStream(recognizer).index()
        if lastErrorStates == nil {
            lastErrorStates = IntervalSet()
        }
        try lastErrorStates!.add(recognizer.getState())
        let followSet = getErrorRecoverySet(recognizer)
        try consumeUntil(recognizer, followSet)
    }

    /// 
    /// The default implementation of _org.antlr.v4.runtime.ANTLRErrorStrategy#sync_ makes sure
    /// that the current lookahead symbol is consistent with what were expecting
    /// at this point in the ATN. You can call this anytime but ANTLR only
    /// generates code to check before subrules/loops and each iteration.
    /// 
    /// Implements Jim Idle's magic sync mechanism in closures and optional
    /// subrules. E.g.,
    /// 
    /// 
    /// a : sync ( stuff sync )* ;
    /// sync : {consume to what can follow sync} ;
    /// 
    /// 
    /// At the start of a sub rule upon error, _#sync_ performs single
    /// token deletion, if possible. If it can't do that, it bails on the current
    /// rule and uses the default error recovery, which consumes until the
    /// resynchronization set of the current rule.
    /// 
    /// If the sub rule is optional (`(...)?`, `(...)*`, or block
    /// with an empty alternative), then the expected set includes what follows
    /// the subrule.
    /// 
    /// During loop iteration, it consumes until it sees a token that can start a
    /// sub rule or what follows loop. Yes, that is pretty aggressive. We opt to
    /// stay in the loop as long as possible.
    /// 
    /// __ORIGINS__
    /// 
    /// Previous versions of ANTLR did a poor job of their recovery within loops.
    /// A single mismatch token or missing token would force the parser to bail
    /// out of the entire rules surrounding the loop. So, for rule
    /// 
    /// 
    /// classDef : 'class' ID '{' member* '}'
    /// 
    /// 
    /// input with an extra token between members would force the parser to
    /// consume until it found the next class definition rather than the next
    /// member definition of the current class.
    /// 
    /// This functionality cost a little bit of effort because the parser has to
    /// compare token set at the start of the loop and at each iteration. If for
    /// some reason speed is suffering for you, you can turn off this
    /// functionality by simply overriding this method as a blank { }.
    /// 

    open func sync(_ recognizer: Parser) throws {
        let s = recognizer.getInterpreter().atn.states[recognizer.getState()]!
//		errPrint("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName());
        // If already recovering, don't try to sync
        if inErrorRecoveryMode(recognizer) {
            return
        }

        let tokens = getTokenStream(recognizer)
        let la = try tokens.LA(1)

        // try cheaper subset first; might get lucky. seems to shave a wee bit off
        let nextToks = recognizer.getATN().nextTokens(s)
        if nextToks.contains(la) {
            // We are sure the token matches
            nextTokensContext = nil
            nextTokensState = ATNState.INVALID_STATE_NUMBER
            return
        }

        if nextToks.contains(CommonToken.EPSILON) {
            if nextTokensContext == nil {
                    // It's possible the next token won't match; information tracked
                    // by sync is restricted for performance.
                    nextTokensContext = recognizer.getContext()
                    nextTokensState = recognizer.getState()
            }
            return
        }

        switch s.getStateType() {
        case ATNState.BLOCK_START: fallthrough
        case ATNState.STAR_BLOCK_START: fallthrough
        case ATNState.PLUS_BLOCK_START: fallthrough
        case ATNState.STAR_LOOP_ENTRY:
            // report error and recover if possible
            if try singleTokenDeletion(recognizer) != nil {
                return
            }
            throw ANTLRException.recognition(e: InputMismatchException(recognizer))

        case ATNState.PLUS_LOOP_BACK: fallthrough
        case ATNState.STAR_LOOP_BACK:
//			errPrint("at loop back: "+s.getClass().getSimpleName());
            reportUnwantedToken(recognizer)
            let expecting = try recognizer.getExpectedTokens()
            let whatFollowsLoopIterationOrRule = expecting.or(getErrorRecoverySet(recognizer)) as! IntervalSet
            try consumeUntil(recognizer, whatFollowsLoopIterationOrRule)
            break

        default:
            // do nothing if we can't identify the exact kind of ATN state
            break
        }
    }

    /// 
    /// This is called by _#reportError_ when the exception is a
    /// _org.antlr.v4.runtime.NoViableAltException_.
    /// 
    /// - seealso: #reportError
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception
    /// 
    open func reportNoViableAlternative(_ recognizer: Parser, _ e: NoViableAltException) {
        let tokens = getTokenStream(recognizer)
        var input: String
        if e.getStartToken().getType() == CommonToken.EOF {
            input = "<EOF>"
        }
        else {
            do {
                input = try tokens.getText(e.getStartToken(), e.getOffendingToken())
            }
            catch {
                input = "<unknown>"
            }
        }
        let msg = "no viable alternative at input " + escapeWSAndQuote(input)
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e)
    }

    /// 
    /// This is called by _#reportError_ when the exception is an
    /// _org.antlr.v4.runtime.InputMismatchException_.
    /// 
    /// - seealso: #reportError
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception
    /// 
    open func reportInputMismatch(_ recognizer: Parser, _ e: InputMismatchException) {
        let tok = getTokenErrorDisplay(e.getOffendingToken())
        let expected = e.getExpectedTokens()?.toString(recognizer.getVocabulary()) ?? "<missing>"
        let msg = "mismatched input \(tok) expecting \(expected)"
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e)
    }

    /// 
    /// This is called by _#reportError_ when the exception is a
    /// _org.antlr.v4.runtime.FailedPredicateException_.
    /// 
    /// - seealso: #reportError
    /// 
    /// - parameter recognizer: the parser instance
    /// - parameter e: the recognition exception
    /// 
    open func reportFailedPredicate(_ recognizer: Parser, _ e: FailedPredicateException) {
        let ruleName = recognizer.getRuleNames()[recognizer._ctx!.getRuleIndex()]
        let msg = "rule \(ruleName) \(e.message!)"
        recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e)
    }

    /// 
    /// This method is called to report a syntax error which requires the removal
    /// of a token from the input stream. At the time this method is called, the
    /// erroneous symbol is current `LT(1)` symbol and has not yet been
    /// removed from the input stream. When this method returns,
    /// `recognizer` is in error recovery mode.
    /// 
    /// This method is called when _#singleTokenDeletion_ identifies
    /// single-token deletion as a viable recovery strategy for a mismatched
    /// input error.
    /// 
    /// The default implementation simply returns if the handler is already in
    /// error recovery mode. Otherwise, it calls _#beginErrorCondition_ to
    /// enter error recovery mode, followed by calling
    /// _org.antlr.v4.runtime.Parser#notifyErrorListeners_.
    /// 
    /// - parameter recognizer: the parser instance
    /// 
    open func reportUnwantedToken(_ recognizer: Parser) {
        if inErrorRecoveryMode(recognizer) {
            return
        }

        beginErrorCondition(recognizer)

        let t = try? recognizer.getCurrentToken()
        let tokenName = getTokenErrorDisplay(t)
        let expecting = (try? getExpectedTokens(recognizer)) ?? IntervalSet.EMPTY_SET
        let msg = "extraneous input \(tokenName) expecting \(expecting.toString(recognizer.getVocabulary()))"
        recognizer.notifyErrorListeners(t, msg, nil)
    }

    /// 
    /// This method is called to report a syntax error which requires the
    /// insertion of a missing token into the input stream. At the time this
    /// method is called, the missing token has not yet been inserted. When this
    /// method returns, `recognizer` is in error recovery mode.
    /// 
    /// This method is called when _#singleTokenInsertion_ identifies
    /// single-token insertion as a viable recovery strategy for a mismatched
    /// input error.
    /// 
    /// The default implementation simply returns if the handler is already in
    /// error recovery mode. Otherwise, it calls _#beginErrorCondition_ to
    /// enter error recovery mode, followed by calling
    /// _org.antlr.v4.runtime.Parser#notifyErrorListeners_.
    /// 
    /// - parameter recognizer: the parser instance
    /// 
    open func reportMissingToken(_ recognizer: Parser) {
        if inErrorRecoveryMode(recognizer) {
            return
        }

        beginErrorCondition(recognizer)

        let t = try? recognizer.getCurrentToken()
        let expecting = (try? getExpectedTokens(recognizer)) ?? IntervalSet.EMPTY_SET
        let msg = "missing \(expecting.toString(recognizer.getVocabulary())) at \(getTokenErrorDisplay(t))"

        recognizer.notifyErrorListeners(t, msg, nil)
    }

    /// 
    /// 
    /// 
    /// The default implementation attempts to recover from the mismatched input
    /// by using single token insertion and deletion as described below. If the
    /// recovery attempt fails, this method throws an
    /// _org.antlr.v4.runtime.InputMismatchException_.
    /// 
    /// __EXTRA TOKEN__ (single token deletion)
    /// 
    /// `LA(1)` is not what we are looking for. If `LA(2)` has the
    /// right token, however, then assume `LA(1)` is some extra spurious
    /// token and delete it. Then consume and return the next token (which was
    /// the `LA(2)` token) as the successful result of the match operation.
    /// 
    /// This recovery strategy is implemented by _#singleTokenDeletion_.
    /// 
    /// __MISSING TOKEN__ (single token insertion)
    /// 
    /// If current token (at `LA(1)`) is consistent with what could come
    /// after the expected `LA(1)` token, then assume the token is missing
    /// and use the parser's _org.antlr.v4.runtime.TokenFactory_ to create it on the fly. The
    /// "insertion" is performed by returning the created token as the successful
    /// result of the match operation.
    /// 
    /// This recovery strategy is implemented by _#singleTokenInsertion_.
    /// 
    /// __EXAMPLE__
    /// 
    /// For example, Input `i=(3;` is clearly missing the `')'`. When
    /// the parser returns from the nested call to `expr`, it will have
    /// call chain:
    /// 
    /// 
    /// stat &rarr; expr &rarr; atom
    /// 
    /// 
    /// and it will be trying to match the `')'` at this point in the
    /// derivation:
    /// 
    /// 
    /// =&gt; ID '=' '(' INT ')' ('+' atom)* ';'
    /// ^
    /// 
    /// 
    /// The attempt to match `')'` will fail when it sees `';'` and
    /// call _#recoverInline_. To recover, it sees that `LA(1)==';'`
    /// is in the set of tokens that can follow the `')'` token reference
    /// in rule `atom`. It can assume that you forgot the `')'`.
    /// 

    open func recoverInline(_ recognizer: Parser) throws -> Token {
        // SINGLE TOKEN DELETION
        let matchedSymbol = try singleTokenDeletion(recognizer)
        if let matchedSymbol = matchedSymbol {
            // we have deleted the extra token.
            // now, move past ttype token as if all were ok
            try recognizer.consume()
            return matchedSymbol
        }

        // SINGLE TOKEN INSERTION
        if try singleTokenInsertion(recognizer) {
            return try getMissingSymbol(recognizer)
        }
        // even that didn't work; must throw the exception
        let exn = InputMismatchException(recognizer, state: nextTokensState, ctx: nextTokensContext)
        throw ANTLRException.recognition(e: exn)
    }

    /// 
    /// This method implements the single-token insertion inline error recovery
    /// strategy. It is called by _#recoverInline_ if the single-token
    /// deletion strategy fails to recover from the mismatched input. If this
    /// method returns `true`, `recognizer` will be in error recovery
    /// mode.
    /// 
    /// This method determines whether or not single-token insertion is viable by
    /// checking if the `LA(1)` input symbol could be successfully matched
    /// if it were instead the `LA(2)` symbol. If this method returns
    /// `true`, the caller is responsible for creating and inserting a
    /// token with the correct type to produce this behavior.
    /// 
    /// - parameter recognizer: the parser instance
    /// - returns: `true` if single-token insertion is a viable recovery
    /// strategy for the current mismatched input, otherwise `false`
    /// 
    open func singleTokenInsertion(_ recognizer: Parser) throws -> Bool {
        let currentSymbolType = try getTokenStream(recognizer).LA(1)
        // if current token is consistent with what could come after current
        // ATN state, then we know we're missing a token; error recovery
        // is free to conjure up and insert the missing token
        let currentState = recognizer.getInterpreter().atn.states[recognizer.getState()]!
        let next = currentState.transition(0).target
        let atn = recognizer.getInterpreter().atn
        let expectingAtLL2 = atn.nextTokens(next, recognizer._ctx)
//		print("LT(2) set="+expectingAtLL2.toString(recognizer.getTokenNames()));
        if expectingAtLL2.contains(currentSymbolType) {
            reportMissingToken(recognizer)
            return true
        }
        return false
    }

    /// 
    /// This method implements the single-token deletion inline error recovery
    /// strategy. It is called by _#recoverInline_ to attempt to recover
    /// from mismatched input. If this method returns null, the parser and error
    /// handler state will not have changed. If this method returns non-null,
    /// `recognizer` will __not__ be in error recovery mode since the
    /// returned token was a successful match.
    /// 
    /// If the single-token deletion is successful, this method calls
    /// _#reportUnwantedToken_ to report the error, followed by
    /// _org.antlr.v4.runtime.Parser#consume_ to actually "delete" the extraneous token. Then,
    /// before returning _#reportMatch_ is called to signal a successful
    /// match.
    /// 
    /// - parameter recognizer: the parser instance
    /// - returns: the successfully matched _org.antlr.v4.runtime.Token_ instance if single-token
    /// deletion successfully recovers from the mismatched input, otherwise
    /// `null`
    /// 
    open func singleTokenDeletion(_ recognizer: Parser) throws -> Token? {
        let nextTokenType = try getTokenStream(recognizer).LA(2)
        let expecting = try getExpectedTokens(recognizer)
        if expecting.contains(nextTokenType) {
            reportUnwantedToken(recognizer)
            /// 
            /// errPrint("recoverFromMismatchedToken deleting "+
            /// ((TokenStream)getTokenStream(recognizer)).LT(1)+
            /// " since "+((TokenStream)getTokenStream(recognizer)).LT(2)+
            /// " is what we want");
            /// 
            try recognizer.consume() // simply delete extra token
            // we want to return the token we're actually matching
            let matchedSymbol = try recognizer.getCurrentToken()
            reportMatch(recognizer)  // we know current token is correct
            return matchedSymbol
        }
        return nil
    }

    /// 
    /// Conjure up a missing token during error recovery.
    /// 
    /// The recognizer attempts to recover from single missing
    /// symbols. But, actions might refer to that missing symbol.
    /// For example, x=ID {f($x);}. The action clearly assumes
    /// that there has been an identifier matched previously and that
    /// $x points at that token. If that token is missing, but
    /// the next token in the stream is what we want we assume that
    /// this token is missing and we keep going. Because we
    /// have to return some token to replace the missing token,
    /// we have to conjure one up. This method gives the user control
    /// over the tokens returned for missing tokens. Mostly,
    /// you will want to create something special for identifier
    /// tokens. For literals such as '{' and ',', the default
    /// action in the parser or tree parser works. It simply creates
    /// a CommonToken of the appropriate type. The text will be the token.
    /// If you change what tokens must be created by the lexer,
    /// override this method to create the appropriate tokens.
    /// 
    open func getTokenStream(_ recognizer: Parser) -> TokenStream {
        return recognizer.getInputStream() as! TokenStream
    }

    open func getMissingSymbol(_ recognizer: Parser) throws -> Token {
        let currentSymbol = try recognizer.getCurrentToken()
        let expecting = try getExpectedTokens(recognizer)
        let expectedTokenType = expecting.getMinElement() // get any element
        var tokenText: String
        if expectedTokenType == CommonToken.EOF {
            tokenText = "<missing EOF>"
        } else {
            tokenText = "<missing " + recognizer.getVocabulary().getDisplayName(expectedTokenType) + ">"
        }
        var current = currentSymbol
        let lookback = try getTokenStream(recognizer).LT(-1)
        if current.getType() == CommonToken.EOF && lookback != nil {
            current = lookback!
        }

        let token = recognizer.getTokenFactory().create(
            current.getTokenSourceAndStream(),
            expectedTokenType, tokenText,
            CommonToken.DEFAULT_CHANNEL,
            -1, -1,
            current.getLine(), current.getCharPositionInLine())

        return token
    }


    open func getExpectedTokens(_ recognizer: Parser) throws -> IntervalSet {
        return try recognizer.getExpectedTokens()
    }

    /// 
    /// How should a token be displayed in an error message? The default
    /// is to display just the text, but during development you might
    /// want to have a lot of information spit out.  Override in that case
    /// to use t.toString() (which, for CommonToken, dumps everything about
    /// the token). This is better than forcing you to override a method in
    /// your token objects because you don't have to go modify your lexer
    /// so that it creates a new Java type.
    /// 
    open func getTokenErrorDisplay(_ t: Token?) -> String {
        guard let t = t else {
            return "<no token>"
        }
        var s = getSymbolText(t)
        if s == nil {
            if getSymbolType(t) == CommonToken.EOF {
                s = "<EOF>"
            } else {
                s = "<\(getSymbolType(t))>"
            }
        }
        return escapeWSAndQuote(s!)
    }

    open func getSymbolText(_ symbol: Token) -> String? {
        return symbol.getText()
    }

    open func getSymbolType(_ symbol: Token) -> Int {
        return symbol.getType()
    }


    open func escapeWSAndQuote(_ s: String) -> String {
        var s = s
        s = s.replacingOccurrences(of: "\n", with: "\\n")
        s = s.replacingOccurrences(of: "\r", with: "\\r")
        s = s.replacingOccurrences(of: "\t", with: "\\t")
        return "'" + s + "'"
    }

    /// 
    /// Compute the error recovery set for the current rule.  During
    /// rule invocation, the parser pushes the set of tokens that can
    /// follow that rule reference on the stack; this amounts to
    /// computing FIRST of what follows the rule reference in the
    /// enclosing rule. See LinearApproximator.FIRST().
    /// This local follow set only includes tokens
    /// from within the rule; i.e., the FIRST computation done by
    /// ANTLR stops at the end of a rule.
    /// 
    /// EXAMPLE
    /// 
    /// When you find a "no viable alt exception", the input is not
    /// consistent with any of the alternatives for rule r.  The best
    /// thing to do is to consume tokens until you see something that
    /// can legally follow a call to r *or* any rule that called r.
    /// You don't want the exact set of viable next tokens because the
    /// input might just be missing a token--you might consume the
    /// rest of the input looking for one of the missing tokens.
    /// 
    /// Consider grammar:
    /// 
    /// a : '[' b ']'
    /// | '(' b ')'
    /// ;
    /// b : c '^' INT ;
    /// c : ID
    /// | INT
    /// ;
    /// 
    /// At each rule invocation, the set of tokens that could follow
    /// that rule is pushed on a stack.  Here are the various
    /// context-sensitive follow sets:
    /// 
    /// FOLLOW(b1_in_a) = FIRST(']') = ']'
    /// FOLLOW(b2_in_a) = FIRST(')') = ')'
    /// FOLLOW(c_in_b) = FIRST('^') = '^'
    /// 
    /// Upon erroneous input "[]", the call chain is
    /// 
    /// a -> b -> c
    /// 
    /// and, hence, the follow context stack is:
    /// 
    /// depth     follow set       start of rule execution
    /// 0         <EOF>                    a (from main())
    /// 1          ']'                     b
    /// 2          '^'                     c
    /// 
    /// Notice that ')' is not included, because b would have to have
    /// been called from a different context in rule a for ')' to be
    /// included.
    /// 
    /// For error recovery, we cannot consider FOLLOW(c)
    /// (context-sensitive or otherwise).  We need the combined set of
    /// all context-sensitive FOLLOW sets--the set of all tokens that
    /// could follow any reference in the call chain.  We need to
    /// resync to one of those tokens.  Note that FOLLOW(c)='^' and if
    /// we resync'd to that token, we'd consume until EOF.  We need to
    /// sync to context-sensitive FOLLOWs for a, b, and c: {']','^'}.
    /// In this case, for input "[]", LA(1) is ']' and in the set, so we would
    /// not consume anything. After printing an error, rule c would
    /// return normally.  Rule b would not find the required '^' though.
    /// At this point, it gets a mismatched token error and throws an
    /// exception (since LA(1) is not in the viable following token
    /// set).  The rule exception handler tries to recover, but finds
    /// the same recovery set and doesn't consume anything.  Rule b
    /// exits normally returning to rule a.  Now it finds the ']' (and
    /// with the successful match exits errorRecovery mode).
    /// 
    /// So, you can see that the parser walks up the call chain looking
    /// for the token that was a member of the recovery set.
    /// 
    /// Errors are not generated in errorRecovery mode.
    /// 
    /// ANTLR's error recovery mechanism is based upon original ideas:
    /// 
    /// "Algorithms + Data Structures = Programs" by Niklaus Wirth
    /// 
    /// and
    /// 
    /// "A note on error recovery in recursive descent parsers":
    /// http://portal.acm.org/citation.cfm?id=947902.947905
    /// 
    /// Later, Josef Grosch had some good ideas:
    /// 
    /// "Efficient and Comfortable Error Recovery in Recursive Descent
    /// Parsers":
    /// ftp://www.cocolab.com/products/cocktail/doca4.ps/ell.ps.zip
    /// 
    /// Like Grosch I implement context-sensitive FOLLOW sets that are combined
    /// at run-time upon error to avoid overhead during parsing.
    /// 
    open func getErrorRecoverySet(_ recognizer: Parser) -> IntervalSet {
        let atn = recognizer.getInterpreter().atn
        var ctx: RuleContext? = recognizer._ctx
        let recoverSet = IntervalSet()
        while let ctxWrap = ctx, ctxWrap.invokingState >= 0 {
            // compute what follows who invoked us
            let invokingState = atn.states[ctxWrap.invokingState]!
            let rt = invokingState.transition(0) as! RuleTransition
            let follow = atn.nextTokens(rt.followState)
            try! recoverSet.addAll(follow)
            ctx = ctxWrap.parent
        }
        try! recoverSet.remove(CommonToken.EPSILON)
//		print("recover set "+recoverSet.toString(recognizer.getTokenNames()));
        return recoverSet
    }

    /// 
    /// Consume tokens until one matches the given token set.
    /// 
    open func consumeUntil(_ recognizer: Parser, _ set: IntervalSet) throws {
//		errPrint("consumeUntil("+set.toString(recognizer.getTokenNames())+")");
        var ttype = try getTokenStream(recognizer).LA(1)
        while ttype != CommonToken.EOF && !set.contains(ttype) {
            //print("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
            try recognizer.consume()
            ttype = try getTokenStream(recognizer).LA(1)
        }
    }
}
