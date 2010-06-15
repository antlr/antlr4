package org.antlr.v4.automata;

import org.antlr.v4.analysis.NFAConfig;
import org.antlr.v4.analysis.Resolver;
import org.antlr.v4.analysis.SemanticContext;
import org.antlr.v4.misc.IntSet;
import org.antlr.v4.misc.OrderedHashSet;

import java.util.*;

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
	public OrderedHashSet<NFAConfig> nfaConfigs = new OrderedHashSet<NFAConfig>();

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
	}

	public void addNFAConfig(NFAConfig c) {
		if ( nfaConfigs.contains(c) ) return;
		nfaConfigs.add(c);
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
		OrderedHashSet<NFAState> alts = new OrderedHashSet<NFAState>();
		for (NFAConfig c : nfaConfigs) {
			if ( alt==NFA.INVALID_ALT_NUMBER || c.alt==alt ) alts.add(c.state);
		}
		if ( alts.size()==0 ) return null;
		return alts;
	}

	public Map<Integer, SemanticContext> getPredicatesForAlts() {
		// map alt to combined SemanticContext
		Map<Integer, SemanticContext> altToPredicateContextMap =
			new HashMap<Integer, SemanticContext>();
		Set<Integer> alts = getAltSet();
		for (Integer alt : alts) {
			SemanticContext ctx = getPredicatesForAlt(alt);
			altToPredicateContextMap.put(alt, ctx);
		}
		return altToPredicateContextMap;
	}

	public SemanticContext getPredicatesForAlt(int alt) {
		SemanticContext preds = null;
		for (NFAConfig c : nfaConfigs) {
			if ( c.alt == alt &&
				 c.semanticContext!=SemanticContext.EMPTY_SEMANTIC_CONTEXT )
			{
				if ( preds == null ) preds = c.semanticContext;
				else preds = SemanticContext.or(preds, c.semanticContext);
			}
		}
		return preds;
	}

	public List<NFAConfig> getNFAConfigsForAlt(int alt) {
		List<NFAConfig> configs = new ArrayList<NFAConfig>();
		for (NFAConfig c : nfaConfigs) {
			if ( c.alt == alt ) configs.add(c);
		}
		return configs;
	}

	/** For gated productions, we need an OR'd list of all predicates for the
	 *  target of an edge so we can gate the edge based upon the predicates
	 *  associated with taking that path (if any).
	 *
	 *  For syntactic predicates, we only want to generate predicate
	 *  evaluations as we transitions to an accept state; it's a waste to
	 *  do it earlier.  So, only add gated preds derived from manually-
	 *  specified syntactic predicates if this is an accept state.
	 *
	 *  Also, since configurations w/o gated predicates are like true
	 *  gated predicates, finding a configuration whose alt has no gated
	 *  predicate implies we should evaluate the predicate to true. This
	 *  means the whole edge has to be ungated. Consider:
	 *
	 *	 X : ('a' | {p}?=> 'a')
	 *	   | 'a' 'b'
	 *	   ;
	 *
	 *  Here, you 'a' gets you from s0 to s1 but you can't test p because
	 *  plain 'a' is ok.  It's also ok for starting alt 2.  Hence, you can't
	 *  test p.  Even on the edge going to accept state for alt 1 of X, you
	 *  can't test p.  You can get to the same place with and w/o the context.
	 *  Therefore, it is never ok to test p in this situation.
	 */
	public SemanticContext getGatedPredicatesInNFAConfigurations() {
		SemanticContext unionOfPredicatesFromAllAlts = null;
		for (NFAConfig c : nfaConfigs) {
			SemanticContext gatedPredExpr =
				c.semanticContext.getGatedPredicateContext();
			if ( gatedPredExpr==null ) {
				// if we ever find a configuration w/o a gated predicate
				// (even if it's a nongated predicate), we cannot gate
				// the indident edges.
				return null;
			}
			else if ( isAcceptState || !c.semanticContext.isSyntacticPredicate() ) {
				// at this point we have a gated predicate and, due to elseif,
				// we know it's an accept and not a syn pred.  In this case,
				// it's safe to add the gated predicate to the union.  We
				// only want to add syn preds if it's an accept state.  Other
				// gated preds can be used with edges leading to accept states.
				if ( unionOfPredicatesFromAllAlts==null ) {
					unionOfPredicatesFromAllAlts = gatedPredExpr;
				}
				else {
					unionOfPredicatesFromAllAlts =
						SemanticContext.or(unionOfPredicatesFromAllAlts,gatedPredExpr);
				}
			}
		}
		if ( unionOfPredicatesFromAllAlts instanceof SemanticContext.TruePredicate ) {
			return null;
		}
		return unionOfPredicatesFromAllAlts;
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
