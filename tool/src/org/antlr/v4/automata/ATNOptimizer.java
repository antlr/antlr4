/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.automata;

import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.runtime.atn.AtomTransition;
import org.antlr.v4.runtime.atn.BlockEndState;
import org.antlr.v4.runtime.atn.DecisionState;
import org.antlr.v4.runtime.atn.EpsilonTransition;
import org.antlr.v4.runtime.atn.NotSetTransition;
import org.antlr.v4.runtime.atn.RangeTransition;
import org.antlr.v4.runtime.atn.SetTransition;
import org.antlr.v4.runtime.atn.Transition;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.Rule;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sam Harwell
 */
public class ATNOptimizer {

	public static void optimize(Grammar g, ATN atn) {
		optimizeSets(g, atn);
		optimizeStates(atn);
	}

	private static void optimizeSets(Grammar g, ATN atn) {
		if (g.isParser()) {
			// parser codegen doesn't currently support SetTransition
			return;
		}

		int removedStates = 0;
		List<DecisionState> decisions = atn.decisionToState;
		for (DecisionState decision : decisions) {
			if (decision.ruleIndex >= 0) {
				Rule rule = g.getRule(decision.ruleIndex);
				if (Character.isLowerCase(rule.name.charAt(0))) {
					// parser codegen doesn't currently support SetTransition
					continue;
				}
			}

			IntervalSet setTransitions = new IntervalSet();
			for (int i = 0; i < decision.getNumberOfTransitions(); i++) {
				Transition epsTransition = decision.transition(i);
				if (!(epsTransition instanceof EpsilonTransition)) {
					continue;
				}

				if (epsTransition.target.getNumberOfTransitions() != 1) {
					continue;
				}

				Transition transition = epsTransition.target.transition(0);
				if (!(transition.target instanceof BlockEndState)) {
					continue;
				}

				if (transition instanceof NotSetTransition) {
					// TODO: not yet implemented
					continue;
				}

				if (transition instanceof AtomTransition
					|| transition instanceof RangeTransition
					|| transition instanceof SetTransition)
				{
					setTransitions.add(i);
				}
			}

			// due to min alt resolution policies, can only collapse sequential alts
			for (int i = setTransitions.getIntervals().size() - 1; i >= 0; i--) {
				Interval interval = setTransitions.getIntervals().get(i);
				if (interval.length() <= 1) {
					continue;
				}

				ATNState blockEndState = decision.transition(interval.a).target.transition(0).target;
				IntervalSet matchSet = new IntervalSet();
				for (int j = interval.a; j <= interval.b; j++) {
					Transition matchTransition = decision.transition(j).target.transition(0);
					if (matchTransition instanceof NotSetTransition) {
						throw new UnsupportedOperationException("Not yet implemented.");
					} else {
						matchSet.addAll(matchTransition.label());
					}
				}

				Transition newTransition;
				if (matchSet.getIntervals().size() == 1) {
					if (matchSet.size() == 1) {
						newTransition = new AtomTransition(blockEndState, matchSet.getMinElement());
					} else {
						Interval matchInterval = matchSet.getIntervals().get(0);
						newTransition = new RangeTransition(blockEndState, matchInterval.a, matchInterval.b);
					}
				} else {
					newTransition = new SetTransition(blockEndState, matchSet);
				}

				decision.transition(interval.a).target.setTransition(0, newTransition);
				for (int j = interval.a + 1; j <= interval.b; j++) {
					Transition removed = decision.removeTransition(interval.a + 1);
					atn.removeState(removed.target);
					removedStates++;
				}
			}
		}

//		System.out.println("ATN optimizer removed " + removedStates + " states by collapsing sets.");
	}

	private static void optimizeStates(ATN atn) {
//		System.out.println(atn.states);
		List<ATNState> compressed = new ArrayList<ATNState>();
		int i = 0; // new state number
		for (ATNState s : atn.states) {
			if ( s!=null ) {
				compressed.add(s);
				s.stateNumber = i; // reset state number as we shift to new position
				i++;
			}
		}
//		System.out.println(compressed);
//		System.out.println("ATN optimizer removed " + (atn.states.size() - compressed.size()) + " null states.");
		atn.states.clear();
		atn.states.addAll(compressed);
	}

	private ATNOptimizer() {
	}

}
