package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.tool.*;
import org.stringtemplate.v4.misc.MultiMap;

import java.io.File;
import java.util.*;

/** No side-effects; BasicSemanticTriggers.g invokes check rules for these:
 *
 * FILE_AND_GRAMMAR_NAME_DIFFER
 * LEXER_RULES_NOT_ALLOWED
 * PARSER_RULES_NOT_ALLOWED
 * CANNOT_ALIAS_TOKENS
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
 *
 * TODO: 1 action per lex rule
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
				map(ANTLRParser.LEXER, ANTLRParser.COMBINED);

				map(ANTLRParser.PARSER, ANTLRParser.PARSER);
				map(ANTLRParser.PARSER, ANTLRParser.COMBINED);

				map(ANTLRParser.TREE, ANTLRParser.TREE);

				map(ANTLRParser.COMBINED, ANTLRParser.COMBINED);
			}
		};

	public Grammar g;
	public ErrorManager errMgr;

	public BasicSemanticChecks(Grammar g) {
		this.g = g;
		this.errMgr = g.tool.errMgr;
	}

	void checkGrammarName(Token nameToken) {
		if ( g.implicitLexer==null ) return;
		String fullyQualifiedName = nameToken.getInputStream().getSourceName();
		File f = new File(fullyQualifiedName);
		String fileName = f.getName();
		if ( !Utils.stripFileExtension(fileName).equals(nameToken.getText()) ) {
			g.tool.errMgr.grammarError(ErrorType.FILE_AND_GRAMMAR_NAME_DIFFER,
									   fileName, nameToken, nameToken.getText(), fileName);
		}
	}

	void checkNumRules(GrammarAST rulesNode) {
		if ( rulesNode.getChildCount()==0 ) {
			GrammarAST root = (GrammarAST)rulesNode.getParent();
			GrammarAST IDNode = (GrammarAST)root.getChild(0);
			g.tool.errMgr.grammarError(ErrorType.NO_RULES, g.fileName,
									   null, IDNode.getText(), g);
		}
	}

	void checkMode(Token modeNameToken) {
		if ( !g.isLexer() ) {
			g.tool.errMgr.grammarError(ErrorType.MODE_NOT_IN_LEXER, g.fileName,
									   modeNameToken, modeNameToken.getText(), g);
		}
	}

	void checkNumPrequels(List<GrammarAST> options,
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
			g.tool.errMgr.grammarError(ErrorType.REPEATED_PREQUEL,
									   fileName, t);
		}
	}

	void checkInvalidRuleDef(Token ruleID) {
		String fileName = null;
		if ( ruleID.getInputStream()!=null ) {
			fileName = ruleID.getInputStream().getSourceName();
		}
		if ( g.isLexer() && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
			g.tool.errMgr.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
									   fileName, ruleID, ruleID.getText());
		}
		if ( (g.isParser()||g.isTreeGrammar()) &&
			 Character.isUpperCase(ruleID.getText().charAt(0)) )
		{
			g.tool.errMgr.grammarError(ErrorType.LEXER_RULES_NOT_ALLOWED,
									   fileName, ruleID, ruleID.getText());
		}
	}

	void checkInvalidRuleRef(Token ruleID) {
		String fileName = ruleID.getInputStream().getSourceName();
		if ( g.isLexer() && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
			g.tool.errMgr.grammarError(ErrorType.PARSER_RULES_NOT_ALLOWED,
									   fileName, ruleID, ruleID.getText());
		}
	}

	void checkTokenAlias(Token tokenID) {
		String fileName = tokenID.getInputStream().getSourceName();
		if ( Character.isLowerCase(tokenID.getText().charAt(0)) ) {
			g.tool.errMgr.grammarError(ErrorType.TOKEN_NAMES_MUST_START_UPPER,
									   fileName,
									   tokenID,
									   tokenID.getText());
		}
		if ( !g.isCombined() ) {
			g.tool.errMgr.grammarError(ErrorType.CANNOT_ALIAS_TOKENS,
									   fileName,
									   tokenID,
									   tokenID.getText());
		}
	}

	/** At this point, we can only rule out obvious problems like ID[3]
	 *  in parser.  Might be illegal too in later stage when we see ID
	 *  isn't a fragment.
	 */
	void checkTokenArgs(Token tokenID) {
		String fileName = tokenID.getInputStream().getSourceName();
		if ( !g.isLexer() ) {
			g.tool.errMgr.grammarError(ErrorType.ARGS_ON_TOKEN_REF,
									   fileName, tokenID, tokenID.getText());
		}
	}

	/** Check option is appropriate for grammar, rule, subrule */
	boolean checkOptions(GrammarAST parent,
						 Token optionID, String value)
	{
		boolean ok = true;
		if ( optionID.getText().equals("tokenVocab") &&
			 g.parent!=null ) // only allow tokenVocab option in root grammar
		{
			g.tool.errMgr.grammarWarning(ErrorType.TOKEN_VOCAB_IN_DELEGATE,
										 g.fileName,
										 optionID,
										 g.name);
			ok = false;
		}

		if ( parent.getType()==ANTLRParser.BLOCK ) {
			if ( !legalBlockOptions.contains(optionID.getText()) ) { // block
				g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
										   g.fileName,
										   optionID,
										   optionID.getText());
				ok = false;
			}
		}
		else if ( parent.getType()==ANTLRParser.RULE ) {
			if ( !legalRuleOptions.contains(optionID.getText()) ) { // rule
				g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
										   g.fileName,
										   optionID,
										   optionID.getText());
				ok = false;
			}
		}
		else if ( parent.getType()==ANTLRParser.GRAMMAR &&
				  !legalGrammarOption(optionID.getText()) ) { // grammar
			g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
									   g.fileName,
									   optionID,
									   optionID.getText());
			ok = false;
		}

		return ok;
	}

	/** Check option is appropriate for token; parent is ELEMENT_OPTIONS */
	boolean checkTokenOptions(GrammarAST parent,
							  Token optionID, String value)
	{
		String fileName = optionID.getInputStream().getSourceName();
		// don't care about ID<ASTNodeName> options
		if ( value!=null && !legalTokenOptions.contains(optionID.getText()) ) {
			g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
									   fileName,
									   optionID,
									   optionID.getText());
			return false;
		}
		// example (ALT_REWRITE (ALT (ID (ELEMENT_OPTIONS Foo))) (-> (ALT ID))
		if ( parent.hasAncestor(ANTLRParser.ALT_REWRITE) ) {
			g.tool.errMgr.grammarError(ErrorType.HETERO_ILLEGAL_IN_REWRITE_ALT,
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

	boolean legalGrammarOption(String key) {
		switch ( g.getType() ) {
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
	void checkRewriteForMultiRootAltInTreeGrammar(
		Map<String, String> options,
		Token altStart,
		int alt)
	{
		if ( g.isTreeGrammar() &&
			 options!=null && options.get("output")!=null &&
			 options.get("output").equals("template") &&
			 options.get("rewrite")!=null &&
			 options.get("rewrite").equals("true") )
		{
			String fileName = altStart.getInputStream().getSourceName();
			g.tool.errMgr.grammarWarning(ErrorType.REWRITE_FOR_MULTI_ELEMENT_ALT,
										 fileName,
										 altStart,
										 alt);
		}
	}

	void checkASTOps(Map<String, String> options,
					 GrammarAST op,
					 GrammarAST elementRoot)
	{
		RuleAST rule = (RuleAST)op.getAncestor(ANTLRParser.RULE);
		String ruleName = rule.getChild(0).getText();
		String fileName = elementRoot.token.getInputStream().getSourceName();
		if ( options==null || !options.get("output").equals("AST") ) {
			g.tool.errMgr.grammarWarning(ErrorType.AST_OP_WITH_NON_AST_OUTPUT_OPTION,
										 fileName,
										 elementRoot.token,
										 op.getText());
		}
		if ( options!=null && options.get("output")==null ) {
			g.tool.errMgr.grammarWarning(ErrorType.REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION,
										 fileName,
										 elementRoot.token,
										 ruleName);
		}
		if ( op.hasAncestor(ANTLRParser.ALT_REWRITE) ) {
			GrammarAST rew = (GrammarAST)op.getAncestor(ANTLRParser.ALT_REWRITE);
			int altNum = rew.getChildIndex() + 1; // alts are 1..n
			g.tool.errMgr.grammarWarning(ErrorType.AST_OP_IN_ALT_WITH_REWRITE,
										 fileName,
										 elementRoot.token,
										 ruleName,
										 altNum);
		}
	}

	void checkRewriteOk(Map<String, String> options, GrammarAST elementRoot) {
		RuleAST rule = (RuleAST)elementRoot.getAncestor(ANTLRParser.RULE);
		String ruleName = rule.getChild(0).getText();
		String fileName = elementRoot.token.getInputStream().getSourceName();
		if ( options!=null && options.get("output")==null ) {
			g.tool.errMgr.grammarWarning(ErrorType.REWRITE_OR_OP_WITH_NO_OUTPUT_OPTION,
										 fileName,
										 elementRoot.token,
										 ruleName);
		}
	}

	void checkTreeFilterOptions(GrammarRootAST root,
								Map<String, String> options)
	{
		if ( options==null ) return;
		String fileName = root.token.getInputStream().getSourceName();
		String filter = options.get("filter");
		if ( g.isTreeGrammar() && filter!=null && filter.equals("true") ) {
			// check for conflicting options
			// filter => backtrack=true (can't be false)
			// filter&&output!=AST => error
			// filter&&output=AST => rewrite=true
			// any deviation from valid option set is an error
			String backtrack = options.get("backtrack");
			String output = options.get("output");
			String rewrite = options.get("rewrite");
			if ( backtrack!=null && !backtrack.toString().equals("true") ) {
				g.tool.errMgr.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
										   fileName,
										   root.token,
										   "backtrack", backtrack);
			}
			if ( output!=null && !output.equals("AST") ) {
				g.tool.errMgr.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
										   fileName,
										   root.token,
										   "output", output);
			}
			else if ( rewrite!=null && !rewrite.equals("true") ) { // && AST output
				g.tool.errMgr.grammarError(ErrorType.CONFLICTING_OPTION_IN_TREE_FILTER,
										   fileName,
										   root.token,
										   "rewrite", rewrite);
			}
		}
	}

	void checkWildcardRoot(Token wild) {
		String fileName = wild.getInputStream().getSourceName();
		g.tool.errMgr.grammarError(ErrorType.WILDCARD_AS_ROOT,
								   fileName,
								   wild);
	}

	void checkImport(Token importID) {
		Grammar delegate = g.getImportedGrammar(importID.getText());
		if ( delegate==null ) return;
		List<Integer> validDelegators = validImportTypes.get(delegate.getType());
		if ( validDelegators!=null && !validDelegators.contains(g.getType()) ) {
			g.tool.errMgr.grammarError(ErrorType.INVALID_IMPORT,
									   g.fileName,
									   importID,
									   g, delegate);
		}
		if ( g.isCombined() &&
			 (delegate.name.equals(g.name+Grammar.getGrammarTypeToFileNameSuffix(ANTLRParser.LEXER))||
			  delegate.name.equals(g.name+Grammar.getGrammarTypeToFileNameSuffix(ANTLRParser.PARSER))) )
		{
			g.tool.errMgr.grammarError(ErrorType.IMPORT_NAME_CLASH,
									   g.fileName,
									   importID,
									   g, delegate);
		}
	}
}
