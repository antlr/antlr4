package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.DFAState;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AmbiguityMessage extends Message {
	public DFAState d;
	public List<Integer> conflictingAlts;
	public String input;
	public LinkedHashMap<Integer,List<Token>> conflictingPaths;

	public AmbiguityMessage(ErrorType etype,
							String fileName,
							DFAState d,
							List<Integer> conflictingAlts,
							String input,
							LinkedHashMap<Integer,List<Token>> conflictingPaths)
	{
		super(etype);
		this.fileName = fileName;
		this.d = d;
		this.conflictingAlts = conflictingAlts;
		this.input = input;
		this.conflictingPaths = conflictingPaths;

		this.line = d.dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = d.dfa.decisionNFAStartState.ast.getCharPositionInLine();

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfaState", d);
		info.put("conflictingAlts", conflictingAlts);
		info.put("input", input);
		info.put("conflictingPaths", conflictingPaths);
		args = new Object[] {info}; // pass this whole object in to message
	}
}
