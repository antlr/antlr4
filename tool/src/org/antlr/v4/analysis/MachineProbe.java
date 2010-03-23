package org.antlr.v4.analysis;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.*;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.misc.Utils;
import org.antlr.v4.tool.Grammar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MachineProbe {
	DFA dfa;

	public MachineProbe(DFA dfa) { this.dfa = dfa; }

	List<DFAState> getAnyDFAPathToTarget(DFAState targetState) {
		Set<DFAState> visited = new HashSet<DFAState>();
		return getAnyDFAPathToTarget(dfa.startState, targetState, visited);
	}

	public List<DFAState> getAnyDFAPathToTarget(DFAState startState,
												DFAState targetState,
												Set<DFAState> visited)
	{
		List<DFAState> dfaStates = new ArrayList<DFAState>();
		visited.add(startState);
		if ( startState.equals(targetState) ) {
			dfaStates.add(targetState);
			return dfaStates;
		}
		for (Edge e : startState.edges) { // walk edges looking for valid path
			if ( !visited.contains(e.target) ) {
				List<DFAState> path =
					getAnyDFAPathToTarget(e.target, targetState, visited);
				if ( path!=null ) { // found path, we're done
					dfaStates.add(startState);
					dfaStates.addAll(path);
					return dfaStates;
				}
			}
		}
		return null;
	}

	/** Return a list of edge labels from start state to targetState. */
	public List<IntSet> getEdgeLabels(DFAState targetState) {
		List<DFAState> dfaStates = getAnyDFAPathToTarget(targetState);
		List<IntSet> labels = new ArrayList<IntSet>();
		if ( dfaStates==null ) return labels;
		for (int i=0; i<dfaStates.size()-1; i++) {
			DFAState d = dfaStates.get(i);
			DFAState nextState = dfaStates.get(i + 1);
			// walk looking for edge whose target is next dfa state
			for (Edge e : d.edges) {
				if ( e.target.stateNumber == nextState.stateNumber ) {
					labels.add(e.label);
				}
			}
		}
		return labels;
	}

	/** Given List<IntSet>, return a String with a useful representation
	 *  of the associated input string.  One could show something different
	 *  for lexers and parsers, for example.
	 */
	public String getInputSequenceDisplay(Grammar g, List<IntSet> labels) {
		List<String> tokens = new ArrayList<String>();
		for (IntSet label : labels) tokens.add(label.toString(g));
		return Utils.join(tokens.iterator(), " ");
	}

	/** Given an alternative associated with a DFA state, return the list
	 *  of tokens (from grammar) associated with path through NFA following
	 *  the labels sequence.  The nfaStates gives the set of NFA states
	 *  associated with alt that take us from start to stop.  One of the
	 *  NFA states in nfaStates[i] will have an edge intersecting with
	 *  labels[i].
	 */
	public List<Token> getGrammarLocationsForInputSequence(List<Set<NFAState>> nfaStates,
														   List<IntSet> labels)
	{
		List<Token> tokens = new ArrayList<Token>();
		for (int i=0; i<nfaStates.size()-1; i++) {
			Set<NFAState> cur = nfaStates.get(i);
			Set<NFAState> next = nfaStates.get(i + 1);
			IntSet label = labels.get(i);
			// find NFA state with edge whose label matches labels[i]
	nfaConfigLoop:
			for (NFAState p : cur) {
				// walk p's transitions, looking for label
				for (int j=0; j<p.getNumberOfTransitions(); j++) {
					Transition t = p.transition(j);
					if ( !t.isEpsilon() &&
						 !t.label().and(label).isNil() &&
						 next.contains(t.target) )
					{
						tokens.add(p.ast.token);
						break nfaConfigLoop; // found path, move to next NFAState set
					}
				}
			}
		}
		return tokens;
	}

//	/** Used to find paths through syntactically ambiguous DFA. If we've
//	 *  seen statement number before, what did we learn?
//	 */
//	protected Map<Integer, Integer> stateReachable;
//
//	public Map<DFAState, Set<DFAState>> getReachSets(Collection<DFAState> targets) {
//		Map<DFAState, Set<DFAState>> reaches = new HashMap<DFAState, Set<DFAState>>();
//		// targets can reach themselves
//		for (final DFAState d : targets) {
//			reaches.put(d,new HashSet<DFAState>() {{add(d);}});
//		}
//
//		boolean changed = true;
//		while ( changed ) {
//			changed = false;
//			for (DFAState d : dfa.states.values()) {
//				if ( d.getNumberOfEdges()==0 ) continue;
//				Set<DFAState> r = reaches.get(d);
//				if ( r==null ) {
//					r = new HashSet<DFAState>();
//					reaches.put(d, r);
//				}
//				int before = r.size();
//				// add all reaches from all edge targets
//				for (Edge e : d.edges) {
//					//if ( targets.contains(e.target) ) r.add(e.target);
//					r.addAll( reaches.get(e.target) );
//				}
//				int after = r.size();
//				if ( after>before) changed = true;
//			}
//		}
//		return reaches;
//	}

}
