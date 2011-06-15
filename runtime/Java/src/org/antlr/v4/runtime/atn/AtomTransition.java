package org.antlr.v4.runtime.atn;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** TODO: make all transitions sets? no, should remove set edges */
public class AtomTransition extends Transition {
	/** The token type or character value; or, signifies special label. */
	public int label;

	public AtomTransition(int label, ATNState target) {
		this.label = label;
		this.target = target;
	}

	public AtomTransition(ATNState target) {
		super(target);
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

	@Override
	public String toString(Grammar g) {
		if (g!=null ) return g.getTokenDisplayName(label);
		return toString();
	}

	public String toString() {
		return String.valueOf(label);
	}
}
