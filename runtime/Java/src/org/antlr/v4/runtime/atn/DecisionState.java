package org.antlr.v4.runtime.atn;

import java.util.*;

public class DecisionState extends ATNState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;

	/** Track the transitions emanating from this ATN state. */
	public List<Transition> transitions = new ArrayList<Transition>(INITIAL_NUM_TRANSITIONS);

	public int decision;

	@Override
	public int getNumberOfTransitions() { return transitions.size(); }

	@Override
	public void addTransition(Transition e) { transitions.add(e); }

	public void addTransitionFirst(Transition e) { transitions.add(0, e); }

	@Override
	public Transition transition(int i) { return transitions.get(i); }

	@Override
	public boolean onlyHasEpsilonTransitions() { return true; }

	@Override
	public void setTransition(int i, Transition e) {
		transitions.set(i, e);
	}

}
