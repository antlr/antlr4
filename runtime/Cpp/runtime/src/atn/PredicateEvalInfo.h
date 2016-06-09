/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

#include "atn/DecisionEventInfo.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// This class represents profiling event information for semantic predicate
  /// evaluations which occur during prediction.
  /// </summary>
  /// <seealso cref= ParserATNSimulator#evalSemanticContext
  ///
  /// @since 4.3 </seealso>
  class ANTLR4CPP_PUBLIC PredicateEvalInfo : public DecisionEventInfo {
  public:
    /// The semantic context which was evaluated.
    const Ref<SemanticContext> semctx;

    /// <summary>
    /// The alternative number for the decision which is guarded by the semantic
    /// context <seealso cref="#semctx"/>. Note that other ATN
    /// configurations may predict the same alternative which are guarded by
    /// other semantic contexts and/or <seealso cref="SemanticContext#NONE"/>.
    /// </summary>
    const int predictedAlt;

    /// The result of evaluating the semantic context <seealso cref="#semctx"/>.
    const bool evalResult;

    /// <summary>
    /// Constructs a new instance of the <seealso cref="PredicateEvalInfo"/> class with the
    /// specified detailed predicate evaluation information.
    /// </summary>
    /// <param name="decision"> The decision number </param>
    /// <param name="input"> The input token stream </param>
    /// <param name="startIndex"> The start index for the current prediction </param>
    /// <param name="stopIndex"> The index at which the predicate evaluation was
    /// triggered. Note that the input stream may be reset to other positions for
    /// the actual evaluation of individual predicates. </param>
    /// <param name="semctx"> The semantic context which was evaluated </param>
    /// <param name="evalResult"> The results of evaluating the semantic context </param>
    /// <param name="predictedAlt"> The alternative number for the decision which is
    /// guarded by the semantic context {@code semctx}. See <seealso cref="#predictedAlt"/>
    /// for more information. </param>
    /// <param name="fullCtx"> {@code true} if the semantic context was
    /// evaluated during LL prediction; otherwise, {@code false} if the semantic
    /// context was evaluated during SLL prediction
    /// </param>
    /// <seealso cref= ParserATNSimulator#evalSemanticContext(SemanticContext, ParserRuleContext, int, boolean) </seealso>
    /// <seealso cref= SemanticContext#eval(Recognizer, RuleContext) </seealso>
    PredicateEvalInfo(int decision, TokenStream *input, int startIndex, int stopIndex, Ref<SemanticContext> const& semctx,
                      bool evalResult, int predictedAlt, bool fullCtx);
  };

} // namespace atn
} // namespace antlr4
