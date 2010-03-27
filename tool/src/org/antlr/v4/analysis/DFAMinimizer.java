package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFA;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Edge;
import org.antlr.v4.automata.PredicateEdge;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.misc.Interval;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** First consolidate accept states, which leads to smaller DFA. Also,
 *  consolidate all edges from p to q into a single edge with set.
 */
public class DFAMinimizer {
	DFA dfa;

	public DFAMinimizer(DFA dfa) {
		this.dfa = dfa;
	}

	public boolean minimize() {
		int n = dfa.states.size();
		boolean[][] distinct = new boolean[n][n];

		Set<IntSet> labels = new HashSet<IntSet>();
		for (DFAState d : dfa.states) {
			for (Edge e : d.edges) {
				// todo: slow? might want to flatten to list of int token types
				if ( !(e instanceof PredicateEdge) ) {
					labels.add(e.label);	
				}
			}
		}
		System.out.println("labels="+labels);


		// create initial partition distinguishing between states and accept states
		// we need to distinguish between accepts for different alts.
		// we may have also have multiple accepts per alt--put all of them in same partition
		for (int alt=1; alt<=dfa.nAlts; alt++) {
			List<DFAState> acceptsForAlt = dfa.altToAcceptStates[alt];
			if ( acceptsForAlt==null ) continue; // hmm...must be unreachable
			// distinguish all these accepts from every other state
			for (DFAState p : acceptsForAlt) {
				for (int i=0; i<n; i++) {
					DFAState q = dfa.states.get(i);
					// if q not accept state or p and q predict diff alts, distinguish them
					if ( !q.isAcceptState || q.predictsAlt!=alt ) {
						distinct[p.stateNumber][i] = true;
						distinct[i][p.stateNumber] = true;
					}
				}
			}
		}

		// Nobody can merge with a state resolved with predicates to be safe
		if ( dfa.converter!=null ) {
			for (DFAState d : dfa.converter.resolvedWithSemanticPredicates) {
				for (int i=1; i<n; i++) {
					distinct[d.stateNumber][i] = true;
					distinct[i][d.stateNumber] = true;
				}
			}
		}

		for (int i=1; i<n; i++) {
			for (int j=0; j<i; j++) {
				DFAState p = dfa.states.get(i);
				DFAState q = dfa.states.get(j);
				if ( (p.isAcceptState && !q.isAcceptState) ||
					 (!p.isAcceptState && q.isAcceptState) )
				{
					// make symmetric even though algorithsm on web don't
					// seems that DISTINCT(?(p, a),?(q, a)) might go out of
					// range in my examples.  Maybe they assume symmetric
					// w/o saying it.  Didn't see any code.
					distinct[i][j] = true;
					distinct[j][i] = true;
				}
			}
		}
		print(distinct);

		boolean changed = true;
		while ( changed ) {
			changed = false;

			for (int i=1; i<n; i++) {
				for (int j=0; j<i; j++) {
					if ( distinct[i][j] ) continue;
					DFAState p = dfa.states.get(i);
					DFAState q = dfa.states.get(j);
					for (IntSet label : labels) {
						DFAState pt = p.target(label);
						DFAState qt = q.target(label);
//						System.out.println(p.stateNumber+"-"+label.toString(dfa.g)+"->"+pt);
//						System.out.println(q.stateNumber+"-"+label.toString(dfa.g)+"->"+qt);
						// if DISTINCT(p,q) is empty and
						//    DISTINCT(?(p, a),?(q, a)) is not empty
						// then DISTINCT(p,q) = a.
						// No one seems to show example of case where
						// ?(p,a)==nil.  I assume that if one of states
						// can't transition on label, assume p,q are distinct.
						// If both p,q can't transition on label, we don't
						// know anything about their distinctness.
						// AH! jflex code says alg assumes DFA is "total" so
						// it adds error state.  If both are errors, same state
						// so leave as equiv (nondistinct).  If one goes to
						// error (pt or qt is null) and other doesn't, must
						// be in distinct sets so p,q are distinct.
						if ( pt==null && qt==null ) continue;
						if ( pt==null || qt==null ||
							 distinct[pt.stateNumber][qt.stateNumber] )
						{
							distinct[i][j] = true;
							distinct[j][i] = true;
							changed = true;
							break; // we've marked; move to next state
						}
					}
				}
			}
		}

		print(distinct);

		// Make equiv sets using transitive property
		IntervalSet[] stateToSet = new IntervalSet[n];
		for (int i=0; i<n; i++) stateToSet[i] = new IntervalSet();

		System.out.println("equiv pairs:");
		for (int i=1; i<n; i++) {
			for (int j=0; j<i; j++) {
				if ( !distinct[i][j] ) {
					System.out.println(i+","+j);
					stateToSet[i].add(i);
					stateToSet[i].add(j);
					stateToSet[j].add(i);
					stateToSet[j].add(j);
				}
			}
		}

		System.out.println("equiv sets:");
		OrderedHashSet<IntervalSet> uniq = new OrderedHashSet<IntervalSet>();
		for (int i=0; i<stateToSet.length; i++) {
			IntervalSet s = stateToSet[i];
			if ( s.isNil() ) s.add(i); // i must be it's own set if not equiv
			//if ( s.isNil() ) continue;
			System.out.println(s);
			uniq.add(s);
		}
		System.out.println("uniq sets = "+uniq);
		if ( uniq.size()==dfa.states.size() ) {
			System.out.println("was already minimal");
			return false;
		}

		// minimize the DFA (combine equiv sets)
		// merge all edges from a set to first state in set
		DFAState[] states = new DFAState[n];
		// first map all states in set to same DFA state (old min)
		for (IntervalSet s : uniq) {
			int min = s.getMinElement();
			states[min] = dfa.states.get(min);
			List<Interval> intervals = s.getIntervals();
			for (Interval I : intervals) {
				for (int i=I.a; i<=I.b; i++) {
					states[i] = states[min];
				}
			}
		}
		for (DFAState s : states) System.out.println(s);
		// now do edges
		for (IntervalSet s : uniq) {
			List<Interval> intervals = s.getIntervals();
			System.out.println("do set "+s);
			for (Interval I : intervals) {
				for (int i=I.a; i<=I.b; i++) {
					DFAState p = dfa.states.get(i);
					for (Edge e : p.edges) {
						System.out.println(p.stateNumber+" upon "+e.toString(dfa.g)+
										   " used to point at "+e.target.stateNumber+
										   " now points at "+states[e.target.stateNumber].stateNumber);
						e.target = states[e.target.stateNumber];
					}
				}
			}
		}
		// now kill unused states
		for (IntervalSet s : uniq) {
			List<Interval> intervals = s.getIntervals();
			for (Interval I : intervals) {
				for (int i=I.a; i<=I.b; i++) {
					if ( states[i].stateNumber != i ) { // if not one of our merged states
						System.out.println("kill "+i);
						DFAState d = dfa.states.get(i);
						dfa.stateSet.remove(d);
						if ( d.isAcceptState ) {
							dfa.altToAcceptStates[d.predictsAlt].remove(d);
						}
						dfa.states.set(i, null);
					}
				}
			}
		}

		// RENUMBER STATES
		int i = 0;
		for (DFAState d : dfa.states) {
			if ( d!=null ) d.stateNumber = i++;
		}

		return true;
	}

	void print(boolean[][] distinct) {
		int n = distinct.length;
		for (int i=0; i<n; i++) {
			System.out.print(dfa.states.get(i).stateNumber+":");
			for (int j=0; j<n; j++) {
				System.out.print(" "+(distinct[i][j]?"T":"F"));
			}
			System.out.println();
		}
		System.out.print("  ");
		for (int j=0; j<n; j++) System.out.print(" "+j);
		System.out.println();
		System.out.println();
	}
}
