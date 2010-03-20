package org.antlr.v4.tool;

import org.antlr.v4.automata.DFA;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UnreachableAltsMessage extends Message {
	public DFA dfa;
	public Collection<Integer> conflictingAlts;

	public UnreachableAltsMessage(ErrorType etype,
							String fileName,
							DFA dfa,
							Collection<Integer> conflictingAlts)
	{
		super(etype);
		this.fileName = fileName;
		this.dfa = dfa;
		this.conflictingAlts = conflictingAlts;
		this.line = dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = dfa.decisionNFAStartState.ast.getCharPositionInLine();		

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfa", dfa);
		info.put("alts", conflictingAlts);
		args = new Object[] {info}; // pass in a map so we can name the args instead of arg1, arg2, ...
	}

}
