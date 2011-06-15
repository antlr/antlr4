package org.antlr.v4.runtime.atn;

import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.GrammarAST;

/** TODO: this is old comment:
 *  A tree of semantic predicates from the grammar AST if label==SEMPRED.
 *  In the ATN, labels will always be exactly one predicate, but the DFA
 *  may have to combine a bunch of them as it collects predicates from
 *  multiple ATN configurations into a single DFA state.
 */
public class PredicateTransition extends Transition {
	public int ruleIndex;
	public int predIndex;
	public GrammarAST predAST;
	public SemanticContext semanticContext;

	public PredicateTransition(GrammarAST predicateASTNode, ATNState target) {
		super(target);
		this.predAST = predicateASTNode;
		this.semanticContext = new SemanticContext.Predicate(predicateASTNode);
	}

	public PredicateTransition(ATNState target, int ruleIndex, int predIndex) {
		super(target);
		this.ruleIndex = ruleIndex;
		this.predIndex = predIndex;
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
		if ( semanticContext!=null ) return semanticContext.toString();
		if ( predAST!=null ) return predAST.getText();
		return "pred-"+ruleIndex+":"+predIndex;
	}

	public String toString(Grammar g) {
		return toString();
	}
}
