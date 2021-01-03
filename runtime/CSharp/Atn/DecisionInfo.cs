/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;


namespace Antlr4.Runtime.Atn
{
	/// <summary>This class contains profiling gathered for a particular decision.</summary>
	/// <remarks>
	/// This class contains profiling gathered for a particular decision.
	/// <p>
	/// Parsing performance in ANTLR 4 is heavily influenced by both static factors
	/// (e.g. the form of the rules in the grammar) and dynamic factors (e.g. the
	/// choice of input and the state of the DFA cache at the time profiling
	/// operations are started). For best results, gather and use aggregate
	/// statistics from a large sample of inputs representing the inputs expected in
	/// production before using the results to make changes in the grammar.</p>
	/// </remarks>
	/// <since>4.3</since>
	public class DecisionInfo
	{

		/**
		 * The decision number, which is an index into {@link ATN#decisionToState}.
		 */
		public readonly int decision;

		/**
		 * The total number of times {@link ParserATNSimulator#adaptivePredict} was
		 * invoked for this decision.
		 */
		public long invocations;

		/**
		 * The total time spent in {@link ParserATNSimulator#adaptivePredict} for
		 * this decision, in nanoseconds.
		 *
		 * <p>
		 * The value of this field contains the sum of differential results obtained
		 * by {@link System#nanoTime()}, and is not adjusted to compensate for JIT
		 * and/or garbage collection overhead. For best accuracy, use a modern JVM
		 * implementation that provides precise results from
		 * {@link System#nanoTime()}, and perform profiling in a separate process
		 * which is warmed up by parsing the input prior to profiling. If desired,
		 * call {@link ATNSimulator#clearDFA} to reset the DFA cache to its initial
		 * state before starting the profiling measurement pass.</p>
		 */
		public long timeInPrediction;

		/**
		 * The sum of the lookahead required for SLL prediction for this decision.
		 * Note that SLL prediction is used before LL prediction for performance
		 * reasons even when {@link PredictionMode#LL} or
		 * {@link PredictionMode#LL_EXACT_AMBIG_DETECTION} is used.
		 */
		public long SLL_TotalLook;

		/**
		 * Gets the minimum lookahead required for any single SLL prediction to
		 * complete for this decision, by reaching a unique prediction, reaching an
		 * SLL conflict state, or encountering a syntax error.
		 */
		public long SLL_MinLook;

		/**
		 * Gets the maximum lookahead required for any single SLL prediction to
		 * complete for this decision, by reaching a unique prediction, reaching an
		 * SLL conflict state, or encountering a syntax error.
		 */
		public long SLL_MaxLook;

		/**
		 * Gets the {@link LookaheadEventInfo} associated with the event where the
		 * {@link #SLL_MaxLook} value was set.
		 */
		public LookaheadEventInfo SLL_MaxLookEvent;

		/**
		 * The sum of the lookahead required for LL prediction for this decision.
		 * Note that LL prediction is only used when SLL prediction reaches a
		 * conflict state.
		 */
		public long LL_TotalLook;

		/**
		 * Gets the minimum lookahead required for any single LL prediction to
		 * complete for this decision. An LL prediction completes when the algorithm
		 * reaches a unique prediction, a conflict state (for
		 * {@link PredictionMode#LL}, an ambiguity state (for
		 * {@link PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
		 */
		public long LL_MinLook;

		/**
		 * Gets the maximum lookahead required for any single LL prediction to
		 * complete for this decision. An LL prediction completes when the algorithm
		 * reaches a unique prediction, a conflict state (for
		 * {@link PredictionMode#LL}, an ambiguity state (for
		 * {@link PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
		 */
		public long LL_MaxLook;

		/**
		 * Gets the {@link LookaheadEventInfo} associated with the event where the
		 * {@link #LL_MaxLook} value was set.
		 */
		public LookaheadEventInfo LL_MaxLookEvent;

		/**
		 * A collection of {@link ContextSensitivityInfo} instances describing the
		 * context sensitivities encountered during LL prediction for this decision.
		 *
		 * @see ContextSensitivityInfo
		 */
		public readonly List<ContextSensitivityInfo> contextSensitivities = new List<ContextSensitivityInfo>();

		/**
		 * A collection of {@link ErrorInfo} instances describing the parse errors
		 * identified during calls to {@link ParserATNSimulator#adaptivePredict} for
		 * this decision.
		 *
		 * @see ErrorInfo
		 */
		public readonly List<ErrorInfo> errors = new List<ErrorInfo>();

		/**
		 * A collection of {@link AmbiguityInfo} instances describing the
		 * ambiguities encountered during LL prediction for this decision.
		 *
		 * @see AmbiguityInfo
		 */
		public readonly List<AmbiguityInfo> ambiguities = new List<AmbiguityInfo>();

		/**
		 * A collection of {@link PredicateEvalInfo} instances describing the
		 * results of evaluating individual predicates during prediction for this
		 * decision.
		 *
		 * @see PredicateEvalInfo
		 */
		public readonly List<PredicateEvalInfo> predicateEvals = new List<PredicateEvalInfo>();

		/**
		 * The total number of ATN transitions required during SLL prediction for
		 * this decision. An ATN transition is determined by the number of times the
		 * DFA does not contain an edge that is required for prediction, resulting
		 * in on-the-fly computation of that edge.
		 *
		 * <p>
		 * If DFA caching of SLL transitions is employed by the implementation, ATN
		 * computation may cache the computed edge for efficient lookup during
		 * future parsing of this decision. Otherwise, the SLL parsing algorithm
		 * will use ATN transitions exclusively.</p>
		 *
		 * @see #SLL_ATNTransitions
		 * @see ParserATNSimulator#computeTargetState
		 * @see LexerATNSimulator#computeTargetState
		 */
		public long SLL_ATNTransitions;

		/**
		 * The total number of DFA transitions required during SLL prediction for
		 * this decision.
		 *
		 * <p>If the ATN simulator implementation does not use DFA caching for SLL
		 * transitions, this value will be 0.</p>
		 *
		 * @see ParserATNSimulator#getExistingTargetState
		 * @see LexerATNSimulator#getExistingTargetState
		 */
		public long SLL_DFATransitions;

		/**
		 * Gets the total number of times SLL prediction completed in a conflict
		 * state, resulting in fallback to LL prediction.
		 *
		 * <p>Note that this value is not related to whether or not
		 * {@link PredictionMode#SLL} may be used successfully with a particular
		 * grammar. If the ambiguity resolution algorithm applied to the SLL
		 * conflicts for this decision produce the same result as LL prediction for
		 * this decision, {@link PredictionMode#SLL} would produce the same overall
		 * parsing result as {@link PredictionMode#LL}.</p>
		 */
		public long LL_Fallback;

		/**
		 * The total number of ATN transitions required during LL prediction for
		 * this decision. An ATN transition is determined by the number of times the
		 * DFA does not contain an edge that is required for prediction, resulting
		 * in on-the-fly computation of that edge.
		 *
		 * <p>
		 * If DFA caching of LL transitions is employed by the implementation, ATN
		 * computation may cache the computed edge for efficient lookup during
		 * future parsing of this decision. Otherwise, the LL parsing algorithm will
		 * use ATN transitions exclusively.</p>
		 *
		 * @see #LL_DFATransitions
		 * @see ParserATNSimulator#computeTargetState
		 * @see LexerATNSimulator#computeTargetState
		 */
		public long LL_ATNTransitions;

		/**
		 * The total number of DFA transitions required during LL prediction for
		 * this decision.
		 *
		 * <p>If the ATN simulator implementation does not use DFA caching for LL
		 * transitions, this value will be 0.</p>
		 *
		 * @see ParserATNSimulator#getExistingTargetState
		 * @see LexerATNSimulator#getExistingTargetState
		 */
		public long LL_DFATransitions;

		/**
		 * Constructs a new instance of the {@link DecisionInfo} class to contain
		 * statistics for a particular decision.
		 *
		 * @param decision The decision number
		 */
		public DecisionInfo(int decision)
		{
			this.decision = decision;
		}

		public override string ToString()
		{
			return "{" +
				   "decision=" + decision +
					", contextSensitivities=" + contextSensitivities.Count +
				   ", errors=" + errors.Count +
				   ", ambiguities=" + ambiguities.Count +
				   ", SLL_lookahead=" + SLL_TotalLook +
				   ", SLL_ATNTransitions=" + SLL_ATNTransitions +
				   ", SLL_DFATransitions=" + SLL_DFATransitions +
				   ", LL_Fallback=" + LL_Fallback +
				   ", LL_lookahead=" + LL_TotalLook +
				   ", LL_ATNTransitions=" + LL_ATNTransitions +
				   '}';
		}
	}
}
