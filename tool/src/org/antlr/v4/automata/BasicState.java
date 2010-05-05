package org.antlr.v4.automata;

/** */
public class BasicState extends NFAState {
	public Transition transition;

	/** For o-A->o type NFA tranitions, record the label that leads to this
	 *  state.  Useful for creating rich error messages when we find
	 *  insufficiently (with preds) covered states.
	 */
	public Transition incidentTransition;

	public BasicState(NFA nfa) { super(nfa); }

	@Override
	public int getNumberOfTransitions() {
		if ( transition!=null ) return 1;
		return  0;
	}

	@Override
	public void addTransition(Transition e) {
		if ( transition!=null ) throw new IllegalArgumentException("only one transition");
		transition = e;
	}

	@Override
	public Transition transition(int i) {
		if ( i>0 ) throw new IllegalArgumentException("only one transition");
		return transition;
	}

	@Override
	public boolean onlyHasEpsilonTransitions() {
		return transition!=null && transition.isEpsilon();
	}
}
