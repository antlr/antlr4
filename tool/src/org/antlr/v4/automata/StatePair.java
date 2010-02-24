package org.antlr.v4.automata;

/** A pair of states pointing to the left/right (start and end) states of a
 *  state submachine.  Used to build NFAs.
 */
public class StatePair {
    public NFAState left;
    public NFAState right;

    public StatePair(NFAState left, NFAState right) {
        this.left = left;
        this.right = right;
    }
}
