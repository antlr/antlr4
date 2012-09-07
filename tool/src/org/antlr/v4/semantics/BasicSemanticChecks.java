/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.semantics;

import org.antlr.runtime.Token;
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
 *
 * TODO: 1 action per lex rule
 */
public class BasicSemanticChecks extends GrammarTreeVisitor {
	/** Set of valid imports.  Maps delegate to set of delegator grammar types.
	 *  validDelegations.get(LEXER) gives list of the kinds of delegators
	 *  that can import lexers.
	 */
	@SuppressWarnings("serial")
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

	public BasicSemanticChecks(Grammar g, RuleCollector ruleCollector) {
		this.g = g;
		this.ruleCollector = ruleCollector;
		this.errMgr = g.tool.errMgr;
	}

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
	public void modeDef(GrammarAST m, GrammarAST ID) {
		checkMode(ID.token);
	}

	@Override
	public void discoverRule(RuleAST rule, GrammarAST ID,
							 List<GrammarAST> modifiers,
							 ActionAST arg, ActionAST returns,
							 GrammarAST thrws, GrammarAST options,
							 GrammarAST locals,
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
		@SuppressWarnings("unused")
		boolean ok = checkOptions(g.ast, ID.token, valueAST);
		//if ( ok ) g.ast.setOption(ID.getText(), value);
	}

	@Override
	public void elementOption(GrammarASTWithOptions elem, GrammarAST ID, GrammarAST valueAST) {
		String v = null;
		@SuppressWarnings("unused")
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
		if ( g.implicitLexer==null ) return;
		String fullyQualifiedName = nameToken.getInputStream().getSourceName();
		File f = new File(fullyQualifiedName);
		String fileName = f.getName();
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
		if ( g.isParser() &&
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

	/** Check option is appropriate for grammar, rule, subrule */
	boolean checkOptions(GrammarAST parent,
						 Token optionID,
						 GrammarAST valueAST)
	{
		boolean ok = true;
		if ( parent.getType()==ANTLRParser.BLOCK ) {
			if ( !Grammar.subruleOptions.contains(optionID.getText()) ) { // block
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
