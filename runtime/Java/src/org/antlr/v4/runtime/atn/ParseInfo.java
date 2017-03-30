/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.dfa.DFA;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides access to specific and aggregate statistics gathered
 * during profiling of a parser.
 *
 * @since 4.3
 */
public class ParseInfo {
	protected final ProfilingATNSimulator atnSimulator;

	public ParseInfo(ProfilingATNSimulator atnSimulator) {
		this.atnSimulator = atnSimulator;
	}

	/**
	 * Gets an array of {@link DecisionInfo} instances containing the profiling
	 * information gathered for each decision in the ATN.
	 *
	 * @return An array of {@link DecisionInfo} instances, indexed by decision
	 * number.
	 */
	public DecisionInfo[] getDecisionInfo() {
		return atnSimulator.getDecisionInfo();
	}

	/**
	 * Gets the decision numbers for decisions that required one or more
	 * full-context predictions during parsing. These are decisions for which
	 * {@link DecisionInfo#LL_Fallback} is non-zero.
	 *
	 * @return A list of decision numbers which required one or more
	 * full-context predictions during parsing.
	 */
	public List<Integer> getLLDecisions() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		List<Integer> LL = new ArrayList<Integer>();
		for (int i=0; i<decisions.length; i++) {
			long fallBack = decisions[i].LL_Fallback;
			if ( fallBack>0 ) LL.add(i);
		}
		return LL;
	}

	/**
	 * Gets the total time spent during prediction across all decisions made
	 * during parsing. This value is the sum of
	 * {@link DecisionInfo#timeInPrediction} for all decisions.
	 */
	public long getTotalTimeInPrediction() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long t = 0;
		for (int i=0; i<decisions.length; i++) {
			t += decisions[i].timeInPrediction;
		}
		return t;
	}

	/**
	 * Gets the total number of SLL lookahead operations across all decisions
	 * made during parsing. This value is the sum of
	 * {@link DecisionInfo#SLL_TotalLook} for all decisions.
	 */
	public long getTotalSLLLookaheadOps() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long k = 0;
		for (int i = 0; i < decisions.length; i++) {
			k += decisions[i].SLL_TotalLook;
		}
		return k;
	}

	/**
	 * Gets the total number of LL lookahead operations across all decisions
	 * made during parsing. This value is the sum of
	 * {@link DecisionInfo#LL_TotalLook} for all decisions.
	 */
	public long getTotalLLLookaheadOps() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long k = 0;
		for (int i = 0; i < decisions.length; i++) {
			k += decisions[i].LL_TotalLook;
		}
		return k;
	}

	/**
	 * Gets the total number of ATN lookahead operations for SLL prediction
	 * across all decisions made during parsing.
	 */
	public long getTotalSLLATNLookaheadOps() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long k = 0;
		for (int i = 0; i < decisions.length; i++) {
			k += decisions[i].SLL_ATNTransitions;
		}
		return k;
	}

	/**
	 * Gets the total number of ATN lookahead operations for LL prediction
	 * across all decisions made during parsing.
	 */
	public long getTotalLLATNLookaheadOps() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long k = 0;
		for (int i = 0; i < decisions.length; i++) {
			k += decisions[i].LL_ATNTransitions;
		}
		return k;
	}

	/**
	 * Gets the total number of ATN lookahead operations for SLL and LL
	 * prediction across all decisions made during parsing.
	 *
	 * <p>
	 * This value is the sum of {@link #getTotalSLLATNLookaheadOps} and
	 * {@link #getTotalLLATNLookaheadOps}.</p>
	 */
	public long getTotalATNLookaheadOps() {
		DecisionInfo[] decisions = atnSimulator.getDecisionInfo();
		long k = 0;
		for (int i = 0; i < decisions.length; i++) {
			k += decisions[i].SLL_ATNTransitions;
			k += decisions[i].LL_ATNTransitions;
		}
		return k;
	}

	/**
	 * Gets the total number of DFA states stored in the DFA cache for all
	 * decisions in the ATN.
	 */
	public int getDFASize() {
		int n = 0;
		DFA[] decisionToDFA = atnSimulator.decisionToDFA;
		for (int i = 0; i < decisionToDFA.length; i++) {
			n += getDFASize(i);
		}
		return n;
	}

	/**
	 * Gets the total number of DFA states stored in the DFA cache for a
	 * particular decision.
	 */
	public int getDFASize(int decision) {
		DFA decisionToDFA = atnSimulator.decisionToDFA[decision];
		return decisionToDFA.states.size();
	}
}
