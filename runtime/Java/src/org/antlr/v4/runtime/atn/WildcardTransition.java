package org.antlr.v4.runtime.atn;

import org.antlr.v4.tool.Grammar;

public class WildcardTransition extends Transition {
	public WildcardTransition(ATNState target) { super(target); }
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String toString(Grammar g) {
		return ".";
	}
}
