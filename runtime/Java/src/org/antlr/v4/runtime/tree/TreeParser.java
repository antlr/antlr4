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

package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.*;

import java.util.regex.*;

/** A parser for a stream of tree nodes.  "tree grammars" result in a subclass
 *  of this.  All the error reporting and recovery is shared with Parser via
 *  the BaseRecognizer superclass.
*/
public class TreeParser extends BaseRecognizer {
	public static final int DOWN = Token.DOWN;
	public static final int UP = Token.UP;

	public ASTAdaptor _adaptor = new CommonASTAdaptor();

    // precompiled regex used by inContext
    static String dotdot = ".*[^.]\\.\\.[^.].*";
    static String doubleEtc = ".*\\.\\.\\.\\s+\\.\\.\\..*";
    static Pattern dotdotPattern = Pattern.compile(dotdot);
    static Pattern doubleEtcPattern = Pattern.compile(doubleEtc);

	protected ASTNodeStream _input;

	public TreeParser(ASTNodeStream input) {
		super(input);
	}

	public void reset() {
		super.reset(); // reset all recognizer state variables
		if ( _input !=null ) {
			_input.seek(0); // rewind the input
		}
	}

	protected Object getCurrentInputSymbol() { return _input.LT(1); }

	@Override
	public ASTNodeStream getInputStream() { return _input; }

	@Override
	public void setInputStream(IntStream input) { _input = (ASTNodeStream)input; }

	/** Always called by generated parsers upon entry to a rule.
	 *  This occurs after the new context has been pushed. Access field
	 *  _ctx get the current context.
	 *
	 *  This is flexible because users do not have to regenerate parsers
	 *  to get trace facilities.
	 */
	public void enterRule(TreeParserRuleContext localctx, int ruleIndex) {
		_ctx = localctx;
		localctx.start = _input.LT(1);
		localctx.ruleIndex = ruleIndex;
	}

	public String getSourceName() {
		return _input.getSourceName();
	}

	protected Object getCurrentInputSymbol(IntStream input) {
		return ((ASTNodeStream)input).LT(1);
	}

	protected Object getMissingSymbol(IntStream input,
									  RecognitionException e,
									  int expectedTokenType)
	{
		String tokenText =
			"<missing "+getTokenNames()[expectedTokenType]+">";
        ASTAdaptor adaptor = ((ASTNodeStream)e.input).getTreeAdaptor();
        return adaptor.create(new CommonToken(expectedTokenType, tokenText));
	}

    /** Match '.' in tree parser has special meaning.  Skip node or
	 *  entire tree if node has children.  If children, scan until
	 *  corresponding UP node.
	 */
	public void matchAny(IntStream ignore) { // ignore stream, copy of input
		errorRecovery = false;
//		failed = false;
		Object look = _input.LT(1);
		if ( _input.getTreeAdaptor().getChildCount(look)==0 ) {
			_input.consume(); // not subtree, consume 1 node and return
			return;
		}
		// current node is a subtree, skip to corresponding UP.
		// must count nesting level to get right UP
		int level=0;
		int tokenType = _input.getTreeAdaptor().getType(look);
		while ( tokenType!=Token.EOF && !(tokenType==UP && level==0) ) {
			_input.consume();
			look = _input.LT(1);
			tokenType = _input.getTreeAdaptor().getType(look);
			if ( tokenType == DOWN ) {
				level++;
			}
			else if ( tokenType == UP ) {
				level--;
			}
		}
		_input.consume(); // consume UP
	}

    /** We have DOWN/UP nodes in the stream that have no line info; override.
	 *  plus we want to alter the exception type.  Don't try to recover
	 *  from tree parser errors inline...
     */
    protected Object recoverFromMismatchedToken(IntStream input,
                                                int ttype)
        throws RecognitionException
    {
        //throw new MismatchedTreeNodeException(ttype, (TreeNodeStream)input);
		return null;
    }

    /** Prefix error message with the grammar name because message is
	 *  always intended for the programmer because the parser built
	 *  the input tree not the user.
	public String getErrorHeader(RecognitionException e) {
		// todo: might not have token; use node?
		int line = e.offendingToken.getLine();
		int charPositionInLine = e.offendingToken.getCharPositionInLine();
		return getGrammarFileName()+": node from "+
			   (e.approximateLineInfo?"after ":"")+"line "+line+":"+charPositionInLine;
	}
	 */

	/** Tree parsers parse nodes they usually have a token object as
	 *  payload. Set the exception token and do the default behavior.
	public String getErrorMessage(RecognitionException e, String[] tokenNames) {
		if ( this instanceof TreeParser ) {
			ASTAdaptor adaptor = ((ASTNodeStream)e.input).getTreeAdaptor();
			e.offendingToken = adaptor.getToken(e.offendingNode);
			if ( e.offendingToken ==null ) { // could be an UP/DOWN node
				e.offendingToken = new CommonToken(adaptor.getType(e.offendingNode),
										  adaptor.getText(e.offendingNode));
			}
		}
		return super.getErrorMessage(e);
	}
	 */

    /** Check if current node in input has a context.  Context means sequence
     *  of nodes towards root of tree.  For example, you might say context
     *  is "MULT" which means my parent must be MULT.  "CLASS VARDEF" says
     *  current node must be child of a VARDEF and whose parent is a CLASS node.
     *  You can use "..." to mean zero-or-more nodes.  "METHOD ... VARDEF"
     *  means my parent is VARDEF and somewhere above that is a METHOD node.
     *  The first node in the context is not necessarily the root.  The context
     *  matcher stops matching and returns true when it runs out of context.
     *  There is no way to force the first node to be the root.
     */
    public boolean inContext(String context) {
        return inContext(_input.getTreeAdaptor(), getTokenNames(), _input.LT(1), context);
    }

    /** The worker for inContext.  It's static and full of parameters for
     *  testing purposes.
     */
    public static boolean inContext(ASTAdaptor adaptor,
                                    String[] tokenNames,
                                    Object t,
                                    String context)
    {
        Matcher dotdotMatcher = dotdotPattern.matcher(context);
        Matcher doubleEtcMatcher = doubleEtcPattern.matcher(context);
        if ( dotdotMatcher.find() ) { // don't allow "..", must be "..."
            throw new IllegalArgumentException("invalid syntax: ..");
        }
        if ( doubleEtcMatcher.find() ) { // don't allow double "..."
            throw new IllegalArgumentException("invalid syntax: ... ...");
        }
        context = context.replaceAll("\\.\\.\\.", " ... "); // ensure spaces around ...
        context = context.trim();
        String[] nodes = context.split("\\s+");
        int ni = nodes.length-1;
        t = adaptor.getParent(t);
        while ( ni>=0 && t!=null ) {
            if ( nodes[ni].equals("...") ) {
                // walk upwards until we see nodes[ni-1] then continue walking
                if ( ni==0 ) return true; // ... at start is no-op
                String goal = nodes[ni-1];
                Object ancestor = getAncestor(adaptor, tokenNames, t, goal);
                if ( ancestor==null ) return false;
                t = ancestor;
                ni--;
            }
            String name = tokenNames[adaptor.getType(t)];
            if ( !name.equals(nodes[ni]) ) {
                //System.err.println("not matched: "+nodes[ni]+" at "+t);
                return false;
            }
            // advance to parent and to previous element in context node list
            ni--;
            t = adaptor.getParent(t);
        }

        if ( t==null && ni>=0 ) return false; // at root but more nodes to match
        return true;
    }

    /** Helper for static inContext */
    protected static Object getAncestor(ASTAdaptor adaptor, String[] tokenNames, Object t, String goal) {
        while ( t!=null ) {
            String name = tokenNames[adaptor.getType(t)];
            if ( name.equals(goal) ) return t;
            t = adaptor.getParent(t);
        }
        return null;
    }

//	public void traceIn(String ruleName, int ruleIndex)  {
//		super.traceIn(ruleName, ruleIndex, input.LT(1));
//	}
//
//	public void traceOut(String ruleName, int ruleIndex)  {
//		super.traceOut(ruleName, ruleIndex, input.LT(1));
//	}
}
