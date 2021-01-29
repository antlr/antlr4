/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{

	/**
	 * @since 4.3
	 */
	public class ProfilingATNSimulator : ParserATNSimulator
	{
	protected readonly DecisionInfo[] decisions;
	protected int numDecisions;

		protected int sllStopIndex;
		protected int llStopIndex;

	protected int currentDecision;
	protected DFAState currentState;

	/** At the point of LL failover, we record how SLL would resolve the conflict so that
	 *  we can determine whether or not a decision / input pair is context-sensitive.
	 *  If LL gives a different result than SLL's predicted alternative, we have a
	 *  context sensitivity for sure. The converse is not necessarily true, however.
	 *  It's possible that after conflict resolution chooses minimum alternatives,
	 *  SLL could get the same answer as LL. Regardless of whether or not the result indicates
	 *  an ambiguity, it is not treated as a context sensitivity because LL prediction
	 *  was not required in order to produce a correct prediction for this decision and input sequence.
	 *  It may in fact still be a context sensitivity but we don't know by looking at the
	 *  minimum alternatives for the current input.
 	 */
	protected int conflictingAltResolvedBySLL;

	public ProfilingATNSimulator(Parser parser)

		: base(parser,
				parser.Interpreter.atn,
				parser.Interpreter.decisionToDFA,
			   parser.Interpreter.getSharedContextCache())
		{
			numDecisions = atn.decisionToState.Count;
		decisions = new DecisionInfo[numDecisions];
		for (int i = 0; i < numDecisions; i++)
		{
			decisions[i] = new DecisionInfo(i);
		}
	}

	public override int AdaptivePredict(ITokenStream input, int decision, ParserRuleContext outerContext)
	{
		try
		{
			this.sllStopIndex = -1;
			this.llStopIndex = -1;
			this.currentDecision = decision;
				long start = DateTime.Now.ToFileTime(); // expensive but useful info
			int alt = base.AdaptivePredict(input, decision, outerContext);
			long stop = DateTime.Now.ToFileTime();
			decisions[decision].timeInPrediction += (stop - start);
			decisions[decision].invocations++;

			int SLL_k = sllStopIndex - startIndex + 1;
			decisions[decision].SLL_TotalLook += SLL_k;
			decisions[decision].SLL_MinLook = decisions[decision].SLL_MinLook == 0 ? SLL_k : Math.Min(decisions[decision].SLL_MinLook, SLL_k);
			if (SLL_k > decisions[decision].SLL_MaxLook)
			{
				decisions[decision].SLL_MaxLook = SLL_k;
				decisions[decision].SLL_MaxLookEvent =
						new LookaheadEventInfo(decision, null/*, alt*/, input, startIndex, sllStopIndex, false);
			}

			if (llStopIndex >= 0)
			{
				int LL_k = llStopIndex - startIndex + 1;
				decisions[decision].LL_TotalLook += LL_k;
				decisions[decision].LL_MinLook = decisions[decision].LL_MinLook == 0 ? LL_k : Math.Min(decisions[decision].LL_MinLook, LL_k);
				if (LL_k > decisions[decision].LL_MaxLook)
				{
					decisions[decision].LL_MaxLook = LL_k;
					decisions[decision].LL_MaxLookEvent =
							new LookaheadEventInfo(decision, null/*, alt*/, input, startIndex, llStopIndex, true);
				}
			}

			return alt;
		}
		finally
		{
			this.currentDecision = -1;
		}
	}

	protected override DFAState GetExistingTargetState(DFAState previousD, int t)
	{
		// this method is called after each time the input position advances
		// during SLL prediction
		sllStopIndex = input.Index;

		DFAState existingTargetState = base.GetExistingTargetState(previousD, t);
		if (existingTargetState != null)
		{
			decisions[currentDecision].SLL_DFATransitions++; // count only if we transition over a DFA state
			if (existingTargetState == ERROR)
			{
				decisions[currentDecision].errors.Add(
						new ErrorInfo(currentDecision, null /*previousD.configs*/, input, startIndex, sllStopIndex)
				);
			}
		}

		currentState = existingTargetState;
		return existingTargetState;
	}

	protected override DFAState ComputeTargetState(DFA dfa, DFAState previousD, int t)
	{
		DFAState state = base.ComputeTargetState(dfa, previousD, t);
		currentState = state;
		return state;
	}

	protected override ATNConfigSet ComputeReachSet(ATNConfigSet closure, int t, bool fullCtx)
	{
		if (fullCtx)
		{
			// this method is called after each time the input position advances
			// during full context prediction
			llStopIndex = input.Index;
		}

		ATNConfigSet reachConfigs = base.ComputeReachSet(closure, t, fullCtx);
		if (fullCtx)
		{
			decisions[currentDecision].LL_ATNTransitions++; // count computation even if error
			if (reachConfigs != null)
			{
			}
			else { // no reach on current lookahead symbol. ERROR.
				   // TODO: does not handle delayed errors per getSynValidOrSemInvalidAltThatFinishedDecisionEntryRule()
				decisions[currentDecision].errors.Add(
					new ErrorInfo(currentDecision, null /*closure*/, input, startIndex, llStopIndex)
				);
			}
		}
		else {
			decisions[currentDecision].SLL_ATNTransitions++;
			if (reachConfigs != null)
			{
			}
			else { // no reach on current lookahead symbol. ERROR.
				decisions[currentDecision].errors.Add(
					new ErrorInfo(currentDecision, null /*closure*/, input, startIndex, sllStopIndex)
				);
			}
		}
		return reachConfigs;
	}

	protected override bool EvalSemanticContext(SemanticContext pred, ParserRuleContext parserCallStack, int alt, bool fullCtx)
	{
		bool result = base.EvalSemanticContext(pred, parserCallStack, alt, fullCtx);
		if (!(pred is SemanticContext.PrecedencePredicate)) {
			bool fullContext = llStopIndex >= 0;
			int stopIndex = fullContext ? llStopIndex : sllStopIndex;
			decisions[currentDecision].predicateEvals.Add(
				new PredicateEvalInfo(null , currentDecision, input, startIndex, stopIndex, pred, result, alt/*, fullCtx*/)
			);
		}

		return result;
	}

	protected override void ReportAttemptingFullContext(DFA dfa, BitSet conflictingAlts, ATNConfigSet configs, int startIndex, int stopIndex)
	{
		if (conflictingAlts != null)
		{
			conflictingAltResolvedBySLL = conflictingAlts.NextSetBit(0);
		}
		else {
				conflictingAltResolvedBySLL = configs.GetAlts().NextSetBit(0);
		}
		decisions[currentDecision].LL_Fallback++;
		base.ReportAttemptingFullContext(dfa, conflictingAlts, configs, startIndex, stopIndex);
	}

	protected override void ReportContextSensitivity(DFA dfa, int prediction, ATNConfigSet configs, int startIndex, int stopIndex)
	{
		if (prediction != conflictingAltResolvedBySLL)
		{
			decisions[currentDecision].contextSensitivities.Add(
					new ContextSensitivityInfo(currentDecision, null /*configs*/, input, startIndex, stopIndex)
			);
		}
			base.ReportContextSensitivity(dfa, prediction, configs, startIndex, stopIndex);
	}

	protected override void ReportAmbiguity(DFA dfa, DFAState D, int startIndex, int stopIndex, bool exact,
		                                    BitSet ambigAlts, ATNConfigSet configSet)
	{
		int prediction;
		if (ambigAlts != null)
		{
			prediction = ambigAlts.NextSetBit(0);
		}
		else {
				prediction = configSet.GetAlts().NextSetBit(0);
		}
			if (configSet.fullCtx && prediction != conflictingAltResolvedBySLL)
		{
			// Even though this is an ambiguity we are reporting, we can
			// still detect some context sensitivities.  Both SLL and LL
			// are showing a conflict, hence an ambiguity, but if they resolve
			// to different minimum alternatives we have also identified a
			// context sensitivity.
			decisions[currentDecision].contextSensitivities.Add( new ContextSensitivityInfo(currentDecision, null /*configs*/, input, startIndex, stopIndex) );
		}
		decisions[currentDecision].ambiguities.Add(
			new AmbiguityInfo(currentDecision, null /*configs, ambigAlts*/,
				              input, startIndex, stopIndex/*, configs.IsFullContext*/)
		);
		base.ReportAmbiguity(dfa, D, startIndex, stopIndex, exact, ambigAlts, configSet);
	}

	// ---------------------------------------------------------------------

	public DecisionInfo[] getDecisionInfo()
	{
		return decisions;
	}

	public DFAState getCurrentState()
	{
		return currentState;
	}
}

}
