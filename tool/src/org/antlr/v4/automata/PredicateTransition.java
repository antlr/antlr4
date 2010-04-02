package org.antlr.v4.automata;

import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;

/** A tree of semantic predicates from the grammar AST if label==SEMPRED.
 *  In the NFA, labels will always be exactly one predicate, but the DFA
 *  may have to combine a bunch of them as it collects predicates from
 *  multiple NFA configurations into a single DFA state.
 */
public class PredicateTransition extends Transition {
	public SemanticContext semanticContext;

	public PredicateTransition(GrammarAST predicateASTNode, NFAState target) {
		super(target);
		this.semanticContext = new SemanticContext.Predicate(predicateASTNode);
	}

	public boolean isEpsilon() { return true; }
	
	public int compareTo(Object o) {
		return 0;
	}

	public int hashCode() {
		return semanticContext.hashCode();
	}

	public boolean equals(Object o) {
		if ( o==null ) {
			return false;
		}
		if ( this == o ) {
			return true; // equals if same object
		}
		if ( !(o instanceof PredicateTransition) ) {
			return false;
		}
		return semanticContext.equals(((PredicateTransition)o).semanticContext);
	}

	public String toString() {
		return semanticContext.toString();
	}

	public String toString(Grammar g) {
		return toString();
	}	
}
