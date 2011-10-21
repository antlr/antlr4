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

import org.antlr.v4.runtime.atn.ATN;

import java.util.*;

public class Recognizer<ATNInterpreter> {
	public static final int EOF=-1;

	protected ANTLRErrorStrategy _errHandler = new DefaultANTLRErrorStrategy();
	protected List<ANTLRErrorListener> _listeners;

	protected ATNInterpreter _interp;

	/** Used to print out token names like ID during debugging and
	 *  error reporting.  The generated parsers implement a method
	 *  that overrides this to point to their String[] tokenNames.
	 */
	public String[] getTokenNames() {
		return null;
	}

	public String[] getRuleNames() {
		return null;
	}

	public ATN getATN() { return null; }

	public ATNInterpreter getInterpreter() { return _interp; }

	/*
	public void displayRecognitionError(RecognitionException e) {
		String hdr = getErrorHeader(e);
		String msg = getErrorMessage(e);
		emitErrorMessage(hdr+" "+msg);
	}
	*/

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
	public String getErrorMessage(RecognitionException e) {
		String[] tokenNames = getTokenNames();
		String msg = e.getMessage();
		if ( e instanceof UnwantedTokenException ) {
			UnwantedTokenException ute = (UnwantedTokenException)e;
			String tokenName="<unknown>";
			if ( ute.expecting.contains(Token.EOF) ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[ute.expecting.getSingleElement()];
			}
			msg = "extraneous input "+getTokenErrorDisplay(ute.getUnexpectedToken())+
				" expecting "+tokenName;
		}
		else if ( e instanceof MissingTokenException ) {
			MissingTokenException mte = (MissingTokenException)e;
			String tokenName="<unknown>";
			if ( mte.expecting.contains(Token.EOF) ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mte.expecting.getSingleElement()];
			}
			msg = "missing "+tokenName+" at "+getTokenErrorDisplay(e.offendingToken);
		}
		else if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			String tokenName="<unknown>";
//			if ( mte.expecting.member(Token.EOF) ) {
//				tokenName = "EOF";
//			}
//			else {
//				tokenName = tokenNames[mte.expecting.getSingleElement()];
//			}
			msg = "mismatched input "+getTokenErrorDisplay(e.offendingToken)+
				" expecting "+tokenName;
		}
		else if ( e instanceof MismatchedASTNodeException) {
			MismatchedASTNodeException mtne = (MismatchedASTNodeException)e;
			String tokenName="<unknown>";
			if ( mtne.expecting.contains(Token.EOF) ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mtne.expecting.getSingleElement()];
			}
			msg = "mismatched tree node: "+mtne.offendingNode +
				" expecting "+tokenName;
		}
		else if ( e instanceof NoViableAltException ) {
			//NoViableAltException nvae = (NoViableAltException)e;
			// for development, can add "decision=<<"+nvae.grammarDecisionDescription+">>"
			// and "(decision="+nvae.decisionNumber+") and
			// "state "+nvae.stateNumber
			msg = "no viable alternative at input "+getTokenErrorDisplay(e.offendingToken);
		}
		else if ( e instanceof MismatchedSetException ) {
			MismatchedSetException mse = (MismatchedSetException)e;
			msg = "mismatched input "+getTokenErrorDisplay(e.offendingToken)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedNotSetException ) {
			MismatchedNotSetException mse = (MismatchedNotSetException)e;
			msg = "mismatched input "+getTokenErrorDisplay(e.offendingToken)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof FailedPredicateException ) {
			FailedPredicateException fpe = (FailedPredicateException)e;
			msg = "rule "+fpe.ruleName+" failed predicate: {"+
				fpe.predicateText+"}?";
		}
		return msg;
	}
	 */

	/** What is the error header, normally line/character position information? */
	public String getErrorHeader(RecognitionException e) {
		int line = e.offendingToken.getLine();
		int charPositionInLine = e.offendingToken.getCharPositionInLine();
		return "line "+line+":"+charPositionInLine;
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
		if ( t==null ) return "<no token>";
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

	public void addListener(ANTLRErrorListener pl) {
		if ( _listeners ==null ) {
			_listeners =
				Collections.synchronizedList(new ArrayList<ANTLRErrorListener>(2));
		}
		if ( pl!=null ) _listeners.add(pl);
	}

	public void removeListener(ANTLRErrorListener pl) { _listeners.remove(pl); }

	public void removeListeners() { _listeners.clear(); }

	public List<ANTLRErrorListener> getListeners() { return _listeners; }

	public ANTLRErrorStrategy getErrHandler() { return _errHandler; }

	public void setErrHandler(ANTLRErrorStrategy h) { this._errHandler = h; }

	// subclass needs to override these if there are sempreds or actions
	// that the ATN interp needs to execute
	public boolean sempred(RuleContext _localctx, int ruleIndex, int actionIndex) {
		return true;
	}

	/** In lexer, both indexes are same; one action per rule. */
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
	}

	/** Create context for a rule reference IN fromRuleIndex using parent _localctx.
	 *  Used only when there are arguments to the rule function. _localctx
	 *  must be correct context for fromRuleIndex.
	public RuleContext newContext(RuleContext _localctx, int s, int fromRuleIndex, int actionIndex) {
		return new ParserRuleContext(_localctx, s);
	}
	 */

	/** Map a rule index to appropriate RuleContext subclass. Used when rule
	 *  has no arguments.
	public RuleContext newContext(RuleContext _localctx, int s, int targetRuleIndex) {
		return new ParserRuleContext(_localctx, s);
	}
	 */
}
