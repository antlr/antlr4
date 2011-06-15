package org.antlr.v4.runtime.atn;

import java.util.ArrayList;
import java.util.List;

/** The last node in the ATN for a rule, unless that rule is the start symbol.
 *  In that case, there is one transition to EOF. Later, we might encode
 *  references to all calls to this rule to compute FOLLOW sets for
 *  error handling.
 */
public class RuleStopState extends ATNState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;

	//public int actionIndex; // for lexer, this is right edge action in rule

	/** Track the transitions emanating from this ATN state. */
	protected List<Transition> transitions =
		new ArrayList<Transition>(INITIAL_NUM_TRANSITIONS);

	@Override
	public int getNumberOfTransitions() { return transitions.size(); }

	@Override
	public void addTransition(Transition e) { transitions.add(e); }

	@Override
	public Transition transition(int i) { return transitions.get(i); }

	@Override
	public void setTransition(int i, Transition e) {
		transitions.set(i, e);
	}
}
