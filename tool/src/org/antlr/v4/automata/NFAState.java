package org.antlr.v4.automata;

import org.antlr.v4.tool.GrammarAST;

public class NFAState extends State {
	/** Which NFA are we in? */
	public NFA nfa = null;

	/** NFA state is associated with which node in AST? */
	public GrammarAST ast;

	public NFAState(NFA nfa) { this.nfa = nfa; }
	
	@Override
	public int getNumberOfTransitions() {
		return 0;
	}

	@Override
	public void addTransition(Transition e) {
	}

	@Override
	public Transition transition(int i) {
		return null;
	}
}
