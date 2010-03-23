package org.antlr.v4.analysis;

import org.antlr.v4.automata.NFAState;

public class RecursionOverflowSignal extends RuntimeException {
	int altNum;
	int depth;
	NFAState state;
	public RecursionOverflowSignal(int altNum, int depth, NFAState state) {
		this.altNum = altNum;
		this.depth = depth;
		this.state = state;
	}
}
