package org.antlr.v4.tool;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.NFAState;

import java.util.HashMap;
import java.util.Map;

public class RecursionOverflowMessage extends Message {
	DFA dfa;
	NFAState s;
	int altNum;
	int depth;
	public RecursionOverflowMessage(String fileName, DFA dfa, NFAState s, int altNum, int depth) {
		super(ErrorType.RECURSION_OVERFLOW);
		this.dfa = dfa;
		this.s = s;
		this.altNum = altNum;
		this.depth = depth;
		
		this.line = dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = dfa.decisionNFAStartState.ast.getCharPositionInLine();
		this.fileName = fileName;

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfa", dfa);
		info.put("alt", altNum);
		info.put("depth", depth);
		info.put("nfaState", s);
		info.put("sourceRule", s.rule);
		info.put("targetRule", s.transition(0).target.rule);		
		args = new Object[] {info}; // pass this whole object in to message
	}
}
