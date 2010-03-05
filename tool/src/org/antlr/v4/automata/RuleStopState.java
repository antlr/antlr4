package org.antlr.v4.automata;

import org.antlr.v4.tool.Rule;

public class RuleStopState extends BasicState {
	public Rule rule;
	public RuleStopState(NFA nfa) { super(nfa); }			
}
