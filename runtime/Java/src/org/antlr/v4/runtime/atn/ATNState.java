/*
 [The "BSD license"]
  Copyright (c) 2011 Terence Parr
  All rights reserved.

  Redistribution and use in source and binary forms, with or without
  modification, are permitted provided that the following conditions
  are met:

  1. Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
  2. Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
  3. The name of the author may not be used to endorse or promote products
     derived from this software without specific prior written permission.

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.*;

public class ATNState {
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

	@SuppressWarnings("serial")
	public static final Map<Class<? extends ATNState>, Integer> serializationTypes =
		Collections.unmodifiableMap(new HashMap<Class<? extends ATNState>, Integer>() {{
            put(ATNState.class, BASIC);
            put(RuleStartState.class, RULE_START);
            put(BlockStartState.class, BLOCK_START);
            put(PlusBlockStartState.class, PLUS_BLOCK_START);
            put(StarBlockStartState.class, STAR_BLOCK_START);
            put(TokensStartState.class, TOKEN_START);
            put(RuleStopState.class, RULE_STOP);
            put(BlockEndState.class, BLOCK_END);
            put(PlusLoopbackState.class, PLUS_LOOP_BACK);
            put(StarLoopbackState.class, STAR_LOOP_BACK);
			put(StarLoopEntryState.class, STAR_LOOP_ENTRY);
			put(LoopEndState.class, LOOP_END);
        }});

	public static final int INVALID_STATE_NUMBER = -1;

    /** Which ATN are we in? */
   	public ATN atn = null;

	public int stateNumber = INVALID_STATE_NUMBER;

	public int ruleIndex; // at runtime, we don't have Rule objects

	public boolean epsilonOnlyTransitions = false;

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

	@Override
	public String toString() {
		return String.valueOf(stateNumber);
	}

	public int getNumberOfTransitions() { return transitions.size(); }

	public void addTransition(Transition e) {
		if (transitions.isEmpty()) {
			epsilonOnlyTransitions = e.isEpsilon();
		}
		else if (epsilonOnlyTransitions != e.isEpsilon()) {
			System.err.format("ATN state %d has both epsilon and non-epsilon transitions.\n", stateNumber);
			epsilonOnlyTransitions = false;
		}

		transitions.add(e);
	}

	public Transition transition(int i) { return transitions.get(i); }

	public void setTransition(int i, Transition e) {
		transitions.set(i, e);
	}

	public int getStateType() {
		return serializationTypes.get(this.getClass());
	}

	public final boolean onlyHasEpsilonTransitions() {
		return epsilonOnlyTransitions;
	}

	public void setRuleIndex(int ruleIndex) { this.ruleIndex = ruleIndex; }
}
