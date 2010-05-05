package org.antlr.v4.automata;

/** */
public class LoopbackState extends DecisionState {
	EpsilonTransition loopBack; // edge 2 (transition is edge 1)

	public LoopbackState(NFA nfa) { super(nfa); }

	@Override
	public int getNumberOfTransitions() {
		int n = 0;
		if ( transition!=null ) n++;
		if ( loopBack!=null ) n++;
		return n;
	}

	@Override
	public void addTransition(Transition e) {
		if ( getNumberOfTransitions()>=2 ) throw new IllegalArgumentException("only two transitions");
		if ( transition==null ) transition = e;
		else loopBack = (EpsilonTransition)e;
	}

	@Override
	public Transition transition(int i) {
		if ( i>=2 ) throw new IllegalArgumentException("only two transitions");
		if ( i==1 ) return transition;
		return loopBack;
	}

	@Override
	public boolean onlyHasEpsilonTransitions() { return true; }
}
