package org.antlr.v4.runtime.pda;

import org.antlr.runtime.Token;

import java.util.ArrayList;
import java.util.List;

/** */
public class Bytecode {
	public static final int MAX_OPNDS = 3; // Or single opnd indicating variable number
	public static final int ADDR_SIZE = 2;
	public enum OperandType {
		NONE(0), BYTE(1), CHAR(2), ADDR(ADDR_SIZE), SHORT(2), INT(4), VARARGS(0);
		public int sizeInBytes;
		OperandType(int sizeInBytes) { this.sizeInBytes = sizeInBytes; }
	}

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
	public static final short ACCEPT	 = 1;
	public static final short JMP		 = 2;
	public static final short SPLIT		 = 3;
	public static final short MATCH8	 = 4;
	public static final short MATCH16	 = 5;
	public static final short RANGE8	 = 6;
	public static final short RANGE16	 = 7;
	public static final short WILDCARD	 = 8;
	public static final short SET	     = 9;
	public static final short CALL		 = 10; // JMP with a push
	public static final short RET		 = 11; // an accept instr for fragment rules
	public static final short LABEL		 = 12;
	public static final short SAVE		 = 13;
	public static final short SEMPRED	 = 14;
	public static final short ACTION	 = 15;
	public static final short NOT	     = 16; // not next match instr

	/** Used for disassembly; describes instruction set */
	public static Instruction[] instructions = new Instruction[] {
		null, // <INVALID>
		new Instruction("accept", OperandType.SHORT), // index is the opcode
		new Instruction("jmp", OperandType.ADDR),
		new Instruction("split", OperandType.VARARGS),
		new Instruction("match8", OperandType.BYTE),
		new Instruction("match16", OperandType.CHAR),
		new Instruction("range8", OperandType.BYTE, OperandType.BYTE),
		new Instruction("range16", OperandType.CHAR, OperandType.CHAR),
		new Instruction("wildcard"),
		new Instruction("set", OperandType.SHORT),
		new Instruction("call", OperandType.ADDR),
		new Instruction("ret"),
		new Instruction("label", OperandType.SHORT),
		new Instruction("save", OperandType.SHORT),
		new Instruction("sempred", OperandType.SHORT, OperandType.SHORT), // sempred ruleIndex, predIndex
		new Instruction("action", OperandType.SHORT, OperandType.SHORT), // action ruleIndex, actionIndex
		new Instruction("not"),
	};

	public static String disassemble(byte[] code, int start, boolean operandsAreChars) {
		StringBuilder buf = new StringBuilder();
		int i=start;
		while (i<code.length) {
			i = disassembleInstruction(buf, code, i, operandsAreChars);
			buf.append('\n');
		}
		return buf.toString();
	}

	public static String disassemble(byte[] code) { return disassemble(code, 0, true); }

	public static String disassemble(byte[] code, boolean operandsAreChars) {
		return disassemble(code, 0, operandsAreChars);
	}

	public static String disassembleInstruction(byte[] code, int ip, boolean operandsAreChars) {
		StringBuilder buf = new StringBuilder();
		disassembleInstruction(buf, code, ip, operandsAreChars);
		return buf.toString();
	}

	public static int disassembleInstruction(StringBuilder buf, byte[] code, int ip, boolean operandsAreChars) {
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
		if ( I.n==1 && I.type[0]==OperandType.VARARGS) { // get n (opnd) operands
			int n = getShort(code, ip);
			ip += 2;
			// operands.add(String.valueOf(n)); don't show n in varargs
			for (int j=1; j<=n; j++) {
				operands.add(String.valueOf(getShort(code, ip)));
				ip += ADDR_SIZE; // VARARGS only works on address for now
			}
		}
		else {
			for (int i=0; i<I.n; i++) {
				switch ( I.type[i] ) {
					case NONE:
						break;
					case BYTE:
						if ( operandsAreChars ) operands.add(quotedCharLiteral((char)code[ip]));
						else operands.add(String.valueOf(code[ip]));
						break;
					case CHAR :
						if ( operandsAreChars ) operands.add(quotedCharLiteral(getShort(code, ip)));
						else operands.add(String.valueOf(getShort(code, ip)));
						break;
					case INT :
						if ( operandsAreChars ) operands.add(quotedCharLiteral(getInt(code, ip)));
						else operands.add(String.valueOf(getInt(code, ip)));
					case SHORT :
					case ADDR :
						operands.add(String.valueOf(getShort(code, ip)));
						break;
					default :
						System.err.println("invalid opnd type: "+I.type[i]);
				}
				ip += I.type[i].sizeInBytes;
			}
		}
		for (int i = 0; i < operands.size(); i++) {
			String s = operands.get(i);
			if ( i>0 ) buf.append(", ");
			buf.append( s );
		}
		return ip;
	}

	public static int getInt(byte[] memory, int index) {
		int b1 = memory[index++]&0xFF; // high byte
		int b2 = memory[index++]&0xFF;
		int b3 = memory[index++]&0xFF;
		int b4 = memory[index++]&0xFF; // low byte
		return b1<<(8*3) | b2<<(8*2) | b3<<(8*1) | b4;
	}

	public static int getShort(byte[] memory, int index) {
		int b1 = memory[index++]&0xFF; // mask off sign-extended bits
		int b2 = memory[index++]&0xFF;
		return b1<<(8*1) | b2;
	}

	public static String LiteralCharValueEscape[] = new String[255];

	static {
		LiteralCharValueEscape['\n'] = "\\n";
		LiteralCharValueEscape['\r'] = "\\r";
		LiteralCharValueEscape['\t'] = "\\t";
		LiteralCharValueEscape['\b'] = "\\b";
		LiteralCharValueEscape['\f'] = "\\f";
		LiteralCharValueEscape['\\'] = "\\\\";
		LiteralCharValueEscape['\''] = "\\'";		
	}
	
	/** Return a string representing the escaped char for code c.  E.g., If c
	 *  has value 0x100, you will get "\u0100".  ASCII gets the usual
	 *  char (non-hex) representation.  Control characters are spit out
	 *  as unicode.
	 */
	public static String quotedCharLiteral(int c) {
		if ( c== Token.EOF ) return "'<EOF>'";
		if ( c<LiteralCharValueEscape.length && LiteralCharValueEscape[c]!=null ) {
			return '\''+LiteralCharValueEscape[c]+'\'';
		}
		if ( Character.UnicodeBlock.of((char)c)==Character.UnicodeBlock.BASIC_LATIN &&
			 !Character.isISOControl((char)c) ) {
			if ( c=='\\' ) {
				return "'\\\\'";
			}
			if ( c=='\'') {
				return "'\\''";
			}
			return '\''+Character.toString((char)c)+'\'';
		}
		// turn on the bit above max "\uFFFF" value so that we pad with zeros
		// then only take last 4 digits
		String hex = Integer.toHexString(c|0x10000).toUpperCase().substring(1,5);
		String unicodeStr = "'\\u"+hex+"'";
		return unicodeStr;
	}

	
}
