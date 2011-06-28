package org.antlr.v4.runtime.atn;

public class EpsilonTransition extends Transition {
	public EpsilonTransition(ATNState target) { super(target); }

	public boolean isEpsilon() { return true; }

	public String toString() {
		return "epsilon";
	}
}
