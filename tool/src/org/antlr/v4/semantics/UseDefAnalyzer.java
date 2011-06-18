package org.antlr.v4.semantics;

import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.tool.*;

import java.util.*;

/** Look for errors and deadcode stuff */
public class UseDefAnalyzer {
	public static void checkRewriteElementsPresentOnLeftSide(Grammar g) {
		for (Rule r : g.rules.values()) {
			for (int a=1; a<=r.numberOfAlts; a++) {
				Alternative alt = r.alt[a];
				for (GrammarAST e : alt.rewriteElements) {
					if ( !(alt.ruleRefs.containsKey(e.getText()) ||
						   g.getTokenType(e.getText())!= Token.INVALID_TYPE ||
						   alt.labelDefs.containsKey(e.getText()) ||
						   e.getText().equals(r.name)) ) // $r ok in rule r
					{
						g.tool.errMgr.grammarError(ErrorType.REWRITE_ELEMENT_NOT_PRESENT_ON_LHS,
												   g.fileName, e.token, e.getText());
					}
				}
			}
		}
	}

	// side-effect: updates Alternative with refs in actions
	public static void trackTokenRuleRefsInActions(Grammar g) {
		for (Rule r : g.rules.values()) {
			for (int i=1; i<=r.numberOfAlts; i++) {
				Alternative alt = r.alt[i];
				for (ActionAST a : alt.actions) {
					ActionSniffer sniffer =	new ActionSniffer(g, r, alt, a, a.token);
					sniffer.examineAction();
				}
			}
		}
	}

	/** Find all rules reachable from r directly or indirectly for all r in g */
	public static Map<Rule, Set<Rule>> getRuleDependencies(Grammar g) {
		return getRuleDependencies(g, g.rules.values());
	}

	public static Map<Rule, Set<Rule>> getRuleDependencies(LexerGrammar g, String modeName) {
		return getRuleDependencies(g, g.modes.get(modeName));
	}

	public static Map<Rule, Set<Rule>> getRuleDependencies(Grammar g, Collection<Rule> rules) {
		Map<Rule, Set<Rule>> dependencies = new HashMap<Rule, Set<Rule>>();

		for (Rule r : rules) {
			List<GrammarAST> tokenRefs = r.ast.getNodesWithType(ANTLRParser.TOKEN_REF);
			for (GrammarAST tref : tokenRefs) {
				Set<Rule> calls = dependencies.get(r);
				if ( calls==null ) {
					calls = new HashSet<Rule>();
					dependencies.put(r, calls);
				}
				calls.add(g.getRule(tref.getText()));
			}
		}

		return dependencies;
	}

}
