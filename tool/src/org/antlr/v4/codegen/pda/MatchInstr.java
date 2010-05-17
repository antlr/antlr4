package org.antlr.v4.codegen.pda;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class MatchInstr extends Instr {
	public Token token;
	public int c;
	public MatchInstr(Token t, int c) { super(); this.token = t; this.c = c; }
	public short opcode() { return charSize(c)==1? Bytecode.MATCH8:Bytecode.MATCH16; };
	public int nBytes() { return 1+charSize(c); }
	public void write(byte[] code) {
		super.write(code);
		if ( charSize(c)==1 ) code[addr+1] = (byte)(c&0xFF);
		else PDABytecodeGenerator.writeShort(code, addr+1, (short)c);
	}

	@Override
	public String toString() {
		return addr+":MatchInstr{" +
			   "c=" + c +
			   '}';
	}
}
