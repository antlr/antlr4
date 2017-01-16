/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// This class represents profiling event information for semantic predicate
/// evaluations which occur during prediction.
/// 
/// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#evalSemanticContext
/// 
/// -  4.3

public class PredicateEvalInfo: DecisionEventInfo {
    /// The semantic context which was evaluated.
    public private(set) var semctx: SemanticContext
    /// The alternative number for the decision which is guarded by the semantic
    /// context {@link #semctx}. Note that other ATN
    /// configurations may predict the same alternative which are guarded by
    /// other semantic contexts and/or {@link org.antlr.v4.runtime.atn.SemanticContext#NONE}.
    public private(set) var predictedAlt: Int
    /// The result of evaluating the semantic context {@link #semctx}.
    public private(set) var evalResult: Bool

    /// Constructs a new instance of the {@link org.antlr.v4.runtime.atn.PredicateEvalInfo} class with the
    /// specified detailed predicate evaluation information.
    /// 
    /// - parameter decision: The decision number
    /// - parameter input: The input token stream
    /// - parameter startIndex: The start index for the current prediction
    /// - parameter stopIndex: The index at which the predicate evaluation was
    /// triggered. Note that the input stream may be reset to other positions for
    /// the actual evaluation of individual predicates.
    /// - parameter semctx: The semantic context which was evaluated
    /// - parameter evalResult: The results of evaluating the semantic context
    /// - parameter predictedAlt: The alternative number for the decision which is
    /// guarded by the semantic context {@code semctx}. See {@link #predictedAlt}
    /// for more information.
    /// - parameter fullCtx: {@code true} if the semantic context was
    /// evaluated during LL prediction; otherwise, {@code false} if the semantic
    /// context was evaluated during SLL prediction
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#evalSemanticContext(org.antlr.v4.runtime.atn.SemanticContext, org.antlr.v4.runtime.ParserRuleContext, int, boolean)
    /// - seealso: org.antlr.v4.runtime.atn.SemanticContext#eval(org.antlr.v4.runtime.Recognizer, org.antlr.v4.runtime.RuleContext)
    public init(_ decision: Int,
                _ input: TokenStream,
                _ startIndex: Int,
                _ stopIndex: Int,
                _ semctx: SemanticContext,
                _ evalResult: Bool,
                _ predictedAlt: Int,
                _ fullCtx: Bool) {

        self.semctx = semctx
        self.evalResult = evalResult
        self.predictedAlt = predictedAlt
        super.init(decision, ATNConfigSet(), input, startIndex, stopIndex, fullCtx)
    }
}
