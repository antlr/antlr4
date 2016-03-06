/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;

/**
 * This class represents profiling event information for semantic predicate
 * evaluations which occur during prediction.
 *
 * @see ParserATNSimulator#evalSemanticContext
 *
 * @since 4.3
 */
public class PredicateEvalInfo extends DecisionEventInfo {
	/**
	 * The semantic context which was evaluated.
	 */
	public final SemanticContext semctx;
	/**
	 * The alternative number for the decision which is guarded by the semantic
	 * context {@link #semctx}. Note that other ATN
	 * configurations may predict the same alternative which are guarded by
	 * other semantic contexts and/or {@link SemanticContext#NONE}.
	 */
	public final int predictedAlt;
	/**
	 * The result of evaluating the semantic context {@link #semctx}.
	 */
	public final boolean evalResult;

	/**
	 * Constructs a new instance of the {@link PredicateEvalInfo} class with the
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
	 * @see ParserATNSimulator#evalSemanticContext(SemanticContext, ParserRuleContext, int, boolean)
	 * @see SemanticContext#eval(Recognizer, RuleContext)
	 */
	public PredicateEvalInfo(int decision,
							 TokenStream input, int startIndex, int stopIndex,
							 SemanticContext semctx,
							 boolean evalResult,
							 int predictedAlt,
							 boolean fullCtx)
	{
		super(decision, new ATNConfigSet(), input, startIndex, stopIndex, fullCtx);
		this.semctx = semctx;
		this.evalResult = evalResult;
		this.predictedAlt = predictedAlt;
	}
}
