package org.antlr.v4.codegen;

import org.antlr.runtime.Token;
import org.antlr.v4.codegen.pda.CallInstr;
import org.antlr.v4.codegen.pda.Instr;
import org.antlr.v4.codegen.pda.MatchInstr;
import org.antlr.v4.misc.CharSupport;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Rule;

import java.util.Map;

/** http://swtch.com/~rsc/regexp/regexp2.html */
public class PDABytecodeGenerator {
	public Rule currentRule;

	public CompiledPDA obj;

	public int ip = 0; // where to write next

	int labelIndex = 0; // first time we ask for labels we index

	public PDABytecodeGenerator(int numAlts) {
		obj = new CompiledPDA(numAlts);
	}

	public void compile() {
		obj.code = convertInstrsToBytecode();
	}

	public void emit(Instr I) {
		I.addr = ip;
		I.rule = currentRule;
		I.gen = this;
		ip += I.nBytes();
		obj.instrs.add(I);
	}

	// indexed from 0 per rule
	public int getActionIndex(Rule r, Token actionToken) {
		Integer I = obj.ruleActions.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = obj.ruleActions.get(r);
		int i = 0;
		if ( labels!=null ) i = labels.size();
		obj.ruleActions.put(r, actionToken, i);
		return i;
	}

	// indexed from 0 per rule
	public int getSempredIndex(Rule r, Token actionToken) {
		Integer I = obj.ruleSempreds.get(r, actionToken);
		if ( I!=null ) return I; // already got its label
		Map<Token, Integer> labels = obj.ruleSempreds.get(r);
		int i = 0;
		if ( labels!=null ) i = labels.size();
		obj.ruleSempreds.put(r, actionToken, i);
		return i;
	}

	/** labels in all rules share single label space
	 *  but we still track labels per rule so we can translate $label
	 *  to an index in an action.
	 */
	public int getLabelIndex(Rule r, String labelName) {
		Integer I = obj.ruleLabels.get(r, labelName);
		if ( I!=null ) return I; // already got its label
		int i = labelIndex++;
		obj.ruleLabels.put(r, labelName, i);
		return i;
	}

	public int getSetIndex(IntervalSet set) {
		obj.set8table.add(set);
		return obj.set8table.size()-1;
	}

	public void emitString(Token t) {
		String chars = CharSupport.getStringFromGrammarStringLiteral(t.getText());
		for (char c : chars.toCharArray()) {
			emit(new MatchInstr(t, c));
		}
	}

	public byte[] convertInstrsToBytecode() {
		Instr last = obj.instrs.get(obj.instrs.size() - 1);
		int size = last.addr + last.nBytes();
		byte[] code = new byte[size];

		// resolve CALL instruction targets before generating code
		for (Instr I : obj.instrs) {
			if ( I instanceof CallInstr ) {
				CallInstr C = (CallInstr) I;
				String ruleName = C.token.getText();
				C.target = obj.ruleToAddr.get(ruleName);
			}
		}
		for (Instr I : obj.instrs) {
			I.write(code);
		}
		return code;
	}

	public void defineRuleAddr(String name, int ip) {
		obj.ruleToAddr.put(name, ip);
	}

	public void defineRuleIndexToAddr(int index, int ip) {
		obj.altToAddr[index] = ip;
	}

	public void defineTokenTypeToAddr(int ttype, int ip) {
		defineRuleIndexToAddr(ttype, ip);
	}

	/** Write value at index into a byte array highest to lowest byte,
	 *  left to right.
	 */
	public static void writeShort(byte[] memory, int index, short value) {
		memory[index+0] = (byte)((value>>(8*1))&0xFF);
		memory[index+1] = (byte)(value&0xFF);
	}

}
