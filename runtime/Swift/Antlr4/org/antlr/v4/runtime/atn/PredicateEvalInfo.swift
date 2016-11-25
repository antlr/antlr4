/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
 *  Copyright (c) 2015 Janyou
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



/**
 * This class represents profiling event information for semantic predicate
 * evaluations which occur during prediction.
 *
 * @see org.antlr.v4.runtime.atn.ParserATNSimulator#evalSemanticContext
 *
 * @since 4.3
 */

public class PredicateEvalInfo: DecisionEventInfo {
    /**
     * The semantic context which was evaluated.
     */
    public private(set) var semctx: SemanticContext
    /**
     * The alternative number for the decision which is guarded by the semantic
     * context {@link #semctx}. Note that other ATN
     * configurations may predict the same alternative which are guarded by
     * other semantic contexts and/or {@link org.antlr.v4.runtime.atn.SemanticContext#NONE}.
     */
    public private(set) var predictedAlt: Int
    /**
     * The result of evaluating the semantic context {@link #semctx}.
     */
    public private(set) var evalResult: Bool

    /**
     * Constructs a new instance of the {@link org.antlr.v4.runtime.atn.PredicateEvalInfo} class with the
     * specified detailed predicate evaluation information.
     *
     * @param decision The decision number
     * @param input The input token stream
     * @param startIndex The start index for the current prediction
     * @param stopIndex The index at which the predicate evaluation was
     * triggered. Note that the input stream may be reset to other positions for
     * the actual evaluation of individual predicates.
     * @param semctx The semantic context which was evaluated
     * @param evalResult The results of evaluating the semantic context
     * @param predictedAlt The alternative number for the decision which is
     * guarded by the semantic context {@code semctx}. See {@link #predictedAlt}
     * for more information.
     * @param fullCtx {@code true} if the semantic context was
     * evaluated during LL prediction; otherwise, {@code false} if the semantic
     * context was evaluated during SLL prediction
     *
     * @see org.antlr.v4.runtime.atn.ParserATNSimulator#evalSemanticContext(org.antlr.v4.runtime.atn.SemanticContext, org.antlr.v4.runtime.ParserRuleContext, int, boolean)
     * @see org.antlr.v4.runtime.atn.SemanticContext#eval(org.antlr.v4.runtime.Recognizer, org.antlr.v4.runtime.RuleContext)
     */
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
