package org.antlr.v4.semantics;

import org.antlr.v4.automata.Label;
import org.antlr.v4.tool.*;

import java.util.List;

/** Look for errors and deadcode stuff */
public class UseDefAnalyzer {
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
						g.tool.errMgr.grammarError(ErrorType.REWRITE_ELEMENT_NOT_PRESENT_ON_LHS,
												   g.fileName, e.token, e.getText());
					}
				}
			}
		}
	}

	// side-effect: updates Alternative with refs in actions
	public void trackTokenRuleRefsInActions(Grammar g) {
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

}
