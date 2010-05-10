package org.antlr.v4.automata;

/** */
public class StarBlockStartState extends BlockStartState {
	public LoopbackState loopBackState;	
	public StarBlockStartState(NFA nfa) { super(nfa); }		
}
