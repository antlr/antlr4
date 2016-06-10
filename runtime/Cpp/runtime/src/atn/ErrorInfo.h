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
  /// This class represents profiling event information for a syntax error
  /// identified during prediction. Syntax errors occur when the prediction
  /// algorithm is unable to identify an alternative which would lead to a
  /// successful parse.
  /// </summary>
  /// <seealso cref= Parser#notifyErrorListeners(Token, String, RecognitionException) </seealso>
  /// <seealso cref= ANTLRErrorListener#syntaxError
  ///
  /// @since 4.3 </seealso>
  class ANTLR4CPP_PUBLIC ErrorInfo : public DecisionEventInfo {
  public:
    /// <summary>
    /// Constructs a new instance of the <seealso cref="ErrorInfo"/> class with the
    /// specified detailed syntax error information.
    /// </summary>
    /// <param name="decision"> The decision number </param>
    /// <param name="configs"> The final configuration set reached during prediction
    /// prior to reaching the <seealso cref="ATNSimulator#ERROR"/> state </param>
    /// <param name="input"> The input token stream </param>
    /// <param name="startIndex"> The start index for the current prediction </param>
    /// <param name="stopIndex"> The index at which the syntax error was identified </param>
    /// <param name="fullCtx"> {@code true} if the syntax error was identified during LL
    /// prediction; otherwise, {@code false} if the syntax error was identified
    /// during SLL prediction </param>
    ErrorInfo(int decision, ATNConfigSet *configs, TokenStream *input, int startIndex, int stopIndex,
              bool fullCtx);
  };

} // namespace atn
} // namespace antlr4
