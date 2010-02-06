package org.antlr.v4.parse;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.GrammarASTErrorNode;

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
    /*

    public Object nil() { return delegate. }

    public boolean isNil(Object tree) {
        return false;
    }

    public void addChild(Object t, Object child) {
    }

    public Object becomeRoot(Object newRoot, Object oldRoot) { return delegate. }

    public Object rulePostProcessing(Object root) { return delegate. }

    public int getUniqueID(Object node) { return delegate. }

    public Object becomeRoot(Token newRoot, Object oldRoot) { return delegate. }

    public Object create(int tokenType, Token fromToken) { return delegate. }

    public Object create(int tokenType, Token fromToken, String text) { return delegate. }

    public int getType(Object t) { return delegate. }

    public void setType(Object t, int type) { return delegate. }

    public String getText(Object t) { return delegate. }

    public void setText(Object t, String text) { return delegate. }

    public Token getToken(Object t) { return delegate. }

    public void setTokenBoundaries(Object t, Token startToken, Token stopToken) { return delegate. }

    public int getTokenStartIndex(Object t) { return delegate. }

    public int getTokenStopIndex(Object t) { return delegate. }

    public Object getChild(Object t, int i) { return delegate. }

    public void setChild(Object t, int i, Object child) { return delegate. }

    public Object deleteChild(Object t, int i) { return delegate. }

    public int getChildCount(Object t) { return delegate. }

    public Object getParent(Object t){ return delegate. }

    public void setParent(Object t, Object parent){ return delegate. }
    public int getChildIndex(Object t) { return delegate. }

    public void setChildIndex(Object t, int index) { delegate.setChildIndex(t,index);   }

    public void replaceChildren(Object parent, int startChildIndex, int stopChildIndex, Object t) {
        delegate.replaceChildren(parent, startChildIndex, stopChildIndex, t);
    }
     */
}
