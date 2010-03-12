package org.antlr.v4.automata;

import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.tool.Grammar;

public class PredicateEdge extends Edge {
	SemanticContext semanticContext;
	public PredicateEdge(SemanticContext semanticContext, DFAState target) {
		super(target);
		this.semanticContext = semanticContext;
	}

	public String toString(Grammar g) {
		return semanticContext.toString();
	}	
}
