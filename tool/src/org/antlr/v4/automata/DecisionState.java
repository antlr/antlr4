package org.antlr.v4.automata;

public class DecisionState extends BasicState {
	public int decision;
	public DecisionState(NFA nfa) { super(nfa); }
}
