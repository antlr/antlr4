package org.antlr.v4.automata;

import java.util.ArrayList;
import java.util.List;

/** */
public class BlockStartState extends DecisionState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;

	BlockEndState endState;

	/** Track the transitions emanating from this NFA state. */
	protected List<Transition> transitions =
		new ArrayList<Transition>(INITIAL_NUM_TRANSITIONS);

	public BlockStartState(NFA nfa) { super(nfa); }

	@Override
	public int getNumberOfTransitions() { return transitions.size(); }

	@Override
	public void addTransition(Transition e) { transitions.add(e); }

	@Override
	public Transition transition(int i) { return transitions.get(i); }
}
