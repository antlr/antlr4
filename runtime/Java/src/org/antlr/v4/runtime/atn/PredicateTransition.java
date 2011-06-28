package org.antlr.v4.runtime.atn;

/** TODO: this is old comment:
 *  A tree of semantic predicates from the grammar AST if label==SEMPRED.
 *  In the ATN, labels will always be exactly one predicate, but the DFA
 *  may have to combine a bunch of them as it collects predicates from
 *  multiple ATN configurations into a single DFA state.
 */
public class PredicateTransition extends Transition {
	public int ruleIndex;
	public int predIndex;

	public PredicateTransition(ATNState target) {
		super(target);
	}

	public PredicateTransition(ATNState target, int ruleIndex, int predIndex) {
		super(target);
		this.ruleIndex = ruleIndex;
		this.predIndex = predIndex;
	}

	public boolean isEpsilon() { return true; }

	public String toString() {
		return "pred-"+ruleIndex+":"+predIndex;
	}

}
