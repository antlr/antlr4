package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;

public class BasicSemanticChecks {
    // TODO: track errors?
    
    protected static void checkGrammarName(Token nameToken) {
        String fileName = nameToken.getInputStream().getSourceName();
        if ( !Utils.stripFileExtension(fileName).equals(nameToken.getText()) ) {
            ErrorManager.grammarError(ErrorType.FILE_AND_GRAMMAR_NAME_DIFFER,
                                      fileName, nameToken, nameToken.getText(), fileName);
        }
    }

    protected static void checkInvalidRuleDef(int gtype, Token ruleID) {
        String fileName = ruleID.getInputStream().getSourceName();
        if ( gtype==ANTLRParser.LEXER_GRAMMAR && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
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
    protected static void checkInvalidRuleRef(int gtype, Token ruleID) {
        String fileName = ruleID.getInputStream().getSourceName();
        if ( gtype==ANTLRParser.LEXER_GRAMMAR && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
            ErrorManager.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
                                      fileName, ruleID, ruleID.getText());
        }
    }

    protected static void checkTokenAlias(int gtype, Token tokenID) {
        String fileName = tokenID.getInputStream().getSourceName();
        if ( gtype==ANTLRParser.LEXER_GRAMMAR ) {
            ErrorManager.grammarError(ErrorType.CANNOT_ALIAS_TOKENS_IN_LEXER,
                                      fileName,
                                      tokenID,
                                      tokenID.getText());
        }
    }

    /** At this point, we can only rule out obvious problems like ID[3]
     *  in parser.  Might be illegal too in later stage when we see ID
     *  isn't a fragment.
     */
    protected static void checkTokenArgs(int gtype, Token tokenID) {
        String fileName = tokenID.getInputStream().getSourceName();
        if ( gtype!=ANTLRParser.LEXER_GRAMMAR ) {
            ErrorManager.grammarError(ErrorType.ARGS_ON_TOKEN_REF,
                                      fileName, tokenID, tokenID.getText());
        }
    }

    protected static void checkFOO(int gtype, Token ID) {
    }
}
