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

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.parse.*;
import org.antlr.v4.tool.*;

import java.util.Map;

/** Do as much semantic checking as we can and fill in grammar
 *  with rules, dynamic scopes, actions, and token definitions.
 *  The only side effects are in the grammar pass to process().
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
 *  imported grammars like includes (the generated code treats them
 *  as separate objects, however).
 */
public class SemanticPipeline {
	public Grammar g;

	public SemanticPipeline(Grammar g) {
		this.g = g;
	}

	public void process() {
		if ( g.ast==null ) return;

		// VALIDATE AST STRUCTURE
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		// use buffered node stream as we will look around in stream
		// to give good error messages.
		BufferedTreeNodeStream nodes =
			new BufferedTreeNodeStream(adaptor,g.ast);
		ASTVerifier walker = new ASTVerifier(nodes);
		try {walker.grammarSpec();}
		catch (RecognitionException re) {
			ErrorManager.fatalInternalError("bad grammar AST structure: "+
											g.ast.toStringTree(),
											re);
		}

		// DO BASIC / EASY SEMANTIC CHECKS
		nodes.reset();
		BasicSemanticTriggers basics = new BasicSemanticTriggers(nodes,g);
		basics.downup(g.ast);

		// don't continue if we get errors in this basic check
		if ( false ) return;

		// COLLECT SYMBOLS: RULES, ACTIONS, TERMINALS, ...
		nodes.reset();
		CollectSymbols collector = new CollectSymbols(nodes,g);
		collector.downup(g.ast); // no side-effects; compute lists

		// CHECK FOR SYMBOL COLLISIONS
		SymbolChecks symcheck = new SymbolChecks(g, collector);
		symcheck.examine(); // side-effect: strip away redef'd rules.

		// don't continue if we get symbol errors
		//if ( ErrorManager.getNumErrors()>0 ) return;
		// hmm...we don't get missing arg errors and such if we bail out here

		// STORE RULES/ACTIONS/SCOPES IN GRAMMAR
		for (Rule r : collector.rules) g.defineRule(r);
		for (AttributeDict s : collector.scopes) g.defineScope(s);
		for (GrammarAST a : collector.actions) g.defineAction(a);

		// LINK ALT NODES WITH Alternatives
		for (Rule r : g.rules.values()) {
			for (int i=1; i<=r.numberOfAlts; i++) {
				r.alt[i].ast.alt = r.alt[i];
			}
		}

		// CHECK RULE REFS NOW (that we've defined rules in grammar)
		symcheck.checkRuleArgs(g, collector.rulerefs);
		identifyStartRules(collector);
		symcheck.checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// don't continue if we got symbol errors
		if ( g.tool.getNumErrors()>0 ) return;

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		// ASSIGN TOKEN TYPES
		String vocab = g.getOption("tokenVocab");
		if ( vocab!=null ) {
			TokenVocabParser vparser = new TokenVocabParser(g.tool, vocab);
			Map<String,Integer> tokens = vparser.load();
			System.out.println("tokens="+tokens);
			for (String t : tokens.keySet()) {
				if ( t.charAt(0)=='\'' ) g.defineStringLiteral(t, tokens.get(t));
				else g.defineTokenName(t, tokens.get(t));
			}
		}
		if ( g.isLexer() ) assignLexerTokenTypes(g, collector);
		else assignTokenTypes(g, collector, symcheck);

		UseDefAnalyzer.checkRewriteElementsPresentOnLeftSide(g);
		UseDefAnalyzer.trackTokenRuleRefsInActions(g);
	}

	void identifyStartRules(CollectSymbols collector) {
		for (GrammarAST ref : collector.rulerefs) {
			String ruleName = ref.getText();
			Rule r = g.getRule(ruleName);
			if ( r!=null ) r.isStartRule = false;
		}
	}

	void assignLexerTokenTypes(Grammar g, CollectSymbols collector) {
		Grammar G = g.getOutermostGrammar(); // put in root, even if imported
		for (GrammarAST def : collector.tokensDefs) {
			if ( def.getType()== ANTLRParser.ID ) G.defineTokenName(def.getText());
		}

		// DEFINE TOKEN TYPES FOR NONFRAGMENT RULES
		for (Rule r : g.rules.values()) {
			if ( !r.isFragment() ) G.defineTokenName(r.name);
		}

		// FOR ALL X : 'xxx'; RULES, DEFINE 'xxx' AS TYPE X
		Map<String,String> litAliases = Grammar.getStringLiteralAliasesFromLexerRules(g.ast);
		if ( litAliases!=null ) {
			for (String lit : litAliases.keySet()) {
				G.defineTokenAlias(litAliases.get(lit), lit);
			}
		}

	}

	void assignTokenTypes(Grammar g, CollectSymbols collector, SymbolChecks symcheck) {
		Grammar G = g.getOutermostGrammar(); // put in root, even if imported

		// DEFINE tokens { X='x'; } ALIASES
		for (GrammarAST alias : collector.tokensDefs) {
			if ( alias.getType()== ANTLRParser.ASSIGN ) {
				String name = alias.getChild(0).getText();
				String lit = alias.getChild(1).getText();
				G.defineTokenAlias(name, lit);
			}
		}

		// DEFINE TOKEN TYPES FOR X : 'x' ; RULES
		/* done by previous import
		   Map<String,String> litAliases = Grammar.getStringLiteralAliasesFromLexerRules(g.ast);
		   if ( litAliases!=null ) {
			   for (String lit : litAliases.keySet()) {
				   G.defineTokenAlias(litAliases.get(lit), lit);
			   }
		   }
		   */

		// DEFINE TOKEN TYPES FOR TOKEN REFS LIKE ID, INT
		for (String id : symcheck.tokenIDs) { G.defineTokenName(id); }

		// DEFINE TOKEN TYPES FOR STRING LITERAL REFS LIKE 'while', ';'
		for (String s : collector.strings) { G.defineStringLiteral(s); }
		System.out.println("tokens="+G.tokenNameToTypeMap);
		System.out.println("strings="+G.stringLiteralToTypeMap);
	}
}
