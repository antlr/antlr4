package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.DFAState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/** */
public class InsufficientPredicatesMessage extends Message {
	public DFAState d;
	public String input;
	public Map<Integer, Set<Token>> incompletelyCoveredAlts;

	public InsufficientPredicatesMessage(ErrorType etype,
										 String fileName,
										 DFAState d,
										 String input,
										 Map<Integer, Set<Token>> incompletelyCoveredAlts,
										 boolean hasPredicateBlockedByAction)
	{
		super(etype);
		this.fileName = fileName;
		this.d = d;
		this.input = input;
		this.incompletelyCoveredAlts = incompletelyCoveredAlts;

		this.line = d.dfa.decisionNFAStartState.ast.getLine();
		this.charPosition = d.dfa.decisionNFAStartState.ast.getCharPositionInLine();

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("dfaState", d);
		info.put("input", input);
		info.put("altToLocations", incompletelyCoveredAlts);
		info.put("hasPredicateBlockedByAction", hasPredicateBlockedByAction);				
		args = new Object[] {info}; // pass this whole object in to message
	}
}
