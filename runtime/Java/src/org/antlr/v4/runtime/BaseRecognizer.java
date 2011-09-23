/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
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

import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.OrderedHashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** A generic recognizer that can handle recognizers generated from
 *  parser and tree grammars.  This is all the parsing
 *  support code essentially; most of it is error recovery stuff and
 *  backtracking.
 *
 *  TODO: rename since lexer not under. or reorg parser/treeparser; treeparser under parser?
 */
public abstract class BaseRecognizer extends Recognizer<ParserATNSimulator> {
	public static final String NEXT_TOKEN_RULE_NAME = "nextToken";

	/** The RuleContext object for the currently executing rule. This
	 *  must be non-null during parsing, but is initially null.
	 *  When somebody calls the start rule, this gets set to the
	 *  root context.
	 */
	protected ParserRuleContext _ctx;

	protected boolean buildParseTrees;
	protected boolean traceATNStates;

	/** This is true when we see an error and before having successfully
	 *  matched a token.  Prevents generation of more than one error message
	 *  per error.
	 */
	protected boolean errorRecovery = false;

	/** The index into the input stream where the last error occurred.
	 * 	This is used to prevent infinite loops where an error is found
	 *  but no token is consumed during recovery...another error is found,
	 *  ad naseum.  This is a failsafe mechanism to guarantee that at least
	 *  one token/tree node is consumed for two errors.
	 */
	protected int lastErrorIndex = -1;

	/** In lieu of a return value, this indicates that a rule or token
	 *  has failed to match.  Reset to false upon valid token match.
	 */
//	protected boolean failed = false;

	/** Did the recognizer encounter a syntax error?  Track how many. */
	public int syntaxErrors = 0;

	public BaseRecognizer(IntStream input) {
		setInputStream(input);
	}

	/** reset the parser's state */
	public void reset() {
		if ( getInputStream()!=null ) getInputStream().seek(0);
		errorRecovery = false;
		_ctx = null;
		lastErrorIndex = -1;
//		failed = false;
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
	public Object match(int ttype) throws RecognitionException {
//		System.out.println("match "+((TokenStream)input).LT(1)+" vs expected "+ttype);
		Object matchedSymbol = getCurrentInputSymbol();
		if ( getInputStream().LA(1)==ttype ) {
			getInputStream().consume();
			errorRecovery = false;
//			failed = false;
			if ( buildParseTrees ) _ctx.addChild((Token)matchedSymbol);
			return matchedSymbol;
		}
//		System.out.println("MATCH failure at state "+_ctx.s+
//			", ctx="+_ctx.toString(this));
		IntervalSet expecting = _interp.atn.nextTokens(_ctx);
//		System.out.println("could match "+expecting);

		matchedSymbol = recoverFromMismatchedToken(ttype, expecting);
//		System.out.println("rsync'd to "+matchedSymbol);
		return matchedSymbol;
	}

	// like matchSet but w/o consume; error checking routine.
	public void sync(IntervalSet expecting) {
		if ( expecting.member(getInputStream().LA(1)) ) return;
//		System.out.println("failed sync to "+expecting);
		IntervalSet followSet = computeErrorRecoverySet();
		followSet.addAll(expecting);
		NoViableAltException e = new NoViableAltException(this, _ctx);
		recoverFromMismatchedSet(e, followSet);
	}

	/** Match the wildcard: in a symbol */
	public void matchAny() {
		errorRecovery = false;
//		failed = false;
		getInputStream().consume();
	}

	public boolean mismatchIsUnwantedToken(int ttype) {
		return getInputStream().LA(2)==ttype;
	}

	public boolean mismatchIsMissingToken(IntervalSet follow) {
		return false;
		/*
		if ( follow==null ) {
			// we have no information about the follow; we can only consume
			// a single token and hope for the best
			return false;
		}
		// compute what can follow this grammar element reference
		if ( follow.member(Token.EOR_TOKEN_TYPE) ) {
			IntervalSet viableTokensFollowingThisRule = computeNextViableTokenSet();
			follow = follow.or(viableTokensFollowingThisRule);
            if ( ctx.sp>=0 ) { // remove EOR if we're not the start symbol
                follow.remove(Token.EOR_TOKEN_TYPE);
            }
		}
		// if current token is consistent with what could come after set
		// then we know we're missing a token; error recovery is free to
		// "insert" the missing token

		//System.out.println("viable tokens="+follow.toString(getTokenNames()));
		//System.out.println("LT(1)="+((TokenStream)input).LT(1));

		// IntervalSet cannot handle negative numbers like -1 (EOF) so I leave EOR
		// in follow set to indicate that the fall of the start symbol is
		// in the set (EOF can follow).
		if ( follow.member(input.LA(1)) || follow.member(Token.EOR_TOKEN_TYPE) ) {
			//System.out.println("LT(1)=="+((TokenStream)input).LT(1)+" is consistent with what follows; inserting...");
			return true;
		}
		return false;
		*/
	}

	/** Track the RuleContext objects during the parse and hook them up
	 *  using the children list so that it forms a parse tree.
	 *  The RuleContext returned from the start rule represents the root
	 *  of the parse tree.
	 *
	 *  To built parse trees, all we have to do is put a hook in move()
	 *  and enterRule(). In move(), we had tokens to the current context
	 *  as children. By the time we get to enterRule(), we are already
	 *  in in invoke rule so we add this context As a child of the parent
	 *  (invoking) context. Simple and effective.
	 *
	 *  Note that if we are not building parse trees, rule contexts
	 *  only point upwards. When a rule exits, it returns the context
	 *  but that gets garbage collected if nobody holds a reference.
	 *  It points upwards but nobody points at it.
	 *
	 *  When we build parse trees, we are adding all of these contexts to
	 *  somebody's children list. Contexts are then not candidates
	 *  for garbage collection.
	 */
	public void setBuildParseTrees(boolean buildParseTrees) {
		this.buildParseTrees = buildParseTrees;
	}

	public boolean getBuildParseTrees() {
		return buildParseTrees;
	}

	public void setTraceATNStates(boolean traceATNStates) {
		this.traceATNStates = traceATNStates;
	}

	public boolean getTraceATNStates() {
		return traceATNStates;
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
	 */
	public void reportError(RecognitionException e) {
		// if we've already reported an error and have not matched a token
		// yet successfully, don't report any errors.
		if ( errorRecovery ) {
			//System.err.print("[SPURIOUS] ");
			return;
		}
		syntaxErrors++; // don't count spurious
		errorRecovery = true;

		notifyListeners(e);
	}


	/** Get number of recognition errors (lexer, parser, tree parser).  Each
	 *  recognizer tracks its own number.  So parser and lexer each have
	 *  separate count.  Does not count the spurious errors found between
	 *  an error and next valid token match
	 *
	 *  See also reportError()
	 */
	public int getNumberOfSyntaxErrors() {
		return syntaxErrors;
	}


	/** Recover from an error found on the input stream.  This is
	 *  for NoViableAlt and mismatched symbol exceptions.  If you enable
	 *  single token insertion and deletion, this will usually not
	 *  handle mismatched symbol exceptions but there could be a mismatched
	 *  token that the match() routine could not recover from.
	 */
	public void recover() {
		getInputStream().consume();
		/*
		if ( lastErrorIndex==input.index() ) {
			// uh oh, another error at same token index; must be a case
			// where LT(1) is in the recovery token set so nothing is
			// consumed; consume a single token so at least to prevent
			// an infinite loop; this is a failsafe.
			input.consume();
		}
		lastErrorIndex = input.index();
		IntervalSet followSet = computeErrorRecoverySet();
		beginResync();
		consumeUntil(followSet);
		endResync();
		*/
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
	protected IntervalSet computeErrorRecoverySet() {
		return null;
//		int top = ctx.sp;
//		IntervalSet followSet = new IntervalSet();
//		for (int i=top; i>=0; i--) { // i==0 is EOF context for start rule invocation
//			IntervalSet f = (IntervalSet)ctx.get(i).follow;
//			followSet.orInPlace(f);
//		}
//		return followSet;
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
	public IntervalSet computeNextViableTokenSet() {
		return null;
//		int top = ctx.sp;
//		IntervalSet followSet = new IntervalSet();
//		for (int i=top; i>=0; i--) { // i==0 is EOF context for start rule invocation
//			IntervalSet f = (IntervalSet)ctx.get(i).follow;
//			followSet.orInPlace(f);
//			// can we see end of rule? if not, don't include follow of this rule
//			if ( !f.member(Token.EOR_TOKEN_TYPE) ) break;
//			// else combine with tokens that can follow this rule (rm EOR also)
//			// EOR indicates we have to include follow(start rule); i.e., EOF
//			followSet.remove(Token.EOR_TOKEN_TYPE);
//		}
//		return followSet;
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
	protected Object recoverFromMismatchedToken(int ttype, IntervalSet follow)
		throws RecognitionException
	{
		RecognitionException e = null;
		// if next token is what we are looking for then "delete" this token
		if ( mismatchIsUnwantedToken(ttype) ) {
			e = new UnwantedTokenException(this, getInputStream(), ttype);
			/*
			System.err.println("recoverFromMismatchedToken deleting "+
							   ((TokenStream)input).LT(1)+
							   " since "+((TokenStream)input).LT(2)+" is what we want");
							   */
			beginResync();
			getInputStream().consume(); // simply delete extra token
			endResync();
			reportError(e);  // report after consuming so AW sees the token in the exception
			// we want to return the token we're actually matching
			Object matchedSymbol = getCurrentInputSymbol();
			getInputStream().consume(); // move past ttype token as if all were ok
			return matchedSymbol;
		}
		// can't recover with single token deletion, try insertion
		if ( mismatchIsMissingToken(follow) ) {
			Object inserted = getMissingSymbol(e, ttype, follow);
			e = new MissingTokenException(this, getInputStream(), ttype, inserted);
			reportError(e);  // report after inserting so AW sees the token in the exception
			return inserted;
		}
		// even that didn't work; must throw the exception
		e = new MismatchedTokenException(this, getInputStream(), ttype);
		throw e;
	}

	public Object recoverFromMismatchedSet(RecognitionException e,
										   IntervalSet follow)
		throws RecognitionException
	{
		if ( mismatchIsMissingToken(follow) ) {
			// System.out.println("missing token");
			reportError(e);
			// we don't know how to conjure up a token for sets yet
			return getMissingSymbol(e, Token.INVALID_TYPE, follow);
		}
		// TODO do single token deletion like above for Token mismatch
		throw e;
	}

	public abstract IntStream getInputStream();
	public abstract void setInputStream(IntStream input);

	/** Match needs to return the current input symbol, which gets put
	 *  into the label for the associated token ref; e.g., x=ID.  Token
	 *  and tree parsers need to return different objects. Rather than test
	 *  for input stream type or change the IntStream interface, I use
	 *  a simple method to ask the recognizer to tell me what the current
	 *  input symbol is.
	 */
	protected Object getCurrentInputSymbol() { return null; }


	public void enterOuterAlt(ParserRuleContext localctx, int altNum) {
		_ctx = localctx;
		_ctx.altNum = altNum;
		if ( buildParseTrees ) {
			if ( _ctx.parent!=null ) _ctx.parent.addChild(_ctx);
		}
	}

	public void exitRule(int ruleIndex) {
		_ctx = (ParserRuleContext)_ctx.parent;
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
	protected Object getMissingSymbol(RecognitionException e,
									  int expectedTokenType,
									  IntervalSet follow)
	{
		return null;
	}

	public void consumeUntil(int tokenType) {
		//System.out.println("consumeUntil "+tokenType);
		int ttype = getInputStream().LA(1);
		while (ttype != Token.EOF && ttype != tokenType) {
			getInputStream().consume();
			ttype = getInputStream().LA(1);
		}
	}

	/** Consume tokens until one matches the given token set */
	public void consumeUntil(IntervalSet set) {
		//System.out.println("consumeUntil("+set.toString(getTokenNames())+")");
		int ttype = getInputStream().LA(1);
		while (ttype != Token.EOF && !set.member(ttype) ) {
			//System.out.println("consume during recover LA(1)="+getTokenNames()[input.LA(1)]);
			getInputStream().consume();
			ttype = getInputStream().LA(1);
		}
	}

	public ParserRuleContext getInvokingContext(int ruleIndex) {
		ParserRuleContext p = _ctx;
		while ( p!=null ) {
			if ( p.getRuleIndex() == ruleIndex ) return p;
			p = (ParserRuleContext)p.parent;
		}
		return null;
	}

	public boolean inContext(String context) {
		// TODO: useful in parser?
		return false;
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
		// TODO: walk ctx chain now; this is legacy
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
		// TODO: walk ctx chain now; this is legacy
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

    /** Return whether or not a backtracking attempt failed. */
//    public boolean failed() { return failed; }

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

//	public void traceIn(String ruleName, int ruleIndex, Object inputSymbol)  {
//		System.out.print("enter "+ruleName+" "+inputSymbol);
//		System.out.println();
//	}
//
//	public void traceOut(String ruleName,
//						 int ruleIndex,
//						 Object inputSymbol)
//	{
//		System.out.print("exit "+ruleName+" "+inputSymbol);
//		System.out.println();
//	}

	/** Indicate that the recognizer has changed internal state that is
	 *  consistent with the ATN state passed in.  This way we always know
	 *  where we are in the ATN as the parser goes along. The rule
	 *  context objects form a stack that lets us see the stack of
	 *  invoking rules. Combine this and we have complete ATN
	 *  configuration information.
	 */
	public void setState(int atnState) {
		_ctx.s = atnState;
		if ( traceATNStates ) _ctx.trace(atnState);
	}

	/* In v3, programmers altered error messages by overriding
	   displayRecognitionError() and possibly getTokenErrorDisplay().
	   They overrode emitErrorMessage(String) to change where the output goes.

	   Now, in v4, we're going to use a listener mechanism. This makes it
	   easier for language applications to have parsers notify them
	   upon error without having to override the parsers. If you don't specify
	   a listener, ANTLR calls the v3 legacy displayRecognitionError()
	   method. All that does is format a message and call emitErrorMessage().
	   Otherwise, your listener will receive RecognitionException
	   exceptions and you can do what ever you want with them including
	   reproducing the same behavior by calling the legacy methods.
	   (In v4, RecognitionException includes the recognizer object).

	   Grammar tools can have a listeners without having to worry about
	   messing up the programmers' error handling.
	 */

	public void reportConflict(int startIndex, int stopIndex, Set<Integer> alts,
							   OrderedHashSet<ATNConfig> configs) {}

	public void reportContextSensitivity(int startIndex, int stopIndex,
										 Set<Integer> alts,
										 OrderedHashSet<ATNConfig> configs) {}

	/** If context sensitive parsing, we know it's ambiguity not conflict */
	public void reportAmbiguity(int startIndex, int stopIndex, Set<Integer> alts,
								OrderedHashSet<ATNConfig> configs) {}
}
