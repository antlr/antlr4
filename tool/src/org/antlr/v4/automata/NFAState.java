package org.antlr.v4.automata;

import org.antlr.v4.tool.GrammarAST;
import org.antlr.v4.tool.Rule;

public class NFAState {
	public static final int INVALID_STATE_NUMBER = -1;

	public int stateNumber = INVALID_STATE_NUMBER;

	/** NFAState created for which rule? */
	public Rule rule;

	/** Which NFA are we in? */
	public NFA nfa = null;

	/** NFA state is associated with which node in AST? */
	public GrammarAST ast;

	public NFAState(NFA nfa) { this.nfa = nfa; }

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		// are these states same object?
		if ( o instanceof NFAState ) return stateNumber==((NFAState)o).stateNumber;
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(stateNumber);
	}
	
	public int getNumberOfTransitions() {
		return 0;
	}

	public void addTransition(Transition e) {
	}

	public Transition transition(int i) {
		return null;
	}
}
