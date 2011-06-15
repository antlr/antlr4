package org.antlr.v4.runtime.atn;

/** Decision state for A+ and (A|B)+. The first
 *  transition points at the start of the first alternative.
 *  The last transition is the exit transition.
 */
public class PlusLoopbackState extends DecisionState {
	@Override
	public boolean onlyHasEpsilonTransitions() { return true; }
}
