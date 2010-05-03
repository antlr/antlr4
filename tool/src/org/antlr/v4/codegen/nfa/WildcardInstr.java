package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.runtime.nfa.Bytecode;

/** */
public class WildcardInstr extends Instr {
	public Token token;
	public WildcardInstr(Token t) { super(); this.token = t; }
	public short opcode() { return Bytecode.WILDCARD; }
	public int nBytes() { return 1; }
}
