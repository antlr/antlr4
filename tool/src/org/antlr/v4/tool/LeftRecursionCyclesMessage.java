package org.antlr.v4.tool;

import java.util.Collection;

public class LeftRecursionCyclesMessage extends Message {
	public Collection cycles;

	public LeftRecursionCyclesMessage(Collection cycles) {
		super(ErrorType.LEFT_RECURSION_CYCLES, cycles);
		this.cycles = cycles;
	}
}
