﻿/*
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
#include "atn/Transition.h"
#include "misc/IntervalSet.h"
#include "support/CPPUtils.h"

#include "atn/ATNState.h"

using namespace antlr4::atn;
using namespace antlrcpp;

ATNState::ATNState() {
}

ATNState::~ATNState() {
  for (auto transition : transitions) {
    delete transition;
  }
}

const std::vector<std::string> ATNState::serializationNames = {
  "INVALID", "BASIC", "RULE_START", "BLOCK_START",
  "PLUS_BLOCK_START", "STAR_BLOCK_START", "TOKEN_START", "RULE_STOP",
  "BLOCK_END", "STAR_LOOP_BACK", "STAR_LOOP_ENTRY", "PLUS_LOOP_BACK", "LOOP_END"
};

size_t ATNState::hashCode() {
  return stateNumber;
}

bool ATNState::operator == (const ATNState &other) {
  return stateNumber == other.stateNumber;
}

bool ATNState::isNonGreedyExitState() {
  return false;
}

std::string ATNState::toString() const {
  return std::to_string(stateNumber);
}

void ATNState::addTransition(Transition *e) {
  addTransition(transitions.size(), e);
}

void ATNState::addTransition(size_t index, Transition *e) {
  for (Transition *transition : transitions)
    if (transition->target->stateNumber == e->target->stateNumber) {
      delete e;
      return;
    }

  if (transitions.empty()) {
    epsilonOnlyTransitions = e->isEpsilon();
  } else if (epsilonOnlyTransitions != e->isEpsilon()) {
    std::cerr << "ATN state %d has both epsilon and non-epsilon transitions.\n" << stateNumber;
    epsilonOnlyTransitions = false;
  }

  transitions.insert(transitions.begin() + index, e);
}

Transition *ATNState::removeTransition(size_t index) {
  transitions.erase(transitions.begin() + index);
  return nullptr;
}
