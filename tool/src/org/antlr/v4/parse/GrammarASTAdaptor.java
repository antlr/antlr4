package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTErrorNode;
import org.antlr.v4.tool.GrammarASTWithOptions;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    CharStream input; // where we can find chars ref'd by tokens in tree
    public GrammarASTAdaptor() { ; }
    public GrammarASTAdaptor(CharStream input) { this.input = input; }

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
		else {
			t = (GrammarAST)super.create(tokenType, text);
		}
        ((CommonToken)t.token).setInputStream(input);
        return t;
    }

    public Object dupNode(Object t) {
        if ( t==null ) return null;
        return create(((GrammarAST)t).token);
    }
    public Object errorNode(TokenStream input, Token start, Token stop,
                            RecognitionException e)
    {
        return new GrammarASTErrorNode(input, start, stop, e);
    }
}
