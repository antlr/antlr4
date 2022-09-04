/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// This class represents profiling event information for semantic predicate
    /// evaluations which occur during prediction.
    /// </summary>
    /// <remarks>
    /// This class represents profiling event information for semantic predicate
    /// evaluations which occur during prediction.
    /// </remarks>
    /// <seealso cref="ParserATNSimulator.EvalSemanticContext(Dfa.PredPrediction[], ParserRuleContext, bool)"/>
    /// <since>4.3</since>
    public class PredicateEvalInfo : DecisionEventInfo
    {
        /// <summary>The semantic context which was evaluated.</summary>
        /// <remarks>The semantic context which was evaluated.</remarks>
        public readonly SemanticContext semctx;

        /// <summary>
        /// The alternative number for the decision which is guarded by the semantic
        /// context
        /// <see cref="semctx"/>
        /// . Note that other ATN
        /// configurations may predict the same alternative which are guarded by
        /// other semantic contexts and/or
        /// <see cref="SemanticContext.Empty.Instance"/>
        /// .
        /// </summary>
        public readonly int predictedAlt;

        /// <summary>
        /// The result of evaluating the semantic context
        /// <see cref="semctx"/>
        /// .
        /// </summary>
        public readonly bool evalResult;

        /// <summary>
        /// Constructs a new instance of the
        /// <see cref="PredicateEvalInfo"/>
        /// class with the
        /// specified detailed predicate evaluation information.
        /// </summary>
        /// <param name="decision">The decision number</param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">
        /// The index at which the predicate evaluation was
        /// triggered. Note that the input stream may be reset to other positions for
        /// the actual evaluation of individual predicates.
        /// </param>
        /// <param name="semctx">The semantic context which was evaluated</param>
        /// <param name="evalResult">The results of evaluating the semantic context</param>
        /// <param name="predictedAlt">
        /// The alternative number for the decision which is
        /// guarded by the semantic context
        /// <paramref name="semctx"/>
        /// . See
        /// <see cref="predictedAlt"/>
        /// for more information.
        /// </param>
        /// <param name="fullCtx">{@code true} if the semantic context was
        /// evaluated during LL prediction; otherwise, {@code false} if the semantic
        /// context was evaluated during SLL prediction
        /// </param>
        ///
        /// <seealso cref="ParserATNSimulator.EvalSemanticContext(SemanticContext, ParserRuleContext, int, bool)"/>
        /// <seealso cref="SemanticContext.Eval"/>
        public PredicateEvalInfo(int decision, ITokenStream input, int startIndex, int stopIndex, SemanticContext semctx, bool evalResult, int predictedAlt, bool fullCtx)
            : base(decision, new ATNConfigSet(), input, startIndex, stopIndex, fullCtx)
        {
            this.semctx = semctx;
            this.evalResult = evalResult;
            this.predictedAlt = predictedAlt;
        }
    }
}
