package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;

/** */
public class BasicSemanticChecks {
    protected static void checkInvalidRuleDef(int gtype, String fileName, Token ruleID) {
        if ( gtype== ANTLRParser.LEXER_GRAMMAR && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
            ErrorManager.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
                                      fileName, ruleID, ruleID.getText());
        }
        if ( (gtype==ANTLRParser.PARSER_GRAMMAR||gtype==ANTLRParser.TREE_GRAMMAR) &&
             Character.isUpperCase(ruleID.getText().charAt(0)) )
        {
            ErrorManager.grammarError(ErrorType.LEXER_RULES_NOT_ALLOWED,
                                      fileName, ruleID, ruleID.getText());
        }
    }

    // todo: get filename from stream via token?
    protected static void checkInvalidRuleRef(int gtype, String fileName, Token ruleID) {}
}
