package org.antlr.v4.codegen;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.runtime.nfa.Bytecode;
import org.antlr.v4.runtime.nfa.NFA;
import org.antlr.v4.runtime.tree.TreeParser;
import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.LexerGrammar;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class NFABytecodeGenerator extends TreeParser {
	public abstract static class Instr {
		public int addr;
		public abstract short opcode();
		public abstract int nBytes();
		public int charSize(int a, int b) { return Math.max(charSize(a), charSize(b)); }
		public int charSize(int c) {
			if ( c<=0xFF ) return 1;
			if ( c<=0xFFFF ) return 2;
			return 4;
		}
		public void write(byte[] code) { code[addr] = (byte)opcode();	}
	}

	public static class MatchInstr extends Instr {
		Token token;
		int c;
		public MatchInstr(Token t, int c) { super(); this.token = t; this.c = c; }
		public short opcode() { return charSize(c)==1?Bytecode.MATCH8:Bytecode.MATCH16; };
		public int nBytes() { return 1+charSize(c); }
		public void write(byte[] code) {
			super.write(code);
			if ( charSize(c)==1 ) code[addr+1] = (byte)(c&0xFF);
			else writeShort(code, addr+1, (short)c);
		}

		@Override
		public String toString() {
			return addr+":MatchInstr{" +
				   "c=" + c +
				   '}';
		}
	}

	public static class RangeInstr extends Instr {
		Token start, stop;
		int a, b;
		public RangeInstr(Token start, Token stop) {
			this.start = start;
			this.stop = stop;
			a = (char)Target.getCharValueFromGrammarCharLiteral(start.getText());
			b = (char)Target.getCharValueFromGrammarCharLiteral(stop.getText());
		}
		public short opcode() { return charSize(a, b)==1?Bytecode.RANGE8:Bytecode.RANGE16; };
		public int nBytes() { return 1+2*charSize(a, b); }
		public void write(byte[] code) {
			super.write(code);
			if ( charSize(a,b)==1 ) {
				code[addr+1] = (byte)(a&0xFF);
				code[addr+2] = (byte)(b&0xFF);
			}
			else {
				writeShort(code, addr+1, (short)a);
				writeShort(code, addr+1+charSize(a,b), (short)b);
			}
		}

		@Override
		public String toString() {
			return addr+":RangeInstr{"+ a +".."+ b +"}";
		}
	}

	public static class AcceptInstr extends Instr {
		int ruleIndex;
		public AcceptInstr(int ruleIndex) {
			this.ruleIndex = ruleIndex;
		}
		public short opcode() { return Bytecode.ACCEPT; };
		public int nBytes() { return 1+2; }
		public void write(byte[] code) {
			super.write(code);
			writeShort(code, addr+1, (short)ruleIndex);
		}
		public String toString() { return addr+":AcceptInstr "+ruleIndex; }
	}

	public static class JumpInstr extends Instr {
		int target;
		public short opcode() { return Bytecode.JMP; };
		public int nBytes() { return 1+Bytecode.ADDR_SIZE; }
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
		int nAlts;
		public SplitInstr(int nAlts) { this.nAlts = nAlts; }
		public short opcode() { return Bytecode.SPLIT; };
		public int nBytes() { return 1+2+nAlts*Bytecode.ADDR_SIZE; }
		public void write(byte[] code) {
			super.write(code);
			int a = addr + 1;
			writeShort(code, a, (short)addrs.size());
			a += 2;
			for (int x : addrs) {
				writeShort(code, a, (short)x);
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
		ip += I.nBytes();
		instrs.add(I);
	}

	public void emitString(Token t) {
		String chars = Target.getStringFromGrammarStringLiteral(t.getText());
		for (char c : chars.toCharArray()) {
			emit(new MatchInstr(t, c));
		}
	}

	/** Given any block of alts, return list of instruction objects */
	public static List<Instr> getInstructions(GrammarAST blk, int acceptValue) {
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
		NFABytecodeTriggers gen = new NFABytecodeTriggers(nodes);
		try {
			gen.block();
			gen.emit(new NFABytecodeGenerator.AcceptInstr(acceptValue));
		}
		catch (Exception e){
			e.printStackTrace(System.err);
		}
		return gen.instrs;
	}

	public static byte[] getByteCode(List<Instr> instrs) {
		Instr last = instrs.get(instrs.size() - 1);
		int size = last.addr + last.nBytes();
		byte[] code = new byte[size];
		for (Instr I : instrs) {
			I.write(code);
		}
		return code;
	}

	public static NFA getBytecode(LexerGrammar lg, String modeName) {
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		NFABytecodeTriggers gen = new NFABytecodeTriggers(null);

		// add split for s0 to hook up rules (fill in operands as we gen rules)
		int numRules = lg.modes.get(modeName).size();
		int numFragmentRules = 0;
		for (Rule r : lg.modes.get(modeName)) { if ( r.isFragment() ) numFragmentRules++; }
		SplitInstr s0 = new SplitInstr(numRules - numFragmentRules);
		gen.emit(s0);

		Map<String, Integer> ruleToAddr = new HashMap<String, Integer>();
		for (Rule r : lg.modes.get(modeName)) { // for each rule in mode
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			gen.setTreeNodeStream(nodes);
			ruleToAddr.put(r.name, gen.ip);
			if ( !r.isFragment() ) s0.addrs.add(gen.ip);
			try {
				gen.block();
				int ruleTokenType = lg.getTokenType(r.name);
				gen.emit(new NFABytecodeGenerator.AcceptInstr(ruleTokenType));
			}
			catch (Exception e){
				e.printStackTrace(System.err);
			}
		}
		byte[] code = NFABytecodeGenerator.getByteCode(gen.instrs);
		System.out.println("all:");
		System.out.println(Bytecode.disassemble(code));
		System.out.println("rule addrs="+ruleToAddr);

		NFA nfa = new NFA(code, ruleToAddr);
		return nfa;
	}

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}

}
