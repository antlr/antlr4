package org.antlr.v4.codegen.nfa;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class RangeInstr extends Instr {
	public Token start, stop;
	public int a, b;
	public RangeInstr(Token start, Token stop) {
		this.start = start;
		this.stop = stop;
		a = (char)CharSupport.getCharValueFromGrammarCharLiteral(start.getText());
		b = (char)CharSupport.getCharValueFromGrammarCharLiteral(stop.getText());
	}
	public short opcode() { return charSize(a, b)==1? Bytecode.RANGE8:Bytecode.RANGE16; };
	public int nBytes() { return 1+2*charSize(a, b); }
	public void write(byte[] code) {
		super.write(code);
		if ( charSize(a,b)==1 ) {
			code[addr+1] = (byte)(a&0xFF);
			code[addr+2] = (byte)(b&0xFF);
		}
		else {
			NFABytecodeGenerator.writeShort(code, addr+1, (short)a);
			NFABytecodeGenerator.writeShort(code, addr+1+charSize(a,b), (short)b);
		}
	}

	@Override
	public String toString() {
		return addr+":RangeInstr{"+ a +".."+ b +"}";
	}
}
