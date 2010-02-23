package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.analysis.Label;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.tool.*;

import java.util.List;
import java.util.Map;

/** */
public class SemanticPipeline {
	public void process(Grammar g) {
		if ( g.ast==null ) return;
		
		// VALIDATE AST STRUCTURE
		// use buffered node stream as we will look around in stream
		// to give good error messages.
		// TODO: send parse errors to buffer not stderr
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		BufferedTreeNodeStream nodes =
			new BufferedTreeNodeStream(adaptor,g.ast);
		ASTVerifier walker = new ASTVerifier(nodes);
		try {walker.grammarSpec();}
		catch (RecognitionException re) {
			ErrorManager.internalError("bad grammar AST structure", re);
			return; // don't process; will get internal errors
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
		if ( false ) return;

		// STORE RULES/ACTIONS/SCOPES IN GRAMMAR
		for (Rule r : collector.rules) g.defineRule(r);
		for (AttributeDict s : collector.scopes) g.defineScope(s);
		for (GrammarAST a : collector.actions) g.defineAction(a);

		// CHECK RULE REFS NOW (that we've defined rules in grammar)
		symcheck.checkRuleArgs(g, collector.rulerefs);
		symcheck.checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// don't continue if we get symbol errors
		if ( false ) return;

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		// ASSIGN TOKEN TYPES
		assignTokenTypes(g, collector, symcheck);

		// TODO: move to a use-def or deadcode eliminator
		checkRewriteElementsPresentOnLeftSide(g, collector.rules);
	}

	public void assignTokenTypes(Grammar g, CollectSymbols collector, SymbolChecks symcheck) {
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
			System.out.println("tokens="+G.tokenNameToTypeMap);
			System.out.println("strings="+G.stringLiteralToTypeMap);
		}
	}

	public void checkRewriteElementsPresentOnLeftSide(Grammar g, List<Rule> rules) {
		for (Rule r : rules) {
			for (int a=1; a<=r.numberOfAlts; a++) {
				Alternative alt = r.alt[a];
				for (GrammarAST e : alt.rewriteElements) {
					if ( !(alt.ruleRefs.containsKey(e.getText()) ||
						   g.getTokenType(e.getText())!= Label.INVALID ||
						   alt.labelDefs.containsKey(e.getText()) ||
						   e.getText().equals(r.name)) ) // $r ok in rule r
					{
						ErrorManager.grammarError(ErrorType.REWRITE_ELEMENT_NOT_PRESENT_ON_LHS,
												  g.fileName, e.token, e.getText());
					}
				}
			}
		}
	}
}
