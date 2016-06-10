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

  /// This class represents profiling event information for tracking the lookahead
  /// depth required in order to make a prediction.
  class ANTLR4CPP_PUBLIC LookaheadEventInfo : public DecisionEventInfo {
  public:
    /// The alternative chosen by adaptivePredict(), not necessarily
    ///  the outermost alt shown for a rule; left-recursive rules have
    ///  user-level alts that differ from the rewritten rule with a (...) block
    ///  and a (..)* loop.
    int predictedAlt = 0;

    /// <summary>
    /// Constructs a new instance of the <seealso cref="LookaheadEventInfo"/> class with
    /// the specified detailed lookahead information.
    /// </summary>
    /// <param name="decision"> The decision number </param>
    /// <param name="configs"> The final configuration set containing the necessary
    /// information to determine the result of a prediction, or {@code null} if
    /// the final configuration set is not available </param>
    /// <param name="input"> The input token stream </param>
    /// <param name="startIndex"> The start index for the current prediction </param>
    /// <param name="stopIndex"> The index at which the prediction was finally made </param>
    /// <param name="fullCtx"> {@code true} if the current lookahead is part of an LL
    /// prediction; otherwise, {@code false} if the current lookahead is part of
    /// an SLL prediction </param>
    LookaheadEventInfo(int decision, ATNConfigSet *configs, int predictedAlt, TokenStream *input, int startIndex,
                       int stopIndex, bool fullCtx);
  };

} // namespace atn
} // namespace antlr4
