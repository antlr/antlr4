/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.tool;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeVisitor;
import org.antlr.runtime.tree.TreeVisitorAction;
import org.antlr.v4.Tool;
import org.antlr.v4.analysis.LeftRecursiveRuleTransformer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.BlockSetTransformer;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.parse.GrammarToken;
import org.antlr.v4.runtime.misc.DoubleKeyMap;
import org.antlr.v4.runtime.misc.Pair;
import org.antlr.v4.tool.ast.AltAST;
import org.antlr.v4.tool.ast.BlockAST;
import org.antlr.v4.tool.ast.GrammarAST;
import org.antlr.v4.tool.ast.GrammarASTWithOptions;
import org.antlr.v4.tool.ast.GrammarRootAST;
import org.antlr.v4.tool.ast.RuleAST;
import org.antlr.v4.tool.ast.TerminalAST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Handle left-recursion and block-set transforms */
public class GrammarTransformPipeline {
	public Grammar g;
	public Tool tool;

	public GrammarTransformPipeline(Grammar g, Tool tool) {
		this.g = g;
		this.tool = tool;
	}

	public void process() {
		GrammarRootAST root = g.ast;
		if ( root==null ) return;
        tool.log("grammar", "before: "+root.toStringTree());

        integrateImportedGrammars(g);
		reduceBlocksToSets(root);
        expandParameterizedLoops(root);

        tool.log("grammar", "after: "+root.toStringTree());
	}

	public void reduceBlocksToSets(GrammarAST root) {
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(new GrammarASTAdaptor(), root);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		BlockSetTransformer transformer = new BlockSetTransformer(nodes, g);
		transformer.setTreeAdaptor(adaptor);
		transformer.downup(root);
	}

    /** Find and replace
     *      ID*[','] with ID (',' ID)*
     *      ID+[','] with ID (',' ID)+
     *      (x {action} y)+[','] with x {action} y (',' x {action} y)+
     *
     *  Parameter must be a token.
     *  todo: do we want?
     */
    public void expandParameterizedLoops(GrammarAST root) {
        TreeVisitor v = new TreeVisitor(new GrammarASTAdaptor());
        v.visit(root, new TreeVisitorAction() {
            @Override
            public Object pre(Object t) {
                if ( ((GrammarAST)t).getType() == 3 ) {
                    return expandParameterizedLoop((GrammarAST)t);
                }
                return t;
            }
            @Override
            public Object post(Object t) { return t; }
        });
    }

    public GrammarAST expandParameterizedLoop(GrammarAST t) {
        // todo: update grammar, alter AST
        return t;
    }

    /** Utility visitor that sets grammar ptr in each node */
	public static void setGrammarPtr(final Grammar g, GrammarAST tree) {
		if ( tree==null ) return;
		// ensure each node has pointer to surrounding grammar
		TreeVisitor v = new TreeVisitor(new GrammarASTAdaptor());
		v.visit(tree, new TreeVisitorAction() {
			@Override
			public Object pre(Object t) { ((GrammarAST)t).g = g; return t; }
			@Override
			public Object post(Object t) { return t; }
		});
	}

	public static void augmentTokensWithOriginalPosition(final Grammar g, GrammarAST tree) {
		if ( tree==null ) return;

		List<GrammarAST> optionsSubTrees = tree.getNodesWithType(ANTLRParser.ELEMENT_OPTIONS);
		for (int i = 0; i < optionsSubTrees.size(); i++) {
			GrammarAST t = optionsSubTrees.get(i);
			CommonTree elWithOpt = t.parent;
			if ( elWithOpt instanceof GrammarASTWithOptions ) {
				Map<String, GrammarAST> options = ((GrammarASTWithOptions) elWithOpt).getOptions();
				if ( options.containsKey(LeftRecursiveRuleTransformer.TOKENINDEX_OPTION_NAME) ) {
					GrammarToken newTok = new GrammarToken(g, elWithOpt.getToken());
					newTok.originalTokenIndex = Integer.valueOf(options.get(LeftRecursiveRuleTransformer.TOKENINDEX_OPTION_NAME).getText());
					elWithOpt.token = newTok;

					GrammarAST originalNode = g.ast.getNodeWithTokenIndex(newTok.getTokenIndex());
					if (originalNode != null) {
						// update the AST node start/stop index to match the values
						// of the corresponding node in the original parse tree.
						elWithOpt.setTokenStartIndex(originalNode.getTokenStartIndex());
						elWithOpt.setTokenStopIndex(originalNode.getTokenStopIndex());
					}
					else {
						// the original AST node could not be located by index;
						// make sure to assign valid values for the start/stop
						// index so toTokenString will not throw exceptions.
						elWithOpt.setTokenStartIndex(newTok.getTokenIndex());
						elWithOpt.setTokenStopIndex(newTok.getTokenIndex());
					}
				}
			}
		}
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
	public void integrateImportedGrammars(Grammar rootGrammar) {
		List<Grammar> imports = rootGrammar.getAllImportedGrammars();
		if ( imports==null ) return;

		GrammarAST root = rootGrammar.ast;
		GrammarAST id = (GrammarAST) root.getChild(0);
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(id.token.getInputStream());

	 	GrammarAST tokensRoot = (GrammarAST)root.getFirstChildWithType(ANTLRParser.TOKENS_SPEC);

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
			GrammarAST imp_tokensRoot = (GrammarAST)imp.ast.getFirstChildWithType(ANTLRParser.TOKENS_SPEC);
			if ( imp_tokensRoot!=null ) {
				rootGrammar.tool.log("grammar", "imported tokens: "+imp_tokensRoot.getChildren());
				if ( tokensRoot==null ) {
					tokensRoot = (GrammarAST)adaptor.create(ANTLRParser.TOKENS_SPEC, "TOKENS");
					tokensRoot.g = rootGrammar;
					root.insertChild(1, tokensRoot); // ^(GRAMMAR ID TOKENS...)
				}
				tokensRoot.addChildren(Arrays.asList(imp_tokensRoot.getChildren().toArray(new Tree[0])));
			}

			List<GrammarAST> all_actionRoots = new ArrayList<GrammarAST>();
			List<GrammarAST> imp_actionRoots = imp.ast.getAllChildrenWithType(ANTLRParser.AT);
			if ( actionRoots!=null ) all_actionRoots.addAll(actionRoots);
			all_actionRoots.addAll(imp_actionRoots);

			// COPY ACTIONS
			if ( imp_actionRoots!=null ) {
				DoubleKeyMap<String, String, GrammarAST> namedActions =
					new DoubleKeyMap<String, String, GrammarAST>();

				rootGrammar.tool.log("grammar", "imported actions: "+imp_actionRoots);
				for (GrammarAST at : all_actionRoots) {
					String scopeName = rootGrammar.getDefaultActionScope();
					GrammarAST scope, name, action;
					if ( at.getChildCount()>2 ) { // must have a scope
						scope = (GrammarAST)at.getChild(0);
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
						rootGrammar.tool.log("grammar", action.g.name+" "+scopeName+":"+name+"="+action.getText());
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
					rootGrammar.tool.log("grammar", "imported rule: "+r.toStringTree());
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
				// suppress the warning if the options match the options specified
				// in the root grammar
				// https://github.com/antlr/antlr4/issues/707

				boolean hasNewOption = false;
				for (Map.Entry<String, GrammarAST> option : imp.ast.getOptions().entrySet()) {
					String importOption = imp.ast.getOptionString(option.getKey());
					if (importOption == null) {
						continue;
					}

					String rootOption = rootGrammar.ast.getOptionString(option.getKey());
					if (!importOption.equals(rootOption)) {
						hasNewOption = true;
						break;
					}
				}

				if (hasNewOption) {
					rootGrammar.tool.errMgr.grammarError(ErrorType.OPTIONS_IN_DELEGATE,
										optionsRoot.g.fileName, optionsRoot.token, imp.name);
				}
			}
		}
		rootGrammar.tool.log("grammar", "Grammar: "+rootGrammar.ast.toStringTree());
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
	 *  tokenVocab or tokens{} section.  Copy over named actions.
	 *
	 *  Side-effects: it removes children from GRAMMAR & RULES nodes
	 *                in combined AST.  Anything cut out is dup'd before
	 *                adding to lexer to avoid "who's ur daddy" issues
	 */
	public GrammarRootAST extractImplicitLexer(Grammar combinedGrammar) {
		GrammarRootAST combinedAST = combinedGrammar.ast;
		//tool.log("grammar", "before="+combinedAST.toStringTree());
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor(combinedAST.token.getInputStream());
		GrammarAST[] elements = combinedAST.getChildren().toArray(new GrammarAST[0]);

		// MAKE A GRAMMAR ROOT and ID
		String lexerName = combinedAST.getChild(0).getText()+"Lexer";
		GrammarRootAST lexerAST =
		    new GrammarRootAST(new CommonToken(ANTLRParser.GRAMMAR, "LEXER_GRAMMAR"), combinedGrammar.ast.tokenStream);
		lexerAST.grammarType = ANTLRParser.LEXER;
		lexerAST.token.setInputStream(combinedAST.token.getInputStream());
		lexerAST.addChild((GrammarAST)adaptor.create(ANTLRParser.ID, lexerName));

		// COPY OPTIONS
		GrammarAST optionsRoot =
			(GrammarAST)combinedAST.getFirstChildWithType(ANTLRParser.OPTIONS);
		if ( optionsRoot!=null && optionsRoot.getChildCount()!=0 ) {
			GrammarAST lexerOptionsRoot = (GrammarAST)adaptor.dupNode(optionsRoot);
			lexerAST.addChild(lexerOptionsRoot);
			GrammarAST[] options = optionsRoot.getChildren().toArray(new GrammarAST[0]);
			for (GrammarAST o : options) {
				String optionName = o.getChild(0).getText();
				if ( Grammar.lexerOptions.contains(optionName) &&
					 !Grammar.doNotCopyOptionsToLexer.contains(optionName) )
				{
					GrammarAST optionTree = (GrammarAST)adaptor.dupTree(o);
					lexerOptionsRoot.addChild(optionTree);
					lexerAST.setOption(optionName, (GrammarAST)optionTree.getChild(1));
				}
			}
		}

		// COPY all named actions, but only move those with lexer:: scope
		List<GrammarAST> actionsWeMoved = new ArrayList<GrammarAST>();
		for (GrammarAST e : elements) {
			if ( e.getType()==ANTLRParser.AT ) {
				lexerAST.addChild((Tree)adaptor.dupTree(e));
				if ( e.getChild(0).getText().equals("lexer") ) {
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
		GrammarASTWithOptions[] rules;
		if (combinedRulesRoot.getChildCount() > 0) {
			rules = combinedRulesRoot.getChildren().toArray(new GrammarASTWithOptions[0]);
		}
		else {
			rules = new GrammarASTWithOptions[0];
		}

		for (GrammarASTWithOptions r : rules) {
			String ruleName = r.getChild(0).getText();
			if (Grammar.isTokenName(ruleName)) {
				lexerRulesRoot.addChild((Tree)adaptor.dupTree(r));
				rulesWeMoved.add(r);
			}
		}
		for (GrammarAST r : rulesWeMoved) {
			combinedRulesRoot.deleteChild( r );
		}

		// Will track 'if' from IF : 'if' ; rules to avoid defining new token for 'if'
		List<Pair<GrammarAST,GrammarAST>> litAliases =
			Grammar.getStringLiteralAliasesFromLexerRules(lexerAST);

		Set<String> stringLiterals = combinedGrammar.getStringLiterals();
		// add strings from combined grammar (and imported grammars) into lexer
		// put them first as they are keywords; must resolve ambigs to these rules
//		tool.log("grammar", "strings from parser: "+stringLiterals);
		int insertIndex = 0;
		nextLit:
		for (String lit : stringLiterals) {
			// if lexer already has a rule for literal, continue
			if ( litAliases!=null ) {
				for (Pair<GrammarAST,GrammarAST> pair : litAliases) {
					GrammarAST litAST = pair.b;
					if ( lit.equals(litAST.getText()) ) continue nextLit;
				}
			}
			// create for each literal: (RULE <uniquename> (BLOCK (ALT <lit>))
			String rname = combinedGrammar.getStringLiteralLexerRuleName(lit);
			// can't use wizard; need special node types
			GrammarAST litRule = new RuleAST(ANTLRParser.RULE);
			BlockAST blk = new BlockAST(ANTLRParser.BLOCK);
			AltAST alt = new AltAST(ANTLRParser.ALT);
			TerminalAST slit = new TerminalAST(new CommonToken(ANTLRParser.STRING_LITERAL, lit));
			alt.addChild(slit);
			blk.addChild(alt);
			CommonToken idToken = new CommonToken(ANTLRParser.TOKEN_REF, rname);
			litRule.addChild(new TerminalAST(idToken));
			litRule.addChild(blk);
			lexerRulesRoot.insertChild(insertIndex, litRule);
//			lexerRulesRoot.getChildren().add(0, litRule);
			lexerRulesRoot.freshenParentAndChildIndexes(); // reset indexes and set litRule parent

			// next literal will be added after the one just added
			insertIndex++;
		}

		// TODO: take out after stable if slow
		lexerAST.sanityCheckParentAndChildIndexes();
		combinedAST.sanityCheckParentAndChildIndexes();
//		tool.log("grammar", combinedAST.toTokenString());

        combinedGrammar.tool.log("grammar", "after extract implicit lexer ="+combinedAST.toStringTree());
        combinedGrammar.tool.log("grammar", "lexer ="+lexerAST.toStringTree());

		if ( lexerRulesRoot.getChildCount()==0 )	return null;
		return lexerAST;
	}

}
