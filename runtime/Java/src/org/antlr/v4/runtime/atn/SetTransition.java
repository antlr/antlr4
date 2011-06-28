package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.IntervalSet;

/** A transition containing a set of values */
public class SetTransition extends Transition {
	public IntervalSet label;

	public SetTransition(IntervalSet label, ATNState target) {
		super(target);
		if ( label==null ) label = IntervalSet.of(Token.INVALID_TYPE);
		this.label = label;
	}

	public SetTransition(ATNState target) {
		super(target);
	}

	public IntervalSet label() { return label; }

	public String toString() {
		return label.toString();
	}
}
