package org.antlr.v4.tool;

import org.antlr.v4.analysis.MachineProbe;
import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.misc.IntSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecursionOverflowMessage extends Message {
	DFA dfa;
	DFAState d;
	NFAState s;
	int altNum;
	int depth;
	public RecursionOverflowMessage(String fileName, DFAState d, NFAState s, int altNum, int depth) {
		super(ErrorType.RECURSION_OVERFLOW);
		this.d = d;
		this.dfa = d.dfa;
		this.s = s;
		this.altNum = altNum;
		this.depth = depth;
		
		this.line = dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = dfa.decisionNFAStartState.ast.getCharPositionInLine();
		this.fileName = fileName;

		MachineProbe probe = new MachineProbe(dfa);
		List<IntSet> labels = probe.getEdgeLabels(d);		
		String input = probe.getInputSequenceDisplay(dfa.g, labels);

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfa", dfa);
		info.put("dfaState", d);
		info.put("alt", altNum);
		info.put("depth", depth);
		info.put("input", input);
		info.put("nfaState", s);
		info.put("sourceRule", s.rule);
		info.put("targetRule", s.transition(0).target.rule);		
		args = new Object[] {info}; // pass this whole object in to message
	}
}
