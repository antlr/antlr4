package org.antlr.v4.automata;

import org.antlr.v4.analysis.NFAConfig;
import org.antlr.v4.analysis.NFAContext;
import org.antlr.v4.analysis.Resolver;
import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.misc.OrderedHashSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** A DFA state represents a set of possible NFA configurations.
 *  As Aho, Sethi, Ullman p. 117 says "The DFA uses its state
 *  to keep track of all possible states the NFA can be in after
 *  reading each input symbol.  That is to say, after reading
 *  input a1a2..an, the DFA is in a state that represents the
 *  subset T of the states of the NFA that are reachable from the
 *  NFA's start state along some path labeled a1a2..an."
 *  In conventional NFA->DFA conversion, therefore, the subset T
 *  would be a bitset representing the set of states the
 *  NFA could be in.  We need to track the alt predicted by each
 *  state as well, however.  More importantly, we need to maintain
 *  a stack of states, tracking the closure operations as they
 *  jump from rule to rule, emulating rule invocations (method calls).
 *  Recall that NFAs do not normally have a stack like a pushdown-machine
 *  so I have to add one to simulate the proper lookahead sequences for
 *  the underlying LL grammar from which the NFA was derived.
 *
 *  I use a list of NFAConfig objects.  An NFAConfiguration
 *  is both a state (ala normal conversion) and an NFAContext describing
 *  the chain of rules (if any) followed to arrive at that state.  There
 *  is also the semantic context, which is the "set" of predicates found
 *  on the path to this configuration.
 *
 *  A DFA state may have multiple references to a particular state,
 *  but with different NFAContexts (with same or different alts)
 *  meaning that state was reached via a different set of rule invocations.
 */
public class DFAState {
	public static final int INITIAL_NUM_TRANSITIONS = 4;
	public static final int INVALID_STATE_NUMBER = -1;

	public int stateNumber = INVALID_STATE_NUMBER;

	public boolean isAcceptState = false;

	/** If accept, which alt does it predict? */
	public int predictsAlt = NFA.INVALID_ALT_NUMBER;

	/** State in which DFA? */
	public DFA dfa;

	/** Track the transitions emanating from this DFA state. */
	public List<Edge> edges =
		new ArrayList<Edge>(INITIAL_NUM_TRANSITIONS);

	/** The set of NFA configurations (state,alt,context) for this DFA state */
	public OrderedHashSet<NFAConfig> nfaConfigs;

	/** Rather than recheck every NFA configuration in a DFA state (after
	 *  resolving) in reach just check this boolean.  Saves a linear walk
	 *  perhaps DFA state creation. Every little bit helps.
	 *
	 *  This indicates that at least 2 alts were resolved, but not necessarily
	 *  all alts in DFA state configs.
	 */
	public boolean resolvedWithPredicates = false;

	//int cachedUniquelyPredicatedAlt = NFA.INVALID_ALT_NUMBER;

	public DFAState() {; }	

	public DFAState(DFA dfa) {
		this.dfa = dfa;
		nfaConfigs = new OrderedHashSet<NFAConfig>();
	}

	public void addNFAConfig(NFAConfig c) {
		if ( nfaConfigs.contains(c) ) return;
		nfaConfigs.add(c);
	}

	public NFAConfig addNFAConfig(NFAState state,
								  int alt,
								  NFAContext context,
								  SemanticContext semanticContext)
	{
		NFAConfig c = new NFAConfig(state, alt,	context, semanticContext);
		addNFAConfig(c);
		return c;
	}

	/** Walk each configuration and if they are all the same alt,
	 *  even the resolved configs.
	 */
	public int getUniquelyPredictedAlt() {
		if ( predictsAlt!=NFA.INVALID_ALT_NUMBER ) return predictsAlt;
		predictsAlt = Resolver.getUniqueAlt(nfaConfigs, false);
		return predictsAlt;
	}

	/** Return the uniquely mentioned alt from the NFA configurations, ignoring
	 *  resolved configs
	 */
	public int getUniqueAlt() { return Resolver.getUniqueAlt(nfaConfigs, true); }

	/** Get the set of all alts mentioned by all NFA configurations in this
	 *  DFA state.
	 */
	public Set<Integer> getAltSet() {
		Set<Integer> alts = new HashSet<Integer>();
		for (NFAConfig c : nfaConfigs) {
			alts.add(c.alt);
		}
		if ( alts.size()==0 ) return null;
		return alts;
	}

	public int getMinAlt() {
		int min = Integer.MAX_VALUE;
		for (NFAConfig c : nfaConfigs) {
			if ( c.alt < min ) min = c.alt;
		}
		return min;
	}

	public Set<NFAState> getUniqueNFAStates() {
		return getUniqueNFAStates(NFA.INVALID_ALT_NUMBER);
	}

	public Set<NFAState> getUniqueNFAStates(int alt) {
		Set<NFAState> alts = new HashSet<NFAState>();
		for (NFAConfig c : nfaConfigs) {
			if ( alt==NFA.INVALID_ALT_NUMBER || c.alt==alt ) alts.add(c.state);
		}
		if ( alts.size()==0 ) return null;
		return alts;
	}

	public int getNumberOfEdges() { return edges.size(); }

	public void addEdge(Edge e) { edges.add(e); }

	public Edge edge(int i) { return edges.get(i); }

	public DFAState target(IntSet label) {
		for (Edge e : edges) {
			if ( !(e instanceof PredicateEdge) &&
				 !e.label.and(label).isNil() )
			{
				return e.target;
			}
		}
		return null;
	}

	/** A decent hash for a DFA state is the sum of the NFA state/alt pairs. */
	public int hashCode() {
		int h = 0;
		for (NFAConfig c : nfaConfigs) {
			h += c.state.stateNumber + c.alt;
		}
		return h;
	}

	/** Two DFAStates are equal if their NFA configuration sets are the
	 *  same. This method is used to see if a DFA state already exists.
	 *
	 *  Because the number of alternatives and number of NFA configurations are
	 *  finite, there is a finite number of DFA states that can be processed.
	 *  This is necessary to show that the algorithm terminates.
	 *
	 *  Cannot test the DFA state numbers here because in DFA.addState we need
	 *  to know if any other state exists that has this exact set of NFA
	 *  configurations.  The DFAState state number is irrelevant.
	 */
	public boolean equals(Object o) {
		// compare set of NFA configurations in this set with other
		if ( this==o ) return true;
		DFAState other = (DFAState)o;
		boolean sameSet = this.nfaConfigs.equals(other.nfaConfigs);
		//System.out.println("DFAState.equals: "+nfaConfigs+(sameSet?"==":"!=")+other.nfaConfigs);
		return sameSet;
	}

	/** Print all NFA states plus what alts they predict */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(stateNumber+":{");
		for (int i = 0; i < nfaConfigs.size(); i++) {
			NFAConfig c = (NFAConfig)nfaConfigs.get(i);
			if ( i>0 ) {
				buf.append(", ");
			}
			buf.append(c);
		}
		buf.append("}");
		return buf.toString();
	}	
	
}
