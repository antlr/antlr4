package org.antlr.v4.automata;

import org.antlr.v4.misc.IntervalSet;

/** A label containing a set of values */
public class SetTransition extends Transition {
	/** A set of token types or character codes if label==SET */
	public IntervalSet label;

	public SetTransition(IntervalSet label) {
		if ( label==null ) {
			this.label = IntervalSet.of(Label.INVALID);
			return;
		}
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
}
