package org.antlr.v4.automata;

import org.antlr.v4.analysis.StackLimitedNFAToDFAConverter;
import org.antlr.v4.tool.Grammar;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A DFA (converted from a grammar's NFA).
 *  DFAs are used as prediction machine for alternative blocks in all kinds
 *  of recognizers (lexers, parsers, tree walkers).
 */
public class DFA {
	public Grammar g;

	/** What's the start state for this DFA? */
    public DFAState startState;

	public int decision;

	/** From what NFAState did we create the DFA? */
	public DecisionState decisionNFAStartState;

	/** A set of all DFA states. Use Map so
	 *  we can get old state back (Set only allows you to see if it's there).
	 *  Not used during fixed k lookahead as it's a waste to fill it with
	 *  a dup of states array.
     */
    public Map<DFAState, DFAState> stateSet = new HashMap<DFAState, DFAState>();

	/** Maps the state number to the actual DFAState. 
	 *
	 *  This is managed in parallel with stateSet and simply provides
	 *  a way to go from state number to DFAState rather than via a
	 *  hash lookup.
	 */
	public List<DFAState> states = new ArrayList<DFAState>();

	public int nAlts = 0;

	/** We only want one accept state per predicted alt; track here */
	public List<DFAState>[] altToAcceptStates;

	/** Did DFA minimization do anything? */
	public boolean minimized;

	public boolean cyclic;

	/** Unique state numbers per DFA */
	int stateCounter = 0;

	public StackLimitedNFAToDFAConverter converter;

	public DFA(Grammar g, DecisionState startState) {
		this.g = g;
		this.decisionNFAStartState = startState;
		nAlts = startState.getNumberOfTransitions();
		decision = startState.decision;
		altToAcceptStates = (ArrayList<DFAState>[])Array.newInstance(ArrayList.class,nAlts+1);
	}

	public DFA(Grammar g, int nAlts) {
		this.g = g;
		this.nAlts = nAlts;
		altToAcceptStates = (ArrayList<DFAState>[])Array.newInstance(ArrayList.class,nAlts+1);
	}

	/** Add a new DFA state to this DFA (doesn't check if already present). */
	public void addState(DFAState d) {
		stateSet.put(d,d);
		d.stateNumber = stateCounter++;
		states.add( d ); // index in states should be d.stateCounter
	}

	public void defineAcceptState(int alt, DFAState acceptState) {
		acceptState.isAcceptState = true;
		acceptState.predictsAlt = alt;		
		if ( stateSet.get(acceptState)==null ) addState(acceptState);
		if ( altToAcceptStates[alt]==null ) {
			altToAcceptStates[alt] = new ArrayList<DFAState>();
		}
		altToAcceptStates[alt].add(acceptState);
	}
	
	public DFAState newState() {
		DFAState n = new DFAState(this);
//		states.setSize(n.stateNumber+1);
//		states.set(n.stateNumber, n); // track state num to state
		return n;
	}

	// could imply converter.unreachableAlts.size()>0 too
	public boolean isAmbiguous() {
		boolean resolvedWithPredicates = true;
		// flip resolvedWithPredicates if we find an ambig state not resolve with pred
		for (DFAState d : converter.ambiguousStates) {
			if ( !d.resolvedWithPredicates ) resolvedWithPredicates = false;
		}
		return converter.ambiguousStates.size()>0 && !resolvedWithPredicates;
	}

	public boolean valid() {
		return
			converter.danglingStates.size()==0 &&
			converter.abortedDueToMultipleRecursiveAltsAt ==null &&
			converter.recursionOverflowState ==null;
	}
	
	public String toString() {
		if ( startState==null ) return "";
		DFASerializer serializer = new DFASerializer(g, startState);
		return serializer.toString();
	}	

}
