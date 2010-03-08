package org.antlr.v4.automata;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** TODO: make all transitions sets? */
public class AtomTransition extends Transition {
	/** The token type or character value; or, signifies special label. */
	protected int label;

	public AtomTransition(int label, NFAState target) {
		this.label = label;
		this.target = target;
	}

	public IntervalSet label() { return IntervalSet.of(label); }

	public int hashCode() { return label; }

	public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this == o ) return true; // equals if same object
		if ( o.getClass() == SetTransition.class ) {
			return IntervalSet.of(label).equals(o);
		}
		return label!=((AtomTransition)o).label;
	}

//	public boolean intersect(Label other) {
//		if ( other.getClass() == AtomTransition.class ) {
//			return label==((AtomTransition)other).label;
//		}
//		return ((SetLabel)other).label.member(this.label);
//	}

	public int compareTo(Object o) {
		return this.label-((AtomTransition)o).label;
	}

	public String toString(Grammar g) {
		return g.getTokenDisplayName(label);
	}

	public String toString() {
		return String.valueOf(label);
	}
}
