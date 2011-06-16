package org.antlr.v4.tool;

import java.util.Collection;

public class LeftRecursionCyclesMessage extends ANTLRMessage {
	public Collection cycles;

	public LeftRecursionCyclesMessage(String fileName, Collection cycles) {
		super(ErrorType.LEFT_RECURSION_CYCLES, cycles);
		this.cycles = cycles;
		this.fileName = fileName;
	}
}
