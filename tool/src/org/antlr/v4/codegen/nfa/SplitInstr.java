package org.antlr.v4.codegen.nfa;

import org.antlr.v4.codegen.NFABytecodeGenerator;
import org.antlr.v4.runtime.nfa.Bytecode;

import java.util.ArrayList;
import java.util.List;

/** */
public class SplitInstr extends Instr {
	public List<Integer> addrs = new ArrayList<Integer>();
	public int nAlts;
	public SplitInstr(int nAlts) { this.nAlts = nAlts; }
	public short opcode() { return Bytecode.SPLIT; };
	public int nBytes() { return 1+2+nAlts*Bytecode.ADDR_SIZE; }
	public void write(byte[] code) {
		super.write(code);
		int a = addr + 1;
		NFABytecodeGenerator.writeShort(code, a, (short)addrs.size());
		a += 2;
		for (int x : addrs) {
			NFABytecodeGenerator.writeShort(code, a, (short)x);
			a += Bytecode.ADDR_SIZE;
		}
	}

	@Override
	public String toString() {
		return addr+":SplitInstr{" +
			   "addrs=" + addrs +
			   '}';
	}
}
