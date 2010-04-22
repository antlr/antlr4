package org.antlr.v4.runtime.nfa;

import org.antlr.runtime.CharStream;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class Interpreter {
	byte[] code;
	public Interpreter(byte[] code) { this.code = code; }

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
	public int exec(CharStream input, int ip) {
		while ( ip < code.length ) {
			int c = input.LA(1);
			trace(ip);
			short opcode = code[ip];
			ip++; // move to next instruction or first byte of operand
			switch (opcode) {
				case Bytecode.MATCH :
					int o = getShort(code, ip);
					ip += 2;
					if ( c != o ) return 0;
					input.consume();
					break;
				case Bytecode.RANGE :
					int from = getShort(code, ip);
					ip += 2;
					int to = getShort(code, ip);
					ip += 2;
					if ( c<from || c>to ) return 0;
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
						System.out.println("try alt "+i+" at "+addr);
						int m = input.mark();
						int r = exec(input, addr);
						if ( r>0 ) { input.release(m); return r; }
						input.rewind(m);
					}
					// try final alternative (w/o recursion)
					int addr = getShort(code, ip);
					ip = addr;
					System.out.println("try alt "+nopnds+" at "+addr);
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
        int b1 = memory[index++]&0xFF; // mask off sign-extended bits
        int b2 = memory[index++]&0xFF;
        return b1<<(8*1) | b2;
    }
}
