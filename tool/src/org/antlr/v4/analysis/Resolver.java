package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.misc.Utils;
import org.stringtemplate.v4.misc.MultiMap;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Code "module" that knows how to resolve LL(*) nondeterminisms. */
public class Resolver {
	PredicateResolver semResolver;

	/** Track all DFA states with ambiguous configurations.
	 *  By reaching the same DFA state, a path through the NFA for some input
	 *  is able to reach the same NFA state by starting at more than one
	 *  alternative's left edge. If the context is the same or conflicts,
	 *  then we have ambiguity. If the context is different, it's simply
	 *  nondeterministic and we should keep looking for edges that will
	 *  render it deterministic. If we run out of things to add to the DFA,
	 *  we'll get a dangling state; it's non-LL(*). Later we may find that predicates
	 *  resolve the issue, but track ambiguous states anyway.
	 */
	public Set<DFAState> ambiguousStates = new HashSet<DFAState>();

	/** The set of states w/o emanating edges (and w/o resolving sem preds). */
	public Set<DFAState> danglingStates = new HashSet<DFAState>();

	/** Was a syntactic ambiguity resolved with predicates?  Any DFA
	 *  state that predicts more than one alternative, must be resolved
	 *  with predicates or it should be reported to the user.
	 */
	public Set<DFAState> resolvedWithSemanticPredicates = new HashSet<DFAState>();
	
	public Resolver() {
		//this.converter = converter;
		semResolver = new PredicateResolver();
	}
	
	/** Walk each NFA configuration in this DFA state looking for a conflict
	 *  where (s|i|ctx1) and (s|j|ctx2) exist such that ctx1 and ctx2 conflict.
	 *  That indicates that state s predicts alts i and j.  Return an Integer set
	 *  of the alternative numbers that conflict.  Two contexts conflict if
	 *  they are equal or one is a stack suffix of the other or one is
	 *  the empty context.  The conflict is a true ambiguity. No amount
	 *  of further looking in grammar will resolve issue (only preds help).
	 *
	 *  Use a hash table to record the lists of configs for each state
	 *  as they are encountered.  We need only consider states for which
	 *  there is more than one configuration.  The configurations' predicted
	 *  alt must be different or must have different contexts to avoid a
	 *  conflict.
	 *
	 *  We check exact config match only in closure-busy test so we'll see
	 *  empty and nonempty contexts here for same state and alt; e.g.,
	 *  (s|i|$) and (s|i|[21 3 $]).
	 *
	 *  TODO: suffix degenerates to one empty one nonempty; avoid some tests?
	 *  TODO: or perhaps check if i, j are already in and don't do compare?
	 */
	public static Set<Integer> getAmbiguousAlts(DFAState d) {
		//System.out.println("getNondetAlts for DFA state "+stateNumber);
		 Set<Integer> ambiguousAlts = new HashSet<Integer>();

		// If only 1 NFA conf then no way it can be nondeterministic;
		// save the overhead.  There are many o-a->o NFA transitions
		// and so we save a hash map and iterator creation for each
		// state.
		int numConfigs = d.nfaConfigs.size();
		if ( numConfigs<=1 ) return null;

		// First get a list of configurations for each state.
		// Most of the time, each state will have one associated configuration.
		MultiMap<Integer, NFAConfig> stateToConfigListMap =
			new MultiMap<Integer, NFAConfig>();
		for (NFAConfig c : d.nfaConfigs) {
			stateToConfigListMap.map(Utils.integer(c.state.stateNumber), c);
		}

		// potential conflicts are states with > 1 configuration and diff alts
		boolean thisStateHasPotentialProblem = false;
		for (List<NFAConfig> configsForState : stateToConfigListMap.values()) {
			if ( configsForState.size()>1 ) {
				int predictedAlt = Resolver.getUniqueAlt(configsForState);
				if ( predictedAlt > 0 ) {
					// remove NFA state's configurations from
					// further checking; no issues with it
					// (can't remove as it's concurrent modification; set to null)
					stateToConfigListMap.put(configsForState.get(0).state.stateNumber, null);
				}
				else {
					thisStateHasPotentialProblem = true;
				}
			}
		}

		// a fast check for potential issues; most states have none
		if ( !thisStateHasPotentialProblem ) return null;

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
		for (List<NFAConfig> configsForState : stateToConfigListMap.values()) {
			// compare each configuration pair s, t to ensure:
			// s.ctx different than t.ctx if s.alt != t.alt
			int numConfigsForState = 0;
			if ( configsForState!=null ) numConfigsForState = configsForState.size();
			for (int i = 0; i < numConfigsForState; i++) {
				NFAConfig s = (NFAConfig) configsForState.get(i);
				for (int j = i+1; j < numConfigsForState; j++) {
					NFAConfig t = (NFAConfig)configsForState.get(j);
					// conflicts means s.ctx==t.ctx or s.ctx is a stack
					// suffix of t.ctx or vice versa (if alts differ).
					// Also a conflict if s.ctx or t.ctx is empty
					// TODO: might be faster to avoid suffix test if i or j already in ambiguousAlts
					// that set is usually 2 alts, maybe three.  Use a List.
					boolean altConflict = s.alt != t.alt;
					boolean ctxConflict = (s.context==null && t.context!=null) ||
										  (s.context!=null && t.context==null) ||
										  (s.context.suffix(t.context) || s.context.equals(t.context));
					if ( altConflict && ctxConflict ) {
						//System.out.println("ctx conflict between "+s+" and "+t);
						ambiguousAlts.add(s.alt);
						ambiguousAlts.add(t.alt);
					}
				}
			}
		}

		if ( ambiguousAlts.size()==0 ) return null;
		return ambiguousAlts;
	}

	public void resolveAmbiguities(DFAState d) {
		if ( PredictionDFAFactory.debug ) {
			System.out.println("resolveNonDeterminisms "+d.toString());
		}
		Set<Integer> ambiguousAlts = getAmbiguousAlts(d);
		if ( PredictionDFAFactory.debug && ambiguousAlts!=null ) {
			System.out.println("ambig alts="+ambiguousAlts);
		}

		// if no problems return
		if ( ambiguousAlts==null ) return;

		ambiguousStates.add(d);

		// ATTEMPT TO RESOLVE WITH SEMANTIC PREDICATES
		boolean resolved =
			semResolver.tryToResolveWithSemanticPredicates(d, ambiguousAlts);
		if ( resolved ) {
			if ( PredictionDFAFactory.debug ) {
				System.out.println("resolved DFA state "+d.stateNumber+" with pred");
			}
			d.resolvedWithPredicates = true;
			resolvedWithSemanticPredicates.add(d);
			return;
		}

		// RESOLVE SYNTACTIC CONFLICT BY REMOVING ALL BUT ONE ALT
		resolveByPickingMinAlt(d, ambiguousAlts);
	}

	public void resolveDeadState(DFAState d) {
		if ( d.resolvedWithPredicates || d.getNumberOfEdges()>0 ) return;
		
		System.err.println("dangling DFA state "+d+" after reach / closures");
		danglingStates.add(d);
		// turn off all configurations except for those associated with
		// min alt number; somebody has to win else some input will not
		// predict any alt.
		int minAlt = resolveByPickingMinAlt(d, null);
		// force it to be an accept state
		d.isAcceptState = true;
		d.predictsAlt = minAlt;
		// might be adding new accept state for alt, but that's ok
		d.dfa.addAcceptState(minAlt, d);
	}

	/** Turn off all configurations associated with the
	 *  set of incoming alts except the min alt number.
	 *  There may be many alts among the configurations but only turn off
	 *  the ones with problems (other than the min alt of course).
	 *
	 *  If alts is null then turn off all configs 'cept those
	 *  associated with the minimum alt.
	 *
	 *  Return the min alt found.
	 */
	static int resolveByPickingMinAlt(DFAState d, Set<Integer> alts) {
		int min = 0;
		if ( alts !=null ) {
			min = getMinAlt(alts);
		}
		else {
			min = d.getMinAlt();
		}

		turnOffOtherAlts(d, min, alts);

		return min;
	}

	/** turn off all states associated with alts other than the good one
	 *  (as long as they are one of the ones in alts)
	 */
	static void turnOffOtherAlts(DFAState d, int min, Set<Integer> alts) {
		int numConfigs = d.nfaConfigs.size();
		for (int i = 0; i < numConfigs; i++) {
			NFAConfig configuration = d.nfaConfigs.get(i);
			if ( configuration.alt!=min ) {
				if ( alts==null ||
					 alts.contains(configuration.alt) )
				{
					configuration.resolved = true;
				}
			}
		}
	}

	public static int getMinAlt(Set<Integer> alts) {
		int min = Integer.MAX_VALUE;
		for (Integer altI : alts) {
			int alt = altI.intValue();
			if ( alt < min ) min = alt;
		}
		return min;
	}

	public static int getUniqueAlt(Collection<NFAConfig> nfaConfigs) {
		return getUniqueAlt(nfaConfigs, true);
	}

	public static int getUniqueAlt(Collection<NFAConfig> nfaConfigs,
								   boolean ignoreResolvedBit)
	{
		int alt = NFA.INVALID_ALT_NUMBER;
		for (NFAConfig c : nfaConfigs) {
			if ( !ignoreResolvedBit && c.resolved ) continue;
			if ( alt==NFA.INVALID_ALT_NUMBER ) {
				alt = c.alt; // found first alt
			}
			else if ( c.alt!=alt ) {
				return NFA.INVALID_ALT_NUMBER;
			}
		}
		return alt;
	}


	/*
	void issueRecursionWarnings() {
		// RECURSION OVERFLOW
		Set dfaStatesWithRecursionProblems =
			converter.stateToRecursionOverflowConfigurationsMap.keySet();
		// now walk truly unique (unaliased) list of dfa states with inf recur
		// Goal: create a map from alt to map<target,List<callsites>>
		// Map<Map<String target, List<NFAState call sites>>
		Map<Integer, Map<>> altToT argetToCallSitesMap = new HashMap();
		// track a single problem DFA state for each alt
		Map<Integer, DFAState> altToDFAState = new HashMap<Integer, DFAState>();
		computeAltToProblemMaps(dfaStatesWithRecursionProblems,
								converter.stateToRecursionOverflowConfigurationsMap,
								altToTargetToCallSitesMap, // output param
								altToDFAState);            // output param

		// walk each alt with recursion overflow problems and generate error
		Set<Integer> alts = altToTargetToCallSitesMap.keySet();
		List<Integer> sortedAlts = new ArrayList<Integer>(alts);
		Collections.sort(sortedAlts);
		for (Iterator altsIt = sortedAlts.iterator(); altsIt.hasNext();) {
			Integer altI = (Integer) altsIt.next();
			Map<Integer, > targetToCallSiteMap =
				altToTargetToCallSitesMap.get(altI);
			Set targetRules = targetToCallSiteMap.keySet();
			Collection callSiteStates = targetToCallSiteMap.values();
			DFAState sampleBadState = altToDFAState.get(altI);
			ErrorManager.recursionOverflow(this,
										   sampleBadState,
										   altI.intValue(),
										   targetRules,
										   callSiteStates);
		}
	}

	void computeAltToProblemMaps(Set<DFAState> dfaStatesUnaliased,
								 Map configurationsMap,
								 Map<Integer, NFAState> altToTargetToCallSitesMap,
								 Map altToDFAState)
	{
		for (DFAState d : dfaStatesUnaliased) {
			for (NFAConfig c : d.nfaConfigs) {
				NFAState ruleInvocationState = c.state;
				RuleTransition rt = (RuleTransition)ruleInvocationState.transition(0);
				String targetRule = rt.rule.name;
			}
		}
		for (Iterator it = dfaStatesUnaliased.iterator(); it.hasNext();) {
			Integer stateI = (Integer) it.next();
			// walk this DFA's config list
			List configs = (List)configurationsMap.get(stateI);
			for (int i = 0; i < configs.size(); i++) {
				NFAConfig c = (NFAConfig) configs.get(i);
				NFAState ruleInvocationState = c.state;
				Transition transition0 = ruleInvocationState.transition(0);
				RuleTransition ref = (RuleTransition)transition0;
				String targetRule = ((NFAState) ref.target).rule.name;
				Integer altI = org.antlr.misc.Utils.integer(c.alt);
				Map<Integer, NFAState> targetToCallSiteMap =
					altToTargetToCallSitesMap.get(altI);
				if ( targetToCallSiteMap==null ) {
					targetToCallSiteMap = new HashMap();
					altToTargetToCallSitesMap.put(altI, targetToCallSiteMap);
				}
				Set<NFAState> callSites = targetToCallSiteMap.get(targetRule);
				if ( callSites==null ) {
					callSites = new HashSet();
					targetToCallSiteMap.put(targetRule, callSites);
				}
				callSites.add(ruleInvocationState);
				// track one problem DFA state per alt
				if ( altToDFAState.get(altI)==null ) {
					DFAState sampleBadState = converter.dfa.states.get(stateI.intValue());
					altToDFAState.put(altI, sampleBadState);
				}
			}
		}
	}
	*/

}
