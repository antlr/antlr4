package org.antlr.v4.runtime.nfa;

import java.util.ArrayList;
import java.util.List;

/** */
public class Bytecode {
	public static final int MAX_OPNDS = 3; // Or single opnd indicating variable number
	public static final int OPND_SIZE_IN_BYTES = 2;
	public enum OperandType { NONE, CHAR, ADDR, INT, VARARGS }

	public static class Instruction {
		String name; // E.g., "load_str", "new"
		OperandType[] type = new OperandType[MAX_OPNDS];
		int n = 0;
		public Instruction(String name) {
			this(name,OperandType.NONE,OperandType.NONE,OperandType.NONE); n=0;
		}
		public Instruction(String name, OperandType a) {
			this(name,a,OperandType.NONE,OperandType.NONE); n=1;
		}
		public Instruction(String name, OperandType a, OperandType b) {
			this(name,a,b,OperandType.NONE); n=2;
		}
		public Instruction(String name, OperandType a, OperandType b, OperandType c) {
			this.name = name;
			type[0] = a;
			type[1] = b;
			type[2] = c;
			n = MAX_OPNDS;
		}
	}

	// don't use enum for efficiency; don't want code block to
	// be an array of objects (Bytecode[]). We want it to be byte[].

	// INSTRUCTION BYTECODES (byte is signed; use a short to keep 0..255)
	public static final short ACCEPT	= 1;
	public static final short JMP		= 2;
	public static final short SPLIT		= 3;
	public static final short MATCH		= 4;
	public static final short RANGE		= 5;

	/** Used for disassembly; describes instruction set */
	public static Instruction[] instructions = new Instruction[] {
		null, // <INVALID>
		new Instruction("accept", OperandType.INT), // index is the opcode
		new Instruction("jmp", OperandType.ADDR),
		new Instruction("split", OperandType.VARARGS),
		new Instruction("match", OperandType.CHAR),
		new Instruction("range", OperandType.CHAR, OperandType.CHAR)
	};

	public static String disassemble(byte[] code) {
		StringBuilder buf = new StringBuilder();
		int i=0;
		while (i<code.length) {
			i = disassembleInstruction(buf, code, i);
			buf.append('\n');
		}
		return buf.toString();
	}

	public static String disassembleInstruction(byte[] code, int ip) {
		StringBuilder buf = new StringBuilder();
		disassembleInstruction(buf, code, ip);
		return buf.toString();
	}

	public static int disassembleInstruction(StringBuilder buf, byte[] code, int ip) {
		int opcode = code[ip];
		if ( ip>=code.length ) {
			throw new IllegalArgumentException("ip out of range: "+ip);
		}
		Bytecode.Instruction I =
			Bytecode.instructions[opcode];
		if ( I==null ) {
			throw new IllegalArgumentException("no such instruction "+opcode+
				" at address "+ip);
		}
		String instrName = I.name;
		buf.append( String.format("%04d:\t%-14s", ip, instrName) );
		ip++;
		if ( I.n==0 ) {
			buf.append("  ");
			return ip;
		}
		List<String> operands = new ArrayList<String>();
		for (int i=0; i<I.n; i++) {
			int opnd = getShort(code, ip);
			ip += Bytecode.OPND_SIZE_IN_BYTES;
			switch ( I.type[i] ) {
				case CHAR :
					operands.add("'"+(char)opnd+"'");
					break;
				case VARARGS : // get n (opnd) operands
					int n = opnd;
					// operands.add(String.valueOf(n)); don't show n in varargs
					for (int j=0; j<n; j++) {
						operands.add(String.valueOf(getShort(code, ip)));
						ip += OPND_SIZE_IN_BYTES;
					}
					break;
				case INT :
				case ADDR :
				default:
					operands.add(String.valueOf(opnd));
					break;
			}
		}
		for (int i = 0; i < operands.size(); i++) {
			String s = operands.get(i);
			if ( i>0 ) buf.append(", ");
			buf.append( s );
		}
		return ip;
	}

	public static int getShort(byte[] memory, int index) {
		int b1 = memory[index++]&0xFF; // mask off sign-extended bits
		int b2 = memory[index++]&0xFF;
		int word = b1<<(8*1) | b2;
		return word;
	}	
}
