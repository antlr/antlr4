package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;

/** */
public class SaveInstr extends Instr {
	public int labelIndex;
	public Token token;
	public SaveInstr(Token token) {
		this.token = token;
	}
	public short opcode() { return Bytecode.SAVE; };
	public int nBytes() { return 1+2; }
	public void write(byte[] code) {
		super.write(code);
		NFABytecodeGenerator.writeShort(code, addr+1, (short) labelIndex);
	}
	public String toString() { return addr+":SaveInstr "+ labelIndex; }
}
