package org.antlr.v4.codegen.pda;

import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class RetInstr extends Instr {
	public short opcode() { return Bytecode.RET; }
	public int nBytes() { return 1; }
}
