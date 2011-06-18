package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.Grammar;

public class WildcardTransition extends Transition {
	public WildcardTransition(ATNState target) { super(target); }

	@Override
	public String toString(Grammar g) {
		return ".";
	}
}
