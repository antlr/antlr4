package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;

/** */
public class LabelInstr extends Instr {
	public int labelIndex;
	public Token token;
	public LabelInstr(Token token) {
		this.token = token;
	}
	public short opcode() { return Bytecode.LABEL; };
	public int nBytes() { return 1+2; }
	public void write(byte[] code) {
		super.write(code);
		labelIndex = gen.getLabelIndex(rule, token.getText());
		NFABytecodeGenerator.writeShort(code, addr+1, (short)labelIndex);
	}
	public String toString() { return addr+":LabelInstr "+ labelIndex; }
}
