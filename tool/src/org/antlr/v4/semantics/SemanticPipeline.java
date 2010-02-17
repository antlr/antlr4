package org.antlr.v4.semantics;

import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.BufferedTreeNodeStream;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.ASTVerifier;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.tool.*;

import java.util.List;

/** */
public class SemanticPipeline {
	public void process(Grammar g) {
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
		}

		// DO BASIC / EASY SEMANTIC CHECKS
		nodes.reset();
		BasicSemanticTriggers basics = new BasicSemanticTriggers(nodes,g);
		basics.downup(g.ast);

		// don't continue if we get errors in this basic check
		if ( false ) return;

		// TODO: can i move to Tool.process? why recurse here?
		// NOW DO BASIC / EASY SEMANTIC CHECKS FOR DELEGATES (IF ANY)
		if ( g.getImportedGrammars()!=null ) {
			for (Grammar d : g.getImportedGrammars()) {
				process(d);
			}
		}

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

		// CHECK RULE REFS NOW
		checkRuleArgs(g, collector.rulerefs);
		checkForQualifiedRuleIssues(g, collector.qualifiedRulerefs);

		// CHECK ATTRIBUTE EXPRESSIONS FOR SEMANTIC VALIDITY
		AttributeChecks.checkAllAttributeExpressions(g);

		// ASSIGN TOKEN TYPES
	}

	public void checkRuleArgs(Grammar g, List<GrammarAST> rulerefs) {
		if ( rulerefs==null ) return;
		for (GrammarAST ref : rulerefs) {
			String ruleName = ref.getText();
			Rule r = g.getRule(ruleName);
			if ( r==null && !ref.hasAncestor(ANTLRParser.DOT)) {
				// only give error for unqualified rule refs now
				ErrorManager.grammarError(ErrorType.UNDEFINED_RULE_REF,
										  g.fileName, ref.token, ruleName);
			}
			GrammarAST arg = (GrammarAST)ref.getChild(0);
			if ( arg!=null && r.args==null ) {
				ErrorManager.grammarError(ErrorType.RULE_HAS_NO_ARGS,
										  g.fileName, ref.token, ruleName);

			}
			else if ( arg==null && (r!=null&&r.args!=null) ) {
				ErrorManager.grammarError(ErrorType.MISSING_RULE_ARGS,
										  g.fileName, ref.token, ruleName);
			}
		}
	}

	public void checkForQualifiedRuleIssues(Grammar g, List<GrammarAST> qualifiedRuleRefs) {
		for (GrammarAST dot : qualifiedRuleRefs) {
			GrammarAST grammar = (GrammarAST)dot.getChild(0);
			GrammarAST rule = (GrammarAST)dot.getChild(1);
			System.out.println(grammar.getText()+"."+rule.getText());
			Grammar delegate = g.getImportedGrammar(grammar.getText());
			if ( delegate==null ) {
				ErrorManager.grammarError(ErrorType.NO_SUCH_GRAMMAR_SCOPE,
										  g.fileName, grammar.token, grammar.getText(),
										  rule.getText());
			}
			else {
				if ( g.getRule(grammar.getText(), rule.getText())==null ) {
					ErrorManager.grammarError(ErrorType.NO_SUCH_RULE_IN_SCOPE,
											  g.fileName, rule.token, grammar.getText(),
											  rule.getText());
				}
			}
		}
	}
}
