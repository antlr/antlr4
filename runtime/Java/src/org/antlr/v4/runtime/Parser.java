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

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.tree.ParseTreeListener;

import java.util.ArrayList;
import java.util.List;

/** This is all the parsing support code essentially; most of it is error recovery stuff. */
public abstract class Parser extends Recognizer<Token, v2ParserATNSimulator<Token>> {
	public static final String NEXT_TOKEN_RULE_NAME = "nextToken";

	protected TokenStream _input;

	/** The RuleContext object for the currently executing rule. This
	 *  must be non-null during parsing, but is initially null.
	 *  When somebody calls the start rule, this gets set to the
	 *  root context.
	 */
	protected ParserRuleContext<Token> _ctx;

	protected boolean buildParseTrees;
	protected boolean traceATNStates;

    /** If the listener is non-null, trigger enter and exit rule events
     *  *during* the parse. This is typically done only when not building
     *  parse trees for later visiting. We either trigger events during
     *  the parse or during tree walks later. Both could be done.
     *  Not intended for tree parsing but would work.
     */
    protected ParseTreeListener<Token> _listener;

	/** Did the recognizer encounter a syntax error?  Track how many. */
	protected int syntaxErrors = 0;

	public Parser(IntStream input) {
		setInputStream(input);
	}

	/** reset the parser's state */
	public void reset() {
		if ( getInputStream()!=null ) getInputStream().seek(0);
		_errHandler.endErrorCondition(this);
		_ctx = null;
		syntaxErrors = 0;
		ATNSimulator interpreter = getInterpreter();
		if (interpreter != null) {
			interpreter.reset();
		}
	}

	/** Match current input symbol against ttype.  Attempt
	 *  single token insertion or deletion error recovery.  If
	 *  that fails, throw MismatchedTokenException.
	 */
	public Token match(int ttype) throws RecognitionException {
//		System.out.println("match "+((TokenStream)input).LT(1)+" vs expected "+ttype);
		Token currentSymbol = getCurrentToken();
		if ( getInputStream().LA(1)==ttype ) {
			_errHandler.endErrorCondition(this);
			consume();
		}
		else {
			currentSymbol = _errHandler.recoverInline(this);
			if ( buildParseTrees && currentSymbol instanceof Token &&
			     ((Token)currentSymbol).getTokenIndex()==-1 )
			{
				// we must have conjured up a new token during single token insertion
				// if it's not the current symbol
				_ctx.addErrorNode((Token)currentSymbol);
			}
		}
		return currentSymbol;
	}

	/** Track the RuleContext objects during the parse and hook them up
	 *  using the children list so that it forms a parse tree.
	 *  The RuleContext returned from the start rule represents the root
	 *  of the parse tree.
	 *
	 *  To built parse trees, all we have to do is put a hook in setState()
	 *  and enterRule(). In setState(), we add tokens to the current context
	 *  as children. By the time we get to enterRule(), we are already
	 *  in an invoked rule so we add this context as a child of the parent
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
	public void setBuildParseTree(boolean buildParseTrees) {
		this.buildParseTrees = buildParseTrees;
	}

	public boolean getBuildParseTree() {
		return buildParseTrees;
	}

//	public void setTraceATNStates(boolean traceATNStates) {
//		this.traceATNStates = traceATNStates;
//	}
//
//	public boolean getTraceATNStates() {
//		return traceATNStates;
//	}

    public ParseTreeListener<Token> getListener() {
        return _listener;
    }

    public void setListener(ParseTreeListener<Token> listener) {
        this._listener = listener;
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

	/** Tell our token source and error strategy about a new way to create tokens */
	@Override
	public void setTokenFactory(TokenFactory<?> factory) {
		_input.getTokenSource().setTokenFactory(factory);
		_errHandler.setTokenFactory(factory);
	}

	@Override
	public TokenStream getInputStream() { return getTokenStream(); }

	@Override
	public final void setInputStream(IntStream input) {
		setTokenStream((TokenStream)input);
	}

	public TokenStream getTokenStream() {
		return _input;
	}

	/** Set the token stream and reset the parser */
	public void setTokenStream(TokenStream input) {
		this._input = null;
		reset();
		this._input = input;
	}

    public String getInputString(int start) {
        return getInputString(start, getInputStream().index());
    }

    public String getInputString(int start, int stop) {
        SymbolStream<Token> input = getInputStream();
        if ( input instanceof TokenStream ) {
            return ((TokenStream)input).toString(start,stop);
        }
        return "n/a";
    }

    /** Match needs to return the current input symbol, which gets put
     *  into the label for the associated token ref; e.g., x=ID.
     */
    public Token getCurrentToken() {
		return _input.LT(1);
	}

    public void notifyListeners(String msg)	{
		notifyListeners(getCurrentToken(), msg, null);
	}

	public void notifyListeners(Token offendingToken, String msg,
							   @Nullable RecognitionException e)
	{
		int line = -1;
		int charPositionInLine = -1;
		if (offendingToken instanceof Token) {
			line = ((Token) offendingToken).getLine();
			charPositionInLine = ((Token) offendingToken).getCharPositionInLine();
		}
		ANTLRErrorListener<Token>[] listeners = getListeners();
		if ( listeners.length == 0 ) {
			System.err.println("line "+line+":"+charPositionInLine+" "+msg);
			return;
		}
		for (ANTLRErrorListener<Token> pl : listeners) {
			pl.error(this, offendingToken, line, charPositionInLine, msg, e);
		}
	}

	/** Consume the current symbol and return it. E.g., given the following
	 *  input with A being the current lookahead symbol:
	 *
	 *  	A B
	 *  	^
	 *
	 *  this function moves the cursor to B and returns A.
	 *
	 *  If the parser is creating parse trees, the current symbol
	 *  would also be added as a child to the current context (node).
     *
     *  Trigger listener events if there's a listener.
	 */
	public Token consume() {
		Token o = getCurrentToken();
		getInputStream().consume();
		if ( buildParseTrees ) {
			// TODO: tree parsers?
			if ( _errHandler.inErrorRecoveryMode(this) ) {
//				System.out.println("consume in error recovery mode for "+o);
				_ctx.addErrorNode((Token) o);
			}
			else _ctx.addChild((Token)o);
		}
        if ( _listener != null) _listener.visitTerminal(o);
		return o;
	}

	protected void addContextToParseTree() {
		ParserRuleContext parent = (ParserRuleContext)_ctx.parent;
		// add current context to parent if we have a parent
		if ( parent!=null )	{
			parent.addChild(_ctx);
		}
	}

	/** Always called by generated parsers upon entry to a rule.
	 *  This occurs after the new context has been pushed. Access field
	 *  _ctx get the current context.
	 *
	 *  This is flexible because users do not have to regenerate parsers
	 *  to get trace facilities.
	 */
	public void enterRule(ParserRuleContext<Token> localctx, int ruleIndex) {
		_ctx = localctx;
		_ctx.start = _input.LT(1);
		_ctx.ruleIndex = ruleIndex;
		if ( buildParseTrees ) addContextToParseTree();
        if ( _listener != null) {
            _listener.enterEveryRule(_ctx);
            _ctx.enterRule(_listener);
        }
	}

    public void exitRule(int ruleIndex) {
        // trigger event on _ctx, before it reverts to parent
        if ( _listener != null) {
            _ctx.exitRule(_listener);
            _listener.exitEveryRule(_ctx);
        }
		_ctx = (ParserRuleContext<Token>)_ctx.parent;
    }

	public void enterOuterAlt(ParserRuleContext<Token> localctx, int altNum) {
		// if we have new localctx, make sure we replace existing ctx
		// that is previous child of parse tree
		if ( buildParseTrees && _ctx != localctx ) {
			ParserRuleContext parent = (ParserRuleContext)_ctx.parent;
			parent.removeLastChild();
			if ( parent!=null )	parent.addChild(localctx);
		}
		_ctx = localctx;
		_ctx.altNum = altNum;
	}

	public ParserRuleContext<Token> getInvokingContext(int ruleIndex) {
		ParserRuleContext<Token> p = _ctx;
		while ( p!=null ) {
			if ( p.getRuleIndex() == ruleIndex ) return p;
			p = (ParserRuleContext<Token>)p.parent;
		}
		return null;
	}

	public ParserRuleContext<Token> getContext() {
		return _ctx;
	}

	public boolean inContext(String context) {
		// TODO: useful in parser?
		return false;
	}

    public boolean isExpectedToken(int symbol) {
//   		return getInterpreter().atn.nextTokens(_ctx);
        ATN atn = getInterpreter().atn;
		ParserRuleContext ctx = _ctx;
        ATNState s = atn.states.get(ctx.s);
        IntervalSet following = atn.nextTokens(s);
        if (following.contains(symbol)) {
            return true;
        }
//        System.out.println("following "+s+"="+following);
        if ( !following.contains(Token.EPSILON) ) return false;

        while ( ctx!=null && ctx.invokingState>=0 && following.contains(Token.EPSILON) ) {
            ATNState invokingState = atn.states.get(ctx.invokingState);
            RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            if (following.contains(symbol)) {
                return true;
            }

            ctx = (ParserRuleContext)ctx.parent;
        }

        if ( following.contains(Token.EPSILON) && symbol == Token.EOF ) {
            return true;
        }

        return false;
    }

	/** Compute the set of valid tokens reachable from the current
	 *  position in the parse.
	 */
    public IntervalSet getExpectedTokens() {
        ATN atn = getInterpreter().atn;
		ParserRuleContext ctx = _ctx;
        ATNState s = atn.states.get(ctx.s);
        IntervalSet following = atn.nextTokens(s);
//        System.out.println("following "+s+"="+following);
        if ( !following.contains(Token.EPSILON) ) return following;
        IntervalSet expected = new IntervalSet();
        expected.addAll(following);
        expected.remove(Token.EPSILON);
        while ( ctx!=null && ctx.invokingState>=0 && following.contains(Token.EPSILON) ) {
            ATNState invokingState = atn.states.get(ctx.invokingState);
            RuleTransition rt = (RuleTransition)invokingState.transition(0);
            following = atn.nextTokens(rt.followState);
            expected.addAll(following);
            expected.remove(Token.EPSILON);
            ctx = (ParserRuleContext)ctx.parent;
        }
        if ( following.contains(Token.EPSILON) ) {
            expected.add(Token.EOF);
        }
        return expected;
   	}

    public IntervalSet getExpectedTokensWithinCurrentRule() {
        ATN atn = getInterpreter().atn;
        ATNState s = atn.states.get(_ctx.s);
   		return atn.nextTokens(s);
   	}

//	/** Compute the set of valid tokens reachable from the current
//	 *  position in the parse.
//	 */
//	public IntervalSet nextTokens(@NotNull RuleContext ctx) {
//		ATN atn = getInterpreter().atn;
//		ATNState s = atn.states.get(ctx.s);
//		if ( s == null ) return null;
//		return atn.nextTokens(s, ctx);
//	}

	/** Return List<String> of the rule names in your parser instance
	 *  leading up to a call to the current rule.  You could override if
	 *  you want more details such as the file/line info of where
	 *  in the ATN a rule is invoked.
	 *
	 *  This is very useful for error messages.
	 */
	public List<String> getRuleInvocationStack() {
		String[] ruleNames = getRuleNames();
		List<String> stack = new ArrayList<String>();
		RuleContext p = _ctx;
		while ( p!=null ) {
			// compute what follows who invoked us
			stack.add(ruleNames[p.getRuleIndex()]);
			p = p.parent;
		}
		return stack;
	}

	/** For debugging and other purposes, might want the grammar name.
	 *  Have ANTLR generate an implementation for this method.
	 */
	public String getGrammarFileName() {
		return null;
	}

    /** For debugging and other purposes */
    public List<String> getDFAStrings() {
        List<String> s = new ArrayList<String>();
        for (int d = 0; d < _interp.decisionToDFA.length; d++) {
            DFA dfa = _interp.decisionToDFA[d];
            s.add( dfa.toString(getTokenNames()) );
        }
        return s;
    }

    /** For debugging and other purposes */
    public void dumpDFA() {
        boolean seenOne = false;
        for (int d = 0; d < _interp.decisionToDFA.length; d++) {
            DFA dfa = _interp.decisionToDFA[d];
            if ( dfa!=null ) {
                if ( seenOne ) System.out.println();
                System.out.println("Decision " + dfa.decision + ":");
                System.out.print(dfa.toString(getTokenNames()));
                seenOne = true;
            }
        }
    }

	public String getSourceName() {
		return _input.getSourceName();
	}

	/** A convenience method for use most often with template rewrites.
	 *  Convert a List<Token> to List<String>
	 */
	public List<String> toStrings(List<? extends Token> tokens) {
		if ( tokens==null ) return null;
		List<String> strings = new ArrayList<String>(tokens.size());
		for (int i=0; i<tokens.size(); i++) {
			strings.add(tokens.get(i).getText());
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
//		System.err.println("setState "+atnState);
		_ctx.s = atnState;
//		if ( traceATNStates ) _ctx.trace(atnState);
	}
}
