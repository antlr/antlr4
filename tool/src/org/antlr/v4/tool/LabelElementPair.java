package org.antlr.v4.tool;

import org.antlr.v4.parse.ANTLRParser;

import java.util.BitSet;

public class LabelElementPair {
    public static final BitSet tokenTypeForTokens = new BitSet();
    static {
        tokenTypeForTokens.set(ANTLRParser.TOKEN_REF);
        tokenTypeForTokens.set(ANTLRParser.STRING_LITERAL);
        tokenTypeForTokens.set(ANTLRParser.WILDCARD);
    }

    public GrammarAST label;
    public GrammarAST element;
    public LabelType type;

    public LabelElementPair(Grammar g, GrammarAST label, GrammarAST element, int labelOp) {
        this.label = label;
        this.element = element;
        // compute general case for label type
        if ( element.getFirstDescendantWithType(tokenTypeForTokens)!=null ) {
            if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.TOKEN_LABEL;
            else type = LabelType.TOKEN_LIST_LABEL;
        }
        else if ( element.getFirstDescendantWithType(ANTLRParser.RULE_REF)!=null ) {
            if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.RULE_LABEL;
            else type = LabelType.RULE_LIST_LABEL;
        }

        // now reset if lexer and string
        if ( g.getType() == ANTLRParser.LEXER ) {
            if ( element.getFirstDescendantWithType(ANTLRParser.STRING_LITERAL)!=null ) {
                if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.LEXER_STRING_LABEL;
            }
        }
        else if ( g.getType() == ANTLRParser.TREE ) {
            if ( element.getFirstDescendantWithType(ANTLRParser.WILDCARD)!=null ) {
                if ( labelOp==ANTLRParser.ASSIGN ) type = LabelType.WILDCARD_TREE_LABEL;
                else type = LabelType.WILDCARD_TREE_LIST_LABEL;
            }
        }
    }

    public String toString() {
        return label.getText()+" "+type+" "+element.toString();
    }
}
