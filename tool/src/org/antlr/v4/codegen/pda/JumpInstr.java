package org.antlr.v4.codegen.pda;

import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class JumpInstr extends Instr {
	public int target;

	public JumpInstr() {;}
	public JumpInstr(int target) {
		this.target = target;
	}

	public short opcode() { return Bytecode.JMP; };
	public int nBytes() { return 1+Bytecode.ADDR_SIZE; }
	public void write(byte[] code) {
		super.write(code);
		PDABytecodeGenerator.writeShort(code, addr+1, (short)target);
	}

	@Override
	public String toString() {
		return addr+":JumpInstr{" +
			   "target=" + target +
			   '}';
	}
}
