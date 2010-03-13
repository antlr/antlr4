package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Edge;
import org.antlr.v4.misc.Utils;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.*;

/** Detect imperfect DFA:
 *
 *  1. nonreduced DFA (dangling states)
 *  2. unreachable stop states
 *  3. nondeterministic states
 */
public class DFAVerifier {
	public static enum ReachableStatus {
		UNKNOWN,
		BUSY, // in process of computing
		NO,
		YES;
	}
	
	Map<DFAState, ReachableStatus> status = new HashMap<DFAState, ReachableStatus>();
	
	DFA dfa;

	StackLimitedNFAToDFAConverter converter;

	public DFAVerifier(DFA dfa, StackLimitedNFAToDFAConverter converter) {
		this.dfa = dfa;
		this.converter = converter;
	}

	public Set<Integer> getUnreachableAlts() {
		Set<Integer> unreachable = new HashSet<Integer>();
		for (int alt=0; alt<dfa.nAlts; alt++) {
			if ( dfa.altToAcceptState[alt]==null ) unreachable.add(alt);
		}
		return unreachable; 
	}

	public Set<DFAState> getDeadStates() {
		// create 2D matrix showing incident edges; inverse of adjacency matrix 
		// incidentEdges.get(s) is list of edges pointing at state s 
		MultiMap<DFAState, DFAState> incidentStates = new MultiMap<DFAState, DFAState>();
		for (DFAState d : dfa.states.values()) {
			for (Edge e : d.edges) incidentStates.map(e.target, d);
		}
		//Set<DFAState> reaches = new HashSet<DFAState>(dfa.uniqueStates.size());

		Set<DFAState> dead = new HashSet<DFAState>(dfa.states.size());
		dead.addAll(dfa.states.values());
		for (DFAState a : dfa.altToAcceptState) {
			if ( a!=null ) dead.remove(a);
		}

		// obviously accept states reach accept states
		//reaches.addAll(Arrays.asList(dfa.altToAcceptState));

		boolean changed = true;
		while ( changed ) {
			changed = false;
			for (DFAState d : dfa.states.values()) {
				if ( !dead.contains(d) ) {
					// if d isn't dead, it reaches accept state.
					dead.remove(d);
					// and, so do all states pointing at it
					List<DFAState> incoming = incidentStates.get(d);
					if ( incoming!=null ) dead.removeAll(incoming);
					changed = true;
				}
			}
		}

//		boolean changed = true;
//		while ( changed ) {
//			changed = false;
//			for (DFAState d : dfa.uniqueStates.values()) {
//				if ( reaches.contains(d) ) {
//					dead.remove(d);
//					// if d reaches, so do all states pointing at it
//					for (DFAState i : incidentStates.get(d)) {
//						if ( !reaches.contains(i) ) {
//							reaches.add(i);
//							changed = true;
//						}
//					}
//				}
//			}
//		}

		System.out.println("dead="+dead);
		return dead;
	}

	/** figure out if this state eventually reaches an accept state and
	 *  modify the instance variable 'reduced' to indicate if we find
	 *  at least one state that cannot reach an accept state.  This implies
	 *  that the overall DFA is not reduced.  This algorithm should be
	 *  linear in the number of DFA states.
	 *
	 *  The algorithm also tracks which alternatives have no accept state,
	 *  indicating a nondeterminism.
	 *
	 *  Also computes whether the DFA is cyclic.
	 *
	 *  TODO: I call getUniquelyPredicatedAlt too much; cache predicted alt
	 *  TODO: convert to nonrecursive version.
	 */
	 boolean _verify(DFAState d) { // TODO: rename to verify?
		if ( d.isAcceptState ) {
			// accept states have no edges emanating from them so we can return
			status.put(d, ReachableStatus.YES);
			// this alt is uniquely predicted, remove from nondeterministic list
			int predicts = d.getUniquelyPredictedAlt();
			converter.unreachableAlts.remove(Utils.integer(predicts));
			return true;
		}

		// avoid infinite loops
		status.put(d, ReachableStatus.BUSY);

		boolean anEdgeReachesAcceptState = false;
		// Visit every transition, track if at least one edge reaches stop state
		// Cannot terminate when we know this state reaches stop state since
		// all transitions must be traversed to set status of each DFA state.
		for (int i=0; i<d.getNumberOfTransitions(); i++) {
			Edge t = d.transition(i);
			DFAState edgeTarget = (DFAState)t.target;
			ReachableStatus targetStatus = status.get(edgeTarget);
			if ( targetStatus==ReachableStatus.BUSY ) { // avoid cycles; they say nothing
				converter.cyclic = true;
				continue;
			}
			if ( targetStatus==ReachableStatus.YES ) { // avoid unnecessary work
				anEdgeReachesAcceptState = true;
				continue;
			}
			if ( targetStatus==ReachableStatus.NO ) {  // avoid unnecessary work
				continue;
			}
			// target must be ReachableStatus.UNKNOWN (i.e., unvisited)
			if ( _verify(edgeTarget) ) {
				anEdgeReachesAcceptState = true;
				// have to keep looking so don't break loop
				// must cover all states even if we find a path for this state
			}
		}
		if ( anEdgeReachesAcceptState ) {
			status.put(d, ReachableStatus.YES);
		}
		else {
			status.put(d, ReachableStatus.NO);
			converter.reduced = false;
		}
		return anEdgeReachesAcceptState;
	}
}
