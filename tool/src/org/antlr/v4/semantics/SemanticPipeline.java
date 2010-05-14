package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.parse.GrammarASTAdaptor;
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
			ErrorManager.fatalInternalError("bad grammar AST structure", re);
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

		// CHECK RULE REFS NOW (that we've defined rules in grammar)
		symcheck.checkRuleArgs(g, collector.rulerefs);
		identifyStartRules(collector);
		symcheck.checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// don't continue if we got symbol errors
		if ( g.tool.getNumErrors()>0 ) return;

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		// ASSIGN TOKEN TYPES
		if ( g.isLexer() ) assignLexerTokenTypes(g, collector);
		else assignTokenTypes(g, collector, symcheck);

		UseDefAnalyzer usedef = new UseDefAnalyzer();
		usedef.checkRewriteElementsPresentOnLeftSide(g, collector.rules);
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
		for (Rule r : g.rules.values()) {
			if ( !r.isFragment() ) G.defineTokenName(r.name);
		}
	}

	void assignTokenTypes(Grammar g, CollectSymbols collector, SymbolChecks symcheck) {
		if ( g.implicitLexerOwner!=null ) {
			// copy vocab from combined to implicit lexer
			g.importVocab(g.implicitLexerOwner);
			System.out.println("tokens="+g.tokenNameToTypeMap);
			System.out.println("strings="+g.stringLiteralToTypeMap);
		}
		else {
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
			Map<String,String> litAliases = Grammar.getStringLiteralAliasesFromLexerRules(g.ast);
			if ( litAliases!=null ) {
				for (String lit : litAliases.keySet()) {
					G.defineTokenAlias(litAliases.get(lit), lit);
				}
			}

			// DEFINE TOKEN TYPES FOR TOKEN REFS LIKE ID, INT
			for (String id : symcheck.tokenIDs) { G.defineTokenName(id); }

			// DEFINE TOKEN TYPES FOR STRING LITERAL REFS LIKE 'while', ';'
			for (String s : collector.strings) { G.defineStringLiteral(s); }
//			System.out.println("tokens="+G.tokenNameToTypeMap);
//			System.out.println("strings="+G.stringLiteralToTypeMap);
		}
	}
}
