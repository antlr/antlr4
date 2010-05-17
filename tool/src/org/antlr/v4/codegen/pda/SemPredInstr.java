package org.antlr.v4.codegen.pda;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class SemPredInstr extends Instr {
	public int predIndex;
	public Token token;
	public SemPredInstr(Token token) {
		this.token = token;
	}
	public short opcode() { return Bytecode.SEMPRED; };
	public int nBytes() { return 1+2*2; }
	public void write(byte[] code) {
		super.write(code);
		PDABytecodeGenerator.writeShort(code, addr+1, (short)rule.index);
		PDABytecodeGenerator.writeShort(code, addr+1+2, (short)gen.getSempredIndex(rule, token));
	}
	public String toString() { return addr+":SemPredInstr "+ predIndex; }
}
