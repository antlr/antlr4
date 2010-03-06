package org.antlr.v4.automata;

import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

public class RuleStopState extends NFAState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;

	public Rule rule;
	/** Track the transitions emanating from this NFA state. */
	protected List<Transition> transitions =
		new ArrayList<Transition>(INITIAL_NUM_TRANSITIONS);

	public RuleStopState(NFA nfa) { super(nfa); }			

	@Override
	public int getNumberOfTransitions() { return transitions.size(); }

	@Override
	public void addTransition(Transition e) { transitions.add(e); }

	@Override
	public Transition transition(int i) { return transitions.get(i); }

}
