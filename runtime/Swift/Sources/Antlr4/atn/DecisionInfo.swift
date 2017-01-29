/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// This class contains profiling gathered for a particular decision.
/// 
/// <p>
/// Parsing performance in ANTLR 4 is heavily influenced by both static factors
/// (e.g. the form of the rules in the grammar) and dynamic factors (e.g. the
/// choice of input and the state of the DFA cache at the time profiling
/// operations are started). For best results, gather and use aggregate
/// statistics from a large sample of inputs representing the inputs expected in
/// production before using the results to make changes in the grammar.</p>
/// 
/// -  4.3

public class DecisionInfo: CustomStringConvertible {
    /// The decision number, which is an index into {@link org.antlr.v4.runtime.atn.ATN#decisionToState}.
    public final var decision: Int

    /// The total number of times {@link org.antlr.v4.runtime.atn.ParserATNSimulator#adaptivePredict} was
    /// invoked for this decision.
    public var invocations: Int64 = 0

    /// The total time spent in {@link org.antlr.v4.runtime.atn.ParserATNSimulator#adaptivePredict} for
    /// this decision, in nanoseconds.
    /// 
    /// <p>
    /// The value of this field contains the sum of differential results obtained
    /// by {@link System#nanoTime()}, and is not adjusted to compensate for JIT
    /// and/or garbage collection overhead. For best accuracy, use a modern JVM
    /// implementation that provides precise results from
    /// {@link System#nanoTime()}, and perform profiling in a separate process
    /// which is warmed up by parsing the input prior to profiling. If desired,
    /// call {@link org.antlr.v4.runtime.atn.ATNSimulator#clearDFA} to reset the DFA cache to its initial
    /// state before starting the profiling measurement pass.</p>
    public var timeInPrediction: Int64 = 0

    /// The sum of the lookahead required for SLL prediction for this decision.
    /// Note that SLL prediction is used before LL prediction for performance
    /// reasons even when {@link org.antlr.v4.runtime.atn.PredictionMode#LL} or
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#LL_EXACT_AMBIG_DETECTION} is used.
    public var SLL_TotalLook: Int64 = 0

    /// Gets the minimum lookahead required for any single SLL prediction to
    /// complete for this decision, by reaching a unique prediction, reaching an
    /// SLL conflict state, or encountering a syntax error.
    public var SLL_MinLook: Int64 = 0

    /// Gets the maximum lookahead required for any single SLL prediction to
    /// complete for this decision, by reaching a unique prediction, reaching an
    /// SLL conflict state, or encountering a syntax error.
    public var SLL_MaxLook: Int64 = 0

    /// Gets the {@link org.antlr.v4.runtime.atn.LookaheadEventInfo} associated with the event where the
    /// {@link #SLL_MaxLook} value was set.
    public var SLL_MaxLookEvent: LookaheadEventInfo!

    /// The sum of the lookahead required for LL prediction for this decision.
    /// Note that LL prediction is only used when SLL prediction reaches a
    /// conflict state.
    public var LL_TotalLook: Int64 = 0

    /// Gets the minimum lookahead required for any single LL prediction to
    /// complete for this decision. An LL prediction completes when the algorithm
    /// reaches a unique prediction, a conflict state (for
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#LL}, an ambiguity state (for
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
    public var LL_MinLook: Int64 = 0

    /// Gets the maximum lookahead required for any single LL prediction to
    /// complete for this decision. An LL prediction completes when the algorithm
    /// reaches a unique prediction, a conflict state (for
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#LL}, an ambiguity state (for
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#LL_EXACT_AMBIG_DETECTION}, or a syntax error.
    public var LL_MaxLook: Int64 = 0

    /// Gets the {@link org.antlr.v4.runtime.atn.LookaheadEventInfo} associated with the event where the
    /// {@link #LL_MaxLook} value was set.
    public var LL_MaxLookEvent: LookaheadEventInfo!

    /// A collection of {@link org.antlr.v4.runtime.atn.ContextSensitivityInfo} instances describing the
    /// context sensitivities encountered during LL prediction for this decision.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ContextSensitivityInfo
    public final var contextSensitivities: Array<ContextSensitivityInfo> = Array<ContextSensitivityInfo>()

    /// A collection of {@link org.antlr.v4.runtime.atn.ErrorInfo} instances describing the parse errors
    /// identified during calls to {@link org.antlr.v4.runtime.atn.ParserATNSimulator#adaptivePredict} for
    /// this decision.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ErrorInfo
    public final var errors: Array<ErrorInfo> = Array<ErrorInfo>()

    /// A collection of {@link org.antlr.v4.runtime.atn.AmbiguityInfo} instances describing the
    /// ambiguities encountered during LL prediction for this decision.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.AmbiguityInfo
    public final var ambiguities: Array<AmbiguityInfo> = Array<AmbiguityInfo>()

    /// A collection of {@link org.antlr.v4.runtime.atn.PredicateEvalInfo} instances describing the
    /// results of evaluating individual predicates during prediction for this
    /// decision.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.PredicateEvalInfo
    public final var predicateEvals: Array<PredicateEvalInfo> = Array<PredicateEvalInfo>()

    /// The total number of ATN transitions required during SLL prediction for
    /// this decision. An ATN transition is determined by the number of times the
    /// DFA does not contain an edge that is required for prediction, resulting
    /// in on-the-fly computation of that edge.
    /// 
    /// <p>
    /// If DFA caching of SLL transitions is employed by the implementation, ATN
    /// computation may cache the computed edge for efficient lookup during
    /// future parsing of this decision. Otherwise, the SLL parsing algorithm
    /// will use ATN transitions exclusively.</p>
    /// 
    /// - seealso: #SLL_ATNTransitions
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#computeTargetState
    /// - seealso: org.antlr.v4.runtime.atn.LexerATNSimulator#computeTargetState
    public var SLL_ATNTransitions: Int64 = 0

    /// The total number of DFA transitions required during SLL prediction for
    /// this decision.
    /// 
    /// <p>If the ATN simulator implementation does not use DFA caching for SLL
    /// transitions, this value will be 0.</p>
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#getExistingTargetState
    /// - seealso: org.antlr.v4.runtime.atn.LexerATNSimulator#getExistingTargetState
    public var SLL_DFATransitions: Int64 = 0

    /// Gets the total number of times SLL prediction completed in a conflict
    /// state, resulting in fallback to LL prediction.
    /// 
    /// <p>Note that this value is not related to whether or not
    /// {@link org.antlr.v4.runtime.atn.PredictionMode#SLL} may be used successfully with a particular
    /// grammar. If the ambiguity resolution algorithm applied to the SLL
    /// conflicts for this decision produce the same result as LL prediction for
    /// this decision, {@link org.antlr.v4.runtime.atn.PredictionMode#SLL} would produce the same overall
    /// parsing result as {@link org.antlr.v4.runtime.atn.PredictionMode#LL}.</p>
    public var LL_Fallback: Int64 = 0

    /// The total number of ATN transitions required during LL prediction for
    /// this decision. An ATN transition is determined by the number of times the
    /// DFA does not contain an edge that is required for prediction, resulting
    /// in on-the-fly computation of that edge.
    /// 
    /// <p>
    /// If DFA caching of LL transitions is employed by the implementation, ATN
    /// computation may cache the computed edge for efficient lookup during
    /// future parsing of this decision. Otherwise, the LL parsing algorithm will
    /// use ATN transitions exclusively.</p>
    /// 
    /// - seealso: #LL_DFATransitions
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#computeTargetState
    /// - seealso: org.antlr.v4.runtime.atn.LexerATNSimulator#computeTargetState
    public var LL_ATNTransitions: Int64 = 0

    /// The total number of DFA transitions required during LL prediction for
    /// this decision.
    /// 
    /// <p>If the ATN simulator implementation does not use DFA caching for LL
    /// transitions, this value will be 0.</p>
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#getExistingTargetState
    /// - seealso: org.antlr.v4.runtime.atn.LexerATNSimulator#getExistingTargetState
    public var LL_DFATransitions: Int64 = 0

    /// Constructs a new instance of the {@link org.antlr.v4.runtime.atn.DecisionInfo} class to contain
    /// statistics for a particular decision.
    /// 
    /// - parameter decision: The decision number
    public init(_ decision: Int) {
        self.decision = decision
    }


    public var description: String {
        let desc: StringBuilder = StringBuilder()
        desc.append("{")
        desc.append("decision=\(decision)")
        desc.append(", contextSensitivities=\(contextSensitivities.count)")
        desc.append(", errors=\(errors.count)")
        desc.append(", ambiguities=\(ambiguities.count)")
        desc.append(", SLL_lookahead=\(SLL_TotalLook)")
        desc.append(", SLL_ATNTransitions=\(SLL_ATNTransitions)")
        desc.append(", SLL_DFATransitions=\(SLL_DFATransitions)")
        desc.append(", LL_Fallback=\(LL_Fallback)")
        desc.append(", LL_lookahead=\(LL_TotalLook)")
        desc.append(", LL_ATNTransitions=\(LL_ATNTransitions)")
        desc.append("}")

        return desc.toString()
    }

}
