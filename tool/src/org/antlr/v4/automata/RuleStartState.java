package org.antlr.v4.automata;

import org.antlr.v4.tool.Rule;

public class RuleStartState extends BasicState {
	public RuleStopState stopState;
	public Rule rule;

	public RuleStartState(NFA nfa) { super(nfa); }
}
