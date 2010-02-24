package org.antlr.v4.automata;

/**
 * 	// I need to distinguish between NFA decision states for (...)* and (...)+
	// during NFA interpretation.
	public static final int LOOPBACK = 1;
	public static final int BLOCK_START = 2;
	public static final int OPTIONAL_BLOCK_START = 3;
	public static final int BYPASS = 4;
	public static final int RIGHT_EDGE_OF_BLOCK = 5;

 make subclasses for all of these
 */
public class NFAState extends State {
	@Override
	public int getNumberOfTransitions() {
		return 0;
	}

	@Override
	public void addTransition(NFATransition e) {
	}

	@Override
	public NFATransition transition(int i) {
		return null;
	}
}
