package org.antlr.v4.runtime.atn;

import org.antlr.v4.misc.*;

public class RangeTransition extends Transition {
	public int from;
	public int to;
	public RangeTransition(int from, int to, ATNState target) {
		super(target);
		this.from = from;
		this.to = to;
	}
	public RangeTransition(ATNState target) {
		super(target);
	}

	@Override
	public IntervalSet label() { return IntervalSet.of(from,to); }

	@Override
	public String toString() {
		return CharSupport.getANTLRCharLiteralForChar(from)+".."+
			   CharSupport.getANTLRCharLiteralForChar(to);
	}
}
