package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.*;

public class GrammarASTAdaptor extends CommonTreeAdaptor {
    CharStream input; // where we can find chars ref'd by tokens in tree
    public GrammarASTAdaptor() { ; }
    public GrammarASTAdaptor(CharStream input) { this.input = input; }

    public Object create(Token token) {
        if ( token==null ) return new GrammarAST(token);
        switch ( token.getType() ) {
//            case ANTLRParser.BLOCK :
//                return new BlockAST(token);
//            case ANTLRParser.RULE :
//                return new RuleAST(token);
//            case ANTLRParser.PARSER_GRAMMAR :
//            case ANTLRParser.COMBINED_GRAMMAR :
//            case ANTLRParser.TREE_GRAMMAR :
//            case ANTLRParser.LEXER_GRAMMAR :
//                return new GrammarRootAST(token);
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
