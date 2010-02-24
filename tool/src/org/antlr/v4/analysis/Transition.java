package org.antlr.v4.analysis;

import org.antlr.analysis.State;

/** An NFA transition between any two NFA states.  Subclasses define
 *  atom, set, epsilon, action, predicate, rule transitions.
 *
 *  This is a one way link.  It emanates from a state (usually via a list of
 *  transitions) and has a target state.
 */
public abstract class Transition implements Comparable {
	/** The target of this transition */
	public State target;
	
}
