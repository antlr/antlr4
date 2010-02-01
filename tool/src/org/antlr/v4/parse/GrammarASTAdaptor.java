package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.BlockAST;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTErrorNode;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    CharStream input; // where we can find chars ref'd by tokens in tree
    //TokenStream tokens;
    public GrammarASTAdaptor(CharStream input) { this.input = input; }
    //public GrammarASTAdaptor(TokenStream tokens) { this.tokens = tokens; }
    public Object create(Token token) {
        if ( token==null ) return new GrammarAST(token);
        switch ( token.getType() ) {
            case ANTLRParser.BLOCK : return new BlockAST(token);
            case ANTLRParser.TOKEN_REF :
            case ANTLRParser.STRING_LITERAL :
            case ANTLRParser.WILDCARD :
                return new BlockAST(token);
            default :
                return new GrammarAST(token);
        }
    }

    @Override
    /** Make sure even imaginary nodes know the input stream */
    public Object create(int tokenType, String text) {
        GrammarAST t = (GrammarAST)super.create(tokenType, text);
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
