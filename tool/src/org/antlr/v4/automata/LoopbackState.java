package org.antlr.v4.automata;

/** */
public class LoopbackState extends NFAState {
	BlockStartState loopStartState;

	/** What's its decision number from 1..n? */
	protected int decisionNumber = 0;

	public LoopbackState(NFA nfa) { super(nfa); }	
}
