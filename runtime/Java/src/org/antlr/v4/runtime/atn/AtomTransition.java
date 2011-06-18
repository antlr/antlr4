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

	@Override
	public String toString(Grammar g) {
		if (g!=null ) return g.getTokenDisplayName(label);
		return toString();
	}

	public String toString() {
		return String.valueOf(label);
	}
}
