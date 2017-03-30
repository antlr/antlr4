/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** An ATN transition between any two ATN states.  Subclasses define
 *  atom, set, epsilon, action, predicate, rule transitions.
 *
 *  <p>This is a one way link.  It emanates from a state (usually via a list of
 *  transitions) and has a target state.</p>
 *
 *  <p>Since we never have to change the ATN transitions once we construct it,
 *  we can fix these transitions as specific classes. The DFA transitions
 *  on the other hand need to update the labels as it adds transitions to
 *  the states. We'll use the term Edge for the DFA to distinguish them from
 *  ATN transitions.</p>
 */
public abstract class Transition {
	// constants for serialization
	public static final int EPSILON			= 1;
	public static final int RANGE			= 2;
	public static final int RULE			= 3;
	public static final int PREDICATE		= 4; // e.g., {isType(input.LT(1))}?
	public static final int ATOM			= 5;
	public static final int ACTION			= 6;
	public static final int SET				= 7; // ~(A|B) or ~atom, wildcard, which convert to next 2
	public static final int NOT_SET			= 8;
	public static final int WILDCARD		= 9;
	public static final int PRECEDENCE		= 10;


	public static final List<String> serializationNames =
		Collections.unmodifiableList(Arrays.asList(
			"INVALID",
			"EPSILON",
			"RANGE",
			"RULE",
			"PREDICATE",
			"ATOM",
			"ACTION",
			"SET",
			"NOT_SET",
			"WILDCARD",
			"PRECEDENCE"
		));

	public static final Map<Class<? extends Transition>, Integer> serializationTypes =
		Collections.unmodifiableMap(new HashMap<Class<? extends Transition>, Integer>() {{
			put(EpsilonTransition.class, EPSILON);
			put(RangeTransition.class, RANGE);
			put(RuleTransition.class, RULE);
			put(PredicateTransition.class, PREDICATE);
			put(AtomTransition.class, ATOM);
			put(ActionTransition.class, ACTION);
			put(SetTransition.class, SET);
			put(NotSetTransition.class, NOT_SET);
			put(WildcardTransition.class, WILDCARD);
			put(PrecedencePredicateTransition.class, PRECEDENCE);
		}});

	/** The target of this transition. */

	public ATNState target;

	protected Transition(ATNState target) {
		if (target == null) {
			throw new NullPointerException("target cannot be null.");
		}

		this.target = target;
	}

	public abstract int getSerializationType();

	/**
	 * Determines if the transition is an "epsilon" transition.
	 *
	 * <p>The default implementation returns {@code false}.</p>
	 *
	 * @return {@code true} if traversing this transition in the ATN does not
	 * consume an input symbol; otherwise, {@code false} if traversing this
	 * transition consumes (matches) an input symbol.
	 */
	public boolean isEpsilon() {
		return false;
	}


	public IntervalSet label() { return null; }

	public abstract boolean matches(int symbol, int minVocabSymbol, int maxVocabSymbol);
}
