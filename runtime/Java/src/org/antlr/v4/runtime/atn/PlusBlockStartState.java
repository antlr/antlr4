package org.antlr.v4.runtime.atn;

/** Start of (A|B|...)+ loop. Technically a decision state, but
 *  we don't use for code generation; somebody might need it, so I'm defining
 *  it for completeness. In reality, the PlusLoopbackState node is the
 *  real decision-making note for A+
 */
public class PlusBlockStartState extends BlockStartState {
	public PlusLoopbackState loopBackState;
	//public BlockEndState endState;
}
