package org.antlr.v4.automata;

public class EpsilonTransition extends Transition {
	public EpsilonTransition(NFAState target) { super(target); }

	public boolean isEpsilon() { return true; }	

	public int compareTo(Object o) {
		return 0;
	}
}
