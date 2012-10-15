package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.misc.FlexibleHashMap;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum PredictionMode {
	/** Do only local context prediction (SLL style) and using
	 *  heuristic which almost always works but is much faster
	 *  than precise answer.
	 */
	SLL,

	/** Full LL(*) that always gets right answer. For speed
	 *  reasons, we terminate the prediction process when we know for
	 *  sure which alt to predict. We don't always know what
	 *  the ambiguity is in this mode.
	 */
	LL,

	/** Tell the full LL prediction algorithm to pursue lookahead until
	 *  it has uniquely predicted an alternative without conflict or it's
	 *  certain that it's found an ambiguous input sequence.  when this
	 *  variable is false. When true, the prediction process will
	 *  continue looking for the exact ambiguous sequence even if
	 *  it has already figured out which alternative to predict.
	 */
	LL_EXACT_AMBIG_DETECTION;

	/** A Map that uses just the state and the stack context as the key. */
	static class AltAndContextMap extends FlexibleHashMap<ATNConfig,BitSet> {
		/** Code is function of (s, _, ctx, _) */
		@Override
		public int hashCode(ATNConfig o) {
			int hashCode = 7;
			hashCode = 31 * hashCode + o.state.stateNumber;
			hashCode = 31 * hashCode + o.context.hashCode();
	        return hashCode;
		}

		@Override
		public boolean equals(ATNConfig a, ATNConfig b) {
			if ( a==b ) return true;
			if ( a==null || b==null ) return false;
			if ( hashCode(a) != hashCode(b) ) return false;
			return a.state.stateNumber==b.state.stateNumber
				&& b.context.equals(b.context);
		}
	}

	/**
	 SLL prediction termination.

	 There are two cases: the usual combined SLL+LL parsing and
	 pure SLL parsing that has no fail over to full LL.

	 COMBINED SLL+LL PARSING

	 SLL can decide to give up any point, even immediately,
	 failing over to full LL.  To be as efficient as possible,
	 though, SLL should fail over only when it's positive it can't get
	 anywhere on more lookahead without seeing a conflict.

	 Assuming combined SLL+LL parsing, an SLL confg set with only
	 conflicting subsets should failover to full LL, even if the
	 config sets don't resolve to the same alternative like {1,2}
	 and {3,4}.  If there is at least one nonconflicting set of
	 configs, SLL could continue with the hopes that more lookahead
	 will resolve via one of those nonconflicting configs.

	 Here's the prediction termination rule them: SLL (for SLL+LL
	 parsing) stops when it sees only conflicting config subsets.
	 In contrast, full LL keeps going when there is uncertainty.

	 HEURISTIC

	 As a heuristic, we stop prediction when we see any conflicting subset
	 unless we see a state that only has one alternative associated with
	 it. The single-alt-state thing lets prediction continue upon rules
	 like (otherwise, it would admit defeat too soon):

	 // [12|1|[], 6|2|[], 12|2|[]].
	 s : (ID | ID ID?) ';' ;

	 When the ATN simulation reaches the state before ';', it has a DFA
	 state that looks like: [12|1|[], 6|2|[], 12|2|[]]. Naturally 12|1|[]
	 and 12|2|[] conflict, but we cannot stop processing this node because
	 alternative to has another way to continue, via [6|2|[]].

	 It also let's us continue for this rule:

	 // [1|1|[], 1|2|[], 8|3|[]]
	 a : A | A | A B ;

	 After matching input A, we reach the stop state for rule A, state 1.
	 State 8 is the state right before B. Clearly alternatives 1 and 2
	 conflict and no amount of further lookahead will separate the two.
	 However, alternative 3 will be able to continue and so we do not stop
	 working on this state. In the previous example, we're concerned with
	 states associated with the conflicting alternatives. Here alt 3 is not
	 associated with the conflicting configs, but since we can continue
	 looking for input reasonably, don't declare the state done.

	 PURE SLL PARSING

	 To handle pure SLL parsing, all we have to do is make sure that we
	 combine stack contexts for configurations that differ only by semantic
	 predicate. From there, we can do the usual SLL termination heuristic.

	 PREDICATES IN SLL+LL PARSING

	 SLL decisions don't evaluate predicates until after they reach DFA
	 stop states because they need to create the DFA cache that
	 works in all (semantic) situations.  (In contrast, full LL
	 evaluates predicates collected during start state computation
	 so it can ignore predicates thereafter.) This means that SLL
	 termination detection can totally ignore semantic predicates.

	 Of course, implementation-wise, ATNConfigSets combine stack
	 contexts but not semantic predicate contexts so we might see
	 two configs like this:

	 (s, 1, x, {}), (s, 1, x', {p})

	 Before testing these configurations against others, we have
	 to merge x and x' (w/o modifying the existing configs). For
	 example, we test (x+x')==x'' when looking for conflicts in
	 the following configs.

	 (s, 1, x, {}), (s, 1, x', {p}), (s, 2, x'', {})

	 If the configuration set has predicates, which we can test
	 quickly, this algorithm makes a copy of the configs and
	 strip out all of the predicates so that a standard
	 ATNConfigSet will merge everything ignoring
	 predicates.
	*/
	public static boolean hasSLLConflictTerminatingPrediction(PredictionMode mode, @NotNull ATNConfigSet configs) {
		// pure SLL mode parsing
		if ( mode == PredictionMode.SLL ) {
			// Don't bother with combining configs from different semantic
			// contexts if we can fail over to full LL; costs more time
			// since we'll often fail over anyway.
			if ( configs.hasSemanticContext ) {
				// dup configs, tossing out semantic predicates
				ATNConfigSet dup = new ATNConfigSet();
				for (ATNConfig c : configs) {
					c = new ATNConfig(c,SemanticContext.NONE);
					dup.add(c);
				}
				configs = dup;
			}
			// now we have combined contexts for configs with dissimilar preds
		}

		// pure SLL or combined SLL+LL mode parsing

		Collection<BitSet> altsets = getConflictingAltSubsets(configs);
		boolean heuristic =
			hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
		return heuristic;
	}


	/**
	 Full LL prediction termination.

	 Can we stop looking ahead during ATN simulation or is there some
	 uncertainty as to which alternative we will ultimately pick, after
	 consuming more input?  Even if there are partial conflicts, we might
	 know that everything is going to resolve to the same minimum
	 alt. That means we can stop since no more lookahead will change that
	 fact. On the other hand, there might be multiple conflicts that
	 resolve to different minimums.  That means we need more look ahead to
	 decide which of those alternatives we should predict.

	 The basic idea is to split the set of configurations, C, into
	 conflicting (s, _, ctx, _) subsets and singleton subsets with
	 non-conflicting configurations. Two config's conflict if they have
	 identical state and rule stack contexts but different alternative
	 numbers: (s, i, ctx, _), (s, j, ctx, _) for i!=j.

	 Reduce these config subsets to the set of possible alternatives. You
	 can compute the alternative subsets in one go as follows:

	    A_s,ctx = {i | (s, i, ctx, _) for in C holding s, ctx fixed}

	 Or in pseudo-code:

	 for c in C:
	    map[c] U= c.alt  # map hash/equals uses s and x, not alt and not pred

	 Then map.values is the set of A_s,ctx sets.

	 If |A_s,ctx|=1 then there is no conflict associated with s and ctx.

	 Reduce the subsets to singletons by choosing a minimum of each subset.
	 If the union of these alternatives sets is a singleton, then no amount
	 of more lookahead will help us. We will always pick that
	 alternative. If, however, there is more than one alternative, then we
	 are uncertain which alt to predict and must continue looking for
	 resolution. We may or may not discover an ambiguity in the future,
	 even if there are no conflicting subsets this round.

	 The biggest sin is to terminate early because it means we've made a
	 decision but were uncertain as to the eventual outcome. We haven't
	 used enough lookahead.  On the other hand, announcing a conflict too
	 late is no big deal; you will still have the conflict. It's just
	 inefficient. It	might even look	until the end of file.

	 Semantic predicates for full LL aren't involved in this decision
	 because the predicates are evaluated during start state computation.
	 This set of configurations was derived from the initial subset with
	 configurations holding false predicate stripped out.

	 CONFLICTING CONFIGS

	 Two configurations, (s, i, x) and (s, j, x'), conflict when i!=j but
	 x = x'. Because we merge all (s, i, _) configurations together, that
	 means that there are at most n configurations associated with state s
	 for n possible alternatives in the decision. The merged stacks
	 complicate the comparison of config contexts, x and x'. Sam checks to
	 see if one is a subset of the other by calling merge and checking to
	 see if the merged result is either x or x'. If the x associated with
	 lowest alternative i is the superset, then i is the only possible
	 prediction since the others resolve to min i as well. If, however, x
	 is associated with j>i then at least one stack configuration for j is
	 not in conflict with alt i. The algorithm should keep going, looking
	 for more lookahead due to the uncertainty.

	 For simplicity, I'm doing a equality check between x and x' that lets
	 the algorithm continue to consume lookahead longer than necessary.
	 The reason I like the equality is of course the simplicity but also
	 because that is the test you need to detect the alternatives that are
	 actually in conflict.

	 CONTINUE/STOP RULE

	 Continue if union of resolved alt sets from nonconflicting and
	 conflicting alt subsets has more than one alt.  We are uncertain about
	 which alternative to predict.

	 The complete set of alternatives, [i for (_,i,_)], tells us
	 which alternatives are still in the running for the amount of input
	 we've consumed at this point. The conflicting sets let us to strip
	 away configurations that won't lead to more states (because we
	 resolve conflicts to the configuration with a minimum alternate for
	 given conflicting set.)

	 CASES:

	 * no conflicts & > 1 alt in set => continue

	 * (s, 1, x), (s, 2, x), (s, 3, z)
	   (s', 1, y), (s', 2, y)
	   yields nonconflicting set {3} U conflicting sets min({1,2}) U min({1,2}) = {1,3}
	     => continue

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   (s'', 1, z)
	   yields nonconflicting set you this {1} U conflicting sets  min({1,2}) U min({1,2}) = {1}
	     => stop and predict 1

	 * (s, 1, x), (s, 2, x),
	   (s', 1, y), (s', 2, y)
	   yields conflicting, reduced sets {1} U {1} = {1}
	     => stop and predict 1, can announce ambiguity {1,2}

	 * (s, 1, x), (s, 2, x)
	   (s', 2, y), (s', 3, y)
	   yields conflicting, reduced sets {1} U {2} = {1,2}
	     => continue

	 * (s, 1, x), (s, 2, x)
	   (s', 3, y), (s', 4, y)
	   yields conflicting, reduced sets {1} U {3} = {1,3}
	     => continue

	 EXACT AMBIGUITY DETECTION

	 If all states report the same conflicting alt set, then we know we
	 have the real ambiguity set:

	     |A_i|>1 and A_i = A_j for all i, j.

	 In other words, we continue examining lookahead until all A_i have
	 more than one alt and all A_i are the same. If A={{1,2}, {1,3}}, then
	 regular LL prediction would terminate because the resolved set is
	 {1}. To determine what the real ambiguity is, we have to know whether
	 the ambiguity is between one and two or one and three so we keep
	 going. We can only stop prediction when we need exact ambiguity
	 detection when the sets look like A={{1,2}} or {{1,2},{1,2}} etc...
	 */
	public static boolean resolvesToJustOneViableAlt(Collection<BitSet> altsets) {
		return !hasMoreThanOneViableAlt(altsets);
	}

	public static boolean allSubsetsConflict(Collection<BitSet> altsets) {
		return !hasNonConflictingAltSet(altsets);
	}

	/** return (there exists len(A_i)==1 for some A_i in altsets A) */
	public static boolean hasNonConflictingAltSet(Collection<BitSet> altsets) {
		for (BitSet alts : altsets) {
			if ( alts.cardinality()==1 ) {
				return true;
			}
		}
		return false;
	}

	/** return (there exists len(A_i)>1 for some A_i in altsets A) */
	public static boolean hasConflictingAltSet(Collection<BitSet> altsets) {
		for (BitSet alts : altsets) {
			if ( alts.cardinality()>1 ) {
				return true;
			}
		}
		return false;
	}

	public static boolean allSubsetsEqual(Collection<BitSet> altsets) {
		Iterator<BitSet> it = altsets.iterator();
		BitSet first = it.next();
		while ( it.hasNext() ) {
			BitSet next = it.next();
			if ( !next.equals(first) ) return false;
		}
		return true;
	}


	public static int getUniqueAlt(Collection<BitSet> altsets) {
		BitSet all = getAlts(altsets);
		if ( all.cardinality()==1 ) return all.nextSetBit(0);
		return ATN.INVALID_ALT_NUMBER;
	}

	public static BitSet getAlts(Collection<BitSet> altsets) {
		BitSet all = new BitSet();
		for (BitSet alts : altsets) {
			all.or(alts);
		}
		return all;
	}

	/**
	 *  This function gets the conflicting alt subsets from a configuration set.
	 *    for c in configs:
	 *      map[c] U= c.alt  # map hash/equals uses s and x, not alt and not pred
     */
	public static Collection<BitSet> getConflictingAltSubsets(ATNConfigSet configs) {
		AltAndContextMap configToAlts = new AltAndContextMap();
		for (ATNConfig c : configs) {
			BitSet alts = configToAlts.get(c);
			if ( alts==null ) {
				alts = new BitSet();
				configToAlts.put(c, alts);
			}
			alts.set(c.alt);
		}
		return configToAlts.values();
	}

	/** Get a map from state to alt subset from a configuration set.
	 *  for c in configs:
     *    map[c.state] U= c.alt
	 */
	public static Map<ATNState, BitSet> getStateToAltMap(ATNConfigSet configs) {
		Map<ATNState, BitSet> m = new HashMap<ATNState, BitSet>();
		for (ATNConfig c : configs) {
			BitSet alts = m.get(c.state);
			if ( alts==null ) {
				alts = new BitSet();
				m.put(c.state, alts);
			}
			alts.set(c.alt);
		}
		return m;
	}

	public static boolean hasStateAssociatedWithOneAlt(ATNConfigSet configs) {
		Map<ATNState, BitSet> x = getStateToAltMap(configs);
		for (BitSet alts : x.values()) {
			if ( alts.cardinality()==1 ) return true;
		}
		return false;
	}

	public static boolean hasMoreThanOneViableAlt(Collection<BitSet> altsets) {
		BitSet viableAlts = new BitSet();
		for (BitSet alts : altsets) {
			int minAlt = alts.nextSetBit(0);
			viableAlts.set(minAlt);
			if ( viableAlts.cardinality()>1 ) { // more than 1 viable alt
				return true;
			}
		}
		return false;
	}

}
