package org.antlr.v4.automata;

/** */
public class BasicState extends NFAState {
	public Transition target;

	/** For o-A->o type NFA tranitions, record the label that leads to this
	 *  state.  Useful for creating rich error messages when we find
	 *  insufficiently (with preds) covered states.
	 */
	public Transition incidentTransition;

	public BasicState(NFA nfa) { super(nfa); }
}
