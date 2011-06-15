package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.Grammar;

public class NotAtomTransition extends AtomTransition {
	public NotAtomTransition(int label, ATNState target) {
		super(label, target);
	}
	public NotAtomTransition(ATNState target) {
		super(target);
	}

	@Override
	public String toString(Grammar g) {
		return '~'+super.toString(g);
	}
}
