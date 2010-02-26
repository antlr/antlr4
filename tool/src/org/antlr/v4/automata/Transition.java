package org.antlr.v4.automata;

/** An NFA transition between any two NFA states.  Subclasses define
 *  atom, set, epsilon, action, predicate, rule transitions.
 *
 *  This is a one way link.  It emanates from a state (usually via a list of
 *  transitions) and has a target state.
 *
 *  Since we never have to change the NFA transitions once we construct it,
 *  we can fix these transitions as specific classes. The DFA transitions
 *  on the other hand need to update the labels as it adds transitions to
 *  the states. We'll use the term Edge for the DFA to distinguish them from
 *  NFA transitions.
 */
public abstract class Transition implements Comparable {
	/** The target of this transition */
	public State target;

	public Transition() { }

	public Transition(NFAState target) { this.target = target; }
}
