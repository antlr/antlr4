package org.antlr.v4.codegen.nfa;

import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;

/** */
public class JumpInstr extends Instr {
	public int target;
	public short opcode() { return Bytecode.JMP; };
	public int nBytes() { return 1+Bytecode.ADDR_SIZE; }
	public void write(byte[] code) {
		super.write(code);
		NFABytecodeGenerator.writeShort(code, addr+1, (short)target);
	}

	@Override
	public String toString() {
		return addr+":JumpInstr{" +
			   "target=" + target +
			   '}';
	}
}
