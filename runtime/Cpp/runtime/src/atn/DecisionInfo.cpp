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

#include "atn/ErrorInfo.h"
#include "atn/LookaheadEventInfo.h"

#include "atn/DecisionInfo.h"

using namespace antlr4::atn;

DecisionInfo::DecisionInfo(size_t decision) : decision(decision) {
}

std::string DecisionInfo::toString() const {
  std::stringstream ss;

  ss << "{decision=" << decision << ", contextSensitivities=" << contextSensitivities.size() << ", errors=";
  ss << errors.size() << ", ambiguities=" << ambiguities.size() << ", SLL_lookahead=" << SLL_TotalLook;
  ss << ", SLL_ATNTransitions=" << SLL_ATNTransitions << ", SLL_DFATransitions=" << SLL_DFATransitions;
  ss << ", LL_Fallback=" << LL_Fallback << ", LL_lookahead=" << LL_TotalLook << ", LL_ATNTransitions=" << LL_ATNTransitions << '}';

  return ss.str();
}
