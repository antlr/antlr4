/*
 [The "BSD license"]
 Copyright (c) 2005-2009 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.antlr.v4.runtime;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.v4.runtime.misc.LABitSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A generic recognizer that can handle recognizers generated from
 *  parser and tree grammars.  This is all the parsing
 *  support code essentially; most of it is error recovery stuff and
 *  backtracking.
 */
public abstract class BaseRecognizer {
	public static final int MEMO_RULE_FAILED = -2;
	public static final int MEMO_RULE_UNKNOWN = -1;

	public static final String NEXT_TOKEN_RULE_NAME = "nextToken";

	/** State of a lexer, parser, or tree parser are collected into a state
	 *  object so the state can be shared.  This sharing is needed to
	 *  have one grammar import others and share same error variables
	 *  and other state variables.  It's a kind of explicit multiple
	 *  inheritance via delegation of methods and shared state.
	 */
	public RecognizerSharedState state;

	public BaseRecognizer(IntStream input) {
		this(input, new RecognizerSharedState());
	}

	public BaseRecognizer(IntStream input, RecognizerSharedState state) {
		if ( state==null ) {
			state = new RecognizerSharedState();
		}
		this.state = state;
		state.input = input;
	}

	/** reset the parser's state; subclasses must rewinds the input stream */
	public void reset() {
		// wack everything related to error recovery
		if ( state==null ) {
			return; // no shared state work to do
		}
		state.ctx.clear();
		state.errorRecovery = false;
		state.lastErrorIndex = -1;
		state.failed = false;
		state.syntaxErrors = 0;
		// wack everything related to backtracking and memoization
		state.backtracking = 0;
		for (int i = 0; state.ruleMemo!=null && i < state.ruleMemo.length; i++) { // wipe cache
			state.ruleMemo[i] = null;
		}
	}


	/** Match current input symbol against ttype.  Attempt
	 *  single token insertion or deletion error recovery.  If
	 *  that fails, throw MismatchedTokenException.
	 *
	 *  To turn off single token insertion or deletion error
	 *  recovery, override recoverFromMismatchedToken() and have it
     *  throw an exception. See TreeParser.recoverFromMismatchedToken().
     *  This way any error in a rule will cause an exception and
     *  immediate exit from rule.  Rule would recover by resynchronizing
     *  to the set of symbols that can follow rule ref.
	 */
	public Object match(int ttype, LABitSet follow)
		throws RecognitionException
	{
		System.out.println("match "+((TokenStream)state.input).LT(1)+" vs expected "+ttype);
		Object matchedSymbol = getCurrentInputSymbol();
		if ( state.input.LA(1)==ttype ) {
			state.input.consume();
			state.errorRecovery = false;
			state.failed = false;
			return matchedSymbol;
		}
		if ( state.backtracking>0 ) {
			state.failed = true;
			return matchedSymbol;
		}
		matchedSymbol = recoverFromMismatchedToken(ttype, follow);
		return matchedSymbol;
	}

	/** Match the wildcard: in a symbol */
	public void matchAny() {
		state.errorRecovery = false;
		state.failed = false;
		state.input.consume();
	}

	public boolean mismatchIsUnwantedToken(int ttype) {
		return state.input.LA(2)==ttype;
	}

	public boolean mismatchIsMissingToken(LABitSet follow) {
		if ( follow==null ) {
			// we have no information about the follow; we can only consume
			// a single token and hope for the best
			return false;
		}
		// compute what can follow this grammar element reference
		if ( follow.member(Token.EOR_TOKEN_TYPE) ) {
			LABitSet viableTokensFollowingThisRule = computeContextSensitiveRuleFOLLOW();
			follow = follow.or(viableTokensFollowingThisRule);
            if ( state.ctx.sp>=0 ) { // remove EOR if we're not the start symbol
                follow.remove(Token.EOR_TOKEN_TYPE);
            }
		}
		// if current token is consistent with what could come after set
		// then we know we're missing a token; error recovery is free to
		// "insert" the missing token

		//System.out.println("viable tokens="+follow.toString(getTokenNames()));
		//System.out.println("LT(1)="+((TokenStream)state.input).LT(1));

		// LABitSet cannot handle negative numbers like -1 (EOF) so I leave EOR
		// in follow set to indicate that the fall of the start symbol is
		// in the set (EOF can follow).
		if ( follow.member(state.input.LA(1)) || follow.member(Token.EOR_TOKEN_TYPE) ) {
			//System.out.println("LT(1)=="+((TokenStream)state.input).LT(1)+" is consistent with what follows; inserting...");
			return true;
		}
		return false;
	}

	/** Report a recognition problem.
	 *
	 *  This method sets errorRecovery to indicate the parser is recovering
	 *  not parsing.  Once in recovery mode, no errors are generated.
	 *  To get out of recovery mode, the parser must successfully match
	 *  a token (after a resync).  So it will go:
	 *
	 * 		1. error occurs
	 * 		2. enter recovery mode, report error
	 * 		3. consume until token found in resynch set
	 * 		4. try to resume parsing
	 * 		5. next match() will reset errorRecovery mode
	 *
	 *  If you override, make sure to update syntaxErrors if you care about that.
	 */
	public void reportError(RecognitionException e) {
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if ( state.errorRecovery ) {
			//System.err.print("[SPURIOUS] ");
			return;
		}
		state.syntaxErrors++; // don't count spurious
		state.errorRecovery = true;

		displayRecognitionError(this.getTokenNames(), e);
	}

	public void displayRecognitionError(String[] tokenNames,
										RecognitionException e)
	{
		String hdr = getErrorHeader(e);
		String msg = getErrorMessage(e, tokenNames);
		emitErrorMessage(hdr+" "+msg);
	}

	/** What error message should be generated for the various
	 *  exception types?
	 *
	 *  Not very object-oriented code, but I like having all error message
	 *  generation within one method rather than spread among all of the
	 *  exception classes. This also makes it much easier for the exception
	 *  handling because the exception classes do not have to have pointers back
	 *  to this object to access utility routines and so on. Also, changing
	 *  the message for an exception type would be difficult because you
	 *  would have to subclassing exception, but then somehow get ANTLR
	 *  to make those kinds of exception objects instead of the default.
	 *  This looks weird, but trust me--it makes the most sense in terms
	 *  of flexibility.
	 *
	 *  For grammar debugging, you will want to override this to add
	 *  more information such as the stack frame with
	 *  getRuleInvocationStack(e, this.getClass().getName()) and,
	 *  for no viable alts, the decision description and state etc...
	 *
	 *  Override this to change the message generated for one or more
	 *  exception types.
	 */
	public String getErrorMessage(RecognitionException e, String[] tokenNames) {
		String msg = e.getMessage();
		if ( e instanceof UnwantedTokenException ) {
			UnwantedTokenException ute = (UnwantedTokenException)e;
			String tokenName="<unknown>";
			if ( ute.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[ute.expecting];
			}
			msg = "extraneous input "+getTokenErrorDisplay(ute.getUnexpectedToken())+
				" expecting "+tokenName;
		}
		else if ( e instanceof MissingTokenException ) {
			MissingTokenException mte = (MissingTokenException)e;
			String tokenName="<unknown>";
			if ( mte.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mte.expecting];
			}
			msg = "missing "+tokenName+" at "+getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			String tokenName="<unknown>";
			if ( mte.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mte.expecting];
			}
			msg = "mismatched input "+getTokenErrorDisplay(e.token)+
				" expecting "+tokenName;
		}
		else if ( e instanceof MismatchedTreeNodeException ) {
			MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
			String tokenName="<unknown>";
			if ( mtne.expecting==Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mtne.expecting];
			}
			msg = "mismatched tree node: "+mtne.node+
				" expecting "+tokenName;
		}
		else if ( e instanceof NoViableAltException ) {
			//NoViableAltException nvae = (NoViableAltException)e;
			// for development, can add "decision=<<"+nvae.grammarDecisionDescription+">>"
			// and "(decision="+nvae.decisionNumber+") and
			// "state "+nvae.stateNumber
			msg = "no viable alternative at input "+getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof EarlyExitException ) {
			//EarlyExitException eee = (EarlyExitException)e;
			// for development, can add "(decision="+eee.decisionNumber+")"
			msg = "required (...)+ loop did not match anything at input "+
				getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof MismatchedSetException ) {
			MismatchedSetException mse = (MismatchedSetException)e;
			msg = "mismatched input "+getTokenErrorDisplay(e.token)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedNotSetException ) {
			MismatchedNotSetException mse = (MismatchedNotSetException)e;
			msg = "mismatched input "+getTokenErrorDisplay(e.token)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof FailedPredicateException ) {
			FailedPredicateException fpe = (FailedPredicateException)e;
			msg = "rule "+fpe.ruleName+" failed predicate: {"+
				fpe.predicateText+"}?";
		}
		return msg;
	}

	/** Get number of recognition errors (lexer, parser, tree parser).  Each
	 *  recognizer tracks its own number.  So parser and lexer each have
	 *  separate count.  Does not count the spurious errors found between
	 *  an error and next valid token match
	 *
	 *  See also reportError()
	 */
	public int getNumberOfSyntaxErrors() {
		return state.syntaxErrors;
	}

	/** What is the error header, normally line/character position information? */
	public String getErrorHeader(RecognitionException e) {
		return "line "+e.line+":"+e.charPositionInLine;
	}

	/** How should a token be displayed in an error message? The default
	 *  is to display just the text, but during development you might
	 *  want to have a lot of information spit out.  Override in that case
	 *  to use t.toString() (which, for CommonToken, dumps everything about
	 *  the token). This is better than forcing you to override a method in
	 *  your token objects because you don't have to go modify your lexer
	 *  so that it creates a new Java type.
	 */
	public String getTokenErrorDisplay(Token t) {
		String s = t.getText();
		if ( s==null ) {
			if ( t.getType()==Token.EOF ) {
				s = "<EOF>";
			}
			else {
				s = "<"+t.getType()+">";
			}
		}
		s = s.replaceAll("\n","\\\\n");
		s = s.replaceAll("\r","\\\\r");
		s = s.replaceAll("\t","\\\\t");
		return "'"+s+"'";
	}

	/** Override this method to change where error messages go */
	public void emitErrorMessage(String msg) {
		System.err.println(msg);
	}

	/** Recover from an error found on the input stream.  This is
	 *  for NoViableAlt and mismatched symbol exceptions.  If you enable
	 *  single token insertion and deletion, this will usually not
	 *  handle mismatched symbol exceptions but there could be a mismatched
	 *  token that the match() routine could not recover from.
	 */
	public void recover(RecognitionException re) {
		if ( state.lastErrorIndex==state.input.index() ) {
			// uh oh, another error at same token index; must be a case
			// where LT(1) is in the recovery token set so nothing is
			// consumed; consume a single token so at least to prevent
			// an infinite loop; this is a failsafe.
			state.input.consume();
		}
		state.lastErrorIndex = state.input.index();
		LABitSet followSet = computeErrorRecoverySet();
		beginResync();
		consumeUntil(followSet);
		endResync();
	}

	/** A hook to listen in on the token consumption during error recovery.
	 *  The DebugParser subclasses this to fire events to the listenter.
	 */
	public void beginResync() {
	}

	public void endResync() {
	}

	/*  Compute the error recovery set for the current rule.  During
	 *  rule invocation, the parser pushes the set of tokens that can
	 *  follow that rule reference on the stack; this amounts to
	 *  computing FIRST of what follows the rule reference in the
	 *  enclosing rule. This local follow set only includes tokens
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
	 *  that rule is pushed on a stack.  Here are the various "local"
	 *  follow sets:
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
	 *  depth  local follow set     after call to rule
	 *    0         <EOF>                    a (from main())
	 *    1          ']'                     b
	 *    3          '^'                     c
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
	 *  In this case, for input "[]", LA(1) is in this set so we would
	 *  not consume anything and after printing an error rule c would
	 *  return normally.  It would not find the required '^' though.
	 *  At this point, it gets a mismatched token error and throws an
	 *  exception (since LA(1) is not in the viable following token
	 *  set).  The rule exception handler tries to recover, but finds
	 *  the same recovery set and doesn't consume anything.  Rule b
	 *  exits normally returning to rule a.  Now it finds the ']' (and
	 *  with the successful match exits errorRecovery mode).
	 *
	 *  So, you cna see that the parser walks up call chain looking
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
	 *  Like Grosch I implemented local FOLLOW sets that are combined
	 *  at run-time upon error to avoid overhead during parsing.
	 */
	protected LABitSet computeErrorRecoverySet() {
		return combineFollows(false);
	}

	/** Compute the context-sensitive FOLLOW set for current rule.
	 *  This is set of token types that can follow a specific rule
	 *  reference given a specific call chain.  You get the set of
	 *  viable tokens that can possibly come next (lookahead depth 1)
	 *  given the current call chain.  Contrast this with the
	 *  definition of plain FOLLOW for rule r:
	 *
	 *   FOLLOW(r)={x | S=>*alpha r beta in G and x in FIRST(beta)}
	 *
	 *  where x in T* and alpha, beta in V*; T is set of terminals and
	 *  V is the set of terminals and nonterminals.  In other words,
	 *  FOLLOW(r) is the set of all tokens that can possibly follow
	 *  references to r in *any* sentential form (context).  At
	 *  runtime, however, we know precisely which context applies as
	 *  we have the call chain.  We may compute the exact (rather
	 *  than covering superset) set of following tokens.
	 *
	 *  For example, consider grammar:
	 *
	 *  stat : ID '=' expr ';'      // FOLLOW(stat)=={EOF}
	 *       | "return" expr '.'
	 *       ;
	 *  expr : atom ('+' atom)* ;   // FOLLOW(expr)=={';','.',')'}
	 *  atom : INT                  // FOLLOW(atom)=={'+',')',';','.'}
	 *       | '(' expr ')'
	 *       ;
	 *
	 *  The FOLLOW sets are all inclusive whereas context-sensitive
	 *  FOLLOW sets are precisely what could follow a rule reference.
	 *  For input input "i=(3);", here is the derivation:
	 *
	 *  stat => ID '=' expr ';'
	 *       => ID '=' atom ('+' atom)* ';'
	 *       => ID '=' '(' expr ')' ('+' atom)* ';'
	 *       => ID '=' '(' atom ')' ('+' atom)* ';'
	 *       => ID '=' '(' INT ')' ('+' atom)* ';'
	 *       => ID '=' '(' INT ')' ';'
	 *
	 *  At the "3" token, you'd have a call chain of
	 *
	 *    stat -> expr -> atom -> expr -> atom
	 *
	 *  What can follow that specific nested ref to atom?  Exactly ')'
	 *  as you can see by looking at the derivation of this specific
	 *  input.  Contrast this with the FOLLOW(atom)={'+',')',';','.'}.
	 *
	 *  You want the exact viable token set when recovering from a
	 *  token mismatch.  Upon token mismatch, if LA(1) is member of
	 *  the viable next token set, then you know there is most likely
	 *  a missing token in the input stream.  "Insert" one by just not
	 *  throwing an exception.
	 */
	protected LABitSet computeContextSensitiveRuleFOLLOW() {
		return combineFollows(true);
	}

	// TODO: what is exact? it seems to only add sets from above on stack
	// if EOR is in set i.  When it sees a set w/o EOR, it stops adding.
	// Why would we ever want them all?  Maybe no viable alt instead of
	// mismatched token?
	protected LABitSet combineFollows(boolean exact) {
		int top = state.ctx.sp;
		LABitSet followSet = new LABitSet();
		for (int i=top; i>=0; i--) {
			LABitSet localFollowSet = (LABitSet)state.ctx.get(i).follow;
			/*
			System.out.println("local follow depth "+i+"="+
							   localFollowSet.toString(getTokenNames())+")");
			 */
			followSet.orInPlace(localFollowSet);
			if ( exact ) {
				// can we see end of rule?
				if ( localFollowSet.member(Token.EOR_TOKEN_TYPE) ) {
					// Only leave EOR in set if at top (start rule); this lets
					// us know if have to include follow(start rule); i.e., EOF
					if ( i>0 ) {
						followSet.remove(Token.EOR_TOKEN_TYPE);
					}
				}
				else { // can't see end of rule, quit
					break;
				}
			}
		}
		return followSet;
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
	protected Object recoverFromMismatchedToken(int ttype, LABitSet follow)
		throws RecognitionException
	{
		RecognitionException e = null;
		// if next token is what we are looking for then "delete" this token
		if ( mismatchIsUnwantedToken(ttype) ) {
			e = new UnwantedTokenException(ttype, state.input);
			/*
			System.err.println("recoverFromMismatchedToken deleting "+
							   ((TokenStream)input).LT(1)+
							   " since "+((TokenStream)input).LT(2)+" is what we want");
			 */
			beginResync();
			state.input.consume(); // simply delete extra token
			endResync();
			reportError(e);  // report after consuming so AW sees the token in the exception
			// we want to return the token we're actually matching
			Object matchedSymbol = getCurrentInputSymbol();
			state.input.consume(); // move past ttype token as if all were ok
			return matchedSymbol;
		}
		// can't recover with single token deletion, try insertion
		if ( mismatchIsMissingToken(follow) ) {
			Object inserted = getMissingSymbol(e, ttype, follow);
			e = new MissingTokenException(ttype, state.input, inserted);
			reportError(e);  // report after inserting so AW sees the token in the exception
			return inserted;
		}
		// even that didn't work; must throw the exception
		e = new MismatchedTokenException(ttype, state.input);
		throw e;
	}

	/** Not currently used */
	public Object recoverFromMismatchedSet(RecognitionException e,
										   LABitSet follow)
		throws RecognitionException
	{
		if ( mismatchIsMissingToken(follow) ) {
			// System.out.println("missing token");
			reportError(e);
			// we don't know how to conjure up a token for sets yet
			return getMissingSymbol(e, Token.INVALID_TOKEN_TYPE, follow);
		}
		// TODO do single token deletion like above for Token mismatch
		throw e;
	}

	/** Match needs to return the current input symbol, which gets put
	 *  into the label for the associated token ref; e.g., x=ID.  Token
	 *  and tree parsers need to return different objects. Rather than test
	 *  for input stream type or change the IntStream interface, I use
	 *  a simple method to ask the recognizer to tell me what the current
	 *  input symbol is.
	 */
	protected Object getCurrentInputSymbol() { return null; }

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
	protected Object getMissingSymbol(RecognitionException e,
									  int expectedTokenType,
									  LABitSet follow)
	{
		return null;
	}

	public void consumeUntil(int tokenType) {
		//System.out.println("consumeUntil "+tokenType);
		int ttype = state.input.LA(1);
		while (ttype != Token.EOF && ttype != tokenType) {
			state.input.consume();
			ttype = state.input.LA(1);
		}
	}

	/** Consume tokens until one matches the given token set */
	public void consumeUntil(LABitSet set) {
		//System.out.println("consumeUntil("+set.toString(getTokenNames())+")");
		int ttype = state.input.LA(1);
		while (ttype != Token.EOF && !set.member(ttype) ) {
			//System.out.println("consume during recover LA(1)="+getTokenNames()[state.input.LA(1)]);
			state.input.consume();
			ttype = state.input.LA(1);
		}
	}

	/** Return List<String> of the rules in your parser instance
	 *  leading up to a call to this method.  You could override if
	 *  you want more details such as the file/line info of where
	 *  in the parser java code a rule is invoked.
	 *
	 *  This is very useful for error messages and for context-sensitive
	 *  error recovery.
	 */
	public List getRuleInvocationStack() {
		String parserClassName = getClass().getName();
		return getRuleInvocationStack(new Throwable(), parserClassName);
	}

	/** A more general version of getRuleInvocationStack where you can
	 *  pass in, for example, a RecognitionException to get it's rule
	 *  stack trace.  This routine is shared with all recognizers, hence,
	 *  static.
	 *
	 *  TODO: move to a utility class or something; weird having lexer call this
	 */
	public static List getRuleInvocationStack(Throwable e,
											  String recognizerClassName)
	{
		List rules = new ArrayList();
		StackTraceElement[] stack = e.getStackTrace();
		int i = 0;
		for (i=stack.length-1; i>=0; i--) {
			StackTraceElement t = stack[i];
			if ( t.getClassName().startsWith("org.antlr.v4.runtime.") ) {
				continue; // skip support code such as this method
			}
			if ( t.getMethodName().equals(NEXT_TOKEN_RULE_NAME) ) {
				continue;
			}
			if ( !t.getClassName().equals(recognizerClassName) ) {
				continue; // must not be part of this parser
			}
            rules.add(t.getMethodName());
		}
		return rules;
	}

    public int getBacktrackingLevel() { return state.backtracking; }

    public void setBacktrackingLevel(int n) { state.backtracking = n; }

    /** Return whether or not a backtracking attempt failed. */
    public boolean failed() { return state.failed; }

	/** Used to print out token names like ID during debugging and
	 *  error reporting.  The generated parsers implement a method
	 *  that overrides this to point to their String[] tokenNames.
	 */
	public String[] getTokenNames() {
		return null;
	}

	/** For debugging and other purposes, might want the grammar name.
	 *  Have ANTLR generate an implementation for this method.
	 */
	public String getGrammarFileName() {
		return null;
	}

	public abstract String getSourceName();

	/** A convenience method for use most often with template rewrites.
	 *  Convert a List<Token> to List<String>
	 */
	public List toStrings(List tokens) {
		if ( tokens==null ) return null;
		List strings = new ArrayList(tokens.size());
		for (int i=0; i<tokens.size(); i++) {
			strings.add(((Token)tokens.get(i)).getText());
		}
		return strings;
	}

	/** Given a rule number and a start token index number, return
	 *  MEMO_RULE_UNKNOWN if the rule has not parsed input starting from
	 *  start index.  If this rule has parsed input starting from the
	 *  start index before, then return where the rule stopped parsing.
	 *  It returns the index of the last token matched by the rule.
	 *
	 *  For now we use a hashtable and just the slow Object-based one.
	 *  Later, we can make a special one for ints and also one that
	 *  tosses out data after we commit past input position i.
	 */
	public int getRuleMemoization(int ruleIndex, int ruleStartIndex) {
		if ( state.ruleMemo[ruleIndex]==null ) {
			state.ruleMemo[ruleIndex] = new HashMap();
		}
		Integer stopIndexI =
			(Integer)state.ruleMemo[ruleIndex].get(new Integer(ruleStartIndex));
		if ( stopIndexI==null ) {
			return MEMO_RULE_UNKNOWN;
		}
		return stopIndexI.intValue();
	}

	/** Has this rule already parsed input at the current index in the
	 *  input stream?  Return the stop token index or MEMO_RULE_UNKNOWN.
	 *  If we attempted but failed to parse properly before, return
	 *  MEMO_RULE_FAILED.
	 *
	 *  This method has a side-effect: if we have seen this input for
	 *  this rule and successfully parsed before, then seek ahead to
	 *  1 past the stop token matched for this rule last time.
	 */
	public boolean alreadyParsedRule(int ruleIndex) {
		int stopIndex = getRuleMemoization(ruleIndex, state.input.index());
		if ( stopIndex==MEMO_RULE_UNKNOWN ) {
			return false;
		}
		if ( stopIndex==MEMO_RULE_FAILED ) {
			//System.out.println("rule "+ruleIndex+" will never succeed");
			state.failed=true;
		}
		else {
			//System.out.println("seen rule "+ruleIndex+" before; skipping ahead to @"+(stopIndex+1)+" failed="+state.failed);
			state.input.seek(stopIndex+1); // jump to one past stop token
		}
		return true;
	}

	/** Record whether or not this rule parsed the input at this position
	 *  successfully.  Use a standard java hashtable for now.
	 */
	public void memoize(IntStream input,
						int ruleIndex,
						int ruleStartIndex)
	{
		int stopTokenIndex = state.failed?MEMO_RULE_FAILED:input.index()-1;
		if ( state.ruleMemo==null ) {
			System.err.println("!!!!!!!!! memo array is null for "+ getGrammarFileName());
		}
		if ( ruleIndex >= state.ruleMemo.length ) {
			System.err.println("!!!!!!!!! memo size is "+state.ruleMemo.length+", but rule index is "+ruleIndex);
		}
		if ( state.ruleMemo[ruleIndex]!=null ) {
			state.ruleMemo[ruleIndex].put(
				new Integer(ruleStartIndex), new Integer(stopTokenIndex)
			);
		}
	}

	/** return how many rule/input-index pairs there are in total.
	 *  TODO: this includes synpreds. :(
	 */
	public int getRuleMemoizationCacheSize() {
		int n = 0;
		for (int i = 0; state.ruleMemo!=null && i < state.ruleMemo.length; i++) {
			Map ruleMap = state.ruleMemo[i];
			if ( ruleMap!=null ) {
				n += ruleMap.size(); // how many input indexes are recorded?
			}
		}
		return n;
	}

	public void traceIn(String ruleName, int ruleIndex, Object inputSymbol)  {
		System.out.print("enter "+ruleName+" "+inputSymbol);
		if ( state.backtracking>0 ) {
			System.out.print(" backtracking="+state.backtracking);
		}
		System.out.println();
	}

	public void traceOut(String ruleName,
						 int ruleIndex,
						 Object inputSymbol)
	{
		System.out.print("exit "+ruleName+" "+inputSymbol);
		if ( state.backtracking>0 ) {
            System.out.print(" backtracking="+state.backtracking);
            if ( state.failed ) System.out.print(" failed");
            else System.out.print(" succeeded");
        }
		System.out.println();
	}

}
