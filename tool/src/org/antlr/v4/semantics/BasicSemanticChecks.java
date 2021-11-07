/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarTreeVisitor;
import org.antlr.v4.tool.ErrorManager;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.ActionAST;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.RuleRefAST;
import org.antlr.v4.tool.ast.TerminalAST;
import org.stringtemplate.v4.misc.MultiMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/** No side-effects except for setting options into the appropriate node.
 *  TODO:  make the side effects into a separate pass this
 *
 * Invokes check rules for these:
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
 */
public class BasicSemanticChecks extends GrammarTreeVisitor {
	/** Set of valid imports.  Maps delegate to set of delegator grammar types.
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

				map(ANTLRParser.COMBINED, ANTLRParser.COMBINED);
			}
		};

	public Grammar g;
	public RuleCollector ruleCollector;
	public ErrorManager errMgr;

	/**
	 * When this is {@code true}, the semantic checks will report
	 * {@link ErrorType#UNRECOGNIZED_ASSOC_OPTION} where appropriate. This may
	 * be set to {@code false} to disable this specific check.
	 *
	 * <p>The default value is {@code true}.</p>
	 */
	public boolean checkAssocElementOption = true;

	/**
	 * This field is used for reporting the {@link ErrorType#MODE_WITHOUT_RULES}
	 * error when necessary.
	 */
	protected int nonFragmentRuleCount;

	/**
	 * This is {@code true} from the time {@link #discoverLexerRule} is called
	 * for a lexer rule with the {@code fragment} modifier until
	 * {@link #exitLexerRule} is called.
	 */
	private boolean inFragmentRule;

	public BasicSemanticChecks(Grammar g, RuleCollector ruleCollector) {
		this.g = g;
		this.ruleCollector = ruleCollector;
		this.errMgr = g.tool.errMgr;
	}

	@Override
	public ErrorManager getErrorManager() { return errMgr; }

	public void process() {	visitGrammar(g.ast); }

	// Routines to route visitor traffic to the checking routines

	@Override
	public void discoverGrammar(GrammarRootAST root, GrammarAST ID) {
		checkGrammarName(ID.token);
	}

	@Override
	public void finishPrequels(GrammarAST firstPrequel) {
		if ( firstPrequel==null ) return;
		GrammarAST parent = (GrammarAST)firstPrequel.parent;
		List<GrammarAST> options = parent.getAllChildrenWithType(OPTIONS);
		List<GrammarAST> imports = parent.getAllChildrenWithType(IMPORT);
		List<GrammarAST> tokens = parent.getAllChildrenWithType(TOKENS_SPEC);
		checkNumPrequels(options, imports, tokens);
	}

	@Override
	public void importGrammar(GrammarAST label, GrammarAST ID) {
		checkImport(ID.token);
	}

	@Override
	public void discoverRules(GrammarAST rules) {
		checkNumRules(rules);
	}

	@Override
	protected void enterMode(GrammarAST tree) {
		nonFragmentRuleCount = 0;
	}

	@Override
	protected void exitMode(GrammarAST tree) {
		if (nonFragmentRuleCount == 0) {
			Token token = tree.getToken();
			String name = "?";
			if (tree.getChildCount() > 0) {
				name = tree.getChild(0).getText();
				if (name == null || name.isEmpty()) {
					name = "?";
				}

				token = ((GrammarAST)tree.getChild(0)).getToken();
			}

			g.tool.errMgr.grammarError(ErrorType.MODE_WITHOUT_RULES, g.fileName, token, name, g);
		}
	}

	@Override
	public void modeDef(GrammarAST m, GrammarAST ID) {
		if ( !g.isLexer() ) {
			g.tool.errMgr.grammarError(ErrorType.MODE_NOT_IN_LEXER, g.fileName,
									   ID.token, ID.token.getText(), g);
		}
	}

	@Override
	public void discoverRule(RuleAST rule, GrammarAST ID,
							 List<GrammarAST> modifiers,
							 ActionAST arg, ActionAST returns,
							 GrammarAST thrws, GrammarAST options,
							 ActionAST locals,
							 List<GrammarAST> actions, GrammarAST block)
	{
		// TODO: chk that all or no alts have "# label"
		checkInvalidRuleDef(ID.token);
	}

	@Override
	public void discoverLexerRule(RuleAST rule, GrammarAST ID, List<GrammarAST> modifiers,
								  GrammarAST block)
	{
		checkInvalidRuleDef(ID.token);

		if (modifiers != null) {
			for (GrammarAST tree : modifiers) {
				if (tree.getType() == ANTLRParser.FRAGMENT) {
					inFragmentRule = true;
				}
			}
		}

		if (!inFragmentRule) {
			nonFragmentRuleCount++;
		}
	}

	@Override
	protected void exitLexerRule(GrammarAST tree) {
		inFragmentRule = false;
	}

	@Override
	public void ruleRef(GrammarAST ref, ActionAST arg) {
		checkInvalidRuleRef(ref.token);
	}

	@Override
	public void ruleOption(GrammarAST ID, GrammarAST valueAST) {
		checkOptions((GrammarAST)ID.getAncestor(RULE), ID.token, valueAST);
	}

	@Override
	public void blockOption(GrammarAST ID, GrammarAST valueAST) {
		checkOptions((GrammarAST)ID.getAncestor(BLOCK), ID.token, valueAST);
	}

	@Override
	public void grammarOption(GrammarAST ID, GrammarAST valueAST) {
		boolean ok = checkOptions(g.ast, ID.token, valueAST);
		//if ( ok ) g.ast.setOption(ID.getText(), value);
	}

	@Override
	public void defineToken(GrammarAST ID) {
		checkTokenDefinition(ID.token);
	}

	@Override
	protected void enterChannelsSpec(GrammarAST tree) {
		if (g.isParser()) {
			g.tool.errMgr.grammarError(ErrorType.CHANNELS_BLOCK_IN_PARSER_GRAMMAR, g.fileName, tree.token);
		}
		else if (g.isCombined()) {
			g.tool.errMgr.grammarError(ErrorType.CHANNELS_BLOCK_IN_COMBINED_GRAMMAR, g.fileName, tree.token);
		}
	}

	@Override
	public void defineChannel(GrammarAST ID) {
		checkChannelDefinition(ID.token);
	}

	@Override
	public void elementOption(GrammarASTWithOptions elem, GrammarAST ID, GrammarAST valueAST) {
		String v = null;
		boolean ok = checkElementOptions(elem, ID, valueAST);
//		if ( ok ) {
//			if ( v!=null ) {
//				t.setOption(ID.getText(), v);
//			}
//			else {
//				t.setOption(TerminalAST.defaultTokenOption, v);
//			}
//		}
	}

	@Override
	public void finishRule(RuleAST rule, GrammarAST ID, GrammarAST block) {
		if ( rule.isLexerRule() ) return;
		BlockAST blk = (BlockAST)rule.getFirstChildWithType(BLOCK);
		int nalts = blk.getChildCount();
		GrammarAST idAST = (GrammarAST)rule.getChild(0);
		for (int i=0; i< nalts; i++) {
			AltAST altAST = (AltAST)blk.getChild(i);
			if ( altAST.altLabel!=null ) {
				String altLabel = altAST.altLabel.getText();
				// first check that label doesn't conflict with a rule
				// label X or x can't be rule x.
				Rule r = ruleCollector.rules.get(Utils.decapitalize(altLabel));
				if ( r!=null ) {
					g.tool.errMgr.grammarError(ErrorType.ALT_LABEL_CONFLICTS_WITH_RULE,
											   g.fileName, altAST.altLabel.token,
											   altLabel,
											   r.name);
				}
				// Now verify that label X or x doesn't conflict with label
				// in another rule. altLabelToRuleName has both X and x mapped.
				String prevRuleForLabel = ruleCollector.altLabelToRuleName.get(altLabel);
				if ( prevRuleForLabel!=null && !prevRuleForLabel.equals(rule.getRuleName()) ) {
					g.tool.errMgr.grammarError(ErrorType.ALT_LABEL_REDEF,
											   g.fileName, altAST.altLabel.token,
											   altLabel,
											   rule.getRuleName(),
											   prevRuleForLabel);
				}
			}
		}
		List<GrammarAST> altLabels = ruleCollector.ruleToAltLabels.get(rule.getRuleName());
		int numAltLabels = 0;
		if ( altLabels!=null ) numAltLabels = altLabels.size();
		if ( numAltLabels>0 && nalts != numAltLabels ) {
			g.tool.errMgr.grammarError(ErrorType.RULE_WITH_TOO_FEW_ALT_LABELS,
									   g.fileName, idAST.token, rule.getRuleName());
		}
	}

	// Routines to do the actual work of checking issues with a grammar.
	// They are triggered by the visitor methods above.

	void checkGrammarName(Token nameToken) {
		String fullyQualifiedName = nameToken.getInputStream().getSourceName();
		if (fullyQualifiedName == null) {
			// This wasn't read from a file.
			return;
		}

		File f = new File(fullyQualifiedName);
		String fileName = f.getName();
		if ( g.originalGrammar!=null ) return; // don't warn about diff if this is implicit lexer
		if ( !Utils.stripFileExtension(fileName).equals(nameToken.getText()) &&
		     !fileName.equals(Grammar.GRAMMAR_FROM_STRING_NAME)) {
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
		if ( g.isParser() &&
			Grammar.isTokenName(ruleID.getText()) )
		{
			g.tool.errMgr.grammarError(ErrorType.LEXER_RULES_NOT_ALLOWED,
									   fileName, ruleID, ruleID.getText());
		}
	}

	void checkInvalidRuleRef(Token ruleID) {
		String fileName = ruleID.getInputStream().getSourceName();
		if ( g.isLexer() && Character.isLowerCase(ruleID.getText().charAt(0)) ) {
			g.tool.errMgr.grammarError(ErrorType.PARSER_RULE_REF_IN_LEXER_RULE,
									   fileName, ruleID, ruleID.getText(), currentRuleName);
		}
	}

	void checkTokenDefinition(Token tokenID) {
		String fileName = tokenID.getInputStream().getSourceName();
		if ( !Grammar.isTokenName(tokenID.getText()) ) {
			g.tool.errMgr.grammarError(ErrorType.TOKEN_NAMES_MUST_START_UPPER,
									   fileName,
									   tokenID,
									   tokenID.getText());
		}
	}

	void checkChannelDefinition(Token tokenID) {
	}

	@Override
	protected void enterLexerElement(GrammarAST tree) {
	}

	@Override
	protected void enterLexerCommand(GrammarAST tree) {
		checkElementIsOuterMostInSingleAlt(tree);

		if (inFragmentRule) {
			String fileName = tree.token.getInputStream().getSourceName();
			String ruleName = currentRuleName;
			g.tool.errMgr.grammarError(ErrorType.FRAGMENT_ACTION_IGNORED, fileName, tree.token, ruleName);
		}
	}

	@Override
	public void actionInAlt(ActionAST action) {
		if (inFragmentRule) {
			String fileName = action.token.getInputStream().getSourceName();
			String ruleName = currentRuleName;
			g.tool.errMgr.grammarError(ErrorType.FRAGMENT_ACTION_IGNORED, fileName, action.token, ruleName);
		}
	}

	/**
	 Make sure that action is last element in outer alt; here action,
	 a2, z, and zz are bad, but a3 is ok:
	 (RULE A (BLOCK (ALT {action} 'a')))
	 (RULE B (BLOCK (ALT (BLOCK (ALT {a2} 'x') (ALT 'y')) {a3})))
	 (RULE C (BLOCK (ALT 'd' {z}) (ALT 'e' {zz})))
	 */
	protected void checkElementIsOuterMostInSingleAlt(GrammarAST tree) {
		CommonTree alt = tree.parent;
		CommonTree blk = alt.parent;
		boolean outerMostAlt = blk.parent.getType() == RULE;
		Tree rule = tree.getAncestor(RULE);
		String fileName = tree.getToken().getInputStream().getSourceName();
		if ( !outerMostAlt || blk.getChildCount()>1 )
		{
			ErrorType e = ErrorType.LEXER_COMMAND_PLACEMENT_ISSUE;
			g.tool.errMgr.grammarError(e,
									   fileName,
									   tree.getToken(),
									   rule.getChild(0).getText());

		}
	}

	@Override
	public void label(GrammarAST op, GrammarAST ID, GrammarAST element) {
		switch (element.getType()) {
		// token atoms
		case TOKEN_REF:
		case STRING_LITERAL:
		case RANGE:
		// token sets
		case SET:
		case NOT:
		// rule atoms
		case RULE_REF:
		case WILDCARD:
			return;

		default:
			String fileName = ID.token.getInputStream().getSourceName();
			g.tool.errMgr.grammarError(ErrorType.LABEL_BLOCK_NOT_A_SET, fileName, ID.token, ID.getText());
			break;
		}
	}

	@Override
	protected void enterLabeledLexerElement(GrammarAST tree) {
		Token label = ((GrammarAST)tree.getChild(0)).getToken();
		g.tool.errMgr.grammarError(ErrorType.V3_LEXER_LABEL,
								   g.fileName,
								   label,
								   label.getText());
	}

	@Override
	protected void enterTerminal(GrammarAST tree) {
		String text = tree.getText();
		if (text.equals("''")) {
			g.tool.errMgr.grammarError(ErrorType.EMPTY_STRINGS_AND_SETS_NOT_ALLOWED, g.fileName, tree.token, "''");
		}
	}

	/** Check option is appropriate for grammar, rule, subrule */
	boolean checkOptions(GrammarAST parent,
						 Token optionID,
						 GrammarAST valueAST)
	{
		boolean ok = true;
		if ( parent.getType()==ANTLRParser.BLOCK ) {
			if ( g.isLexer() && !Grammar.LexerBlockOptions.contains(optionID.getText()) ) { // block
				g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
										   g.fileName,
										   optionID,
										   optionID.getText());
				ok = false;
			}
			if ( !g.isLexer() && !Grammar.ParserBlockOptions.contains(optionID.getText()) ) { // block
				g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
										   g.fileName,
										   optionID,
										   optionID.getText());
				ok = false;
			}
		}
		else if ( parent.getType()==ANTLRParser.RULE ) {
			if ( !Grammar.ruleOptions.contains(optionID.getText()) ) { // rule
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

	/** Check option is appropriate for elem; parent of ID is ELEMENT_OPTIONS */
	boolean checkElementOptions(GrammarASTWithOptions elem,
								GrammarAST ID,
								GrammarAST valueAST)
	{
		if (checkAssocElementOption && ID != null && "assoc".equals(ID.getText())) {
			if (elem.getType() != ANTLRParser.ALT) {
				Token optionID = ID.token;
				String fileName = optionID.getInputStream().getSourceName();
				g.tool.errMgr.grammarError(ErrorType.UNRECOGNIZED_ASSOC_OPTION,
										   fileName,
										   optionID,
										   currentRuleName);
			}
		}

		if ( elem instanceof RuleRefAST ) {
			return checkRuleRefOptions((RuleRefAST)elem, ID, valueAST);
		}
		if ( elem instanceof TerminalAST ) {
			return checkTokenOptions((TerminalAST)elem, ID, valueAST);
		}
		if ( elem.getType()==ANTLRParser.ACTION ) {
			return false;
		}
		if ( elem.getType()==ANTLRParser.SEMPRED ) {
			Token optionID = ID.token;
			String fileName = optionID.getInputStream().getSourceName();
			if ( valueAST!=null && !Grammar.semPredOptions.contains(optionID.getText()) ) {
				g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
										   fileName,
										   optionID,
										   optionID.getText());
				return false;
			}
		}
		return false;
	}

	boolean checkRuleRefOptions(RuleRefAST elem, GrammarAST ID, GrammarAST valueAST) {
		Token optionID = ID.token;
		String fileName = optionID.getInputStream().getSourceName();
		// don't care about id<SimpleValue> options
		if ( valueAST!=null && !Grammar.ruleRefOptions.contains(optionID.getText()) ) {
			g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
									   fileName,
									   optionID,
									   optionID.getText());
			return false;
		}
		// TODO: extra checks depending on rule kind?
		return true;
	}

	boolean checkTokenOptions(TerminalAST elem, GrammarAST ID, GrammarAST valueAST) {
		Token optionID = ID.token;
		String fileName = optionID.getInputStream().getSourceName();
		// don't care about ID<ASTNodeName> options
		if ( valueAST!=null && !Grammar.tokenOptions.contains(optionID.getText()) ) {
			g.tool.errMgr.grammarError(ErrorType.ILLEGAL_OPTION,
									   fileName,
									   optionID,
									   optionID.getText());
			return false;
		}
		// TODO: extra checks depending on terminal kind?
		return true;
	}

	boolean legalGrammarOption(String key) {
		switch ( g.getType() ) {
			case ANTLRParser.LEXER :
				return Grammar.lexerOptions.contains(key);
			case ANTLRParser.PARSER :
				return Grammar.parserOptions.contains(key);
			default :
				return Grammar.parserOptions.contains(key);
		}
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
