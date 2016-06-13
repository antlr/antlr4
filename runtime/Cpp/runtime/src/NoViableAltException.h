/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#include "RecognitionException.h"
#include "Token.h"
#include "atn/ATNConfigSet.h"

namespace antlr4 {

  /// Indicates that the parser could not decide which of two or more paths
  /// to take based upon the remaining input. It tracks the starting token
  /// of the offending input and also knows where the parser was
  /// in the various paths when the error. Reported by reportNoViableAlternative()
  class ANTLR4CPP_PUBLIC NoViableAltException : public RecognitionException {
  public:
    NoViableAltException(Parser *recognizer); // LL(1) error
    NoViableAltException(Parser *recognizer, TokenStream *input,Token *startToken,
      Token *offendingToken, atn::ATNConfigSet *deadEndConfigs, Ref<ParserRuleContext> const& ctx);

    virtual Token* getStartToken() const;
    virtual atn::ATNConfigSet* getDeadEndConfigs() const;

  private:
    /// Which configurations did we try at input.index() that couldn't match input.LT(1)?
    atn::ATNConfigSet* _deadEndConfigs;

    /// The token object at the start index; the input stream might
    /// not be buffering tokens so get a reference to it. (At the
    /// time the error occurred, of course the stream needs to keep a
    /// buffer all of the tokens but later we might not have access to those.)
    Token *_startToken;
   
  };

} // namespace antlr4
