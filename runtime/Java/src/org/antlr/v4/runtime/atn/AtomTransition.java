package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

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

	public String toString() {
		return String.valueOf(label);
	}
}
