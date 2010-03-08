package org.antlr.v4.automata;

import org.antlr.analysis.NFA;
import org.antlr.v4.misc.Utils;
import org.stringtemplate.v4.misc.MultiMap;

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

	/** State in which DFA? */
	public DFA dfa;

	/** Track the transitions emanating from this DFA state. */
	protected List<Edge> edges =
		new ArrayList<Edge>(INITIAL_NUM_TRANSITIONS);

	/** The set of NFA configurations (state,alt,context) for this DFA state */
	public OrderedHashSet<NFAConfig> nfaConfigs =
		new OrderedHashSet<NFAConfig>();

	int cachedUniquelyPredicatedAlt = NFA.INVALID_ALT_NUMBER;

	public DFAState(DFA dfa) { this.dfa = dfa; }

	public void addNFAConfig(NFAState s, NFAConfig c) {
		if ( nfaConfigs.contains(c) ) return;
		nfaConfigs.add(c);
	}

	public NFAConfig addNFAConfig(NFAState state,
								  int alt,
								  NFAState context)
	{
		NFAConfig c = new NFAConfig(state, alt,	context);
		addNFAConfig(state, c);
		return c;
	}

	/** Walk each NFA configuration in this DFA state looking for a conflict
	 *  where (s|i|ctx) and (s|j|ctx) exist, indicating that state s with
	 *  context conflicting ctx predicts alts i and j.  Return an Integer set
	 *  of the alternative numbers that conflict.  Two contexts conflict if
	 *  they are equal or one is a stack suffix of the other or one is
	 *  the empty context.
	 *
	 *  Use a hash table to record the lists of configs for each state
	 *  as they are encountered.  We need only consider states for which
	 *  there is more than one configuration.  The configurations' predicted
	 *  alt must be different or must have different contexts to avoid a
	 *  conflict.
	 */
	protected Set<Integer> getConflictingAlts() {
		// TODO this is called multiple times: cache result?
		//System.out.println("getNondetAlts for DFA state "+stateNumber);
		 Set<Integer> nondeterministicAlts = new HashSet<Integer>();

		// If only 1 NFA conf then no way it can be nondeterministic;
		// save the overhead.  There are many o-a->o NFA transitions
		// and so we save a hash map and iterator creation for each
		// state.
		int numConfigs = nfaConfigs.size();
		if ( numConfigs <=1 ) {
			return null;
		}

		// First get a list of configurations for each state.
		// Most of the time, each state will have one associated configuration.
		MultiMap<Integer, NFAConfig> stateToConfigListMap =
			new MultiMap<Integer, NFAConfig>();
		for (int i = 0; i < numConfigs; i++) {
			NFAConfig configuration = (NFAConfig) nfaConfigs.get(i);
			Integer stateI = Utils.integer(configuration.state.stateNumber);
			stateToConfigListMap.map(stateI, configuration);
		}
		// potential conflicts are states with > 1 configuration and diff alts
		Set states = stateToConfigListMap.keySet();
		int numPotentialConflicts = 0;
		for (Iterator it = states.iterator(); it.hasNext();) {
			Integer stateI = (Integer) it.next();
			boolean thisStateHasPotentialProblem = false;
			List configsForState = (List)stateToConfigListMap.get(stateI);
			int alt=0;
			int numConfigsForState = configsForState.size();
			for (int i = 0; i < numConfigsForState && numConfigsForState>1 ; i++) {
				NFAConfig c = (NFAConfig) configsForState.get(i);
				if ( alt==0 ) {
					alt = c.alt;
				}
				else if ( c.alt!=alt ) {
					/*
					System.out.println("potential conflict in state "+stateI+
									   " configs: "+configsForState);
					*/
					numPotentialConflicts++;
					thisStateHasPotentialProblem = true;
				}
			}
			if ( !thisStateHasPotentialProblem ) {
				// remove NFA state's configurations from
				// further checking; no issues with it
				// (can't remove as it's concurrent modification; set to null)
				stateToConfigListMap.put(stateI, null);
			}
		}

		// a fast check for potential issues; most states have none
		if ( numPotentialConflicts==0 ) {
			return null;
		}

		// we have a potential problem, so now go through config lists again
		// looking for different alts (only states with potential issues
		// are left in the states set).  Now we will check context.
		// For example, the list of configs for NFA state 3 in some DFA
		// state might be:
		//   [3|2|[28 18 $], 3|1|[28 $], 3|1, 3|2]
		// I want to create a map from context to alts looking for overlap:
		//   [28 18 $] -> 2
		//   [28 $] -> 1
		//   [$] -> 1,2
		// Indeed a conflict exists as same state 3, same context [$], predicts
		// alts 1 and 2.
		// walk each state with potential conflicting configurations
		for (Iterator it = states.iterator(); it.hasNext();) {
			Integer stateI = (Integer) it.next();
			List configsForState = (List)stateToConfigListMap.get(stateI);
			// compare each configuration pair s, t to ensure:
			// s.ctx different than t.ctx if s.alt != t.alt
			int numConfigsForState = 0;
			if ( configsForState!=null ) {
				numConfigsForState = configsForState.size();
			}
			for (int i = 0; i < numConfigsForState; i++) {
				NFAConfig s = (NFAConfig) configsForState.get(i);
				for (int j = i+1; j < numConfigsForState; j++) {
					NFAConfig t = (NFAConfig)configsForState.get(j);
					// conflicts means s.ctx==t.ctx or s.ctx is a stack
					// suffix of t.ctx or vice versa (if alts differ).
					// Also a conflict if s.ctx or t.ctx is empty
					if ( s.alt != t.alt && s.context != t.context ) {
						nondeterministicAlts.add(Utils.integer(s.alt));
						nondeterministicAlts.add(Utils.integer(t.alt));
					}
				}
			}
		}

		if ( nondeterministicAlts.size()==0 ) {
			return null;
		}
		return nondeterministicAlts;
	}

	/** Walk each configuration and if they are all the same alt, return
	 *  that alt else return NFA.INVALID_ALT_NUMBER.  Ignore resolved
	 *  configurations, but don't ignore resolveWithPredicate configs
	 *  because this state should not be an accept state.  We need to add
	 *  this to the work list and then have semantic predicate edges
	 *  emanating from it.
	 */
	public int getUniquelyPredictedAlt() {
		if ( cachedUniquelyPredicatedAlt!=NFA.INVALID_ALT_NUMBER ) {
			return cachedUniquelyPredicatedAlt;
		}
		int alt = NFA.INVALID_ALT_NUMBER;
		for (NFAConfig c : nfaConfigs) {
			if ( alt== NFA.INVALID_ALT_NUMBER ) {
				alt = c.alt; // found first nonresolved alt
			}
			else if ( c.alt!=alt ) {
				return NFA.INVALID_ALT_NUMBER;
			}
		}
		this.cachedUniquelyPredicatedAlt = alt;
		return alt;
	}

	/** Return the uniquely mentioned alt from the NFA configurations;
	 *  Ignore the resolved bit etc...  Return INVALID_ALT_NUMBER
	 *  if there is more than one alt mentioned.
	 */
	public int getUniqueAlt() {
		int alt = NFA.INVALID_ALT_NUMBER;
		for (NFAConfig c : nfaConfigs) {
			if ( alt== NFA.INVALID_ALT_NUMBER ) {
				alt = c.alt; // found first alt
			}
			else if ( c.alt!=alt ) {
				return NFA.INVALID_ALT_NUMBER;
			}
		}
		return alt;
	}

	/** Get the set of all alts mentioned by all NFA configurations in this
	 *  DFA state.
	 */
	public Set<Integer> getAltSet() {
		Set<Integer> alts = new HashSet<Integer>();
		for (NFAConfig c : nfaConfigs) {
			alts.add(Utils.integer(c.alt));
		}
		if ( alts.size()==0 ) return null;
		return alts;
	}

	public Set<NFAState> getUniqueNFAStates() {
		Set<NFAState> alts = new HashSet<NFAState>();
		for (NFAConfig c : nfaConfigs) alts.add(c.state);
		if ( alts.size()==0 ) return null;
		return alts;
	}

	public int getNumberOfTransitions() { return edges.size(); }

	public void addTransition(Edge e) { edges.add(e); }

	public Edge transition(int i) { return edges.get(i); }

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
		DFAState other = (DFAState)o;
		return this.nfaConfigs.equals(other.nfaConfigs);
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
