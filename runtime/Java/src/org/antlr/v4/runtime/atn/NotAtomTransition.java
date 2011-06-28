package org.antlr.v4.runtime.atn;

public class NotAtomTransition extends AtomTransition {
	public NotAtomTransition(int label, ATNState target) {
		super(label, target);
	}
	public NotAtomTransition(ATNState target) {
		super(target);
	}

	public String toString() {
		return '~'+super.toString();
	}
}
