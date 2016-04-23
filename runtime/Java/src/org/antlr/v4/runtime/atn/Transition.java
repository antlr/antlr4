/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
