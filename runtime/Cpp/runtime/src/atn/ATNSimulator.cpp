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

#include "atn/ATNType.h"
#include "atn/ATNConfigSet.h"
#include "dfa/DFAState.h"
#include "atn/ATNDeserializer.h"
#include "atn/EmptyPredictionContext.h"

#include "atn/ATNSimulator.h"

using namespace antlr4;
using namespace antlr4::dfa;
using namespace antlr4::atn;

const Ref<DFAState> ATNSimulator::ERROR = std::make_shared<DFAState>(INT32_MAX);

ATNSimulator::ATNSimulator(const ATN &atn, PredictionContextCache &sharedContextCache)
: atn(atn), _sharedContextCache(sharedContextCache) {
}

void ATNSimulator::clearDFA() {
  throw UnsupportedOperationException("This ATN simulator does not support clearing the DFA.");
}

PredictionContextCache& ATNSimulator::getSharedContextCache() {
  return _sharedContextCache;
}

Ref<PredictionContext> ATNSimulator::getCachedContext(Ref<PredictionContext> const& context) {
  std::lock_guard<std::recursive_mutex> lck(mtx);
  std::map<Ref<PredictionContext>, Ref<PredictionContext>> visited;
  return PredictionContext::getCachedContext(context, _sharedContextCache, visited);
}

ATN ATNSimulator::deserialize(const std::vector<uint16_t> &data) {
  ATNDeserializer deserializer;
  return deserializer.deserialize(data);
}

void ATNSimulator::checkCondition(bool condition) {
  ATNDeserializer::checkCondition(condition);
}

void ATNSimulator::checkCondition(bool condition, const std::string &message) {
  ATNDeserializer::checkCondition(condition, message);
}

Transition *ATNSimulator::edgeFactory(const ATN &atn, int type, int src, int trg, int arg1, int arg2, int arg3,
                                      const std::vector<misc::IntervalSet> &sets) {
  return ATNDeserializer::edgeFactory(atn, type, src, trg, arg1, arg2, arg3, sets);
}

ATNState *ATNSimulator::stateFactory(int type, int ruleIndex) {
  return ATNDeserializer::stateFactory(type, ruleIndex);
}
