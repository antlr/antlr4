package org.antlr.v4.automata;

/** */
public class PlusBlockStartState extends BlockStartState {
	public LoopbackState loopBackState;
	public PlusBlockStartState(NFA nfa) { super(nfa); }
}
