package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;

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
		NFABytecodeGenerator.writeShort(code, addr+1, (short)rule.index);
		NFABytecodeGenerator.writeShort(code, addr+1+2, (short)gen.getSempredIndex(rule, token));
	}
	public String toString() { return addr+":SemPredInstr "+ predIndex; }
}
