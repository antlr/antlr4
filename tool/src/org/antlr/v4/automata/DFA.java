package org.antlr.v4.automata;

import java.util.HashMap;
import java.util.Map;

/** A DFA (converted from a grammar's NFA).
 *  DFAs are used as prediction machine for alternative blocks in all kinds
 *  of recognizers (lexers, parsers, tree walkers).
 */
public class DFA {
	/** What's the start state for this DFA? */
    public DFAState startState;

	/** Which NFA are we converting (well, which piece of the NFA)? */
//    public NFA nfa;

	/** From what NFAState did we create the DFA? */
	public NFAState decisionNFAStartState;

	/** A set of all uniquely-numbered DFA states.  Maps hash of DFAState
     *  to the actual DFAState object.  We use this to detect
     *  existing DFA states.  Map<DFAState,DFAState>.  Use Map so
	 *  we can get old state back (Set only allows you to see if it's there).
	 *  Not used during fixed k lookahead as it's a waste to fill it with
	 *  a dup of states array.
     */
    public Map<DFAState, DFAState> uniqueStates = new HashMap<DFAState, DFAState>();

	/** Maps the state number to the actual DFAState.  This contains all
	 *  states, but the states are not unique.  s3 might be same as s1 so
	 *  s3 -> s1 in this table.  This is how cycles occur.  If fixed k,
	 *  then these states will all be unique as states[i] always points
	 *  at state i when no cycles exist.
	 *
	 *  This is managed in parallel with uniqueStates and simply provides
	 *  a way to go from state number to DFAState rather than via a
	 *  hash lookup.
	 */
	//protected List<DFAState> states = new ArrayList<DFAState>();

	/** Unique state numbers per DFA */
	int stateCounter = 0;	
}
