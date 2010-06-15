package org.antlr.v4.analysis;

import org.antlr.runtime.Token;
import org.antlr.v4.automata.BasicState;
import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.Label;
import org.antlr.v4.automata.NFAState;
import org.antlr.v4.misc.BitSet;
import org.antlr.v4.tool.ErrorManager;

import java.util.*;

/** */
public class PredicateResolver {
	public Map<DFAState, List<Integer>> statesWithIncompletelyCoveredAlts = new HashMap<DFAState, List<Integer>>();

	/** See if a set of nondeterministic alternatives can be disambiguated
	 *  with the semantic predicate contexts of the alternatives.
	 *
	 *  Without semantic predicates, syntactic conflicts are resolved
	 *  by simply choosing the first viable alternative.  In the
	 *  presence of semantic predicates, you can resolve the issue by
	 *  evaluating boolean expressions at run time.  During analysis,
	 *  this amounts to suppressing grammar error messages to the
	 *  developer.  NFA configurations are always marked as "to be
	 *  resolved with predicates" so that DFA.reach() will know to ignore
	 *  these configurations and add predicate transitions to the DFA
	 *  after adding edge labels.
	 *
	 *  During analysis, we can simply make sure that for n
	 *  ambiguously predicted alternatives there are at least n-1
	 *  unique predicate sets.  The nth alternative can be predicted
	 *  with "not" the "or" of all other predicates.  NFA configurations without
	 *  predicates are assumed to have the default predicate of
	 *  "true" from a user point of view.  When true is combined via || with
	 *  another predicate, the predicate is a tautology and must be removed
	 *  from consideration for disambiguation:
	 *
	 *  a : b | B ; // hoisting p1||true out of rule b, yields no predicate
	 *  b : {p1}? B | B ;
	 *
	 *  This is done down in getPredicatesPerNonDeterministicAlt().
	 */
	public boolean tryToResolveWithSemanticPredicates(DFAState d,
											   Set<Integer> ambiguousAlts)
	{
		Map<Integer, SemanticContext> altToPredMap =
			getPredicatesPerAmbiguousAlt(d, ambiguousAlts);

		if ( altToPredMap.size()==0 ) return false;

		//System.out.println("nondeterministic alts with predicates: "+altToPredMap);

		if ( ambiguousAlts.size()-altToPredMap.size()>1 ) {
			// too few predicates to resolve; just return.
			// We caught/tracked incompletely covered preds in getPredicatesPerNonDeterministicAlt
			return false;
		}

		// Handle case where 1 predicate is missing
		// Case 1. Semantic predicates
		// If the missing pred is on nth alt, !(union of other preds)==true
		// so we can avoid that computation.  If naked alt is ith, then must
		// test it with !(union) since semantic predicated alts are order
		// independent
		// Case 2: Syntactic predicates
		// The naked alt is always assumed to be true as the order of
		// alts is the order of precedence.  The naked alt will be a tautology
		// anyway as it's !(union of other preds).  This implies
		// that there is no such thing as noviable alt for synpred edges
		// emanating from a DFA state.
		if ( altToPredMap.size()==ambiguousAlts.size()-1 ) {
			// if there are n-1 predicates for n nondeterministic alts, can fix
			BitSet ndSet = BitSet.of(ambiguousAlts);
			BitSet predSet = BitSet.of(altToPredMap);
			int nakedAlt = ndSet.subtract(predSet).getSingleElement();
			SemanticContext nakedAltPred = null;
			if ( nakedAlt == Collections.max(ambiguousAlts) ) {
				// the naked alt is the last nondet alt and will be the default clause
				nakedAltPred = new SemanticContext.TruePredicate();
			}
			else {
				// pretend naked alternative is covered with !(union other preds)
				// unless it's a synpred since those have precedence same
				// as alt order
				SemanticContext unionOfPredicatesFromAllAlts =
					getUnionOfPredicates(altToPredMap);
				//System.out.println("all predicates "+unionOfPredicatesFromAllAlts);
				if ( unionOfPredicatesFromAllAlts.isSyntacticPredicate() ) {
					nakedAltPred = new SemanticContext.TruePredicate();
				}
				else {
					nakedAltPred =
						SemanticContext.not(unionOfPredicatesFromAllAlts);
				}
			}

			//System.out.println("covering naked alt="+nakedAlt+" with "+nakedAltPred);

			altToPredMap.put(nakedAlt, nakedAltPred);
			// set all config with alt=nakedAlt to have the computed predicate
			int numConfigs = d.nfaConfigs.size();
			for (int i = 0; i < numConfigs; i++) {
				NFAConfig configuration = (NFAConfig)d.nfaConfigs.get(i);
				if ( configuration.alt == nakedAlt ) {
					configuration.semanticContext = nakedAltPred;
				}
			}
		}

		if ( altToPredMap.size()==ambiguousAlts.size() ) {
			// RESOLVE CONFLICT by picking one NFA configuration for each alt
			// and setting its resolvedWithPredicate flag
			// First, prevent a recursion warning on this state due to
			// pred resolution
//			if ( d.abortedDueToRecursionOverflow ) {
//				d.dfa.probe.removeRecursiveOverflowState(d);
//			}
			for (NFAConfig c : d.nfaConfigs) {
				SemanticContext semCtx = altToPredMap.get(c.alt);
				if ( semCtx!=null ) {
					// resolve (first found) with pred
					// and remove alt from problem list
					c.resolvedWithPredicate = true;
					c.semanticContext = semCtx; // reset to combined
					altToPredMap.remove(c.alt);

					// notify grammar that we've used the preds contained in semCtx
//					if ( semCtx.isSyntacticPredicate() ) {
//						dfa.nfa.grammar.synPredUsedInDFA(dfa, semCtx);
//					}
				}
				else if ( ambiguousAlts.contains(c.alt) ) {
					// resolve all other configurations for nondeterministic alts
					// for which there is no predicate context by turning it off
					c.resolved = true;
				}
			}
			return true;
		}

		return false;  // couldn't fix the problem with predicates
	}

	/** Return a mapping from nondeterministc alt to combined list of predicates.
	 *  If both (s|i|semCtx1) and (t|i|semCtx2) exist, then the proper predicate
	 *  for alt i is semCtx1||semCtx2 because you have arrived at this single
	 *  DFA state via two NFA paths, both of which have semantic predicates.
	 *  We ignore deterministic alts because syntax alone is sufficient
	 *  to predict those.  Do not include their predicates.
	 *
	 *  Alts with no predicate are assumed to have {true}? pred.
	 *
	 *  When combining via || with "true", all predicates are removed from
	 *  consideration since the expression will always be true and hence
	 *  not tell us how to resolve anything.  So, if any NFA configuration
	 *  in this DFA state does not have a semantic context, the alt cannot
	 *  be resolved with a predicate.
	 *
	 *  If nonnull, incidentEdgeLabel tells us what NFA transition label
	 *  we did a reach on to compute state d.  d may have insufficient
	 *  preds, so we really want this for the error message.
	 */
	public Map<Integer, SemanticContext> getPredicatesPerAmbiguousAlt(
		DFAState d,
		Set<Integer> ambiguousAlts)
	{
		// map alt to combined SemanticContext
		Map<Integer, SemanticContext> altToPredicateContextMap =
			new HashMap<Integer, SemanticContext>();
		Map<Integer, Set<SemanticContext>> altToSetOfContextsMap =
			new HashMap<Integer, Set<SemanticContext>>();
		for (int alt : ambiguousAlts) {
			altToSetOfContextsMap.put(alt, new HashSet<SemanticContext>());
		}

		// Create a unique set of predicates from configs
		// Also, track the alts with at least one uncovered configuration
		// (one w/o a predicate); tracks tautologies like p1||true
		Set<Integer> ambigAltsWithUncoveredConfiguration = new HashSet<Integer>();
		for (NFAConfig c : d.nfaConfigs) {
			if ( ambiguousAlts.contains(c.alt) ) {
				if ( c.semanticContext != SemanticContext.EMPTY_SEMANTIC_CONTEXT ) {
					Set<SemanticContext> predSet = altToSetOfContextsMap.get(c.alt);
					predSet.add(c.semanticContext);
				}
				else {
					// if no predicate, but it's part of nondeterministic alt
					// then at least one path exists not covered by a predicate.
					// must remove predicate for this alt; track incomplete alts
					ambigAltsWithUncoveredConfiguration.add(c.alt);
				}
			}
		}
		
		// Walk semantic contexts for nondet alts, ORing them together
		// Also, track the list of incompletely covered alts: those alts
		// with at least 1 predicate and at least one configuration w/o a
		// predicate. We want this in order to report to the decision probe.
		List<Integer> incompletelyCoveredAlts = new ArrayList<Integer>();
		for (int alt : ambiguousAlts) {
			Set<SemanticContext> contextsForThisAlt = altToSetOfContextsMap.get(alt);
			if ( ambigAltsWithUncoveredConfiguration.contains(alt) ) { // >= 1 config has no ctx
				if ( contextsForThisAlt.size()>0 ) {    // && at least one pred
					incompletelyCoveredAlts.add(alt);   // this alt incompleted covered
				}
				continue; // don't include; at least 1 config has no ctx
			}
			SemanticContext combinedContext = null;
			for (Iterator itrSet = contextsForThisAlt.iterator(); itrSet.hasNext();) {
				SemanticContext ctx = (SemanticContext) itrSet.next();
				combinedContext =
					SemanticContext.or(combinedContext,ctx);
			}
			altToPredicateContextMap.put(alt, combinedContext);
		}

		if ( incompletelyCoveredAlts.size()>0 ) {
			// track these troublesome states later for reporting.
			statesWithIncompletelyCoveredAlts.put(d, incompletelyCoveredAlts);
		}

		return altToPredicateContextMap;
	}

	public static Map<Integer, Set<Token>> getInsufficientlyPredicatedLocations(DFAState d,
																				List<Integer> incompletelyCoveredAlts)
	{
		Map<Integer, Set<Token>> altToLocationsReachableWithoutPredicate = new HashMap<Integer, Set<Token>>();
		for (NFAConfig c : d.nfaConfigs) {
			if ( incompletelyCoveredAlts.contains(c.alt) &&
				 c.semanticContext == SemanticContext.EMPTY_SEMANTIC_CONTEXT )
			{
				NFAState s = c.state;
				/*
				   System.out.print("nondet config w/o context "+configuration+
									" incident "+(s.incidentEdgeLabel!=null?s.incidentEdgeLabel.toString(dfa.nfa.grammar):null));
				   if ( s.associatedASTNode!=null ) {
					   System.out.print(" token="+s.associatedASTNode.token);
				   }
				   else System.out.println();
				   */
				// We want to report getting to an NFA state with an
				// incoming label, unless it's EOF, w/o a predicate.
				if ( s instanceof BasicState &&
					 ((BasicState)s).incidentTransition!=null &&
					 !((BasicState)s).incidentTransition.label().member(Label.EOF) )
				{
					if ( s.ast==null || s.ast.token==null ) {
						ErrorManager.internalError("no AST/token for nonepsilon target w/o predicate");
					}
					else {
						Set<Token> locations = altToLocationsReachableWithoutPredicate.get(c.alt);
						if ( locations==null ) {
							locations = new HashSet<Token>();
							altToLocationsReachableWithoutPredicate.put(c.alt, locations);
						}
						locations.add(s.ast.token);
					}
				}
			}
		}

		// get new map sorted by alt nums
		Map<Integer, Set<Token>> sortedMap = new LinkedHashMap<Integer, Set<Token>>();
		List<Integer> alts = new ArrayList<Integer>();
		alts.addAll(altToLocationsReachableWithoutPredicate.keySet());
		Collections.sort(alts);
		for (int alt : alts) {
			sortedMap.put(alt, altToLocationsReachableWithoutPredicate.get(alt));
		}

		return sortedMap;
	}

	/** OR together all predicates from the alts.  Note that the predicate
	 *  for an alt could itself be a combination of predicates.
	 */
	public static SemanticContext getUnionOfPredicates(Map altToPredMap) {
		Iterator iter;
		SemanticContext unionOfPredicatesFromAllAlts = null;
		iter = altToPredMap.values().iterator();
		while ( iter.hasNext() ) {
			SemanticContext semCtx = (SemanticContext)iter.next();
			if ( unionOfPredicatesFromAllAlts==null ) {
				unionOfPredicatesFromAllAlts = semCtx;
			}
			else {
				unionOfPredicatesFromAllAlts =
						SemanticContext.or(unionOfPredicatesFromAllAlts,semCtx);
			}
		}
		return unionOfPredicatesFromAllAlts;
	}	

}
