package org.antlr.v4.parse;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTErrorNode;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    public Object create(Token token) { return new GrammarAST(token); }
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

