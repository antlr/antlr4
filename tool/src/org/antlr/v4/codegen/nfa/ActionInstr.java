package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class ActionInstr extends Instr {
	public int actionIndex;
	public Token token;
	public ActionInstr(Token token) {
		this.token = token;
	}
	public short opcode() { return Bytecode.ACTION; };
	public int nBytes() { return 1+2*2; }
	public void write(byte[] code) {
		super.write(code);
		NFABytecodeGenerator.writeShort(code, addr+1, (short)rule.index);
		NFABytecodeGenerator.writeShort(code, addr+1+2, (short)gen.getActionIndex(rule, token));
	}
	public String toString() { return addr+":ActionInstr "+actionIndex; }
}
