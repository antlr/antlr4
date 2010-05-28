package org.antlr.v4.codegen.pda;

import org.antlr.v4.codegen.PDABytecodeGenerator;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.runtime.pda.Bytecode;

/** */
public class SetInstr extends Instr {
	public IntervalSet set;
	public int setIndex;

	public SetInstr(IntervalSet set) { this.set = set; }
	public short opcode() { return Bytecode.SET; }
	public int nBytes() { return 1+2; }
	public void write(byte[] code) {
		super.write(code);
		setIndex = gen.getSetIndex(set);
		PDABytecodeGenerator.writeShort(code, addr+1, (short)setIndex);
	}
}
