package org.antlr.v4.semantics;

import org.antlr.misc.MultiMap;
import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;

import java.io.File;
import java.util.*;

/** No side-effects; BasicSemanticTriggers.g invokes check rules for these:
 *
 * FILE_AND_GRAMMAR_NAME_DIFFER
 * LEXER_RULES_NOT_ALLOWED
 * PARSER_RULES_NOT_ALLOWED
 * CANNOT_ALIAS_TOKENS_IN_LEXER
 * ARGS_ON_TOKEN_REF
 * ILLEGAL_OPTION
 * REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION
 * NO_RULES
 * REWRITE_FOR_MULTI_ELEMENT_ALT
 * HETERO_ILLEGAL_IN_REWRITE_ALT
 * AST_OP_WITH_NON_AST_OUTPUT_OPTION
 * AST_OP_IN_ALT_WITH_REWRITE
 * CONFLICTING_OPTION_IN_TREE_FILTER
 * WILDCARD_AS_ROOT
 * INVALID_IMPORT
 * TOKEN_VOCAB_IN_DELEGATE
 * IMPORT_NAME_CLASH
 * REPEATED_PREQUEL
 * TOKEN_NAMES_MUST_START_UPPER
 */
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
                add(TerminalAST.defaultTokenOption);
                add("associativity");
                }
            };

    /** Set of valid imports.  E.g., can only import a tree parser into
     *  another tree parser.  Maps delegate to set of delegator grammar types.
     *  validDelegations.get(LEXER) gives list of the kinds of delegators
     *  that can import lexers.
     */
    public static MultiMap<Integer,Integer> validImportTypes =
        new MultiMap<Integer,Integer>() {
            {
                map(ANTLRParser.LEXER, ANTLRParser.LEXER);
                map(ANTLRParser.LEXER, ANTLRParser.PARSER);
                map(ANTLRParser.LEXER, ANTLRParser.COMBINED);

                map(ANTLRParser.PARSER, ANTLRParser.PARSER);
                map(ANTLRParser.PARSER, ANTLRParser.COMBINED);

                map(ANTLRParser.TREE, ANTLRParser.TREE);

                // TODO: allow COMBINED
                // map(ANTLRParser.GRAMMAR, ANTLRParser.GRAMMAR);
            }
        };

    // TODO: track errors?
    
    protected static void checkGrammarName(Grammar g, Token nameToken) {
        if ( g.implicitLexer ) return;
        String fullyQualifiedName = nameToken.getInputStream().getSourceName();
        File f = new File(fullyQualifiedName);
        String fileName = f.getName();
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
        if ( gtype==ANTLRParser.LEXER && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
            ErrorManager.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
                                      fileName, ruleID, ruleID.getText());
        }
        if ( (gtype==ANTLRParser.PARSER||gtype==ANTLRParser.TREE) &&
             Character.isUpperCase(ruleID.getText().charAt(0)) )
        {
            ErrorManager.grammarError(ErrorType.LEXER_RULES_NOT_ALLOWED,
                                      fileName, ruleID, ruleID.getText());
        }
    }

    // todo: get filename from stream via token?
    protected static void checkInvalidRuleRef(int gtype, Token ruleID) {
        String fileName = ruleID.getInputStream().getSourceName();
        if ( gtype==ANTLRParser.LEXER && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
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
        if ( gtype==ANTLRParser.LEXER ) {
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
        if ( gtype!=ANTLRParser.LEXER ) {
            ErrorManager.grammarError(ErrorType.ARGS_ON_TOKEN_REF,
                                      fileName, tokenID, tokenID.getText());
        }
    }

    /** Check option is appropriate for grammar, rule, subrule */
    protected static boolean checkOptions(Grammar g, GrammarAST parent,
                                          Token optionID, String value)
    {
        boolean ok = true;
        if ( optionID.getText().equals("tokenVocab") &&
             g.parent!=null ) // only allow tokenVocab option in root grammar
        {
            ErrorManager.grammarWarning(ErrorType.TOKEN_VOCAB_IN_DELEGATE,
                                        g.fileName,
                                        optionID,
                                        g.name);
            ok = false;
        }

        if ( parent.getType()==ANTLRParser.BLOCK ) {
            if ( !legalBlockOptions.contains(optionID.getText()) ) { // block
                ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                          g.fileName,
                                          optionID,
                                          optionID.getText());
                ok = false;
            }
        }
        else if ( parent.getType()==ANTLRParser.RULE ) {
            if ( !legalRuleOptions.contains(optionID.getText()) ) { // rule
                ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                          g.fileName,
                                          optionID,
                                          optionID.getText());
                ok = false;
            }
        }
        else if ( parent.getType()==ANTLRParser.COMBINED &&
                  !legalGrammarOption(g.getType(), optionID.getText()) ) { // grammar
            ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                      g.fileName,
                                      optionID,
                                      optionID.getText());
            ok = false;
        }

        return ok;
    }

    /** Check option is appropriate for token; parent is ELEMENT_OPTIONS */
    protected static boolean checkTokenOptions(int gtype, GrammarAST parent,
                                               Token optionID, String value)
    {
        String fileName = optionID.getInputStream().getSourceName();
        // don't care about ID<ASTNodeName> options
        if ( value!=null && !legalTokenOptions.contains(optionID.getText()) ) {
            ErrorManager.grammarError(ErrorType.ILLEGAL_OPTION,
                                      fileName,
                                      optionID,
                                      optionID.getText());
            return false;
        }
        // example (ALT_REWRITE (ALT (ID (ELEMENT_OPTIONS Foo))) (-> (ALT ID))
        if ( parent.hasAncestor(ANTLRParser.ALT_REWRITE) ) {
            ErrorManager.grammarError(ErrorType.HETERO_ILLEGAL_IN_REWRITE_ALT,
                                      fileName,
                                      optionID);

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
            case ANTLRParser.LEXER :
                return legalLexerOptions.contains(key);
            case ANTLRParser.PARSER :
                return legalParserOptions.contains(key);
            case ANTLRParser.TREE :
                return legalTreeParserOptions.contains(key);
            default :
                return legalParserOptions.contains(key);
        }
    }

    /** Rules in tree grammar that use -> rewrites and are spitting out
     *  templates via output=template and then use rewrite=true must only
     *  use -> on alts that are simple nodes or trees or single rule refs
     *  that match either nodes or trees.
     */
    public static void checkRewriteForMultiRootAltInTreeGrammar(int gtype,
                                                                Map<String, String> options,
                                                                Token altStart,
                                                                int alt)
    {
        if ( gtype==ANTLRParser.TREE &&
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

    protected static void checkASTOps(int gtype, Map<String, String> options,
                                      GrammarAST op,
                                      GrammarAST elementRoot)
    {
        String fileName = elementRoot.token.getInputStream().getSourceName();
        if ( options==null || !options.get("output").equals("AST") ) {
            ErrorManager.grammarWarning(ErrorType.AST_OP_WITH_NON_AST_OUTPUT_OPTION,
                                        fileName,
                                        elementRoot.token,
                                        op.getText());
        }
        if ( op.hasAncestor(ANTLRParser.ALT_REWRITE) ) {
            RuleAST rule = (RuleAST)op.getAncestor(ANTLRParser.RULE);
            String ruleName = rule.getChild(0).getText();
            GrammarAST rew = (GrammarAST)op.getAncestor(ANTLRParser.ALT_REWRITE);
            int altNum = rew.getChildIndex() + 1; // alts are 1..n
            ErrorManager.grammarWarning(ErrorType.AST_OP_IN_ALT_WITH_REWRITE,
                                        fileName,
                                        elementRoot.token,
                                        ruleName,
                                        altNum);
        }
    }

    protected static void checkTreeFilterOptions(int gtype, GrammarRootAST root,
                                                 Map<String, String> options)
    {
        if ( options==null ) return;
        String fileName = root.token.getInputStream().getSourceName();
        String filter = options.get("filter");
        if ( gtype==ANTLRParser.TREE && filter!=null && filter.equals("true") ) {
            // check for conflicting options
            // filter => backtrack=true (can't be false)
            // filter&&output!=AST => error
            // filter&&output=AST => rewrite=true
            // any deviation from valid option set is an error
            String backtrack = options.get("backtrack");
            String output = options.get("output");
            String rewrite = options.get("rewrite");
            if ( backtrack!=null && !backtrack.toString().equals("true") ) {
                ErrorManager.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
                                          fileName,
                                          root.token,
                                          "backtrack", backtrack);
            }
            if ( output!=null && !output.equals("AST") ) {
                ErrorManager.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
                                          fileName,
                                          root.token,
                                          "output", output);
            }
            else if ( rewrite!=null && !rewrite.equals("true") ) { // && AST output
                ErrorManager.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
                                          fileName,
                                          root.token,
                                          "rewrite", rewrite);
            }
        }
    }

    protected static void checkWildcardRoot(int gtype, Token wild) {
        String fileName = wild.getInputStream().getSourceName();
        ErrorManager.grammarError(ErrorType.WILDCARD_AS_ROOT,
                                  fileName,
                                  wild);
    }

    protected static void checkImport(Grammar g, Token importID) {
        Grammar delegate = g.getImportedGrammar(importID.getText());
        if ( delegate==null ) return;
        List<Integer> validDelegators = validImportTypes.get(delegate.getType());
        if ( validDelegators!=null && !validDelegators.contains(g.getType()) ) {
            ErrorManager.grammarError(ErrorType.INVALID_IMPORT,
                                      g.fileName,
                                      importID,
                                      g, delegate);
        }
        if ( g.getType()==ANTLRParser.COMBINED &&
             (delegate.name.equals(g.name+Grammar.getGrammarTypeToFileNameSuffix(ANTLRParser.LEXER))||
              delegate.name.equals(g.name+Grammar.getGrammarTypeToFileNameSuffix(ANTLRParser.PARSER))) )
        {
            ErrorManager.grammarError(ErrorType.IMPORT_NAME_CLASH,
                                      g.fileName,
                                      importID,
                                      g, delegate);
        }
    }
}
