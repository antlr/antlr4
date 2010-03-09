package org.antlr.v4.automata;

public class RuleStartState extends BasicState {
	public RuleStopState stopState;

	public RuleStartState(NFA nfa) { super(nfa); }
}
