/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * The following images show the relation of states and
 * {@link ATNState#transitions} for various grammar constructs.
 *
 * <ul>
 *
 * <li>Solid edges marked with an &#0949; indicate a required
 * {@link EpsilonTransition}.</li>
 *
 * <li>Dashed edges indicate locations where any transition derived from
 * {@link Transition} might appear.</li>
 *
 * <li>Dashed nodes are place holders for either a sequence of linked
 * {@link BasicState} states or the inclusion of a block representing a nested
 * construct in one of the forms below.</li>
 *
 * <li>Nodes showing multiple outgoing alternatives with a {@code ...} support
 * any number of alternatives (one or more). Nodes without the {@code ...} only
 * support the exact number of alternatives shown in the diagram.</li>
 *
 * </ul>
 *
 * <h2>Basic Blocks</h2>
 *
 * <h3>Rule</h3>
 *
 * <embed src="images/Rule.svg" type="image/svg+xml"/>
 *
 * <h3>Block of 1 or more alternatives</h3>
 *
 * <embed src="images/Block.svg" type="image/svg+xml"/>
 *
 * <h2>Greedy Loops</h2>
 *
 * <h3>Greedy Closure: {@code (...)*}</h3>
 *
 * <embed src="images/ClosureGreedy.svg" type="image/svg+xml"/>
 *
 * <h3>Greedy Positive Closure: {@code (...)+}</h3>
 *
 * <embed src="images/PositiveClosureGreedy.svg" type="image/svg+xml"/>
 *
 * <h3>Greedy Optional: {@code (...)?}</h3>
 *
 * <embed src="images/OptionalGreedy.svg" type="image/svg+xml"/>
 *
 * <h2>Non-Greedy Loops</h2>
 *
 * <h3>Non-Greedy Closure: {@code (...)*?}</h3>
 *
 * <embed src="images/ClosureNonGreedy.svg" type="image/svg+xml"/>
 *
 * <h3>Non-Greedy Positive Closure: {@code (...)+?}</h3>
 *
 * <embed src="images/PositiveClosureNonGreedy.svg" type="image/svg+xml"/>
 *
 * <h3>Non-Greedy Optional: {@code (...)??}</h3>
 *
 * <embed src="images/OptionalNonGreedy.svg" type="image/svg+xml"/>
 */
public abstract class ATNState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;

	// constants for serialization
	public static final int INVALID_TYPE = 0;
	public static final int BASIC = 1;
	public static final int RULE_START = 2;
	public static final int BLOCK_START = 3;
	public static final int PLUS_BLOCK_START = 4;
	public static final int STAR_BLOCK_START = 5;
	public static final int TOKEN_START = 6;
	public static final int RULE_STOP = 7;
	public static final int BLOCK_END = 8;
	public static final int STAR_LOOP_BACK = 9;
	public static final int STAR_LOOP_ENTRY = 10;
	public static final int PLUS_LOOP_BACK = 11;
	public static final int LOOP_END = 12;

	public static final List<String> serializationNames =
		Collections.unmodifiableList(Arrays.asList(
			"INVALID",
			"BASIC",
			"RULE_START",
			"BLOCK_START",
			"PLUS_BLOCK_START",
			"STAR_BLOCK_START",
			"TOKEN_START",
			"RULE_STOP",
			"BLOCK_END",
			"STAR_LOOP_BACK",
			"STAR_LOOP_ENTRY",
			"PLUS_LOOP_BACK",
			"LOOP_END"
		));

	public static final int INVALID_STATE_NUMBER = -1;

    /** Which ATN are we in? */
   	public ATN atn = null;

	public int stateNumber = INVALID_STATE_NUMBER;

	public int ruleIndex; // at runtime, we don't have Rule objects

	private Boolean epsilonOnlyTransitions = null;

	/** Track the transitions emanating from this ATN state. */
	protected final List<Transition> transitions =
		new ArrayList<Transition>(INITIAL_NUM_TRANSITIONS);

	/** Used to cache lookahead during parsing, not used during construction */
    public IntervalSet nextTokenWithinRule;

	@Override
	public int hashCode() { return stateNumber; }

	@Override
	public boolean equals(Object o) {
		// are these states same object?
		if ( o instanceof ATNState ) return stateNumber==((ATNState)o).stateNumber;
		return false;
	}

	public boolean isNonGreedyExitState() {
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(stateNumber);
	}

	public Transition[] getTransitions() {
		return transitions.toArray(new Transition[0]);
	}

	public int getNumberOfTransitions() {
		return transitions.size();
	}

	public void addTransition(Transition e) {
		addTransition(transitions.size(), e);
	}

	public void addTransition(int index, Transition e) {
		if (epsilonOnlyTransitions != null && epsilonOnlyTransitions != e.isEpsilon()) {
			System.err.format(Locale.getDefault(), "ATN state %d has both epsilon and non-epsilon transitions.\n", stateNumber);
		}

		boolean alreadyPresent = false;
		for (Transition t : transitions) {
			if ( t.target.stateNumber == e.target.stateNumber ) {
				if ( t.label()!=null && e.label()!=null && t.label().equals(e.label()) ) {
//					System.err.println("Repeated transition upon "+e.label()+" from "+stateNumber+"->"+t.target.stateNumber);
					alreadyPresent = true;
					break;
				}
				else if ( t.isEpsilon() && e.isEpsilon() ) {
//					System.err.println("Repeated epsilon transition from "+stateNumber+"->"+t.target.stateNumber);
					alreadyPresent = true;
					break;
				}
			}
		}
		if ( !alreadyPresent ) {
			transitions.add(index, e);
			recalculateEpsilonOnlyTransitions();
		}
	}

	public Transition transition(int i) { return transitions.get(i); }

	public void setTransition(int i, Transition e) {
		transitions.remove(i);
		recalculateEpsilonOnlyTransitions();
		if (epsilonOnlyTransitions != null && epsilonOnlyTransitions != e.isEpsilon()) {
			System.err.format(Locale.getDefault(), "ATN state %d has both epsilon and non-epsilon transitions.\n", stateNumber);
		}
		transitions.add(i, e);
		recalculateEpsilonOnlyTransitions();
	}

	public Transition removeTransition(int index) {
		Transition result = transitions.remove(index);
		recalculateEpsilonOnlyTransitions();
		return result;
	}

	public boolean removeTransition(Transition transition) {
		boolean result = transitions.remove(transition);
		recalculateEpsilonOnlyTransitions();
		return result;
	}

	public Transition getTransition(Predicate<Transition> predicate) {
		return transitions.stream().filter(predicate).findFirst().orElse(null);
	}

	public int getTransitionIndex(Transition transition) {
		return transitions.indexOf(transition);
	}

	public abstract int getStateType();

	public final boolean onlyHasEpsilonTransitions() {
		return epsilonOnlyTransitions != null && epsilonOnlyTransitions;
	}

	private void recalculateEpsilonOnlyTransitions() {
		if (transitions.size() == 0) {
			epsilonOnlyTransitions = null;
		} else {
			epsilonOnlyTransitions = transitions.stream().allMatch(Transition::isEpsilon);
		}
	}

	public void setRuleIndex(int ruleIndex) { this.ruleIndex = ruleIndex; }

	public void clearTransitions() {
		transitions.clear();
	}
}
