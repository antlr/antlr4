package org.antlr.v4.tool;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.misc.IntSet;

import java.util.HashMap;
import java.util.Map;

public class MultipleRecursiveAltsMessage extends Message {
	public DFAState d;
	public DFA dfa;
	public IntSet recursiveAltSet;

	public MultipleRecursiveAltsMessage(String fileName, DFAState d, IntSet recursiveAltSet) {
		super(ErrorType.MULTIPLE_RECURSIVE_ALTS);
		this.d = d;
		this.dfa = d.dfa;
		this.recursiveAltSet = recursiveAltSet;

		this.line = dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = dfa.decisionNFAStartState.ast.getCharPositionInLine();
		this.fileName = fileName;

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfa", dfa);
		info.put("ruleName", dfa.decisionNFAStartState.rule.name);
		info.put("alts", recursiveAltSet);
		args = new Object[] {info}; // pass this whole object in to message
	}
}
