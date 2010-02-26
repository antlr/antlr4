package org.antlr.v4.automata;

import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.List;

/** */
public class NFA {
	public Grammar g;
	public List<NFAState> states = new ArrayList<NFAState>();
	int stateNumber = 0;
	
	public NFA(Grammar g) { this.g = g; }

	public void addState(NFAState state) {
		states.add(state);
		state.stateNumber = stateNumber++;
	}
}
