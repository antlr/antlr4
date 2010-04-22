package org.antlr.v4.codegen;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.runtime.nfa.Bytecode;
import org.antlr.v4.runtime.tree.TreeParser;

import java.util.ArrayList;
import java.util.List;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class NFABytecodeGenerator extends TreeParser {
	public abstract static class Instr {
		public short opcode;
		public int addr;
		public int nBytes;
		public Instr(short opcode, int nBytes) { this.opcode = opcode; this.nBytes = nBytes; }
		public void write(byte[] code) { code[addr] = (byte)opcode;	}
	}

	public static class MatchInstr extends Instr {
		Token token;
		char c;
		public MatchInstr(Token t, char c) { super(Bytecode.MATCH, 3); this.token = t; this.c = c; }
		public void write(byte[] code) {
			super.write(code);
			writeShort(code, addr+1, (short)c);
		}

		@Override
		public String toString() {
			return addr+":MatchInstr{" +
				   "c=" + c +
				   '}';
		}
	}

	public static class RangeInstr extends Instr {
		Token a, b;
		char start, stop;
		public RangeInstr(Token a, Token b) {
			super(Bytecode.RANGE, 1+2*Bytecode.OPND_SIZE_IN_BYTES);
			this.a = a;
			this.b = b;
			start = (char)Target.getCharValueFromGrammarCharLiteral(a.getText());
			stop = (char)Target.getCharValueFromGrammarCharLiteral(b.getText());
		}
		public void write(byte[] code) {
			super.write(code);
			writeShort(code, addr+1, (short)start);
			writeShort(code, addr+1+Bytecode.OPND_SIZE_IN_BYTES, (short)stop);
		}

		@Override
		public String toString() {
			return addr+":RangeInstr{"+start+".."+stop+"}";
		}
	}

	public static class AcceptInstr extends Instr {
		int ruleIndex;
		public AcceptInstr(int ruleIndex) {
			super(Bytecode.ACCEPT, 3);
			this.ruleIndex = ruleIndex;
		}
		public void write(byte[] code) {
			super.write(code);
			writeShort(code, addr+1, (short)ruleIndex);
		}
		public String toString() { return addr+":AcceptInstr "+ruleIndex; }
	}

	public static class JumpInstr extends Instr {
		int target;
		public JumpInstr() { super(Bytecode.JMP, 3); }
		public void write(byte[] code) {
			super.write(code);
			writeShort(code, addr+1, (short)target);
		}

		@Override
		public String toString() {
			return addr+":JumpInstr{" +
				   "target=" + target +
				   '}';
		}
	}

	public static class SplitInstr extends Instr {
		List<Integer> addrs = new ArrayList<Integer>();
		public SplitInstr(int nAlts) { super(Bytecode.SPLIT, 1+2+nAlts*2); }
		public void write(byte[] code) {
			super.write(code);
			int a = addr + 1;
			writeShort(code, a, (short)addrs.size());
			a += Bytecode.OPND_SIZE_IN_BYTES;
			for (int x : addrs) {
				writeShort(code, a, (short)x);
				a += Bytecode.OPND_SIZE_IN_BYTES;
			}
		}

		@Override
		public String toString() {
			return addr+":SplitInstr{" +
				   "addrs=" + addrs +
				   '}';
		}
	}

	public List<Instr> instrs = new ArrayList<Instr>();
	public int ip = 0; // where to write next

	public NFABytecodeGenerator(TreeNodeStream input) {
		super(input);
	}

	public NFABytecodeGenerator(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public void emit(Instr I) {
		I.addr = ip;
		ip += I.nBytes;
		instrs.add(I);
	}

	public void emitString(Token t) {
		String chars = Target.getStringFromGrammarStringLiteral(t.getText());
		for (char c : chars.toCharArray()) {
			emit(new MatchInstr(t, c));
		}
	}

	public byte[] getCode() {
		Instr last = instrs.get(instrs.size() - 1);
		int size = last.addr + last.nBytes;
		byte[] code = new byte[size];
		for (Instr I : instrs) {
			I.write(code);
		}
		return code;
	}

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}

	/* CODE TO GENERATE NFA BYTECODES
			// testing code gen concept
			GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
			for (Rule r : lg.modes.get(modeName)) {
				GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
				CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
				NFABytecodeTriggers gen = new NFABytecodeTriggers(nodes);
				try {
					gen.block();
					gen.emit(new NFABytecodeGenerator.AcceptInstr(r.index));
					System.out.println("code=\n"+gen.instrs);
					byte[] code = gen.getCode();
					System.out.println(Bytecode.disassemble(code));
					Interpreter in = new Interpreter(code);
					String s = "i";
					ANTLRStringStream input = new ANTLRStringStream(s);
					int rule = in.exec(input, 0);
					System.out.println(s+" matched rule "+rule+" leaving off at index="+input.index());
				}
				catch (Exception e){
					e.printStackTrace(System.err);
				}
			}

	 */

}
