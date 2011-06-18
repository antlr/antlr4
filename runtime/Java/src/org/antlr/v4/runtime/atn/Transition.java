package org.antlr.v4.runtime.atn;

import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;

import java.util.*;

/** An ATN transition between any two ATN states.  Subclasses define
 *  atom, set, epsilon, action, predicate, rule transitions.
 *
 *  This is a one way link.  It emanates from a state (usually via a list of
 *  transitions) and has a target state.
 *
 *  Since we never have to change the ATN transitions once we construct it,
 *  we can fix these transitions as specific classes. The DFA transitions
 *  on the other hand need to update the labels as it adds transitions to
 *  the states. We'll use the term Edge for the DFA to distinguish them from
 *  ATN transitions.
 */
public abstract class Transition {
	// constants for serialization
	public static final int EPSILON			= 1;
	public static final int RANGE			= 2;
	public static final int RULE			= 3;
	public static final int PREDICATE		= 4;
	public static final int ATOM			= 5;
	public static final int ACTION			= 6;
	public static final int FORCED_ACTION	= 7;
	public static final int SET				= 8; // ~(A|B) or ~atom, wildcard, which convert to next 2
	public static final int NOT_ATOM		= 9;
	public static final int NOT_SET			= 10;
	public static final int WILDCARD		= 11;


	public static String[] serializationNames = {
		"INVALID",
		"EPSILON",
		"RANGE",
		"RULE",
		"PREDICATE",
		"ATOM",
		"ACTION",
		"FORCED_ACTION",
		"SET",
		"NOT_ATOM",
		"NOT_SET",
		"WILDCARD",
	};

	public static Map<Class, Integer> serializationTypes =
		new HashMap<Class, Integer>() {{
			put(EpsilonTransition.class, EPSILON);
			put(RangeTransition.class, RANGE);
			put(RuleTransition.class, RULE);
			put(PredicateTransition.class, PREDICATE);
			put(AtomTransition.class, ATOM);
			put(ActionTransition.class, ACTION); // TODO: FORCED?
			put(SetTransition.class, SET);
			put(NotAtomTransition.class, NOT_ATOM);
			put(NotSetTransition.class, NOT_SET);
			put(WildcardTransition.class, WILDCARD);
		}};

	/** The target of this transition */
	public ATNState target;

	public Transition() { }

	public Transition(ATNState target) { this.target = target; }

	public int getSerializationType() { return 0; }

	/** Are we epsilon, action, sempred? */
	public boolean isEpsilon() { return false; }

	public IntervalSet label() { return null; }

	public String toString(Grammar g) { return toString(); }
}
