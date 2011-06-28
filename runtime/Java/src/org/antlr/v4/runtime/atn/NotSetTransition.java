package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

public class NotSetTransition extends SetTransition {
	public NotSetTransition(IntervalSet label, ATNState target) {
		super(label, target);
	}

	public NotSetTransition(ATNState target) {
		super(target);
	}

	@Override
	public String toString() {
		return '~'+super.toString();
	}
}
