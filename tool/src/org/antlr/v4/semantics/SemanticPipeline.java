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

import org.antlr.v4.analysis.LeftRecursiveRuleTransformer;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Tuple2;
import org.antlr.v4.tool.ErrorType;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.List;

/** Do as much semantic checking as we can and fill in grammar
 *  with rules, actions, and token definitions.
 *  The only side effects are in the grammar passed to process().
 *  We consume a bunch of memory here while we build up data structures
 *  to perform checking, but all of it goes away after this pipeline object
 *  gets garbage collected.
 *
 *  After this pipeline finishes, we can be sure that the grammar
 *  is syntactically correct and that it's semantically correct enough for us
 *  to attempt grammar analysis. We have assigned all token types.
 *  Note that imported grammars bring in token and rule definitions
 *  but only the root grammar and any implicitly created lexer grammar
 *  get their token definitions filled up. We are treating the
 *  imported grammars like includes.
 *
 *  The semantic pipeline works on root grammars (those that do the importing,
 *  if any). Upon entry to the semantic pipeline, all imported grammars
 *  should have been loaded into delegate grammar objects with their
 *  ASTs created.  The pipeline does the BasicSemanticChecks on the
 *  imported grammar before collecting symbols. We cannot perform the
 *  simple checks such as undefined rule until we have collected all
 *  tokens and rules from the imported grammars into a single collection.
 */
public class SemanticPipeline {
	public Grammar g;

	public SemanticPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		if ( g.ast==null ) return;

		// COLLECT RULE OBJECTS
		RuleCollector ruleCollector = new RuleCollector(g);
		ruleCollector.process(g.ast);

		// DO BASIC / EASY SEMANTIC CHECKS
		BasicSemanticChecks basics = new BasicSemanticChecks(g, ruleCollector);
		basics.process();

		// don't continue if we get errors in this basic check
		//if ( false ) return;

		// TRANSFORM LEFT-RECURSIVE RULES
		LeftRecursiveRuleTransformer lrtrans =
			new LeftRecursiveRuleTransformer(g.ast, ruleCollector.rules.values(), g.tool);
		lrtrans.translateLeftRecursiveRules();

		// STORE RULES IN GRAMMAR
		for (Rule r : ruleCollector.rules.values()) {
			g.defineRule(r);
		}

		// COLLECT SYMBOLS: RULES, ACTIONS, TERMINALS, ...
		SymbolCollector collector = new SymbolCollector(g);
		collector.process(g.ast);

		// CHECK FOR SYMBOL COLLISIONS
		SymbolChecks symcheck = new SymbolChecks(g, collector);
		symcheck.process(); // side-effect: strip away redef'd rules.

		for (GrammarAST a : collector.namedActions) {
			g.defineAction(a);
		}

		// LINK (outermost) ALT NODES WITH Alternatives
		for (Rule r : g.rules.values()) {
			for (int i=1; i<=r.numberOfAlts; i++) {
				r.alt[i].ast.alt = r.alt[i];
			}
		}

		// ASSIGN TOKEN TYPES
		g.importTokensFromTokensFile();
		if ( g.isLexer() ) {
			assignLexerTokenTypes(g, collector.tokensDefs);
		}
		else {
			assignTokenTypes(g, collector.tokensDefs,
							 collector.tokenIDRefs, collector.terminals);
		}

		// CHECK RULE REFS NOW (that we've defined rules in grammar)
		symcheck.checkRuleArgs(g, collector.rulerefs);
		identifyStartRules(collector);
		symcheck.checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// don't continue if we got symbol errors
		if ( g.tool.getNumErrors()>0 ) return;

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		UseDefAnalyzer.trackTokenRuleRefsInActions(g);
	}

	void identifyStartRules(SymbolCollector collector) {
		for (GrammarAST ref : collector.rulerefs) {
			String ruleName = ref.getText();
			Rule r = g.getRule(ruleName);
			if ( r!=null ) r.isStartRule = false;
		}
	}

	void assignLexerTokenTypes(Grammar g, List<GrammarAST> tokensDefs) {
		Grammar G = g.getOutermostGrammar(); // put in root, even if imported
		for (GrammarAST def : tokensDefs) {
			if ( def.getType()== ANTLRParser.ID ) G.defineTokenName(def.getText());
		}

		// DEFINE TOKEN TYPES FOR NONFRAGMENT RULES
		for (Rule r : g.rules.values()) {
			if ( !r.isFragment() ) G.defineTokenName(r.name);
		}

		// FOR ALL X : 'xxx'; RULES, DEFINE 'xxx' AS TYPE X
		List<Tuple2<GrammarAST,GrammarAST>> litAliases = Grammar.getStringLiteralAliasesFromLexerRules(g.ast);
		if ( litAliases!=null ) {
			for (Tuple2<GrammarAST,GrammarAST> pair : litAliases) {
				GrammarAST nameAST = pair.getItem1();
				GrammarAST litAST = pair.getItem2();
				if ( !G.stringLiteralToTypeMap.containsKey(litAST.getText()) ) {
					G.defineTokenAlias(nameAST.getText(), litAST.getText());
				}
				else {
					g.tool.errMgr.grammarError(ErrorType.ALIAS_REASSIGNMENT,
											   g.fileName, nameAST.token,
											   litAST.getText(), nameAST.getText());
				}
			}
		}

	}

	void assignTokenTypes(Grammar g, List<GrammarAST> tokensDefs,
						  List<GrammarAST> tokenIDs, List<GrammarAST> terminals)
	{
		//Grammar G = g.getOutermostGrammar(); // put in root, even if imported

		// DEFINE tokens { X='x'; } ALIASES
		for (GrammarAST alias : tokensDefs) {
			if ( alias.getType()== ANTLRParser.ASSIGN ) {
				String name = alias.getChild(0).getText();
				String lit = alias.getChild(1).getText();
				g.defineTokenAlias(name, lit);
			}
			else {
				g.defineTokenName(alias.getText());
			}
		}

		// DEFINE TOKEN TYPES FOR TOKEN REFS LIKE ID, INT
		for (GrammarAST idAST : tokenIDs) {
			if (g.getTokenType(idAST.getText()) == Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.IMPLICIT_TOKEN_DEFINITION, g.fileName, idAST.token, idAST.getText());
			}

			g.defineTokenName(idAST.getText());
		}

		// VERIFY TOKEN TYPES FOR STRING LITERAL REFS LIKE 'while', ';'
		for (GrammarAST termAST : terminals) {
			if (termAST.getType() != ANTLRParser.STRING_LITERAL) {
				continue;
			}

			if (g.getTokenType(termAST.getText()) == Token.INVALID_TYPE) {
				g.tool.errMgr.grammarError(ErrorType.IMPLICIT_STRING_DEFINITION, g.fileName, termAST.token, termAST.getText());
			}
		}

		g.tool.log("semantics", "tokens="+g.tokenNameToTypeMap);
        g.tool.log("semantics", "strings="+g.stringLiteralToTypeMap);
	}
}
