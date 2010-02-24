package org.antlr.v4.analysis;

import org.antlr.v4.misc.IntSet;
import org.antlr.v4.misc.IntervalSet;

/** A label containing a set of values */
public class SetLabel extends Label {
	/** A set of token types or character codes if label==SET */
	// TODO: try IntervalSet for everything
	protected IntSet label;

	public SetLabel(IntSet label) {
		if ( label==null ) {
			this.label = IntervalSet.of(INVALID);
			return;
		}
		this.label = label;
	}

	public boolean intersect(Label other) {
		if ( other.getClass() == SetLabel.class ) {
			return label.and(((SetLabel)other).label).isNil();
		}
		return label.member(((AtomLabel)other).label);
	}
	
	public int hashCode() {	return label.hashCode(); }

	public boolean equals(Object o) {
		if ( o==null ) return false;
		if ( this == o ) return true; // equals if same object
		if ( o.getClass() == AtomLabel.class ) {
			o = IntervalSet.of(((AtomLabel)o).label);
		}
		return this.label.equals(((SetLabel)o).label);
	}
}
