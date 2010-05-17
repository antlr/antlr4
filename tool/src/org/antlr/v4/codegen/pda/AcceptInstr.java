package org.antlr.v4.codegen.pda;

import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class AcceptInstr extends Instr {
	public int ruleIndex;
	public AcceptInstr(int ruleIndex) {
		this.ruleIndex = ruleIndex;
	}
	public short opcode() { return Bytecode.ACCEPT; };
	public int nBytes() { return 1+2; }
	public void write(byte[] code) {
		super.write(code);
		PDABytecodeGenerator.writeShort(code, addr+1, (short)ruleIndex);
	}
	public String toString() { return addr+":AcceptInstr "+ruleIndex; }
}
