/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This is the default implementation of
    /// <see cref="IAntlrErrorStrategy"/>
    /// used for
    /// error reporting and recovery in ANTLR parsers.
    /// </summary>
    public class DefaultErrorStrategy : IAntlrErrorStrategy
    {
        /// <summary>
        /// Indicates whether the error strategy is currently "recovering from an
        /// error".
        /// </summary>
        /// <remarks>
        /// Indicates whether the error strategy is currently "recovering from an
        /// error". This is used to suppress reporting multiple error messages while
        /// attempting to recover from a detected syntax error.
        /// </remarks>
        /// <seealso cref="InErrorRecoveryMode(Parser)"/>
        protected internal bool errorRecoveryMode = false;

        /// <summary>The index into the input stream where the last error occurred.</summary>
        /// <remarks>
        /// The index into the input stream where the last error occurred.
        /// This is used to prevent infinite loops where an error is found
        /// but no token is consumed during recovery...another error is found,
        /// ad nauseum.  This is a failsafe mechanism to guarantee that at least
        /// one token/tree node is consumed for two errors.
        /// </remarks>
        protected internal int lastErrorIndex = -1;

        protected internal IntervalSet lastErrorStates;

        /**
         * This field is used to propagate information about the lookahead following
         * the previous match. Since prediction prefers completing the current rule
         * to error recovery efforts, error reporting may occur later than the
         * original point where it was discoverable. The original context is used to
         * compute the true expected sets as though the reporting occurred as early
         * as possible.
         */
        protected ParserRuleContext nextTokensContext;

        /**
         * @see #nextTokensContext
         */
        protected int nextTokensState;

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation simply calls
        /// <see cref="EndErrorCondition(Parser)"/>
        /// to
        /// ensure that the handler is not in error recovery mode.</p>
        /// </summary>
        public virtual void Reset(Parser recognizer)
        {
            EndErrorCondition(recognizer);
        }

        /// <summary>
        /// This method is called to enter error recovery mode when a recognition
        /// exception is reported.
        /// </summary>
        /// <remarks>
        /// This method is called to enter error recovery mode when a recognition
        /// exception is reported.
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        protected internal virtual void BeginErrorCondition(Parser recognizer)
        {
            errorRecoveryMode = true;
        }

        /// <summary><inheritDoc/></summary>
        public virtual bool InErrorRecoveryMode(Parser recognizer)
        {
            return errorRecoveryMode;
        }

        /// <summary>
        /// This method is called to leave error recovery mode after recovering from
        /// a recognition exception.
        /// </summary>
        /// <remarks>
        /// This method is called to leave error recovery mode after recovering from
        /// a recognition exception.
        /// </remarks>
        /// <param name="recognizer"/>
        protected internal virtual void EndErrorCondition(Parser recognizer)
        {
            errorRecoveryMode = false;
            lastErrorStates = null;
            lastErrorIndex = -1;
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation simply calls
        /// <see cref="EndErrorCondition(Parser)"/>
        /// .</p>
        /// </summary>
        public virtual void ReportMatch(Parser recognizer)
        {
            EndErrorCondition(recognizer);
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation returns immediately if the handler is already
        /// in error recovery mode. Otherwise, it calls
        /// <see cref="BeginErrorCondition(Parser)"/>
        /// and dispatches the reporting task based on the runtime type of
        /// <paramref name="e"/>
        /// according to the following table.</p>
        /// <ul>
        /// <li>
        /// <see cref="NoViableAltException"/>
        /// : Dispatches the call to
        /// <see cref="ReportNoViableAlternative(Parser, NoViableAltException)"/>
        /// </li>
        /// <li>
        /// <see cref="InputMismatchException"/>
        /// : Dispatches the call to
        /// <see cref="ReportInputMismatch(Parser, InputMismatchException)"/>
        /// </li>
        /// <li>
        /// <see cref="FailedPredicateException"/>
        /// : Dispatches the call to
        /// <see cref="ReportFailedPredicate(Parser, FailedPredicateException)"/>
        /// </li>
        /// <li>All other types: calls
        /// <see cref="Parser.NotifyErrorListeners(string)"/>
        /// to report
        /// the exception</li>
        /// </ul>
        /// </summary>
        public virtual void ReportError(Parser recognizer, RecognitionException e)
        {
            // if we've already reported an error and have not matched a token
            // yet successfully, don't report any errors.
            if (InErrorRecoveryMode(recognizer))
            {
                //			System.err.print("[SPURIOUS] ");
                return;
            }
            // don't report spurious errors
            BeginErrorCondition(recognizer);
            if (e is NoViableAltException)
            {
                ReportNoViableAlternative(recognizer, (NoViableAltException)e);
            }
            else
            {
                if (e is InputMismatchException)
                {
                    ReportInputMismatch(recognizer, (InputMismatchException)e);
                }
                else
                {
                    if (e is FailedPredicateException)
                    {
                        ReportFailedPredicate(recognizer, (FailedPredicateException)e);
                    }
                    else
                    {
                        System.Console.Error.WriteLine("unknown recognition error type: " + e.GetType().FullName);
                        NotifyErrorListeners(recognizer, e.Message, e);
                    }
                }
            }
        }

        protected internal virtual void NotifyErrorListeners(Parser recognizer, string message, RecognitionException e)
        {
            recognizer.NotifyErrorListeners(e.OffendingToken, message, e);
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation resynchronizes the parser by consuming tokens
        /// until we find one in the resynchronization set--loosely the set of tokens
        /// that can follow the current rule.</p>
        /// </summary>
        public virtual void Recover(Parser recognizer, RecognitionException e)
        {
            //		System.out.println("recover in "+recognizer.getRuleInvocationStack()+
            //						   " index="+recognizer.getInputStream().index()+
            //						   ", lastErrorIndex="+
            //						   lastErrorIndex+
            //						   ", states="+lastErrorStates);
            if (lastErrorIndex == ((ITokenStream)recognizer.InputStream).Index && lastErrorStates != null && lastErrorStates.Contains(recognizer.State))
            {
                // uh oh, another error at same token index and previously-visited
                // state in ATN; must be a case where LT(1) is in the recovery
                // token set so nothing got consumed. Consume a single token
                // at least to prevent an infinite loop; this is a failsafe.
                //			System.err.println("seen error condition before index="+
                //							   lastErrorIndex+", states="+lastErrorStates);
                //			System.err.println("FAILSAFE consumes "+recognizer.getTokenNames()[recognizer.getInputStream().LA(1)]);
                recognizer.Consume();
            }
            lastErrorIndex = ((ITokenStream)recognizer.InputStream).Index;
            if (lastErrorStates == null)
            {
                lastErrorStates = new IntervalSet();
            }
            lastErrorStates.Add(recognizer.State);
            IntervalSet followSet = GetErrorRecoverySet(recognizer);
            ConsumeUntil(recognizer, followSet);
        }

        /// <summary>
        /// The default implementation of
        /// <see cref="IAntlrErrorStrategy.Sync(Parser)"/>
        /// makes sure
        /// that the current lookahead symbol is consistent with what were expecting
        /// at this point in the ATN. You can call this anytime but ANTLR only
        /// generates code to check before subrules/loops and each iteration.
        /// <p>Implements Jim Idle's magic sync mechanism in closures and optional
        /// subrules. E.g.,</p>
        /// <pre>
        /// a : sync ( stuff sync )* ;
        /// sync : {consume to what can follow sync} ;
        /// </pre>
        /// At the start of a sub rule upon error,
        /// <see cref="Sync(Parser)"/>
        /// performs single
        /// token deletion, if possible. If it can't do that, it bails on the current
        /// rule and uses the default error recovery, which consumes until the
        /// resynchronization set of the current rule.
        /// <p>If the sub rule is optional (
        /// <c>(...)?</c>
        /// ,
        /// <c>(...)*</c>
        /// , or block
        /// with an empty alternative), then the expected set includes what follows
        /// the subrule.</p>
        /// <p>During loop iteration, it consumes until it sees a token that can start a
        /// sub rule or what follows loop. Yes, that is pretty aggressive. We opt to
        /// stay in the loop as long as possible.</p>
        /// <p><strong>ORIGINS</strong></p>
        /// <p>Previous versions of ANTLR did a poor job of their recovery within loops.
        /// A single mismatch token or missing token would force the parser to bail
        /// out of the entire rules surrounding the loop. So, for rule</p>
        /// <pre>
        /// classDef : 'class' ID '{' member* '}'
        /// </pre>
        /// input with an extra token between members would force the parser to
        /// consume until it found the next class definition rather than the next
        /// member definition of the current class.
        /// <p>This functionality cost a little bit of effort because the parser has to
        /// compare token set at the start of the loop and at each iteration. If for
        /// some reason speed is suffering for you, you can turn off this
        /// functionality by simply overriding this method as a blank { }.</p>
        /// </summary>
        /// <exception cref="Antlr4.Runtime.RecognitionException"/>
        public virtual void Sync(Parser recognizer)
        {
            ATNState s = recognizer.Interpreter.atn.states[recognizer.State];
            //		System.err.println("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName());
            // If already recovering, don't try to sync
            if (InErrorRecoveryMode(recognizer))
            {
                return;
            }
            ITokenStream tokens = ((ITokenStream)recognizer.InputStream);
            int la = tokens.LA(1);
            // try cheaper subset first; might get lucky. seems to shave a wee bit off
            var nextTokens = recognizer.Atn.NextTokens(s);
            if (nextTokens.Contains(la))
            {
                nextTokensContext = null;
                nextTokensState = ATNState.InvalidStateNumber;
                return;
            }

            if (nextTokens.Contains(TokenConstants.EPSILON))
            {
                if (nextTokensContext == null)
                {
                    // It's possible the next token won't match; information tracked
                    // by sync is restricted for performance.
                    nextTokensContext = recognizer.Context;
                    nextTokensState = recognizer.State;
                }
                return;
            }
            switch (s.StateType)
            {
                case StateType.BlockStart:
                case StateType.StarBlockStart:
                case StateType.PlusBlockStart:
                case StateType.StarLoopEntry:
                {
                    // report error and recover if possible
                    if (SingleTokenDeletion(recognizer) != null)
                    {
                        return;
                    }
                    throw new InputMismatchException(recognizer);
                }

                case StateType.PlusLoopBack:
                case StateType.StarLoopBack:
                {
                    //			System.err.println("at loop back: "+s.getClass().getSimpleName());
                    ReportUnwantedToken(recognizer);
                    IntervalSet expecting = recognizer.GetExpectedTokens();
                    IntervalSet whatFollowsLoopIterationOrRule = expecting.Or(GetErrorRecoverySet(recognizer));
                    ConsumeUntil(recognizer, whatFollowsLoopIterationOrRule);
                    break;
                }

                default:
                {
                    // do nothing if we can't identify the exact kind of ATN state
                    break;
                }
            }
        }

        /// <summary>
        /// This is called by
        /// <see cref="ReportError(Parser, RecognitionException)"/>
        /// when the exception is a
        /// <see cref="NoViableAltException"/>
        /// .
        /// </summary>
        /// <seealso cref="ReportError(Parser, RecognitionException)"/>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="e">the recognition exception</param>
        protected internal virtual void ReportNoViableAlternative(Parser recognizer, NoViableAltException e)
        {
            ITokenStream tokens = ((ITokenStream)recognizer.InputStream);
            string input;
            if (tokens != null)
            {
                if (e.StartToken.Type == TokenConstants.EOF)
                {
                    input = "<EOF>";
                }
                else
                {
                    input = tokens.GetText(e.StartToken, e.OffendingToken);
                }
            }
            else
            {
                input = "<unknown input>";
            }
            string msg = "no viable alternative at input " + EscapeWSAndQuote(input);
            NotifyErrorListeners(recognizer, msg, e);
        }

        /// <summary>
        /// This is called by
        /// <see cref="ReportError(Parser, RecognitionException)"/>
        /// when the exception is an
        /// <see cref="InputMismatchException"/>
        /// .
        /// </summary>
        /// <seealso cref="ReportError(Parser, RecognitionException)"/>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="e">the recognition exception</param>
        protected internal virtual void ReportInputMismatch(Parser recognizer, InputMismatchException e)
        {
            string msg = "mismatched input " + GetTokenErrorDisplay(e.OffendingToken) + " expecting " + e.GetExpectedTokens().ToString(recognizer.Vocabulary);
            NotifyErrorListeners(recognizer, msg, e);
        }

        /// <summary>
        /// This is called by
        /// <see cref="ReportError(Parser, RecognitionException)"/>
        /// when the exception is a
        /// <see cref="FailedPredicateException"/>
        /// .
        /// </summary>
        /// <seealso cref="ReportError(Parser, RecognitionException)"/>
        /// <param name="recognizer">the parser instance</param>
        /// <param name="e">the recognition exception</param>
        protected internal virtual void ReportFailedPredicate(Parser recognizer, FailedPredicateException e)
        {
			string ruleName = recognizer.RuleNames[recognizer.RuleContext.RuleIndex];
            string msg = "rule " + ruleName + " " + e.Message;
            NotifyErrorListeners(recognizer, msg, e);
        }

        /// <summary>
        /// This method is called to report a syntax error which requires the removal
        /// of a token from the input stream.
        /// </summary>
        /// <remarks>
        /// This method is called to report a syntax error which requires the removal
        /// of a token from the input stream. At the time this method is called, the
        /// erroneous symbol is current
        /// <c>LT(1)</c>
        /// symbol and has not yet been
        /// removed from the input stream. When this method returns,
        /// <paramref name="recognizer"/>
        /// is in error recovery mode.
        /// <p>This method is called when
        /// <see cref="SingleTokenDeletion(Parser)"/>
        /// identifies
        /// single-token deletion as a viable recovery strategy for a mismatched
        /// input error.</p>
        /// <p>The default implementation simply returns if the handler is already in
        /// error recovery mode. Otherwise, it calls
        /// <see cref="BeginErrorCondition(Parser)"/>
        /// to
        /// enter error recovery mode, followed by calling
        /// <see cref="Parser.NotifyErrorListeners(string)"/>
        /// .</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        protected internal virtual void ReportUnwantedToken(Parser recognizer)
        {
            if (InErrorRecoveryMode(recognizer))
            {
                return;
            }
            BeginErrorCondition(recognizer);
            IToken t = recognizer.CurrentToken;
            string tokenName = GetTokenErrorDisplay(t);
            IntervalSet expecting = GetExpectedTokens(recognizer);
            string msg = "extraneous input " + tokenName + " expecting " + expecting.ToString(recognizer.Vocabulary);
            recognizer.NotifyErrorListeners(t, msg, null);
        }

        /// <summary>
        /// This method is called to report a syntax error which requires the
        /// insertion of a missing token into the input stream.
        /// </summary>
        /// <remarks>
        /// This method is called to report a syntax error which requires the
        /// insertion of a missing token into the input stream. At the time this
        /// method is called, the missing token has not yet been inserted. When this
        /// method returns,
        /// <paramref name="recognizer"/>
        /// is in error recovery mode.
        /// <p>This method is called when
        /// <see cref="SingleTokenInsertion(Parser)"/>
        /// identifies
        /// single-token insertion as a viable recovery strategy for a mismatched
        /// input error.</p>
        /// <p>The default implementation simply returns if the handler is already in
        /// error recovery mode. Otherwise, it calls
        /// <see cref="BeginErrorCondition(Parser)"/>
        /// to
        /// enter error recovery mode, followed by calling
        /// <see cref="Parser.NotifyErrorListeners(string)"/>
        /// .</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        protected internal virtual void ReportMissingToken(Parser recognizer)
        {
            if (InErrorRecoveryMode(recognizer))
            {
                return;
            }
            BeginErrorCondition(recognizer);
            IToken t = recognizer.CurrentToken;
            IntervalSet expecting = GetExpectedTokens(recognizer);
            string msg = "missing " + expecting.ToString(recognizer.Vocabulary) + " at " + GetTokenErrorDisplay(t);
            recognizer.NotifyErrorListeners(t, msg, null);
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The default implementation attempts to recover from the mismatched input
        /// by using single token insertion and deletion as described below. If the
        /// recovery attempt fails, this method throws an
        /// <see cref="InputMismatchException"/>
        /// .</p>
        /// <p><strong>EXTRA TOKEN</strong> (single token deletion)</p>
        /// <p>
        /// <c>LA(1)</c>
        /// is not what we are looking for. If
        /// <c>LA(2)</c>
        /// has the
        /// right token, however, then assume
        /// <c>LA(1)</c>
        /// is some extra spurious
        /// token and delete it. Then consume and return the next token (which was
        /// the
        /// <c>LA(2)</c>
        /// token) as the successful result of the match operation.</p>
        /// <p>This recovery strategy is implemented by
        /// <see cref="SingleTokenDeletion(Parser)"/>
        /// .</p>
        /// <p><strong>MISSING TOKEN</strong> (single token insertion)</p>
        /// <p>If current token (at
        /// <c>LA(1)</c>
        /// ) is consistent with what could come
        /// after the expected
        /// <c>LA(1)</c>
        /// token, then assume the token is missing
        /// and use the parser's
        /// <see cref="ITokenFactory"/>
        /// to create it on the fly. The
        /// "insertion" is performed by returning the created token as the successful
        /// result of the match operation.</p>
        /// <p>This recovery strategy is implemented by
        /// <see cref="SingleTokenInsertion(Parser)"/>
        /// .</p>
        /// <p><strong>EXAMPLE</strong></p>
        /// <p>For example, Input
        /// <c>i=(3;</c>
        /// is clearly missing the
        /// <c>')'</c>
        /// . When
        /// the parser returns from the nested call to
        /// <c>expr</c>
        /// , it will have
        /// call chain:</p>
        /// <pre>
        /// stat &#x2192; expr &#x2192; atom
        /// </pre>
        /// and it will be trying to match the
        /// <c>')'</c>
        /// at this point in the
        /// derivation:
        /// <pre>
        /// =&gt; ID '=' '(' INT ')' ('+' atom)* ';'
        /// ^
        /// </pre>
        /// The attempt to match
        /// <c>')'</c>
        /// will fail when it sees
        /// <c>';'</c>
        /// and
        /// call
        /// <see cref="RecoverInline(Parser)"/>
        /// . To recover, it sees that
        /// <c>LA(1)==';'</c>
        /// is in the set of tokens that can follow the
        /// <c>')'</c>
        /// token reference
        /// in rule
        /// <c>atom</c>
        /// . It can assume that you forgot the
        /// <c>')'</c>
        /// .
        /// </summary>
        /// <exception cref="Antlr4.Runtime.RecognitionException"/>
        public virtual IToken RecoverInline(Parser recognizer)
        {
            // SINGLE TOKEN DELETION
            IToken matchedSymbol = SingleTokenDeletion(recognizer);
            if (matchedSymbol != null)
            {
                // we have deleted the extra token.
                // now, move past ttype token as if all were ok
                recognizer.Consume();
                return matchedSymbol;
            }
            // SINGLE TOKEN INSERTION
            if (SingleTokenInsertion(recognizer))
            {
                return GetMissingSymbol(recognizer);
            }
            // even that didn't work; must throw the exception
            throw new InputMismatchException(recognizer);
        }

        /// <summary>
        /// This method implements the single-token insertion inline error recovery
        /// strategy.
        /// </summary>
        /// <remarks>
        /// This method implements the single-token insertion inline error recovery
        /// strategy. It is called by
        /// <see cref="RecoverInline(Parser)"/>
        /// if the single-token
        /// deletion strategy fails to recover from the mismatched input. If this
        /// method returns
        /// <see langword="true"/>
        /// ,
        /// <paramref name="recognizer"/>
        /// will be in error recovery
        /// mode.
        /// <p>This method determines whether or not single-token insertion is viable by
        /// checking if the
        /// <c>LA(1)</c>
        /// input symbol could be successfully matched
        /// if it were instead the
        /// <c>LA(2)</c>
        /// symbol. If this method returns
        /// <see langword="true"/>
        /// , the caller is responsible for creating and inserting a
        /// token with the correct type to produce this behavior.</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        /// <returns>
        ///
        /// <see langword="true"/>
        /// if single-token insertion is a viable recovery
        /// strategy for the current mismatched input, otherwise
        /// <see langword="false"/>
        /// </returns>
        protected internal virtual bool SingleTokenInsertion(Parser recognizer)
        {
            int currentSymbolType = ((ITokenStream)recognizer.InputStream).LA(1);
            // if current token is consistent with what could come after current
            // ATN state, then we know we're missing a token; error recovery
            // is free to conjure up and insert the missing token
            ATNState currentState = recognizer.Interpreter.atn.states[recognizer.State];
            ATNState next = currentState.Transition(0).target;
            ATN atn = recognizer.Interpreter.atn;
			IntervalSet expectingAtLL2 = atn.NextTokens(next, recognizer.RuleContext);
            if (expectingAtLL2.Contains(currentSymbolType))
            {
                ReportMissingToken(recognizer);
                return true;
            }
            return false;
        }

        /// <summary>
        /// This method implements the single-token deletion inline error recovery
        /// strategy.
        /// </summary>
        /// <remarks>
        /// This method implements the single-token deletion inline error recovery
        /// strategy. It is called by
        /// <see cref="RecoverInline(Parser)"/>
        /// to attempt to recover
        /// from mismatched input. If this method returns null, the parser and error
        /// handler state will not have changed. If this method returns non-null,
        /// <paramref name="recognizer"/>
        /// will <em>not</em> be in error recovery mode since the
        /// returned token was a successful match.
        /// <p>If the single-token deletion is successful, this method calls
        /// <see cref="ReportUnwantedToken(Parser)"/>
        /// to report the error, followed by
        /// <see cref="Parser.Consume()"/>
        /// to actually "delete" the extraneous token. Then,
        /// before returning
        /// <see cref="ReportMatch(Parser)"/>
        /// is called to signal a successful
        /// match.</p>
        /// </remarks>
        /// <param name="recognizer">the parser instance</param>
        /// <returns>
        /// the successfully matched
        /// <see cref="IToken"/>
        /// instance if single-token
        /// deletion successfully recovers from the mismatched input, otherwise
        /// <see langword="null"/>
        /// </returns>
        [return: Nullable]
        protected internal virtual IToken SingleTokenDeletion(Parser recognizer)
        {
            int nextTokenType = ((ITokenStream)recognizer.InputStream).LA(2);
            IntervalSet expecting = GetExpectedTokens(recognizer);
            if (expecting.Contains(nextTokenType))
            {
                ReportUnwantedToken(recognizer);
                recognizer.Consume();
                // simply delete extra token
                // we want to return the token we're actually matching
                IToken matchedSymbol = recognizer.CurrentToken;
                ReportMatch(recognizer);
                // we know current token is correct
                return matchedSymbol;
            }
            return null;
        }

        /// <summary>Conjure up a missing token during error recovery.</summary>
        /// <remarks>
        /// Conjure up a missing token during error recovery.
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
        /// </remarks>
        [return: NotNull]
        protected internal virtual IToken GetMissingSymbol(Parser recognizer)
        {
            IToken currentSymbol = recognizer.CurrentToken;
            IntervalSet expecting = GetExpectedTokens(recognizer);
            int expectedTokenType = expecting.MinElement;
            // get any element
            string tokenText;
            if (expectedTokenType == TokenConstants.EOF)
            {
                tokenText = "<missing EOF>";
            }
            else
            {
                tokenText = "<missing " + recognizer.Vocabulary.GetDisplayName(expectedTokenType) + ">";
            }
            IToken current = currentSymbol;
            IToken lookback = ((ITokenStream)recognizer.InputStream).LT(-1);
            if (current.Type == TokenConstants.EOF && lookback != null)
            {
                current = lookback;
            }
            return ConstructToken(((ITokenStream)recognizer.InputStream).TokenSource, expectedTokenType, tokenText, current);
        }

        protected internal virtual IToken ConstructToken(ITokenSource tokenSource, int expectedTokenType, string tokenText, IToken current)
        {
            ITokenFactory factory = tokenSource.TokenFactory;
            return factory.Create(Tuple.Create(tokenSource, current.TokenSource.InputStream), expectedTokenType, tokenText, TokenConstants.DefaultChannel, -1, -1, current.Line, current.Column);
        }

        [return: NotNull]
        protected internal virtual IntervalSet GetExpectedTokens(Parser recognizer)
        {
            return recognizer.GetExpectedTokens();
        }

        /// <summary>
        /// How should a token be displayed in an error message? The default
        /// is to display just the text, but during development you might
        /// want to have a lot of information spit out.
        /// </summary>
        /// <remarks>
        /// How should a token be displayed in an error message? The default
        /// is to display just the text, but during development you might
        /// want to have a lot of information spit out.  Override in that case
        /// to use t.toString() (which, for CommonToken, dumps everything about
        /// the token). This is better than forcing you to override a method in
        /// your token objects because you don't have to go modify your lexer
        /// so that it creates a new Java type.
        /// </remarks>
        protected internal virtual string GetTokenErrorDisplay(IToken t)
        {
            if (t == null)
            {
                return "<no token>";
            }
            string s = GetSymbolText(t);
            if (s == null)
            {
                if (GetSymbolType(t) == TokenConstants.EOF)
                {
                    s = "<EOF>";
                }
                else
                {
                    s = "<" + GetSymbolType(t) + ">";
                }
            }
            return EscapeWSAndQuote(s);
        }

        protected internal virtual string GetSymbolText(IToken symbol)
        {
            return symbol.Text;
        }

        protected internal virtual int GetSymbolType(IToken symbol)
        {
            return symbol.Type;
        }

        [return: NotNull]
        protected internal virtual string EscapeWSAndQuote(string s)
        {
            //		if ( s==null ) return s;
            s = s.Replace("\n", "\\n");
            s = s.Replace("\r", "\\r");
            s = s.Replace("\t", "\\t");
            return "'" + s + "'";
        }

        [return: NotNull]
        protected internal virtual IntervalSet GetErrorRecoverySet(Parser recognizer)
        {
            ATN atn = recognizer.Interpreter.atn;
			RuleContext ctx = recognizer.RuleContext;
            IntervalSet recoverSet = new IntervalSet();
            while (ctx != null && ctx.invokingState >= 0)
            {
                // compute what follows who invoked us
                ATNState invokingState = atn.states[ctx.invokingState];
                RuleTransition rt = (RuleTransition)invokingState.Transition(0);
                IntervalSet follow = atn.NextTokens(rt.followState);
                recoverSet.AddAll(follow);
                ctx = ctx.Parent;
            }
            recoverSet.Remove(TokenConstants.EPSILON);
            //		System.out.println("recover set "+recoverSet.toString(recognizer.getTokenNames()));
            return recoverSet;
        }

        /// <summary>Consume tokens until one matches the given token set.</summary>
        /// <remarks>Consume tokens until one matches the given token set.</remarks>
        protected internal virtual void ConsumeUntil(Parser recognizer, IntervalSet set)
        {
            //		System.err.println("consumeUntil("+set.toString(recognizer.getTokenNames())+")");
            int ttype = ((ITokenStream)recognizer.InputStream).LA(1);
            while (ttype != TokenConstants.EOF && !set.Contains(ttype))
            {
                //System.out.println("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
                //			recognizer.getInputStream().consume();
                recognizer.Consume();
                ttype = ((ITokenStream)recognizer.InputStream).LA(1);
            }
        }
    }
}
