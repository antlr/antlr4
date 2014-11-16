/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Pair;

/**
 * This is the default implementation of {@link ANTLRErrorStrategy} used for
 * error reporting and recovery in ANTLR parsers.
 */
public class DefaultErrorStrategy implements ANTLRErrorStrategy {
	/**
	 * Indicates whether the error strategy is currently "recovering from an
	 * error". This is used to suppress reporting multiple error messages while
	 * attempting to recover from a detected syntax error.
	 *
	 * @see #inErrorRecoveryMode
	 */
	protected boolean errorRecoveryMode = false;

	/** The index into the input stream where the last error occurred.
	 * 	This is used to prevent infinite loops where an error is found
	 *  but no token is consumed during recovery...another error is found,
	 *  ad nauseum.  This is a failsafe mechanism to guarantee that at least
	 *  one token/tree node is consumed for two errors.
	 */
	protected int lastErrorIndex = -1;

	protected IntervalSet lastErrorStates;

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation simply calls {@link #endErrorCondition} to
	 * ensure that the handler is not in error recovery mode.</p>
	 */
	@Override
	public void reset(Parser recognizer) {
		endErrorCondition(recognizer);
	}

	/**
	 * This method is called to enter error recovery mode when a recognition
	 * exception is reported.
	 *
	 * @param recognizer the parser instance
	 */
	protected void beginErrorCondition(@NotNull Parser recognizer) {
		errorRecoveryMode = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean inErrorRecoveryMode(Parser recognizer) {
		return errorRecoveryMode;
	}

	/**
	 * This method is called to leave error recovery mode after recovering from
	 * a recognition exception.
	 *
	 * @param recognizer
	 */
	protected void endErrorCondition(@NotNull Parser recognizer) {
		errorRecoveryMode = false;
		lastErrorStates = null;
		lastErrorIndex = -1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation simply calls {@link #endErrorCondition}.</p>
	 */
	@Override
	public void reportMatch(Parser recognizer) {
		endErrorCondition(recognizer);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns immediately if the handler is already
	 * in error recovery mode. Otherwise, it calls {@link #beginErrorCondition}
	 * and dispatches the reporting task based on the runtime type of {@code e}
	 * according to the following table.</p>
	 *
	 * <ul>
	 * <li>{@link NoViableAltException}: Dispatches the call to
	 * {@link #reportNoViableAlternative}</li>
	 * <li>{@link InputMismatchException}: Dispatches the call to
	 * {@link #reportInputMismatch}</li>
	 * <li>{@link FailedPredicateException}: Dispatches the call to
	 * {@link #reportFailedPredicate}</li>
	 * <li>All other types: calls {@link Parser#notifyErrorListeners} to report
	 * the exception</li>
	 * </ul>
	 */
	@Override
	public void reportError(Parser recognizer,
							RecognitionException e)
	{
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if (inErrorRecoveryMode(recognizer)) {
//			System.err.print("[SPURIOUS] ");
			return; // don't report spurious errors
		}
		beginErrorCondition(recognizer);
		if ( e instanceof NoViableAltException ) {
			reportNoViableAlternative(recognizer, (NoViableAltException) e);
		}
		else if ( e instanceof InputMismatchException ) {
			reportInputMismatch(recognizer, (InputMismatchException)e);
		}
		else if ( e instanceof FailedPredicateException ) {
			reportFailedPredicate(recognizer, (FailedPredicateException)e);
		}
		else {
			System.err.println("unknown recognition error type: "+e.getClass().getName());
			recognizer.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation resynchronizes the parser by consuming tokens
	 * until we find one in the resynchronization set--loosely the set of tokens
	 * that can follow the current rule.</p>
	 */
	@Override
	public void recover(Parser recognizer, RecognitionException e) {
//		System.out.println("recover in "+recognizer.getRuleInvocationStack()+
//						   " index="+recognizer.getInputStream().index()+
//						   ", lastErrorIndex="+
//						   lastErrorIndex+
//						   ", states="+lastErrorStates);
		if ( lastErrorIndex==recognizer.getInputStream().index() &&
			lastErrorStates != null &&
			lastErrorStates.contains(recognizer.getState()) ) {
			// uh oh, another error at same token index and previously-visited
			// state in ATN; must be a case where LT(1) is in the recovery
			// token set so nothing got consumed. Consume a single token
			// at least to prevent an infinite loop; this is a failsafe.
//			System.err.println("seen error condition before index="+
//							   lastErrorIndex+", states="+lastErrorStates);
//			System.err.println("FAILSAFE consumes "+recognizer.getTokenNames()[recognizer.getInputStream().LA(1)]);
			recognizer.consume();
		}
		lastErrorIndex = recognizer.getInputStream().index();
		if ( lastErrorStates==null ) lastErrorStates = new IntervalSet();
		lastErrorStates.add(recognizer.getState());
		IntervalSet followSet = getErrorRecoverySet(recognizer);
		consumeUntil(recognizer, followSet);
	}

	/**
	 * The default implementation of {@link ANTLRErrorStrategy#sync} makes sure
	 * that the current lookahead symbol is consistent with what were expecting
	 * at this point in the ATN. You can call this anytime but ANTLR only
	 * generates code to check before subrules/loops and each iteration.
	 *
	 * <p>Implements Jim Idle's magic sync mechanism in closures and optional
	 * subrules. E.g.,</p>
	 *
	 * <pre>
	 * a : sync ( stuff sync )* ;
	 * sync : {consume to what can follow sync} ;
	 * </pre>
	 *
	 * At the start of a sub rule upon error, {@link #sync} performs single
	 * token deletion, if possible. If it can't do that, it bails on the current
	 * rule and uses the default error recovery, which consumes until the
	 * resynchronization set of the current rule.
	 *
	 * <p>If the sub rule is optional ({@code (...)?}, {@code (...)*}, or block
	 * with an empty alternative), then the expected set includes what follows
	 * the subrule.</p>
	 *
	 * <p>During loop iteration, it consumes until it sees a token that can start a
	 * sub rule or what follows loop. Yes, that is pretty aggressive. We opt to
	 * stay in the loop as long as possible.</p>
	 *
	 * <p><strong>ORIGINS</strong></p>
	 *
	 * <p>Previous versions of ANTLR did a poor job of their recovery within loops.
	 * A single mismatch token or missing token would force the parser to bail
	 * out of the entire rules surrounding the loop. So, for rule</p>
	 *
	 * <pre>
	 * classDef : 'class' ID '{' member* '}'
	 * </pre>
	 *
	 * input with an extra token between members would force the parser to
	 * consume until it found the next class definition rather than the next
	 * member definition of the current class.
	 *
	 * <p>This functionality cost a little bit of effort because the parser has to
	 * compare token set at the start of the loop and at each iteration. If for
	 * some reason speed is suffering for you, you can turn off this
	 * functionality by simply overriding this method as a blank { }.</p>
	 */
	@Override
	public void sync(Parser recognizer) throws RecognitionException {
		ATNState s = recognizer.getInterpreter().atn.states.get(recognizer.getState());
//		System.err.println("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName());
		// If already recovering, don't try to sync
		if (inErrorRecoveryMode(recognizer)) {
			return;
		}

        TokenStream tokens = recognizer.getInputStream();
        int la = tokens.LA(1);

        // try cheaper subset first; might get lucky. seems to shave a wee bit off
        if ( recognizer.getATN().nextTokens(s).contains(la) || la==Token.EOF ) return;

		// Return but don't end recovery. only do that upon valid token match
		if (recognizer.isExpectedToken(la)) {
			return;
		}

		switch (s.getStateType()) {
		case ATNState.BLOCK_START:
		case ATNState.STAR_BLOCK_START:
		case ATNState.PLUS_BLOCK_START:
		case ATNState.STAR_LOOP_ENTRY:
			// report error and recover if possible
			if ( singleTokenDeletion(recognizer)!=null ) {
				return;
			}

			throw new InputMismatchException(recognizer);

		case ATNState.PLUS_LOOP_BACK:
		case ATNState.STAR_LOOP_BACK:
//			System.err.println("at loop back: "+s.getClass().getSimpleName());
			reportUnwantedToken(recognizer);
			IntervalSet expecting = recognizer.getExpectedTokens();
			IntervalSet whatFollowsLoopIterationOrRule =
				expecting.or(getErrorRecoverySet(recognizer));
			consumeUntil(recognizer, whatFollowsLoopIterationOrRule);
			break;

		default:
			// do nothing if we can't identify the exact kind of ATN state
			break;
		}
	}

	/**
	 * This is called by {@link #reportError} when the exception is a
	 * {@link NoViableAltException}.
	 *
	 * @see #reportError
	 *
	 * @param recognizer the parser instance
	 * @param e the recognition exception
	 */
	protected void reportNoViableAlternative(@NotNull Parser recognizer,
											 @NotNull NoViableAltException e)
	{
		TokenStream tokens = recognizer.getInputStream();
		String input;
		if ( tokens!=null ) {
			if ( e.getStartToken().getType()==Token.EOF ) input = "<EOF>";
			else input = tokens.getText(e.getStartToken(), e.getOffendingToken());
		}
		else {
			input = "<unknown input>";
		}
		String msg = "no viable alternative at input "+escapeWSAndQuote(input);
		recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
	}

	/**
	 * This is called by {@link #reportError} when the exception is an
	 * {@link InputMismatchException}.
	 *
	 * @see #reportError
	 *
	 * @param recognizer the parser instance
	 * @param e the recognition exception
	 */
	protected void reportInputMismatch(@NotNull Parser recognizer,
									   @NotNull InputMismatchException e)
	{
		String msg = "mismatched input "+getTokenErrorDisplay(e.getOffendingToken())+
		" expecting "+e.getExpectedTokens().toString(recognizer.getVocabulary());
		recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
	}

	/**
	 * This is called by {@link #reportError} when the exception is a
	 * {@link FailedPredicateException}.
	 *
	 * @see #reportError
	 *
	 * @param recognizer the parser instance
	 * @param e the recognition exception
	 */
	protected void reportFailedPredicate(@NotNull Parser recognizer,
										 @NotNull FailedPredicateException e)
	{
		String ruleName = recognizer.getRuleNames()[recognizer._ctx.getRuleIndex()];
		String msg = "rule "+ruleName+" "+e.getMessage();
		recognizer.notifyErrorListeners(e.getOffendingToken(), msg, e);
	}

	/**
	 * This method is called to report a syntax error which requires the removal
	 * of a token from the input stream. At the time this method is called, the
	 * erroneous symbol is current {@code LT(1)} symbol and has not yet been
	 * removed from the input stream. When this method returns,
	 * {@code recognizer} is in error recovery mode.
	 *
	 * <p>This method is called when {@link #singleTokenDeletion} identifies
	 * single-token deletion as a viable recovery strategy for a mismatched
	 * input error.</p>
	 *
	 * <p>The default implementation simply returns if the handler is already in
	 * error recovery mode. Otherwise, it calls {@link #beginErrorCondition} to
	 * enter error recovery mode, followed by calling
	 * {@link Parser#notifyErrorListeners}.</p>
	 *
	 * @param recognizer the parser instance
	 */
	protected void reportUnwantedToken(@NotNull Parser recognizer) {
		if (inErrorRecoveryMode(recognizer)) {
			return;
		}

		beginErrorCondition(recognizer);

		Token t = recognizer.getCurrentToken();
		String tokenName = getTokenErrorDisplay(t);
		IntervalSet expecting = getExpectedTokens(recognizer);
		String msg = "extraneous input "+tokenName+" expecting "+
			expecting.toString(recognizer.getVocabulary());
		recognizer.notifyErrorListeners(t, msg, null);
	}

	/**
	 * This method is called to report a syntax error which requires the
	 * insertion of a missing token into the input stream. At the time this
	 * method is called, the missing token has not yet been inserted. When this
	 * method returns, {@code recognizer} is in error recovery mode.
	 *
	 * <p>This method is called when {@link #singleTokenInsertion} identifies
	 * single-token insertion as a viable recovery strategy for a mismatched
	 * input error.</p>
	 *
	 * <p>The default implementation simply returns if the handler is already in
	 * error recovery mode. Otherwise, it calls {@link #beginErrorCondition} to
	 * enter error recovery mode, followed by calling
	 * {@link Parser#notifyErrorListeners}.</p>
	 *
	 * @param recognizer the parser instance
	 */
	protected void reportMissingToken(@NotNull Parser recognizer) {
		if (inErrorRecoveryMode(recognizer)) {
			return;
		}

		beginErrorCondition(recognizer);

		Token t = recognizer.getCurrentToken();
		IntervalSet expecting = getExpectedTokens(recognizer);
		String msg = "missing "+expecting.toString(recognizer.getVocabulary())+
			" at "+getTokenErrorDisplay(t);

		recognizer.notifyErrorListeners(t, msg, null);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation attempts to recover from the mismatched input
	 * by using single token insertion and deletion as described below. If the
	 * recovery attempt fails, this method throws an
	 * {@link InputMismatchException}.</p>
	 *
	 * <p><strong>EXTRA TOKEN</strong> (single token deletion)</p>
	 *
	 * <p>{@code LA(1)} is not what we are looking for. If {@code LA(2)} has the
	 * right token, however, then assume {@code LA(1)} is some extra spurious
	 * token and delete it. Then consume and return the next token (which was
	 * the {@code LA(2)} token) as the successful result of the match operation.</p>
	 *
	 * <p>This recovery strategy is implemented by {@link #singleTokenDeletion}.</p>
	 *
	 * <p><strong>MISSING TOKEN</strong> (single token insertion)</p>
	 *
	 * <p>If current token (at {@code LA(1)}) is consistent with what could come
	 * after the expected {@code LA(1)} token, then assume the token is missing
	 * and use the parser's {@link TokenFactory} to create it on the fly. The
	 * "insertion" is performed by returning the created token as the successful
	 * result of the match operation.</p>
	 *
	 * <p>This recovery strategy is implemented by {@link #singleTokenInsertion}.</p>
	 *
	 * <p><strong>EXAMPLE</strong></p>
	 *
	 * <p>For example, Input {@code i=(3;} is clearly missing the {@code ')'}. When
	 * the parser returns from the nested call to {@code expr}, it will have
	 * call chain:</p>
	 *
	 * <pre>
	 * stat &rarr; expr &rarr; atom
	 * </pre>
	 *
	 * and it will be trying to match the {@code ')'} at this point in the
	 * derivation:
	 *
	 * <pre>
	 * =&gt; ID '=' '(' INT ')' ('+' atom)* ';'
	 *                    ^
	 * </pre>
	 *
	 * The attempt to match {@code ')'} will fail when it sees {@code ';'} and
	 * call {@link #recoverInline}. To recover, it sees that {@code LA(1)==';'}
	 * is in the set of tokens that can follow the {@code ')'} token reference
	 * in rule {@code atom}. It can assume that you forgot the {@code ')'}.
	 */
	@Override
	public Token recoverInline(Parser recognizer)
		throws RecognitionException
	{
		// SINGLE TOKEN DELETION
		Token matchedSymbol = singleTokenDeletion(recognizer);
		if ( matchedSymbol!=null ) {
			// we have deleted the extra token.
			// now, move past ttype token as if all were ok
			recognizer.consume();
			return matchedSymbol;
		}

		// SINGLE TOKEN INSERTION
		if ( singleTokenInsertion(recognizer) ) {
			return getMissingSymbol(recognizer);
		}

		// even that didn't work; must throw the exception
		throw new InputMismatchException(recognizer);
	}

	/**
	 * This method implements the single-token insertion inline error recovery
	 * strategy. It is called by {@link #recoverInline} if the single-token
	 * deletion strategy fails to recover from the mismatched input. If this
	 * method returns {@code true}, {@code recognizer} will be in error recovery
	 * mode.
	 *
	 * <p>This method determines whether or not single-token insertion is viable by
	 * checking if the {@code LA(1)} input symbol could be successfully matched
	 * if it were instead the {@code LA(2)} symbol. If this method returns
	 * {@code true}, the caller is responsible for creating and inserting a
	 * token with the correct type to produce this behavior.</p>
	 *
	 * @param recognizer the parser instance
	 * @return {@code true} if single-token insertion is a viable recovery
	 * strategy for the current mismatched input, otherwise {@code false}
	 */
	protected boolean singleTokenInsertion(@NotNull Parser recognizer) {
		int currentSymbolType = recognizer.getInputStream().LA(1);
		// if current token is consistent with what could come after current
		// ATN state, then we know we're missing a token; error recovery
		// is free to conjure up and insert the missing token
		ATNState currentState = recognizer.getInterpreter().atn.states.get(recognizer.getState());
		ATNState next = currentState.transition(0).target;
		ATN atn = recognizer.getInterpreter().atn;
		IntervalSet expectingAtLL2 = atn.nextTokens(next, recognizer._ctx);
//		System.out.println("LT(2) set="+expectingAtLL2.toString(recognizer.getTokenNames()));
		if ( expectingAtLL2.contains(currentSymbolType) ) {
			reportMissingToken(recognizer);
			return true;
		}
		return false;
	}

	/**
	 * This method implements the single-token deletion inline error recovery
	 * strategy. It is called by {@link #recoverInline} to attempt to recover
	 * from mismatched input. If this method returns null, the parser and error
	 * handler state will not have changed. If this method returns non-null,
	 * {@code recognizer} will <em>not</em> be in error recovery mode since the
	 * returned token was a successful match.
	 *
	 * <p>If the single-token deletion is successful, this method calls
	 * {@link #reportUnwantedToken} to report the error, followed by
	 * {@link Parser#consume} to actually "delete" the extraneous token. Then,
	 * before returning {@link #reportMatch} is called to signal a successful
	 * match.</p>
	 *
	 * @param recognizer the parser instance
	 * @return the successfully matched {@link Token} instance if single-token
	 * deletion successfully recovers from the mismatched input, otherwise
	 * {@code null}
	 */
	@Nullable
	protected Token singleTokenDeletion(@NotNull Parser recognizer) {
		int nextTokenType = recognizer.getInputStream().LA(2);
		IntervalSet expecting = getExpectedTokens(recognizer);
		if ( expecting.contains(nextTokenType) ) {
			reportUnwantedToken(recognizer);
			/*
			System.err.println("recoverFromMismatchedToken deleting "+
							   ((TokenStream)recognizer.getInputStream()).LT(1)+
							   " since "+((TokenStream)recognizer.getInputStream()).LT(2)+
							   " is what we want");
			*/
			recognizer.consume(); // simply delete extra token
			// we want to return the token we're actually matching
			Token matchedSymbol = recognizer.getCurrentToken();
			reportMatch(recognizer);  // we know current token is correct
			return matchedSymbol;
		}
		return null;
	}

	/** Conjure up a missing token during error recovery.
	 *
	 *  The recognizer attempts to recover from single missing
	 *  symbols. But, actions might refer to that missing symbol.
	 *  For example, x=ID {f($x);}. The action clearly assumes
	 *  that there has been an identifier matched previously and that
	 *  $x points at that token. If that token is missing, but
	 *  the next token in the stream is what we want we assume that
	 *  this token is missing and we keep going. Because we
	 *  have to return some token to replace the missing token,
	 *  we have to conjure one up. This method gives the user control
	 *  over the tokens returned for missing tokens. Mostly,
	 *  you will want to create something special for identifier
	 *  tokens. For literals such as '{' and ',', the default
	 *  action in the parser or tree parser works. It simply creates
	 *  a CommonToken of the appropriate type. The text will be the token.
	 *  If you change what tokens must be created by the lexer,
	 *  override this method to create the appropriate tokens.
	 */
	@NotNull
	protected Token getMissingSymbol(@NotNull Parser recognizer) {
		Token currentSymbol = recognizer.getCurrentToken();
		IntervalSet expecting = getExpectedTokens(recognizer);
		int expectedTokenType = expecting.getMinElement(); // get any element
		String tokenText;
		if ( expectedTokenType== Token.EOF ) tokenText = "<missing EOF>";
		else tokenText = "<missing "+recognizer.getVocabulary().getDisplayName(expectedTokenType)+">";
		Token current = currentSymbol;
		Token lookback = recognizer.getInputStream().LT(-1);
		if ( current.getType() == Token.EOF && lookback!=null ) {
			current = lookback;
		}
		return
			recognizer.getTokenFactory().create(new Pair<TokenSource, CharStream>(current.getTokenSource(), current.getTokenSource().getInputStream()), expectedTokenType, tokenText,
							Token.DEFAULT_CHANNEL,
							-1, -1,
							current.getLine(), current.getCharPositionInLine());
	}

	@NotNull
	protected IntervalSet getExpectedTokens(@NotNull Parser recognizer) {
		return recognizer.getExpectedTokens();
	}

	/** How should a token be displayed in an error message? The default
	 *  is to display just the text, but during development you might
	 *  want to have a lot of information spit out.  Override in that case
	 *  to use t.toString() (which, for CommonToken, dumps everything about
	 *  the token). This is better than forcing you to override a method in
	 *  your token objects because you don't have to go modify your lexer
	 *  so that it creates a new Java type.
	 */
	protected String getTokenErrorDisplay(Token t) {
		if ( t==null ) return "<no token>";
		String s = getSymbolText(t);
		if ( s==null ) {
			if ( getSymbolType(t)==Token.EOF ) {
				s = "<EOF>";
			}
			else {
				s = "<"+getSymbolType(t)+">";
			}
		}
		return escapeWSAndQuote(s);
	}

	protected String getSymbolText(@NotNull Token symbol) {
		return symbol.getText();
	}

	protected int getSymbolType(@NotNull Token symbol) {
		return symbol.getType();
	}

	@NotNull
	protected String escapeWSAndQuote(@NotNull String s) {
//		if ( s==null ) return s;
		s = s.replace("\n","\\n");
		s = s.replace("\r","\\r");
		s = s.replace("\t","\\t");
		return "'"+s+"'";
	}

	/*  Compute the error recovery set for the current rule.  During
	 *  rule invocation, the parser pushes the set of tokens that can
	 *  follow that rule reference on the stack; this amounts to
	 *  computing FIRST of what follows the rule reference in the
	 *  enclosing rule. See LinearApproximator.FIRST().
	 *  This local follow set only includes tokens
	 *  from within the rule; i.e., the FIRST computation done by
	 *  ANTLR stops at the end of a rule.
	 *
	 *  EXAMPLE
	 *
	 *  When you find a "no viable alt exception", the input is not
	 *  consistent with any of the alternatives for rule r.  The best
	 *  thing to do is to consume tokens until you see something that
	 *  can legally follow a call to r *or* any rule that called r.
	 *  You don't want the exact set of viable next tokens because the
	 *  input might just be missing a token--you might consume the
	 *  rest of the input looking for one of the missing tokens.
	 *
	 *  Consider grammar:
	 *
	 *  a : '[' b ']'
	 *    | '(' b ')'
	 *    ;
	 *  b : c '^' INT ;
	 *  c : ID
	 *    | INT
	 *    ;
	 *
	 *  At each rule invocation, the set of tokens that could follow
	 *  that rule is pushed on a stack.  Here are the various
	 *  context-sensitive follow sets:
	 *
	 *  FOLLOW(b1_in_a) = FIRST(']') = ']'
	 *  FOLLOW(b2_in_a) = FIRST(')') = ')'
	 *  FOLLOW(c_in_b) = FIRST('^') = '^'
	 *
	 *  Upon erroneous input "[]", the call chain is
	 *
	 *  a -> b -> c
	 *
	 *  and, hence, the follow context stack is:
	 *
	 *  depth     follow set       start of rule execution
	 *    0         <EOF>                    a (from main())
	 *    1          ']'                     b
	 *    2          '^'                     c
	 *
	 *  Notice that ')' is not included, because b would have to have
	 *  been called from a different context in rule a for ')' to be
	 *  included.
	 *
	 *  For error recovery, we cannot consider FOLLOW(c)
	 *  (context-sensitive or otherwise).  We need the combined set of
	 *  all context-sensitive FOLLOW sets--the set of all tokens that
	 *  could follow any reference in the call chain.  We need to
	 *  resync to one of those tokens.  Note that FOLLOW(c)='^' and if
	 *  we resync'd to that token, we'd consume until EOF.  We need to
	 *  sync to context-sensitive FOLLOWs for a, b, and c: {']','^'}.
	 *  In this case, for input "[]", LA(1) is ']' and in the set, so we would
	 *  not consume anything. After printing an error, rule c would
	 *  return normally.  Rule b would not find the required '^' though.
	 *  At this point, it gets a mismatched token error and throws an
	 *  exception (since LA(1) is not in the viable following token
	 *  set).  The rule exception handler tries to recover, but finds
	 *  the same recovery set and doesn't consume anything.  Rule b
	 *  exits normally returning to rule a.  Now it finds the ']' (and
	 *  with the successful match exits errorRecovery mode).
	 *
	 *  So, you can see that the parser walks up the call chain looking
	 *  for the token that was a member of the recovery set.
	 *
	 *  Errors are not generated in errorRecovery mode.
	 *
	 *  ANTLR's error recovery mechanism is based upon original ideas:
	 *
	 *  "Algorithms + Data Structures = Programs" by Niklaus Wirth
	 *
	 *  and
	 *
	 *  "A note on error recovery in recursive descent parsers":
	 *  http://portal.acm.org/citation.cfm?id=947902.947905
	 *
	 *  Later, Josef Grosch had some good ideas:
	 *
	 *  "Efficient and Comfortable Error Recovery in Recursive Descent
	 *  Parsers":
	 *  ftp://www.cocolab.com/products/cocktail/doca4.ps/ell.ps.zip
	 *
	 *  Like Grosch I implement context-sensitive FOLLOW sets that are combined
	 *  at run-time upon error to avoid overhead during parsing.
	 */
	@NotNull
	protected IntervalSet getErrorRecoverySet(@NotNull Parser recognizer) {
		ATN atn = recognizer.getInterpreter().atn;
		RuleContext ctx = recognizer._ctx;
		IntervalSet recoverSet = new IntervalSet();
		while ( ctx!=null && ctx.invokingState>=0 ) {
			// compute what follows who invoked us
			ATNState invokingState = atn.states.get(ctx.invokingState);
			RuleTransition rt = (RuleTransition)invokingState.transition(0);
			IntervalSet follow = atn.nextTokens(rt.followState);
			recoverSet.addAll(follow);
			ctx = ctx.parent;
		}
        recoverSet.remove(Token.EPSILON);
//		System.out.println("recover set "+recoverSet.toString(recognizer.getTokenNames()));
		return recoverSet;
	}

	/** Consume tokens until one matches the given token set. */
	protected void consumeUntil(@NotNull Parser recognizer, @NotNull IntervalSet set) {
//		System.err.println("consumeUntil("+set.toString(recognizer.getTokenNames())+")");
		int ttype = recognizer.getInputStream().LA(1);
		while (ttype != Token.EOF && !set.contains(ttype) ) {
            //System.out.println("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
//			recognizer.getInputStream().consume();
            recognizer.consume();
            ttype = recognizer.getInputStream().LA(1);
        }
    }
}
