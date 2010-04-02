package org.antlr.v4.automata;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

/** A transition containing a set of values */
public class SetTransition extends Transition {
	public IntervalSet label;

	public SetTransition(IntervalSet label, NFAState target) {
		super(target);
		if ( label==null ) label = IntervalSet.of(Label.INVALID);
		this.label = label;
	}

	public IntervalSet label() { return label; }

	public int compareTo(Object o) {
		return 0;
	}

	//	public boolean intersect(Label other) {
//		if ( other.getClass() == SetTransition.class ) {
//			return label.and(((SetTransition)other).label).isNil();
//		}
//		return label.member(((AtomTransition)other).label);
//	}
	
	public int hashCode() {	return label.hashCode(); }

	public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this == o ) return true; // equals if same object
		if ( o.getClass() == AtomTransition.class ) {
			o = IntervalSet.of(((AtomTransition)o).label);
		}
		return this.label.equals(((SetTransition)o).label);
	}

	public String toString(Grammar g) {
		return label.toString(g);
	}

	public String toString() {
		return label.toString();
	}	
}
