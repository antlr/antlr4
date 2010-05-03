package org.antlr.v4.codegen.nfa;

/** */
public abstract class Instr {
	public int addr;
	public abstract short opcode();
	public abstract int nBytes();
	public int charSize(int a, int b) { return Math.max(charSize(a), charSize(b)); }
	public int charSize(int c) {
		if ( c<=0xFF ) return 1;
		if ( c<=0xFFFF ) return 2;
		return 4;
	}
	public void write(byte[] code) { code[addr] = (byte)opcode();	}
}
