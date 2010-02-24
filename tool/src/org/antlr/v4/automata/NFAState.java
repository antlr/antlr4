package org.antlr.v4.automata;

public class NFAState extends State {
	/** Which NFA are we in? */
	public NFA nfa = null;
	
	/** For o-A->o type NFA tranitions, record the label that leads to this
	 *  state.  Useful for creating rich error messages when we find
	 *  insufficiently (with preds) covered states.
	 */
	public NFATransition incidentTransition;
	
	@Override
	public int getNumberOfTransitions() {
		return 0;
	}

	@Override
	public void addTransition(NFATransition e) {
	}

	@Override
	public NFATransition transition(int i) {
		return null;
	}
}
