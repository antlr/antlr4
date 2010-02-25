package org.antlr.v4.automata;

import org.antlr.runtime.RecognizerSharedState;
import org.antlr.runtime.tree.TreeNodeStream;
import org.antlr.v4.runtime.tree.TreeFilter;

/** Superclass of NFABuilder.g that provides actual NFA construction routines. */
public class NFAFactory extends TreeFilter {

	/** A pair of states pointing to the left/right (start and end) states of a
	 *  state submachine.  Used to build NFAs.
	 */
	public static class Grip {
		public NFAState left;
		public NFAState right;

		public Grip(NFAState left, NFAState right) {
			this.left = left;
			this.right = right;
		}
	}

	public NFAFactory(TreeNodeStream input) {
		super(input);
	}
	public NFAFactory(TreeNodeStream input, RecognizerSharedState state) {
		super(input, state);
	}
}
