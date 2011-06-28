package org.antlr.v4.runtime.atn;

import java.util.*;

public class ATNState {
	// constants for serialization
	public static final int BASIC = 1;
	public static final int RULE_START = 2;
	public static final int BLOCK_START = 3;
	public static final int PLUS_BLOCK_START = 4;
	public static final int STAR_BLOCK_START = 5;
	public static final int TOKEN_START = 6;
	public static final int RULE_STOP = 7;
	public static final int BLOCK_END = 8;
	public static final int STAR_LOOP_BACK = 9;
	public static final int PLUS_LOOP_BACK = 10;

	public static String[] serializationNames = {
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
		"PLUS_LOOP_BACK",
	};

	public static Map<Class, Integer> serializationTypes =
		new HashMap<Class, Integer>() {{
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
		}};

	public static final int INVALID_STATE_NUMBER = -1;

	public int stateNumber = INVALID_STATE_NUMBER;

//	public Rule rule;
	public int ruleIndex; // at runtime, we don't have Rule objects

	/** Which ATN are we in? */
	public ATN atn = null;

	/** ATN state is associated with which node in AST? */
//	public GrammarAST ast;
	public Transition transition;
	/** For o-A->o type ATN tranitions, record the label that leads to this
	 *  state.  Useful for creating rich error messages when we find
	 *  insufficiently (with preds) covered states.
	 */
	public Transition incidentTransition;

	@Override
	public int hashCode() { return stateNumber; }

	@Override
	public boolean equals(Object o) {
		// are these states same object?
		if ( o instanceof ATNState) return stateNumber==((ATNState)o).stateNumber;
		return false;
	}

	@Override
	public String toString() {
		return String.valueOf(stateNumber);
	}

	public int getNumberOfTransitions() {
		if ( transition!=null ) return 1;
		return  0;
	}

	public void addTransition(Transition e) {
		if ( transition!=null ) throw new IllegalArgumentException("only one transition");
		transition = e;
	}

	public Transition transition(int i) {
		if ( i>0 ) throw new IllegalArgumentException("only one transition");
		return transition;
	}

	public boolean onlyHasEpsilonTransitions() {
		return transition!=null && transition.isEpsilon();
	}

	public void setTransition(int i, Transition e) {
		if ( i>0 ) throw new IllegalArgumentException("only one transition");
		transition = e;
	}

	public void setRuleIndex(int ruleIndex) { this.ruleIndex = ruleIndex; }
}
