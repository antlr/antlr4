package org.antlr.v4.automata;

import org.antlr.v4.codegen.Target;

public class RangeTransition extends Transition {
	public int from;
	public int to;
	public RangeTransition(int from, int to, NFAState target) {
		super(target);
		this.from = from;
		this.to = to;
	}
	public int compareTo(Object o) {
		return 0;
	}

	@Override
	public String toString() {
		return Target.getANTLRCharLiteralForChar(from)+".."+
			   Target.getANTLRCharLiteralForChar(to);
	}
}
