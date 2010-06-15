package org.antlr.v4.automata;

import org.antlr.v4.analysis.PredictionDFAFactory;
import org.antlr.v4.misc.IntervalSet;
import org.antlr.v4.misc.OrderedHashSet;
import org.antlr.v4.tool.Grammar;

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

	/** accept state(s) per predicted alt; track here */
	public List<DFAState>[] altToAcceptStates;

	/** Did DFA minimization do anything? */
	public boolean minimized;

	//public boolean cyclic;

	/** Unique state numbers per DFA */
	int stateCounter = 0;

	public PredictionDFAFactory converter;

	public DFA(Grammar g, DecisionState startState) {
		this.g = g;
		this.decisionNFAStartState = startState;
		nAlts = startState.getNumberOfTransitions();
		decision = startState.decision;
		altToAcceptStates = new ArrayList[nAlts+1]; //(ArrayList<DFAState>[])Array.newInstance(ArrayList.class,nAlts+1);
	}

	public DFA(Grammar g, int nAlts) {
		this.g = g;
		this.nAlts = nAlts;
		altToAcceptStates = new ArrayList[nAlts+1]; //(ArrayList<DFAState>[])Array.newInstance(ArrayList.class,nAlts+1);
	}

	/** Add a new DFA state to this DFA (doesn't check if already present). */
	public void addState(DFAState d) {
		stateSet.put(d,d);
		d.stateNumber = stateCounter++;
		states.add( d ); // index in states should be d.stateCounter
	}

	public void addAcceptState(int alt, DFAState acceptState) {
		if ( stateSet.get(acceptState)==null ) addState(acceptState);
		defineAcceptState(alt, acceptState);
	}

	public void defineAcceptState(int alt, DFAState acceptState) {
		acceptState.isAcceptState = true;
		acceptState.predictsAlt = alt;
		if ( altToAcceptStates[alt]==null ) {
			altToAcceptStates[alt] = new ArrayList<DFAState>();
		}
		altToAcceptStates[alt].add(acceptState);
	}

	public DFAState newState() {
		DFAState n = new DFAState(this);
		return n;
	}

	public LexerState newLexerState() {
		LexerState n = new LexerState(this);
		return n;
	}

//	// could imply converter.unreachableAlts.size()>0 too
//	public boolean isAmbiguous() {
//		boolean resolvedWithPredicates = true;
//		// flip resolvedWithPredicates if we find an ambig state not resolve with pred
//		for (DFAState d : converter.ambiguousStates) {
//			if ( !d.resolvedWithPredicates ) resolvedWithPredicates = false;
//		}
//		return converter.ambiguousStates.size()>0 && !resolvedWithPredicates;
//	}

	public boolean valid() {
		return
			converter.resolver.danglingStates.size()==0;
//			converter.abortedDueToMultipleRecursiveAltsAt ==null &&
//			converter.recursionOverflowState ==null;
	}
	
	public String toString() {
		if ( startState==null ) return "";
		DFASerializer serializer = new DFASerializer(g, startState);
		return serializer.toString();
	}

	public static OrderedHashSet<IntervalSet> getReachableLabels(DFAState d) {
		OrderedHashSet<IntervalSet> reachableLabels = new OrderedHashSet<IntervalSet>();
		for (NFAState s : d.getUniqueNFAStates()) { // for each state
			int n = s.getNumberOfTransitions();
			for (int i=0; i<n; i++) {               // for each transition
				Transition t = s.transition(i);
				IntervalSet label = t.label();
//				if ( t instanceof AtomTransition ) {
//					label = IntervalSet.of(((AtomTransition)t).label);
//				}
//				else if ( t instanceof RangeTransition ) {
//					label = ((RangeTransition)t).label();
//				}
//				else if ( t instanceof SetTransition ) {
//					label = ((SetTransition)t).label;
//				}
				if ( label!=null ) {
					addReachableLabel(reachableLabels, label);
				}
			}
		}
		//System.out.println("reachable labels for "+d+"="+reachableLabels);
		return reachableLabels;
	}

	/** Add label uniquely and disjointly; intersection with
     *  another set or int/char forces breaking up the set(s).
     *
     *  Example, if reachable list of labels is [a..z, {k,9}, 0..9],
     *  the disjoint list will be [{a..j,l..z}, k, 9, 0..8].
     *
     *  As we add NFA configurations to a DFA state, we might as well track
     *  the set of all possible transition labels to make the DFA conversion
     *  more efficient.  W/o the reachable labels, we'd need to check the
     *  whole vocabulary space (could be 0..\uFFFE)!  The problem is that
     *  labels can be sets, which may overlap with int labels or other sets.
     *  As we need a deterministic set of transitions from any
     *  state in the DFA, we must make the reachable labels set disjoint.
     *  This operation amounts to finding the character classes for this
     *  DFA state whereas with tools like flex, that need to generate a
     *  homogeneous DFA, must compute char classes across all states.
     *  We are going to generate DFAs with heterogeneous states so we
     *  only care that the set of transitions out of a single state is
     *  unique. :)
     *
     *  The idea for adding a new set, t, is to look for overlap with the
     *  elements of existing list s.  Upon overlap, replace
     *  existing set s[i] with two new disjoint sets, s[i]-t and s[i]&t.
     *  (if s[i]-t is nil, don't add).  The remainder is t-s[i], which is
     *  what you want to add to the set minus what was already there.  The
	 *  remainder must then be compared against the i+1..n elements in s
	 *  looking for another collision.  Each collision results in a smaller
	 *  and smaller remainder.  Stop when you run out of s elements or
	 *  remainder goes to nil.  If remainder is non nil when you run out of
	 *  s elements, then add remainder to the end.
	 */
	public static void addReachableLabel(OrderedHashSet<IntervalSet> reachableLabels,
										 IntervalSet label)
	{
		/*
		System.out.println("addReachableLabel to state "+dfa.decisionNumber+"."+stateNumber+": "+label.getSet().toString(dfa.nfa.grammar));
		System.out.println("start of add to state "+dfa.decisionNumber+"."+stateNumber+": " +
				"reachableLabels="+reachableLabels.toString());
				*/
		if ( reachableLabels.contains(label) ) { // exact label present
            return;
        }
        IntervalSet remainder = label; // remainder starts out as whole set to add
        int n = reachableLabels.size(); // only look at initial elements
        // walk the existing list looking for the collision
        for (int i=0; i<n; i++) {
			IntervalSet rl = reachableLabels.get(i);
            /*
			System.out.println("comparing ["+i+"]: "+label.toString(dfa.nfa.grammar)+" & "+
                    rl.toString(dfa.nfa.grammar)+"="+
                    intersection.toString(dfa.nfa.grammar));
            */
			IntervalSet intersection = (IntervalSet)label.and(rl);
			if ( intersection.isNil() ) {
                continue;
            }
			//System.out.println(label+" collides with "+rl);

			// For any (s_i, t) with s_i&t!=nil replace with (s_i-t, s_i&t)
            // (ignoring s_i-t if nil; don't put in list)

            // Replace existing s_i with intersection since we
            // know that will always be a non nil character class
			IntervalSet s_i = rl;
            reachableLabels.set(i, intersection);

            // Compute s_i-t to see what is in current set and not in incoming
            IntervalSet existingMinusNewElements = (IntervalSet)s_i.subtract(label);
			//System.out.println(s_i+"-"+t+"="+existingMinusNewElements);
            if ( !existingMinusNewElements.isNil() ) {
                // found a new character class, add to the end (doesn't affect
                // outer loop duration due to n computation a priori.
                reachableLabels.add(existingMinusNewElements);
            }

			/*
            System.out.println("after collision, " +
                    "reachableLabels="+reachableLabels.toString());
					*/

            // anything left to add to the reachableLabels?
            remainder = (IntervalSet)label.subtract(s_i);
            if ( remainder.isNil() ) {
                break; // nothing left to add to set.  done!
            }

            label = remainder;
        }
        if ( !remainder.isNil() ) {
			/*
			System.out.println("before add remainder to state "+dfa.decisionNumber+"."+stateNumber+": " +
					"reachableLabels="+reachableLabels.toString());
			System.out.println("remainder state "+dfa.decisionNumber+"."+stateNumber+": "+remainder.toString(dfa.nfa.grammar));
            */
            reachableLabels.add(remainder);
        }
		/*
		System.out.println("#END of add to state "+dfa.decisionNumber+"."+stateNumber+": " +
				"reachableLabels="+reachableLabels.toString());
				*/
    }
}
