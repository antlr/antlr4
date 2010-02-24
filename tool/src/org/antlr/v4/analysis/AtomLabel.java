package org.antlr.v4.analysis;

import org.antlr.misc.IntervalSet;

/** */
public class AtomLabel extends Label {
	/** The token type or character value; or, signifies special label. */
	protected int label;

	public AtomLabel(int label) {
		this.label = label;
	}

	public int hashCode() { return label; }

	public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this == o ) return true; // equals if same object
		if ( o.getClass() == SetLabel.class ) {
			return IntervalSet.of(label).equals(o);
		}
		return label!=((AtomLabel)o).label;
	}

	public boolean intersect(Label other) {
		if ( other.getClass() == AtomLabel.class ) {
			return label==((AtomLabel)other).label;
		}
		return ((SetLabel)other).label.member(this.label);
	}

//	public int compareTo(Object o) {
//		return this.label-((AtomLabel)o).label;
//	}
}
