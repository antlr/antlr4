package org.antlr.v4.codegen.pda;

import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class NotInstr extends Instr {
	public short opcode() { return Bytecode.NOT; }
	public int nBytes() { return 1; }	
}
