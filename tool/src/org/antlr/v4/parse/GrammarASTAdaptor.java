package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.*;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    org.antlr.runtime.CharStream input; // where we can find chars ref'd by tokens in tree
    public GrammarASTAdaptor() { ; }
    public GrammarASTAdaptor(org.antlr.runtime.CharStream input) { this.input = input; }

    public Object create(Token token) {
        return new GrammarAST(token);
    }

    @Override
    /** Make sure even imaginary nodes know the input stream */
    public Object create(int tokenType, String text) {
		GrammarAST t = null;
		if ( tokenType==ANTLRParser.RULE ) {
			// needed by TreeWizard to make RULE tree
        	t = new GrammarASTWithOptions(new CommonToken(tokenType, text));
		}
		else if ( tokenType==ANTLRParser.STRING_LITERAL ) {
			// implicit lexer construction done with wizard; needs this node type
			// whereas grammar ANTLRParser.g can use token option to spec node type
			t = new TerminalAST(new CommonToken(tokenType, text));
		}
		else {
			t = (GrammarAST)super.create(tokenType, text);
		}
        ((CommonToken)t.token).setInputStream(input);
        return t;
    }

    public Object dupNode(Object t) {
        if ( t==null ) return null;
        return ((GrammarAST)t).dupNode(); //create(((GrammarAST)t).token);
    }

    public Object errorNode(org.antlr.runtime.TokenStream input, org.antlr.runtime.Token start, org.antlr.runtime.Token stop,
                            org.antlr.runtime.RecognitionException e)
    {
        return new GrammarASTErrorNode(input, start, stop, e);
    }
}
