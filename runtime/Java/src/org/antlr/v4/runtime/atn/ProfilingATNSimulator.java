/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.dfa.DFAState;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;
import org.antlr.v4.runtime.misc.Tuple2;

import java.util.BitSet;

public class ProfilingATNSimulator extends ParserATNSimulator {
	protected final DecisionInfo[] decisions;
	protected int numDecisions;

	protected TokenStream _input;
	protected int _startIndex;
	protected int _sllStopIndex;
	protected int _llStopIndex;

	protected int currentDecision;
	protected SimulatorState currentState;

	public ProfilingATNSimulator(Parser parser) {
		super(parser, parser.getInterpreter().atn);
		optimize_ll1 = false;
		reportAmbiguities = true;
		numDecisions = atn.decisionToState.size();
		decisions = new DecisionInfo[numDecisions];
		for (int i=0; i<numDecisions; i++) {
			decisions[i] = new DecisionInfo(i);
		}
	}

	@Override
	public int adaptivePredict(TokenStream input, int decision, ParserRuleContext outerContext) {
		try {
			this._input = input;
			this._startIndex = input.index();
			this._sllStopIndex = -1;
			this._llStopIndex = -1;
			this.currentDecision = decision;
			this.currentState = null;
			long start = System.nanoTime(); // expensive but useful info
			int alt = super.adaptivePredict(input, decision, outerContext);
			long stop = System.nanoTime();
			decisions[decision].timeInPrediction += (stop-start);
			decisions[decision].invocations++;

			int SLL_k = _sllStopIndex - _startIndex + 1;
			decisions[decision].SLL_TotalLook += SLL_k;
			decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook==0 ? SLL_k : Math.min(decisions[decision].SLL_MinLook, SLL_k);
			if ( SLL_k > decisions[decision].SLL_MaxLook ) {
				decisions[decision].SLL_MaxLook = SLL_k;
				decisions[decision].SLL_MaxLookEvent =
						new LookaheadEventInfo(decision, null, input, _startIndex, _sllStopIndex, false);
			}

			if (_llStopIndex >= 0) {
				int LL_k = _llStopIndex - _startIndex + 1;
				decisions[decision].LL_TotalLook += LL_k;
				decisions[decision].LL_MinLook = decisions[decision].LL_MinLook==0 ? LL_k : Math.min(decisions[decision].LL_MinLook, LL_k);
				if ( LL_k > decisions[decision].LL_MaxLook ) {
					decisions[decision].LL_MaxLook = LL_k;
					decisions[decision].LL_MaxLookEvent =
							new LookaheadEventInfo(decision, null, input, _startIndex, _llStopIndex, true);
				}
			}

			return alt;
		}
		finally {
			this._input = null;
			this.currentDecision = -1;
		}
	}

	@Override
	protected SimulatorState getStartState(DFA dfa, TokenStream input, ParserRuleContext outerContext, boolean useContext) {
		SimulatorState state = super.getStartState(dfa, input, outerContext, useContext);
		currentState = state;
		return state;
	}

	@Override
	protected SimulatorState computeStartState(DFA dfa, ParserRuleContext globalContext, boolean useContext) {
		SimulatorState state = super.computeStartState(dfa, globalContext, useContext);
		currentState = state;
		return state;
	}

	@Override
	protected SimulatorState computeReachSet(DFA dfa, SimulatorState previous, int t, PredictionContextCache contextCache) {
		SimulatorState reachState = super.computeReachSet(dfa, previous, t, contextCache);
		if (reachState == null) {
			// no reach on current lookahead symbol. ERROR.
			decisions[currentDecision].errors.add(
				new ErrorInfo(currentDecision, previous, _input, _startIndex, _input.index())
			);
		}

		currentState = reachState;
		return reachState;
	}

	@Override
	protected DFAState getExistingTargetState(DFAState previousD, int t) {
		// this method is called after each time the input position advances
		if (currentState.useContext) {
			_llStopIndex = _input.index();
		}
		else {
			_sllStopIndex = _input.index();
		}

		DFAState existingTargetState = super.getExistingTargetState(previousD, t);
		if ( existingTargetState!=null ) {
			// this method is directly called by execDFA; must construct a SimulatorState
			// to represent the current state for this case
			currentState = new SimulatorState(currentState.outerContext, existingTargetState, currentState.useContext, currentState.remainingOuterContext);

			if (currentState.useContext) {
				decisions[currentDecision].LL_DFATransitions++;
			}
			else {
				decisions[currentDecision].SLL_DFATransitions++; // count only if we transition over a DFA state
			}

			if ( existingTargetState==ERROR ) {
				SimulatorState state = new SimulatorState(currentState.outerContext, previousD, currentState.useContext, currentState.remainingOuterContext);
				decisions[currentDecision].errors.add(
					new ErrorInfo(currentDecision, state, _input, _startIndex, _input.index())
				);
			}
		}

		return existingTargetState;
	}

	@Override
	protected Tuple2<DFAState, ParserRuleContext> computeTargetState(DFA dfa, DFAState s, ParserRuleContext remainingGlobalContext, int t, boolean useContext, PredictionContextCache contextCache) {
		Tuple2<DFAState, ParserRuleContext> targetState = super.computeTargetState(dfa, s, remainingGlobalContext, t, useContext, contextCache);

		if (useContext) {
			decisions[currentDecision].LL_ATNTransitions++;
		}
		else {
			decisions[currentDecision].SLL_ATNTransitions++;
		}

		return targetState;
	}

	@Override
	protected boolean evalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt) {
		boolean result = super.evalSemanticContext(pred, parserCallStack, alt);
		if (!(pred instanceof SemanticContext.PrecedencePredicate)) {
			boolean fullContext = _llStopIndex >= 0;
			int stopIndex = fullContext ? _llStopIndex : _sllStopIndex;
			decisions[currentDecision].predicateEvals.add(
				new PredicateEvalInfo(currentState, currentDecision, _input, _startIndex, stopIndex, pred, result, alt)
			);
		}

		return result;
	}

	@Override
	protected void reportContextSensitivity(DFA dfa, int prediction, SimulatorState acceptState, int startIndex, int stopIndex) {
		decisions[currentDecision].contextSensitivities.add(
			new ContextSensitivityInfo(currentDecision, acceptState, _input, startIndex, stopIndex)
		);
		super.reportContextSensitivity(dfa, prediction, acceptState, startIndex, stopIndex);
	}

	@Override
	protected void reportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, SimulatorState conflictState, int startIndex, int stopIndex) {
		decisions[currentDecision].LL_Fallback++;
		super.reportAttemptingFullContext(dfa, conflictingAlts, conflictState, startIndex, stopIndex);
	}

	@Override
	protected void reportAmbiguity(@NotNull DFA dfa, DFAState D, int startIndex, int stopIndex, boolean exact, @Nullable BitSet ambigAlts, @NotNull ATNConfigSet configs) {
		decisions[currentDecision].ambiguities.add(
			new AmbiguityInfo(currentDecision, currentState, _input, startIndex, stopIndex)
		);
		super.reportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configs);
	}

	// ---------------------------------------------------------------------

	public DecisionInfo[] getDecisionInfo() {
		return decisions;
	}
}
