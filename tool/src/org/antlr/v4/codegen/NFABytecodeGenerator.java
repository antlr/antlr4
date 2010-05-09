package org.antlr.v4.codegen;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.codegen.nfa.*;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.DoubleKeyMap;
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
	LexerGrammar lg;
	public List<Instr> instrs = new ArrayList<Instr>();
	public int ip = 0; // where to write next
	Map<String, Integer> ruleToAddr = new HashMap<String, Integer>();
	int[] tokenTypeToAddr;

	DoubleKeyMap<Rule, String, Integer> ruleLabels = new DoubleKeyMap<Rule, String, Integer>();
	DoubleKeyMap<Rule, Token, Integer> ruleActions = new DoubleKeyMap<Rule, Token, Integer>();
	DoubleKeyMap<Rule, Token, Integer> ruleSempreds = new DoubleKeyMap<Rule, Token, Integer>();

	public Rule currentRule;

	public int labelIndex = 0; // first time we ask for labels we index

//	public abstract class LabelMaker<Key,Label> {
//		Map<Key,Label> labels = new HashMap<Key,Label>();
//		public LabelMaker(Collection<Key> keys) {
//			for (Key k : keys) labels.put(k, computeLabel(k));
//		}
//		public abstract Label computeLabel(Key k);
//	}

	public NFABytecodeGenerator(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}

	public void emit(Instr I) {
		I.addr = ip;
		I.rule = currentRule;
		I.gen = this;
		ip += I.nBytes();
		instrs.add(I);
	}

	// indexed from 0 per rule
	public int getActionIndex(Rule r, Token actionToken) {
		Integer I = ruleActions.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = ruleActions.get(r);
		int i = labels.size();
		ruleActions.put(r, actionToken, i);
		return i;
	}

	// indexed from 0 per rule
	public int getSempredIndex(Rule r, Token actionToken) {
		Integer I = ruleSempreds.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = ruleSempreds.get(r);
		int i = labels.size();
		ruleSempreds.put(r, actionToken, i);
		return i;
	}

	/** labels in all rules share single label space
	 *  but we still track labels per rule so we can translate $label
	 *  to an index in an action.
	 */
	public int getLabelIndex(Rule r, String labelName) {
		Integer I = ruleLabels.get(r, labelName);
		if ( I!=null ) return I; // already got its label
		int i = labelIndex++;
		ruleLabels.put(r, labelName, i);
		return i;
	}

	public void emitString(Token t) {
		String chars = CharSupport.getStringFromGrammarStringLiteral(t.getText());
		for (char c : chars.toCharArray()) {
			emit(new MatchInstr(t, c));
		}
	}

	public byte[] getBytecode() {
		Instr last = instrs.get(instrs.size() - 1);
		int size = last.addr + last.nBytes();
		byte[] code = new byte[size];

		// resolve CALL instruction targets before generating code
		for (Instr I : instrs) {
			if ( I instanceof CallInstr ) {
				CallInstr C = (CallInstr) I;
				String ruleName = C.token.getText();
				C.target = ruleToAddr.get(ruleName);
			}
		}
		for (Instr I : instrs) {
			I.write(code);
		}
		return code;
	}

	public static NFA getBytecode(LexerGrammar lg, String modeName) {
		GrammarASTAdaptor adaptor = new GrammarASTAdaptor();
		NFABytecodeTriggers gen = new NFABytecodeTriggers(null);
		gen.lg = lg;
		gen.tokenTypeToAddr = new int[lg.getMaxTokenType()+1];

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
			gen.ruleToAddr.put(r.name, gen.ip);
			if ( !r.isFragment() ) {
				s0.addrs.add(gen.ip);
				gen.tokenTypeToAddr[ttype] = gen.ip;
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
		byte[] code = gen.getBytecode();
		System.out.println(Bytecode.disassemble(code));
		System.out.println("rule addrs="+gen.ruleToAddr);

		return new NFA(code, gen.ruleToAddr, gen.tokenTypeToAddr, gen.labelIndex);
	}

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}
}
