package org.antlr.v4.analysis;

import org.antlr.v4.automata.DFAState;
import org.antlr.v4.automata.NFA;
import org.antlr.v4.automata.NFAConfig;
import org.antlr.v4.misc.Utils;

import java.util.Collection;
import java.util.Set;

/** Code "module" that knows how to resolve LL(*) nondeterminisms. */
public class Resolver {
	public static void resolveNonDeterminisms(DFAState d) {
		if ( NFAToDFAConverter.debug ) {
			System.out.println("resolveNonDeterminisms "+d.toString());
		}
		Set nondeterministicAlts = d.getNonDeterministicAlts();
		if ( NFAToDFAConverter.debug && nondeterministicAlts!=null ) {
			System.out.println("nondet alts="+nondeterministicAlts);
		}

		// if no problems return
		if ( nondeterministicAlts==null ) return;

		// ATTEMPT TO RESOLVE WITH SEMANTIC PREDICATES
//		boolean resolved =
//			tryToResolveWithSemanticPredicates(d, nondeterministicAlts);
//		if ( resolved ) {
//			if ( debug ) {
//				System.out.println("resolved DFA state "+d.stateNumber+" with pred");
//			}
//			d.resolvedWithPredicates = true;
//			dfa.probe.reportNondeterminismResolvedWithSemanticPredicate(d);
//			return;
//		}

		// RESOLVE SYNTACTIC CONFLICT BY REMOVING ALL BUT ONE ALT
		resolveByPickingMinAlt(d, nondeterministicAlts);
	}

	/** Turn off all configurations associated with the
	 *  set of incoming nondeterministic alts except the min alt number.
	 *  There may be many alts among the configurations but only turn off
	 *  the ones with problems (other than the min alt of course).
	 *
	 *  If nondeterministicAlts is null then turn off all configs 'cept those
	 *  associated with the minimum alt.
	 *
	 *  Return the min alt found.
	 */
	static int resolveByPickingMinAlt(DFAState d, Set nondeterministicAlts) {
		int min = Integer.MAX_VALUE;
		if ( nondeterministicAlts!=null ) {
			min = getMinAlt(nondeterministicAlts);
		}
		else {
			min = d.getMinAlt();
		}

		turnOffOtherAlts(d, min, nondeterministicAlts);

		return min;
	}

	/** turn off all states associated with alts other than the good one
	 *  (as long as they are one of the nondeterministic ones)
	 */
	static void turnOffOtherAlts(DFAState d, int min, Set<Integer> nondeterministicAlts) {
		int numConfigs = d.nfaConfigs.size();
		for (int i = 0; i < numConfigs; i++) {
			NFAConfig configuration = d.nfaConfigs.get(i);
			if ( configuration.alt!=min ) {
				if ( nondeterministicAlts==null ||
					 nondeterministicAlts.contains(Utils.integer(configuration.alt)) )
				{
					configuration.resolved = true;
				}
			}
		}
	}

	static int getMinAlt(Set<Integer> nondeterministicAlts) {
		int min = Integer.MAX_VALUE;
		for (Integer altI : nondeterministicAlts) {
			int alt = altI.intValue();
			if ( alt < min ) min = alt;
		}
		return min;
	}

	public static int getUniqueAlt(Collection<NFAConfig> nfaConfigs,
								   boolean ignoreResolved)
	{
		int alt = NFA.INVALID_ALT_NUMBER;
		for (NFAConfig c : nfaConfigs) {
			if ( !ignoreResolved && c.resolved ) continue;
			if ( alt==NFA.INVALID_ALT_NUMBER ) {
				alt = c.alt; // found first alt
			}
			else if ( c.alt!=alt ) {
				return NFA.INVALID_ALT_NUMBER;
			}
		}
		return alt;
	}
}
