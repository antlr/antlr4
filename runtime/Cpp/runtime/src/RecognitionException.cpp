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

#include "atn/ATN.h"
#include "Recognizer.h"
#include "support/StringUtils.h"
#include "ParserRuleContext.h"
#include "misc/IntervalSet.h"

#include "RecognitionException.h"

using namespace antlr4;

RecognitionException::RecognitionException(IRecognizer *recognizer, IntStream *input,
  Ref<ParserRuleContext> const& ctx, Token *offendingToken)
  : RecognitionException("", recognizer, input, ctx, offendingToken) {
}

RecognitionException::RecognitionException(const std::string &message, IRecognizer *recognizer, IntStream *input,
                                           Ref<ParserRuleContext> const& ctx, Token *offendingToken)
  : RuntimeException(message), _recognizer(recognizer), _input(input), _ctx(ctx), _offendingToken(offendingToken) {
  InitializeInstanceFields();
  if (recognizer != nullptr) {
    _offendingState = recognizer->getState();
  }
}

int RecognitionException::getOffendingState() const {
  return _offendingState;
}

void RecognitionException::setOffendingState(int offendingState) {
  _offendingState = offendingState;
}

misc::IntervalSet RecognitionException::getExpectedTokens() const {
  if (_recognizer) {
    return _recognizer->getATN().getExpectedTokens(_offendingState, _ctx);
  }
  return misc::IntervalSet::EMPTY_SET;
}

Ref<RuleContext> RecognitionException::getCtx() const {
  return _ctx;
}

IntStream* RecognitionException::getInputStream() const {
  return _input;
}

Token* RecognitionException::getOffendingToken() const {
  return _offendingToken;
}

IRecognizer* RecognitionException::getRecognizer() const {
  return _recognizer;
}

void RecognitionException::InitializeInstanceFields() {
  _offendingState = -1;
}
