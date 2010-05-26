package org.antlr.v4.codegen;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.codegen.pda.*;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.parse.ANTLRParser;
import org.antlr.v4.parse.GrammarASTAdaptor;
import org.antlr.v4.runtime.pda.Bytecode;
import org.antlr.v4.runtime.pda.PDA;
import org.antlr.v4.runtime.tree.TreeParser;
import org.antlr.v4.tool.*;

import java.util.Map;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class PDABytecodeGenerator extends TreeParser {
	public Grammar g;

	public Rule currentRule;

	CompiledPDA pda = new CompiledPDA();

	public int labelIndex = 0; // first time we ask for labels we index

	public PDABytecodeGenerator(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public void emit(Instr I) {
		I.addr = pda.ip;
		I.rule = currentRule;
		I.gen = this;
		pda.ip += I.nBytes();
		pda.instrs.add(I);
	}

	// indexed from 0 per rule
	public int getActionIndex(Rule r, Token actionToken) {
		Integer I = pda.ruleActions.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = pda.ruleActions.get(r);
		int i = 0;
		if ( labels!=null ) i = labels.size();
		pda.ruleActions.put(r, actionToken, i);
		return i;
	}

	// indexed from 0 per rule
	public int getSempredIndex(Rule r, Token actionToken) {
		Integer I = pda.ruleSempreds.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = pda.ruleSempreds.get(r);
		int i = 0;
		if ( labels!=null ) i = labels.size();
		pda.ruleSempreds.put(r, actionToken, i);
		return i;
	}

	/** labels in all rules share single label space
	 *  but we still track labels per rule so we can translate $label
	 *  to an index in an action.
	 */
	public int getLabelIndex(Rule r, String labelName) {
		Integer I = pda.ruleLabels.get(r, labelName);
		if ( I!=null ) return I; // already got its label
		int i = labelIndex++;
		pda.ruleLabels.put(r, labelName, i);
		return i;
	}

	public void emitString(Token t, boolean not) {
		String chars = CharSupport.getStringFromGrammarStringLiteral(t.getText());
		if ( not && chars.length()==1 ) {
			emitNotChar(t, chars);
			return;
		}
		for (char c : chars.toCharArray()) {
			emit(new MatchInstr(t, c));
		}
	}

	public void emitNotChar(Token t, String chars) {
		IntervalSet all = (IntervalSet)g.getTokenTypes();
		int c = chars.charAt(0);
		SplitInstr s = new SplitInstr(2);
		RangeInstr left = new RangeInstr(t, t);
		left.a = all.getMinElement();
		left.b = c-1;
		RangeInstr right = new RangeInstr(t, t);
		right.a = c+1;
		right.b = 127; // all.getMaxElement();
		emit(s);
		emit(left);
		JumpInstr J = new JumpInstr();
		emit(J);
		emit(right);
		s.addrs.add(left.addr);
		s.addrs.add(right.addr);
		int END = pda.ip;
		J.target = END;
		return;
	}

	public byte[] convertInstrsToBytecode() {
		Instr last = pda.instrs.get(pda.instrs.size() - 1);
		int size = last.addr + last.nBytes();
		byte[] code = new byte[size];

		// resolve CALL instruction targets before generating code
		for (Instr I : pda.instrs) {
			if ( I instanceof CallInstr ) {
				CallInstr C = (CallInstr) I;
				String ruleName = C.token.getText();
				C.target = pda.ruleToAddr.get(ruleName);
			}
		}
		for (Instr I : pda.instrs) {
			I.write(code);
		}
		return code;
	}

	public static CompiledPDA compileLexerMode(LexerGrammar lg, String modeName) {
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		PDABytecodeTriggers gen = new PDABytecodeTriggers(null);
		gen.g = lg;
		gen.pda.tokenTypeToAddr = new int[lg.getMaxTokenType()+1];

		// add split for s0 to hook up rules (fill in operands as we gen rules)
		int numRules = lg.modes.get(modeName).size();
		int numFragmentRules = 0;
		for (Rule r : lg.modes.get(modeName)) { if ( r.isFragment() ) numFragmentRules++; }
		SplitInstr s0 = new SplitInstr(numRules - numFragmentRules);
		gen.emit(s0);


		for (Rule r : lg.modes.get(modeName)) { // for each rule in mode
			gen.currentRule = r;
			GrammarAST blk = (GrammarAST)r.ast.getFirstChildWithType(ANTLRParser.BLOCK);
			CommonTreeNodeStream nodes = new CommonTreeNodeStream(adaptor,blk);
			gen.setTreeNodeStream(nodes);
			int ttype = lg.getTokenType(r.name);
			gen.pda.ruleToAddr.put(r.name, gen.pda.ip);
			if ( !r.isFragment() ) {
				s0.addrs.add(gen.pda.ip);
				gen.pda.tokenTypeToAddr[ttype] = gen.pda.ip;
			}
			try {
				gen.block(); // GEN Instr OBJECTS
				int ruleTokenType = lg.getTokenType(r.name);
				if ( !r.isFragment() ) {
					gen.emit(new AcceptInstr(ruleTokenType));
				}
				else {
					gen.emit(new RetInstr());
				}
			}
			catch (Exception e){
				e.printStackTrace(System.err);
			}
		}
		gen.pda.code = gen.convertInstrsToBytecode();
		gen.pda.nLabels = gen.labelIndex;
		System.out.println(Bytecode.disassemble(gen.pda.code));
		System.out.println("rule addrs="+gen.pda.ruleToAddr);
		return gen.pda;
	}

	// (BLOCK (ALT .)) or (BLOCK (ALT 'a') (ALT .))
	public boolean blockHasWildcardAlt(GrammarAST block) {
		for (Object alt : block.getChildren()) {
			AltAST altAST = (AltAST)alt;
			if ( altAST.getChildCount()==1 ) {
				Tree e = altAST.getChild(0);
				if ( e.getChildCount()==0 && e.getType()==ANTLRParser.WILDCARD ) {
					return true;
				}
			}
		}
		return false;
	}

	// testing
	public static PDA getPDA(LexerGrammar lg, String modeName) {
		CompiledPDA info = compileLexerMode(lg, modeName);
		return new PDA(info.code, info.ruleToAddr, info.tokenTypeToAddr, info.nLabels);
	}

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}
}
