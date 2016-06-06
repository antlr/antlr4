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

#include "atn/ParserATNSimulator.h"
#include "Parser.h"
#include "atn/PredicateTransition.h"
#include "atn/ATN.h"
#include "atn/ATNState.h"
#include "support/CPPUtils.h"

#include "FailedPredicateException.h"

using namespace antlr4;
using namespace antlrcpp;

FailedPredicateException::FailedPredicateException(Parser *recognizer) : FailedPredicateException(recognizer, "", "") {
}

FailedPredicateException::FailedPredicateException(Parser *recognizer, const std::string &predicate): FailedPredicateException(recognizer, predicate, "") {
}

FailedPredicateException::FailedPredicateException(Parser *recognizer, const std::string &predicate, const std::string &message)
  : RecognitionException(!message.empty() ? message : "failed predicate: " + predicate + "?", recognizer,
                         recognizer->getInputStream(), recognizer->getContext(), recognizer->getCurrentToken()) {

  atn::ATNState *s = recognizer->getInterpreter<atn::ATNSimulator>()->atn.states[(size_t)recognizer->getState()];
  atn::Transition *transition = s->transition(0);
  if (is<atn::PredicateTransition*>(transition)) {
    _ruleIndex = ((atn::PredicateTransition *)transition)->ruleIndex;
    _predicateIndex = ((atn::PredicateTransition *)transition)->predIndex;
  }
  else {
    _ruleIndex = 0;
    _predicateIndex = 0;
  }

  _predicate = predicate;
}

int FailedPredicateException::getRuleIndex() {
  return _ruleIndex;
}

int FailedPredicateException::getPredIndex() {
  return _predicateIndex;
}

std::string FailedPredicateException::getPredicate() {
  return _predicate;
}
