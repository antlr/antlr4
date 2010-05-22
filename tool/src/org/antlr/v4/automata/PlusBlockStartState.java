package org.antlr.v4.automata;

/** Start of (A|B|...)+ loop. Not decision, inner block has decision state */
public class PlusBlockStartState extends BasicState {
	public LoopbackState loopBackState;
	public BlockEndState endState;
	
	public PlusBlockStartState(NFA nfa) { super(nfa); }
}
