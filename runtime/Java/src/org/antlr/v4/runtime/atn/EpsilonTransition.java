package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.Grammar;

public class EpsilonTransition extends Transition {
	public EpsilonTransition(ATNState target) { super(target); }

	public boolean isEpsilon() { return true; }

	@Override
	public String toString(Grammar g) {
		return "epsilon";
	}
}
