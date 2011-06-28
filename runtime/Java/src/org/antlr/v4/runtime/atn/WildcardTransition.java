package org.antlr.v4.runtime.atn;

public class WildcardTransition extends Transition {
	public WildcardTransition(ATNState target) { super(target); }

	public String toString() {
		return ".";
	}
}
