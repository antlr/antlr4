package org.antlr.v4.automata;

/** A generic state machine state. */
public abstract class State {
    public static final int INVALID_STATE_NUMBER = -1;

    public int stateNumber = INVALID_STATE_NUMBER;

    /** An accept state is an end of rule state for lexers and
     *  parser grammar rules.
	 */
	public boolean acceptState = false;

    public abstract int getNumberOfTransitions();

    public abstract void addTransition(NFATransition e);

    public abstract NFATransition transition(int i);
}
