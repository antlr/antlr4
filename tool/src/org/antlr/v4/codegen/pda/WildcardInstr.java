package org.antlr.v4.codegen.pda;

import org.antlr.runtime.Token;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class WildcardInstr extends Instr {
	public Token token;
	public WildcardInstr(Token t) { super(); this.token = t; }
	public short opcode() { return Bytecode.WILDCARD; }
	public int nBytes() { return 1; }
}
