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

import com.sun.istack.internal.Nullable;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.misc.*;

import java.util.*;

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
//	protected boolean errorRecovery = false;

	/** Did the recognizer encounter a syntax error?  Track how many. */
	protected int syntaxErrors = 0;

	public BaseRecognizer(IntStream input) {
		setInputStream(input);
	}

	/** reset the parser's state */
	public void reset() {
		if ( getInputStream()!=null ) getInputStream().seek(0);
		_errHandler.endErrorCondition();
//		errorRecovery = false;
		_ctx = null;
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
	 *  TODO: mv into Parser etc... to get more precise return value/efficiency
	 */
	public Object match(int ttype) throws RecognitionException {
//		System.out.println("match "+((TokenStream)input).LT(1)+" vs expected "+ttype);
		Object matchedSymbol = getCurrentInputSymbol();
		if ( getInputStream().LA(1)==ttype ) {
			getInputStream().consume();
//			errorRecovery = false;
			_errHandler.endErrorCondition();
			if ( buildParseTrees ) _ctx.addChild((Token)matchedSymbol);
			return matchedSymbol;
		}
		return _errHandler.recoverInline(this);
	}

	/** Match the wildcard: in a symbol */
	public void matchAny() {
//		errorRecovery = false;
		_errHandler.endErrorCondition();
		getInputStream().consume();
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

	public void notifyListeners(Token offendingToken, String msg,
							   @Nullable RecognitionException e)
	{
		int line = offendingToken.getLine();
		int charPositionInLine = offendingToken.getCharPositionInLine();
		if ( _listeners==null || _listeners.size()==0 ) {
			emitErrorMessage("line "+line+":"+charPositionInLine+" "+msg);
			return;
		}
		for (ANTLRParserListener pl : _listeners) {
			pl.error(this, offendingToken, line, charPositionInLine, msg, e);
		}
	}

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

	public IntervalSet getExpectedTokens() {
		return _interp.atn.nextTokens(_ctx);
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

	public void reportConflict(int startIndex, int stopIndex, Set<Integer> alts,
							   OrderedHashSet<ATNConfig> configs) {}

	public void reportContextSensitivity(int startIndex, int stopIndex,
										 Set<Integer> alts,
										 OrderedHashSet<ATNConfig> configs) {}

	/** If context sensitive parsing, we know it's ambiguity not conflict */
	public void reportAmbiguity(int startIndex, int stopIndex, Set<Integer> alts,
								OrderedHashSet<ATNConfig> configs) {}
}
