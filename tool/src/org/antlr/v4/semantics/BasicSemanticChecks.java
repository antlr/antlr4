package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.GrammarAST;

import java.util.*;

/** No side-effects */
public class BasicSemanticChecks {
    public static final Set legalLexerOptions =
            new HashSet() {
                {
                add("language"); add("tokenVocab");
                add("TokenLabelType");
                add("superClass");
                add("filter");
                add("k");
                add("backtrack");
                add("memoize");
                }
            };

    public static final Set legalParserOptions =
            new HashSet() {
                {
                add("language"); add("tokenVocab");
                add("output"); add("rewrite"); add("ASTLabelType");
                add("TokenLabelType");
                add("superClass");
                add("k");
                add("backtrack");
                add("memoize");
                }
            };

    public static final Set legalTreeParserOptions =
        new HashSet() {
            {
                add("language"); add("tokenVocab");
                add("output"); add("rewrite"); add("ASTLabelType");
                add("TokenLabelType");
                add("superClass");
                add("k");
                add("backtrack");
                add("memoize");
                add("filter");
            }
        };

    public static final Set legalRuleOptions =
            new HashSet() {
                {
                    add("k"); add("greedy"); add("memoize");
                    add("backtrack"); add("strategy");
                }
            };

    public static final Set legalBlockOptions =
            new HashSet() {{add("k"); add("greedy"); add("backtrack"); add("memoize");}};

    /** Legal options for terminal refs like ID<node=MyVarNode> */
    public static final Set legalTokenOptions =
            new HashSet() {
                {
                add(defaultTokenOption);
                add("associativity");
                }
            };

    public static final String defaultTokenOption = "node";

    // TODO: track errors?
    
    protected static void checkGrammarName(Token nameToken) {
        String fileName = nameToken.getInputStream().getSourceName();
        if ( !Utils.stripFileExtension(fileName).equals(nameToken.getText()) ) {
            ErrorManager.grammarError(ErrorType.FILE_AND_GRAMMAR_NAME_DIFFER,
                                      fileName, nameToken, nameToken.getText(), fileName);
        }
    }

    protected static void checkNumRules(int gtype, String fileName,
                                        GrammarAST rulesNode)
    {
        if ( rulesNode.getChildCount()==0 ) {
            GrammarAST root = (GrammarAST)rulesNode.getParent();
            GrammarAST IDNode = (GrammarAST)root.getChild(0);
            ErrorManager.grammarError(ErrorType.NO_RULES, fileName, null, IDNode.getText());
        }
    }

    protected static void checkNumPrequels(int gtype, List<GrammarAST> options,
                                           List<GrammarAST> imports,
                                           List<GrammarAST> tokens)
    {
        List<Token> secondOptionTokens = new ArrayList<Token>();
        if ( options!=null && options.size()>1 ) {
            secondOptionTokens.add(options.get(1).token);
        }
        if ( imports!=null && imports.size()>1 ) {
            secondOptionTokens.add(imports.get(1).token);
        }
        if ( tokens!=null && tokens.size()>1 ) {
            secondOptionTokens.add(tokens.get(1).token);
        }
        for (Token t : secondOptionTokens) {
            String fileName = t.getInputStream().getSourceName();
            ErrorManager.grammarError(ErrorType.REPEATED_PREQUEL,
                                      fileName, t);
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
        if ( Character.isLowerCase(tokenID.getText().charAt(0)) ) {
            ErrorManager.grammarError(ErrorType.TOKEN_NAMES_MUST_START_UPPER,
                                      fileName,
                                      tokenID,
                                      tokenID.getText());
        }
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

    /** Check option is appropriate for grammar, rule, subrule */
    protected static boolean checkOptions(int gtype, GrammarAST parent,
                                          Token optionID, String value)
    {
        String fileName = optionID.getInputStream().getSourceName();
        if ( parent.getType()==ANTLRParser.BLOCK ) {
            if ( !legalBlockOptions.contains(optionID.getText()) ) { // grammar
                ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                          fileName,
                                          optionID,
                                          optionID.getText());
                return false;
            }
        }
        else if ( parent.getType()==ANTLRParser.RULE ) {
            if ( !legalRuleOptions.contains(optionID.getText()) ) { // grammar
                ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                          fileName,
                                          optionID,
                                          optionID.getText());
                return false;
            }
        }
        else if ( !legalGrammarOption(gtype, optionID.getText()) ) { // grammar
            ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                      fileName,
                                      optionID,
                                      optionID.getText());
            return false;
        }
        return true;
    }

    /** Check option is appropriate for token */
    protected static boolean checkTokenOptions(int gtype, GrammarAST parent,
                                               Token optionID, String value)
    {
        String fileName = optionID.getInputStream().getSourceName();
        if ( !legalTokenOptions.contains(optionID.getText()) ) {
            ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                      fileName,
                                      optionID,
                                      optionID.getText());
            return false;
        }
        // TODO: extra checks depending on terminal kind?
        switch ( parent.getType() ) {
            case ANTLRParser.TOKEN_REF :
            case ANTLRParser.STRING_LITERAL :
            case ANTLRParser.WILDCARD :
        }
        return true;
    }

    public static boolean legalGrammarOption(int gtype, String key) {
        switch ( gtype ) {
            case ANTLRParser.LEXER_GRAMMAR :
                return legalLexerOptions.contains(key);
            case ANTLRParser.PARSER_GRAMMAR :
                return legalParserOptions.contains(key);
            case ANTLRParser.TREE_GRAMMAR :
                return legalTreeParserOptions.contains(key);
            default :
                return legalParserOptions.contains(key);
        }
    }

    /** Rules in tree grammar that use -> rewrites and are spitting out
     *  templates via output=template and then use rewrite=true must only
     *  use -> on alts that are simple nodes or trees or single rule refs
     *  that match either nodes or trees.  The altAST is the ALT node
     *  for an ALT.  Verify that its first child is simple.  Must be either
     *  ( ALT ^( A B ) <end-of-alt> ) or ( ALT A <end-of-alt> ) or
     *  other element.
     */
    public static void checkRewriteForMultiRootAltInTreeGrammar(int gtype,
                                                                Map<String, String> options,
                                                                Token altStart,
                                                                int alt)
    {
        if ( gtype==ANTLRParser.TREE_GRAMMAR &&
             options!=null && options.get("output").equals("template") &&
             options.get("rewrite").equals("true") )
        {
            String fileName = altStart.getInputStream().getSourceName();
            ErrorManager.grammarWarning(ErrorType.REWRITE_FOR_MULTI_ELEMENT_ALT,
                                        fileName,
                                        altStart,
                                        alt);
        }
    }

    protected static void checkFOO(int gtype, Token ID) {
    }
}
