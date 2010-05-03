package org.antlr.v4.codegen.nfa;

import org.antlr.v4.runtime.nfa.Bytecode;

/** */
public class RetInstr extends Instr {
	public short opcode() { return Bytecode.RET; }
	public int nBytes() { return 1; }
}
