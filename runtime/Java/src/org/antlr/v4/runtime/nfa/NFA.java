package org.antlr.v4.runtime.nfa;

import org.antlr.runtime.CharStream;

import java.util.Map;

/** http://swtch.com/~rsc/regexp/regexp2.html */
/*
for(;;){
	switch(pc->opcode){
	case Char:
		if(*sp != pc->c)
			return 0;
		pc++;
		sp++;
		continue;
	case Match:
		return 1;
	case Jmp:
		pc = pc->x;
		continue;
	case Split:
		if(recursiveloop(pc->x, sp))
			return 1;
		pc = pc->y;
		continue;
	}
	assert(0);
	return -1;
}
 */
public class NFA {
	public byte[] code;
	Map<String, Integer> ruleToAddr;

	public NFA(byte[] code, Map<String, Integer> ruleToAddr) {
		this.code = code;
		this.ruleToAddr = ruleToAddr;
	}

	public int exec(CharStream input, String ruleName) {
		return exec(input, ruleToAddr.get(ruleName));
	}

	public int exec(CharStream input) { return exec(input, 0); }

	public int exec(CharStream input, int ip) {
		while ( ip < code.length ) {
			int c = input.LA(1);
			trace(ip);
			short opcode = code[ip];
			ip++; // move to next instruction or first byte of operand
			switch (opcode) {
				case Bytecode.MATCH8 :
					if ( c != code[ip] ) return 0;
					ip++;
					input.consume();
					break;
				case Bytecode.MATCH16 :
					if ( c != getShort(code, ip) ) return 0;
					ip += 2;
					input.consume();
					break;
				case Bytecode.RANGE8 :
					if ( c<code[ip] || c>code[ip+1] ) return 0;
					ip += 2;
					input.consume();
					break;
				case Bytecode.RANGE16 :
					if ( c<getShort(code, ip) || c>getShort(code, ip+2) ) return 0;
					ip += 4;
					input.consume();
					break;
				case Bytecode.ACCEPT :
					int ruleIndex = getShort(code, ip);
					ip += 2;
					System.out.println("accept "+ruleIndex);
					return ruleIndex;
				case Bytecode.JMP :
					int target = getShort(code, ip);
					ip = target;
					continue;
				case Bytecode.SPLIT :
					int nopnds = getShort(code, ip);
					ip += 2;
					for (int i=1; i<=nopnds-1; i++) {
						int addr = getShort(code, ip);
						ip += 2;
						//System.out.println("try alt "+i+" at "+addr);
						int m = input.mark();
						int r = exec(input, addr);
						if ( r>0 ) { input.release(m); return r; }
						input.rewind(m);
					}
					// try final alternative (w/o recursion)
					int addr = getShort(code, ip);
					ip = addr;
					//System.out.println("try alt "+nopnds+" at "+addr);
					continue;
				default :
					throw new RuntimeException("invalid instruction @ "+ip+": "+opcode);
			}
		}
		return 0;
	}

	void trace(int ip) {
		String instr = Bytecode.disassembleInstruction(code, ip);
		System.out.println(instr);
	}

	public static int getShort(byte[] memory, int index) {
		return (memory[index]&0xFF) <<(8*1) | (memory[index+1]&0xFF); // prevent sign extension with mask
	}
}
