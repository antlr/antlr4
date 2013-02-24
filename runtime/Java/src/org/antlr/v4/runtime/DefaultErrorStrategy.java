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
import org.antlr.v4.runtime.atn.BlockStartState;
import org.antlr.v4.runtime.atn.PlusBlockStartState;
import org.antlr.v4.runtime.atn.PlusLoopbackState;
import org.antlr.v4.runtime.atn.PredictionContext;
import org.antlr.v4.runtime.atn.RuleTransition;
import org.antlr.v4.runtime.atn.StarLoopEntryState;
import org.antlr.v4.runtime.atn.StarLoopbackState;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Tuple;

/** This is the default error handling mechanism for ANTLR parsers
 *  and tree parsers.
 */
public class DefaultErrorStrategy<Symbol extends Token> implements ANTLRErrorStrategy<Symbol> {
	/** This is true after we see an error and before having successfully
	 *  matched a token. Prevents generation of more than one error message
	 *  per error.
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

	@Override
	public void beginErrorCondition(Parser<? extends Symbol> recognizer) {
		errorRecoveryMode = true;
	}

	@Override
	public boolean inErrorRecoveryMode(Parser<? extends Symbol> recognizer) {
		return errorRecoveryMode;
	}

	@Override
	public void endErrorCondition(Parser<? extends Symbol> recognizer) {
		errorRecoveryMode = false;
		lastErrorStates = null;
		lastErrorIndex = -1;
	}

	@Override
	public void reportError(Parser<? extends Symbol> recognizer,
							RecognitionException e)
		throws RecognitionException
	{
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if (errorRecoveryMode) {
//			System.err.print("[SPURIOUS] ");
			return; // don't count spurious errors
		}
		recognizer._syntaxErrors++;
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
			notifyErrorListeners(recognizer, e.getMessage(), e);
		}
	}

	protected <T extends Symbol> void notifyErrorListeners(Parser<T> recognizer, String message, RecognitionException e) {
		if ( recognizer!=null ) {
			recognizer.notifyErrorListeners(e.getOffendingToken(recognizer), message, e);
		}
	}

	/** Recover from NoViableAlt errors. Also there could be a mismatched
	 *  token that the match() routine could not recover from.
	 */
	@Override
	public void recover(Parser<? extends Symbol> recognizer, RecognitionException e) {
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

	/** Make sure that the current lookahead symbol is consistent with
	 *  what were expecting at this point in the ATN.
	 *
	 *  At the start of a sub rule upon error, sync() performs single
	 *  token deletion, if possible. If it can't do that, it bails
	 *  on the current rule and uses the default error recovery,
	 *  which consumes until the resynchronization set of the current rule.
	 *
	 *  If the sub rule is optional, ()? or ()* or optional alternative,
	 *  then the expected set includes what follows the subrule.
	 *
	 *  During loop iteration, it consumes until it sees a token that can
	 *  start a sub rule or what follows loop. Yes, that is pretty aggressive.
	 *  We opt to stay in the loop as long as possible.
 	 */
	@Override
	public void sync(Parser<? extends Symbol> recognizer) {
		ATNState s = recognizer.getInterpreter().atn.states.get(recognizer.getState());
//		System.err.println("sync @ "+s.stateNumber+"="+s.getClass().getSimpleName());
		// If already recovering, don't try to sync
        if ( errorRecoveryMode ) return;

        TokenStream<? extends Symbol> tokens = recognizer.getInputStream();
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

	public void reportNoViableAlternative(Parser<? extends Symbol> recognizer,
										  NoViableAltException e)
	throws RecognitionException
	{
		TokenStream<? extends Symbol> tokens = recognizer.getInputStream();
		String input;
		if (tokens instanceof TokenStream<?>) {
			if ( e.getStartToken().getType()==Token.EOF ) input = "<EOF>";
			else input = tokens.getText(e.getStartToken(), e.getOffendingToken());
		}
		else {
			input = "<unknown input>";
		}
		String msg = "no viable alternative at input "+escapeWSAndQuote(input);
		notifyErrorListeners(recognizer, msg, e);
	}

	public void reportInputMismatch(Parser<? extends Symbol> recognizer,
									InputMismatchException e)
		throws RecognitionException
	{
		String msg = "mismatched input "+getTokenErrorDisplay(e.getOffendingToken(recognizer))+
		" expecting "+e.getExpectedTokens().toString(recognizer.getTokenNames());
		notifyErrorListeners(recognizer, msg, e);
	}

	public void reportFailedPredicate(Parser<? extends Symbol> recognizer,
									  FailedPredicateException e)
		throws RecognitionException
	{
		String ruleName = recognizer.getRuleNames()[recognizer._ctx.getRuleIndex()];
		String msg = "rule "+ruleName+" "+e.getMessage();
		notifyErrorListeners(recognizer, msg, e);
	}

	public <T extends Symbol> void reportUnwantedToken(Parser<T> recognizer) {
		if (errorRecoveryMode) return;
		recognizer._syntaxErrors++;
		beginErrorCondition(recognizer);

		T t = recognizer.getCurrentToken();
		String tokenName = getTokenErrorDisplay(t);
		IntervalSet expecting = getExpectedTokens(recognizer);
		String msg = "extraneous input "+tokenName+" expecting "+
			expecting.toString(recognizer.getTokenNames());
		recognizer.notifyErrorListeners(t, msg, null);
	}

	public <T extends Symbol> void reportMissingToken(Parser<T> recognizer) {
		if (errorRecoveryMode) return;
		recognizer._syntaxErrors++;
		beginErrorCondition(recognizer);

		T t = recognizer.getCurrentToken();
		IntervalSet expecting = getExpectedTokens(recognizer);
		String msg = "missing "+expecting.toString(recognizer.getTokenNames())+
			" at "+getTokenErrorDisplay(t);

		recognizer.notifyErrorListeners(t, msg, null);
	}

	/** Attempt to recover from a single missing or extra token.
	 *
	 *  EXTRA TOKEN
	 *
	 *  LA(1) is not what we are looking for.  If LA(2) has the right token,
	 *  however, then assume LA(1) is some extra spurious token.  Delete it
	 *  and LA(2) as if we were doing a normal match(), which advances the
	 *  input.
	 *
	 *  MISSING TOKEN
	 *
	 *  If current token is consistent with what could come after
	 *  ttype then it is ok to "insert" the missing token, else throw
	 *  exception For example, Input "i=(3;" is clearly missing the
	 *  ')'.  When the parser returns from the nested call to expr, it
	 *  will have call chain:
	 *
	 *    stat -> expr -> atom
	 *
	 *  and it will be trying to match the ')' at this point in the
	 *  derivation:
	 *
	 *       => ID '=' '(' INT ')' ('+' atom)* ';'
	 *                          ^
	 *  match() will see that ';' doesn't match ')' and report a
	 *  mismatched token error.  To recover, it sees that LA(1)==';'
	 *  is in the set of tokens that can follow the ')' token
	 *  reference in rule atom.  It can assume that you forgot the ')'.
	 */
	@Override
	public <T extends Symbol> T recoverInline(Parser<T> recognizer)
		throws RecognitionException
	{
		// SINGLE TOKEN DELETION
		T matchedSymbol = singleTokenDeletion(recognizer);
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

	// if next token is what we are looking for then "delete" this token
	public boolean singleTokenInsertion(Parser<? extends Symbol> recognizer) {
		int currentSymbolType = recognizer.getInputStream().LA(1);
		// if current token is consistent with what could come after current
		// ATN state, then we know we're missing a token; error recovery
		// is free to conjure up and insert the missing token
		ATNState currentState = recognizer.getInterpreter().atn.states.get(recognizer.getState());
		ATNState next = currentState.transition(0).target;
		ATN atn = recognizer.getInterpreter().atn;
		IntervalSet expectingAtLL2 = atn.nextTokens(next, PredictionContext.fromRuleContext(atn, recognizer._ctx));
//		System.out.println("LT(2) set="+expectingAtLL2.toString(recognizer.getTokenNames()));
		if ( expectingAtLL2.contains(currentSymbolType) ) {
			reportMissingToken(recognizer);
			return true;
		}
		return false;
	}

	public <T extends Symbol> T singleTokenDeletion(Parser<T> recognizer) {
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
			T matchedSymbol = recognizer.getCurrentToken();
			endErrorCondition(recognizer);  // we know current token is correct
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
	protected <T extends Symbol> T getMissingSymbol(Parser<T> recognizer) {
		Symbol currentSymbol = recognizer.getCurrentToken();
		IntervalSet expecting = getExpectedTokens(recognizer);
		int expectedTokenType = expecting.getMinElement(); // get any element
		String tokenText;
		if ( expectedTokenType== Token.EOF ) tokenText = "<missing EOF>";
		else tokenText = "<missing "+recognizer.getTokenNames()[expectedTokenType]+">";
		Symbol current = currentSymbol;
		Symbol lookback = recognizer.getInputStream().LT(-1);
		if ( current.getType() == Token.EOF && lookback!=null ) {
			current = lookback;
		}

		return constructToken(recognizer.getInputStream().getTokenSource(), expectedTokenType, tokenText, current);
	}

	protected <T extends Symbol> T constructToken(TokenSource<T> tokenSource, int expectedTokenType, String tokenText, Symbol current) {
		TokenFactory<? extends T> factory = tokenSource.getTokenFactory();
		return
			factory.create(Tuple.create(tokenSource, current.getTokenSource().getInputStream()), expectedTokenType, tokenText,
							Symbol.DEFAULT_CHANNEL,
							-1, -1,
							current.getLine(), current.getCharPositionInLine());
	}

	public IntervalSet getExpectedTokens(Parser<? extends Symbol> recognizer) {
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
	public String getTokenErrorDisplay(Symbol t) {
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

	protected String getSymbolText(@NotNull Symbol symbol) {
		return symbol.getText();
	}

	protected int getSymbolType(@NotNull Symbol symbol) {
		return symbol.getType();
	}

	protected String escapeWSAndQuote(String s) {
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
	protected IntervalSet getErrorRecoverySet(Parser<? extends Symbol> recognizer) {
		ATN atn = recognizer.getInterpreter().atn;
		RuleContext<?> ctx = recognizer._ctx;
		IntervalSet recoverSet = new IntervalSet();
		while ( ctx!=null && ctx.invokingState>=0 ) {
			// compute what follows who invoked us
			ATNState invokingState = atn.states.get(ctx.invokingState);
			RuleTransition rt = (RuleTransition)invokingState.transition(0);
			IntervalSet follow = atn.nextTokens(rt.followState);
			recoverSet.addAll(follow);
			ctx = ctx.parent;
		}
        recoverSet.remove(Symbol.EPSILON);
//		System.out.println("recover set "+recoverSet.toString(recognizer.getTokenNames()));
		return recoverSet;
	}

	/** Consume tokens until one matches the given token set */
	public void consumeUntil(Parser<? extends Symbol> recognizer, IntervalSet set) {
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
