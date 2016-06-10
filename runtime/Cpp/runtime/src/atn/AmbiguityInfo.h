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
#include "support/BitSet.h"

namespace antlr4 {
namespace atn {

  /// <summary>
  /// This class represents profiling event information for an ambiguity.
  /// Ambiguities are decisions where a particular input resulted in an SLL
  /// conflict, followed by LL prediction also reaching a conflict state
  /// (indicating a true ambiguity in the grammar).
  ///
  /// <para>
  /// This event may be reported during SLL prediction in cases where the
  /// conflicting SLL configuration set provides sufficient information to
  /// determine that the SLL conflict is truly an ambiguity. For example, if none
  /// of the ATN configurations in the conflicting SLL configuration set have
  /// traversed a global follow transition (i.e.
  /// <seealso cref="ATNConfig#reachesIntoOuterContext"/> is 0 for all configurations), then
  /// the result of SLL prediction for that input is known to be equivalent to the
  /// result of LL prediction for that input.</para>
  ///
  /// <para>
  /// In some cases, the minimum represented alternative in the conflicting LL
  /// configuration set is not equal to the minimum represented alternative in the
  /// conflicting SLL configuration set. Grammars and inputs which result in this
  /// scenario are unable to use <seealso cref="PredictionMode#SLL"/>, which in turn means
  /// they cannot use the two-stage parsing strategy to improve parsing performance
  /// for that input.</para>
  /// </summary>
  /// <seealso cref= ParserATNSimulator#reportAmbiguity </seealso>
  /// <seealso cref= ANTLRErrorListener#reportAmbiguity
  ///
  /// @since 4.3 </seealso>
  class ANTLR4CPP_PUBLIC AmbiguityInfo : public DecisionEventInfo {
  public:
    /// The set of alternative numbers for this decision event that lead to a valid parse.
    antlrcpp::BitSet ambigAlts;

    /// <summary>
    /// Constructs a new instance of the <seealso cref="AmbiguityInfo"/> class with the
    /// specified detailed ambiguity information.
    /// </summary>
    /// <param name="decision"> The decision number </param>
    /// <param name="configs"> The final configuration set identifying the ambiguous
    /// alternatives for the current input </param>
    /// <param name="ambigAlts"> The set of alternatives in the decision that lead to a valid parse.
    ///                  The predicted alt is the min(ambigAlts) </param>
    /// <param name="input"> The input token stream </param>
    /// <param name="startIndex"> The start index for the current prediction </param>
    /// <param name="stopIndex"> The index at which the ambiguity was identified during
    /// prediction </param>
    /// <param name="fullCtx"> {@code true} if the ambiguity was identified during LL
    /// prediction; otherwise, {@code false} if the ambiguity was identified
    /// during SLL prediction </param>
    AmbiguityInfo(int decision, ATNConfigSet *configs, const antlrcpp::BitSet &ambigAlts, TokenStream *input,
                  size_t startIndex, size_t stopIndex, bool fullCtx);
  };

} // namespace atn
} // namespace antlr4
