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

package org.antlr.v4.tool;

import org.antlr.runtime.*;
import org.antlr.runtime.misc.DoubleKeyMap;
import org.antlr.runtime.tree.*;
import org.antlr.v4.Tool;
import org.antlr.v4.parse.*;
import org.antlr.v4.tool.ast.*;

import java.util.*;

/** Handle left-recursion and block-set transforms */
public class GrammarTransformPipeline {
	public Grammar g;
	public Tool tool;

	public GrammarTransformPipeline(Grammar g, Tool tool) {
		this.g = g;
		this.tool = tool;
	}

	public void process() {
		GrammarRootAST ast = g.ast;
		if ( ast==null ) return;
		System.out.println("before: "+ast.toStringTree());

		if ( ast.grammarType==ANTLRParser.PARSER || ast.grammarType==ANTLRParser.COMBINED ) {
			translateLeftRecursiveRules(ast);
		}

		reduceBlocksToSets(ast);
		System.out.println("after: "+ast.toStringTree());
	}

	public void reduceBlocksToSets(GrammarRootAST ast) {
		org.antlr.runtime.tree.CommonTreeNodeStream nodes =
			new org.antlr.runtime.tree.CommonTreeNodeStream(ast);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		BlockSetTransformer transformer = new BlockSetTransformer(nodes, g);
		transformer.setTreeAdaptor(adaptor);
		transformer.downup(ast);
	}

	public void translateLeftRecursiveRules(GrammarRootAST ast) {
		String language = Grammar.getLanguageOption(ast);
		for (GrammarAST r : ast.getNodesWithType(ANTLRParser.RULE)) {
			String ruleName = r.getChild(0).getText();
			if ( !Character.isUpperCase(ruleName.charAt(0)) ) {
				if ( LeftRecursiveRuleAnalyzer.hasImmediateRecursiveRuleRefs(r, ruleName) ) {
					translateLeftRecursiveRule(ast, r, language);
				}
			}
		}
	}

	public void translateLeftRecursiveRule(GrammarRootAST ast,
										   GrammarAST ruleAST,
										   String language)
	{
		//System.out.println(ruleAST.toStringTree());
		TokenStream tokens = ast.tokens;
		Grammar g = ast.g;
		String ruleName = ruleAST.getChild(0).getText();
		LeftRecursiveRuleAnalyzer leftRecursiveRuleWalker =
			new LeftRecursiveRuleAnalyzer(tokens, ruleAST, tool, ruleName, language);
		boolean isLeftRec = false;
		try {
//			System.out.println("TESTING ---------------\n"+
//							   leftRecursiveRuleWalker.text(ruleAST));
			isLeftRec = leftRecursiveRuleWalker.rec_rule();
		}
		catch (RecognitionException re) {
			isLeftRec = false; // didn't match; oh well
		}
		if ( !isLeftRec ) return;

		// delete old rule
		GrammarAST RULES = (GrammarAST)ast.getFirstChildWithType(ANTLRParser.RULES);
		RULES.deleteChild(ruleAST);

		List<String> rules = new ArrayList<String>();
		rules.add( leftRecursiveRuleWalker.getArtificialPrecStartRule() ) ;

		String outputOption = ast.getOptionString("output");
		boolean buildAST = outputOption!=null && outputOption.equals("AST");

		rules.add( leftRecursiveRuleWalker.getArtificialOpPrecRule(buildAST) );
		rules.add( leftRecursiveRuleWalker.getArtificialPrimaryRule() );
		for (String ruleText : rules) {
//			System.out.println("created: "+ruleText);
			GrammarAST t = parseArtificialRule(g, ruleText);
			// insert into grammar tree
			RULES.addChild(t);
			System.out.println("added: "+t.toStringTree());
		}
	}

	public GrammarAST parseArtificialRule(final Grammar g, String ruleText) {
		ANTLRLexer lexer = new ANTLRLexer(new ANTLRStringStream(ruleText));
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ToolANTLRParser p = new ToolANTLRParser(tokens, tool);
		p.setTreeAdaptor(adaptor);
		try {
			ParserRuleReturnScope r = p.rule();
			GrammarAST tree = (GrammarAST) r.getTree();
			setGrammarPtr(g, tree);
			return tree;
		}
		catch (Exception e) {
			tool.errMgr.toolError(ErrorType.INTERNAL_ERROR,
								  "error parsing rule created during left-recursion detection: "+ruleText,
								  e);
		}
		return null;
	}

	public static void setGrammarPtr(final Grammar g, GrammarAST tree) {
		// ensure each node has pointer to surrounding grammar
		TreeVisitor v = new TreeVisitor(new GrammarASTAdaptor());
		v.visit(tree, new TreeVisitorAction() {
			public Object pre(Object t) { ((GrammarAST)t).g = g; return t; }
			public Object post(Object t) { return t; }
		});
	}

	/** Merge all the rules, token definitions, and named actions from
		imported grammars into the root grammar tree.  Perform:

	 	(tokens { X (= Y 'y')) + (tokens { Z )	->	(tokens { X (= Y 'y') Z)

	 	(@ members {foo}) + (@ members {bar})	->	(@ members {foobar})

	 	(RULES (RULE x y)) + (RULES (RULE z))	->	(RULES (RULE x y z))

	 	Rules in root prevent same rule from being appended to RULES node.

	 	The goal is a complete combined grammar so we can ignore subordinate
	 	grammars.
	 */
	public static void integrateImportedGrammars(Grammar rootGrammar) {
		List<Grammar> imports = rootGrammar.getAllImportedGrammars();
		if ( imports==null ) return;

		GrammarAST root = rootGrammar.ast;
		GrammarAST id = (GrammarAST) root.getChild(0);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(id.token.getInputStream());

	 	GrammarAST tokensRoot = (GrammarAST)root.getFirstChildWithType(ANTLRParser.TOKENS);

		List<GrammarAST> actionRoots = root.getNodesWithType(ANTLRParser.AT);

		// Compute list of rules in root grammar and ensure we have a RULES node
		GrammarAST RULES = (GrammarAST)root.getFirstChildWithType(ANTLRParser.RULES);
		Set<String> rootRuleNames = new HashSet<String>();
		if ( RULES==null ) { // no rules in root, make RULES node, hook in
			RULES = (GrammarAST)adaptor.create(ANTLRParser.RULES, "RULES");
			RULES.g = rootGrammar;
			root.addChild(RULES);
		}
		else {
			// make list of rules we have in root grammar
			List<GrammarAST> rootRules = RULES.getNodesWithType(ANTLRParser.RULE);
			for (GrammarAST r : rootRules) rootRuleNames.add(r.getChild(0).getText());
		}

		for (Grammar imp : imports) {
			// COPY TOKENS
			GrammarAST imp_tokensRoot = (GrammarAST)imp.ast.getFirstChildWithType(ANTLRParser.TOKENS);
			if ( imp_tokensRoot!=null ) {
				System.out.println("imported tokens: "+imp_tokensRoot.getChildren());
				if ( tokensRoot==null ) {
					tokensRoot = (GrammarAST)adaptor.create(ANTLRParser.TOKENS, "TOKENS");
					tokensRoot.g = rootGrammar;
					root.insertChild(1, tokensRoot); // ^(GRAMMAR ID TOKENS...)
				}
				tokensRoot.addChildren(imp_tokensRoot.getChildren());
			}

			List<GrammarAST> all_actionRoots = new ArrayList<GrammarAST>();
			List<GrammarAST> imp_actionRoots = imp.ast.getNodesWithType(ANTLRParser.AT);
			if ( actionRoots!=null ) all_actionRoots.addAll(actionRoots);
			all_actionRoots.addAll(imp_actionRoots);

			// COPY ACTIONS
			if ( imp_actionRoots!=null ) {
				DoubleKeyMap<String, String, GrammarAST> namedActions =
					new DoubleKeyMap<String, String, GrammarAST>();

				System.out.println("imported actions: "+imp_actionRoots);
				for (GrammarAST at : all_actionRoots) {
					String scopeName = rootGrammar.getDefaultActionScope();
					GrammarAST scope, name, action;
					if ( at.getChildCount()>2 ) { // must have a scope
						scope = (GrammarAST)at.getChild(1);
						scopeName = scope.getText();
						name = (GrammarAST)at.getChild(1);
						action = (GrammarAST)at.getChild(2);
					}
					else {
						name = (GrammarAST)at.getChild(0);
						action = (GrammarAST)at.getChild(1);
					}
					GrammarAST prevAction = namedActions.get(scopeName, name.getText());
					if ( prevAction==null ) {
						namedActions.put(scopeName, name.getText(), action);
					}
					else {
						if ( prevAction.g == at.g ) {
							rootGrammar.tool.errMgr.grammarError(ErrorType.ACTION_REDEFINITION,
												at.g.fileName, name.token, name.getText());
						}
						else {
							String s1 = prevAction.getText();
							s1 = s1.substring(1, s1.length()-1);
							String s2 = action.getText();
							s2 = s2.substring(1, s2.length()-1);
							String combinedAction = "{"+s1 + '\n'+ s2+"}";
							prevAction.token.setText(combinedAction);
						}
					}
				}
				// at this point, we have complete list of combined actions,
				// some of which are already living in root grammar.
				// Merge in any actions not in root grammar into root's tree.
				for (String scopeName : namedActions.keySet()) {
					for (String name : namedActions.keySet(scopeName)) {
						GrammarAST action = namedActions.get(scopeName, name);
						System.out.println(action.g.name+" "+scopeName+":"+name+"="+action.getText());
						if ( action.g != rootGrammar ) {
							root.insertChild(1, action.getParent());
						}
					}
				}
			}

			// COPY RULES
			List<GrammarAST> rules = imp.ast.getNodesWithType(ANTLRParser.RULE);
			if ( rules!=null ) {
				for (GrammarAST r : rules) {
					System.out.println("imported rule: "+r.toStringTree());
					String name = r.getChild(0).getText();
					boolean rootAlreadyHasRule = rootRuleNames.contains(name);
					if ( !rootAlreadyHasRule ) {
						RULES.addChild(r); // merge in if not overridden
						rootRuleNames.add(name);
					}
				}
			}

			GrammarAST optionsRoot = (GrammarAST)imp.ast.getFirstChildWithType(ANTLRParser.OPTIONS);
			if ( optionsRoot!=null ) {
				rootGrammar.tool.errMgr.grammarError(ErrorType.OPTIONS_IN_DELEGATE,
									optionsRoot.g.fileName, optionsRoot.token, imp.name);
			}
		}
		System.out.println("Grammar: "+rootGrammar.ast.toStringTree());
	}

	/** Build lexer grammar from combined grammar that looks like:
	 *
	 *  (COMBINED_GRAMMAR A
	 *      (tokens { X (= Y 'y'))
	 *      (OPTIONS (= x 'y'))
	 *      (@ members {foo})
	 *      (@ lexer header {package jj;})
	 *      (RULES (RULE .+)))
	 *
	 *  Move rules and actions to new tree, don't dup. Split AST apart.
	 *  We'll have this Grammar share token symbols later; don't generate
	 *  tokenVocab or tokens{} section.
	 *
	 *  Side-effects: it removes children from GRAMMAR & RULES nodes
	 *                in combined AST.  Anything cut out is dup'd before
	 *                adding to lexer to avoid "who's ur daddy" issues
	 */
	public static GrammarRootAST extractImplicitLexer(Grammar combinedGrammar) {
		GrammarRootAST combinedAST = combinedGrammar.ast;
		//System.out.println("before="+combinedAST.toStringTree());
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(combinedAST.token.getInputStream());
		List<GrammarAST> elements = combinedAST.getChildren();

		// MAKE A GRAMMAR ROOT and ID
		String lexerName = combinedAST.getChild(0).getText()+"Lexer";
		GrammarRootAST lexerAST =
		    new GrammarRootAST(new CommonToken(ANTLRParser.GRAMMAR,"LEXER_GRAMMAR"));
		lexerAST.grammarType = ANTLRParser.LEXER;
		lexerAST.token.setInputStream(combinedAST.token.getInputStream());
		lexerAST.addChild((GrammarAST)adaptor.create(ANTLRParser.ID, lexerName));

		// MOVE OPTIONS
		GrammarAST optionsRoot =
			(GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.OPTIONS);
		if ( optionsRoot!=null ) {
			GrammarAST lexerOptionsRoot = (GrammarAST)adaptor.dupNode(optionsRoot);
			lexerAST.addChild(lexerOptionsRoot);
			List<GrammarAST> options = optionsRoot.getChildren();
			for (GrammarAST o : options) {
				String optionName = o.getChild(0).getText();
				if ( !Grammar.doNotCopyOptionsToLexer.contains(optionName) ) {
					lexerOptionsRoot.addChild((Tree)adaptor.dupTree(o));
				}
			}
		}

		// MOVE lexer:: actions
		List<GrammarAST> actionsWeMoved = new ArrayList<GrammarAST>();
		for (GrammarAST e : elements) {
			if ( e.getType()==ANTLRParser.AT ) {
				if ( e.getChild(0).getText().equals("lexer") ) {
					lexerAST.addChild((Tree)adaptor.dupTree(e));
					actionsWeMoved.add(e);
				}
			}
		}

		for (GrammarAST r : actionsWeMoved) {
			combinedAST.deleteChild( r );
		}

		GrammarAST combinedRulesRoot =
			(GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.RULES);
		if ( combinedRulesRoot==null ) return lexerAST;

		// MOVE lexer rules

		GrammarAST lexerRulesRoot =
			(GrammarAST)adaptor.create(ANTLRParser.RULES, "RULES");
		lexerAST.addChild(lexerRulesRoot);
		List<GrammarAST> rulesWeMoved = new ArrayList<GrammarAST>();
		List<GrammarASTWithOptions> rules = combinedRulesRoot.getChildren();
		if ( rules!=null ) {
			for (GrammarASTWithOptions r : rules) {
				String ruleName = r.getChild(0).getText();
				if ( Character.isUpperCase(ruleName.charAt(0)) ) {
					lexerRulesRoot.addChild((Tree)adaptor.dupTree(r));
					rulesWeMoved.add(r);
				}
			}
		}
		int nLexicalRules = rulesWeMoved.size();
		for (GrammarAST r : rulesWeMoved) {
			combinedRulesRoot.deleteChild( r );
		}

		// Will track 'if' from IF : 'if' ; rules to avoid defining new token for 'if'
		Map<String,String> litAliases =
			Grammar.getStringLiteralAliasesFromLexerRules(lexerAST);

		Set<String> stringLiterals = combinedGrammar.getStringLiterals();
		// add strings from combined grammar (and imported grammars) into lexer
		// put them first as they are keywords; must resolve ambigs to these rules
//		System.out.println("strings from parser: "+stringLiterals);
		for (String lit : stringLiterals) {
			if ( litAliases!=null && litAliases.containsKey(lit) ) continue; // already has rule
			// create for each literal: (RULE <uniquename> (BLOCK (ALT <lit>))
			String rname = combinedGrammar.getStringLiteralLexerRuleName(lit);
			// can't use wizard; need special node types
			GrammarAST litRule = new RuleAST(ANTLRParser.RULE);
			BlockAST blk = new BlockAST(ANTLRParser.BLOCK);
			AltAST alt = new AltAST(ANTLRParser.ALT);
			TerminalAST slit = new TerminalAST(new CommonToken(ANTLRParser.STRING_LITERAL, lit));
			alt.addChild(slit);
			blk.addChild(alt);
			CommonToken idToken = new CommonToken(ANTLRParser.ID, rname);
			litRule.addChild(new TerminalAST(idToken));
			litRule.addChild(blk);
			lexerRulesRoot.insertChild(0, litRule);        // add first
//			lexerRulesRoot.getChildren().add(0, litRule);
			lexerRulesRoot.freshenParentAndChildIndexes(); // reset indexes and set litRule parent
		}

		// TODO: take out after stable if slow
		lexerAST.sanityCheckParentAndChildIndexes();
		combinedAST.sanityCheckParentAndChildIndexes();
//		System.out.println(combinedAST.toTokenString());

		System.out.println("after extract implicit lexer ="+combinedAST.toStringTree());
		System.out.println("lexer ="+lexerAST.toStringTree());

		if ( lexerRulesRoot.getChildCount()==0 )	return null;
		return lexerAST;
	}
}
