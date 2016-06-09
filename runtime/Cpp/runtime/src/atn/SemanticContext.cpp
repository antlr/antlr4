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

#include "misc/MurmurHash.h"
#include "support/CPPUtils.h"

#include "SemanticContext.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

//------------------ Predicate -----------------------------------------------------------------------------------------

SemanticContext::Predicate::Predicate() : Predicate(-1, -1, false) {
}

SemanticContext::Predicate::Predicate(int ruleIndex, int predIndex, bool isCtxDependent)
: ruleIndex(ruleIndex), predIndex(predIndex), isCtxDependent(isCtxDependent) {
}


bool SemanticContext::Predicate::eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  Ref<RuleContext> localctx;
  if (isCtxDependent)
    localctx = parserCallStack;
  return parser->sempred(localctx, ruleIndex, predIndex);
}

size_t SemanticContext::Predicate::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize();
  hashCode = misc::MurmurHash::update(hashCode, (size_t)ruleIndex);
  hashCode = misc::MurmurHash::update(hashCode, (size_t)predIndex);
  hashCode = misc::MurmurHash::update(hashCode, isCtxDependent ? 1 : 0);
  hashCode = misc::MurmurHash::finish(hashCode, 3);
  return hashCode;
}

bool SemanticContext::Predicate::operator == (const SemanticContext &other) const {
  const Predicate *p = dynamic_cast<const Predicate*>(&other);
  if (p == nullptr)
    return false;

  if (this == p) {
    return true;
  }

  if (p == nullptr)
    return false;

  return ruleIndex == p->ruleIndex && predIndex == p->predIndex && isCtxDependent == p->isCtxDependent;
}

std::string SemanticContext::Predicate::toString() const {
  return std::string("{") + std::to_string(ruleIndex) + std::string(":") + std::to_string(predIndex) + std::string("}?");
}

//------------------ PrecedencePredicate -------------------------------------------------------------------------------

SemanticContext::PrecedencePredicate::PrecedencePredicate() : precedence(0) {
}

SemanticContext::PrecedencePredicate::PrecedencePredicate(int precedence) : precedence(precedence) {
}

bool SemanticContext::PrecedencePredicate::eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  return parser->precpred(parserCallStack, precedence);
}

Ref<SemanticContext> SemanticContext::PrecedencePredicate::evalPrecedence(Recognizer *parser,
  Ref<RuleContext> const& parserCallStack) {
  if (parser->precpred(parserCallStack, precedence)) {
    return SemanticContext::NONE;
  }
  else {
    return nullptr;
  }
}

int SemanticContext::PrecedencePredicate::compareTo(PrecedencePredicate *o) {
  return precedence - o->precedence;
}

size_t SemanticContext::PrecedencePredicate::hashCode() const {
  size_t hashCode = 1;
  hashCode = 31 * hashCode + (size_t)precedence;
  return hashCode;
}

bool SemanticContext::PrecedencePredicate::operator == (const SemanticContext &other) const {
  const PrecedencePredicate *predicate = dynamic_cast<const PrecedencePredicate *>(&other);
  if (predicate == nullptr)
    return false;

  if (this == predicate) {
    return true;
  }

  return precedence == predicate->precedence;
}

std::string SemanticContext::PrecedencePredicate::toString() const {
  return "{" + std::to_string(precedence) + ">=prec}?";
}

//------------------ AND -----------------------------------------------------------------------------------------------

SemanticContext::AND::AND(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b) {
  Set operands;

  if (is<AND>(a)) {
    for (auto operand : std::dynamic_pointer_cast<AND>(a)->opnds) {
      operands.insert(operand);
    }
  } else {
    operands.insert(a);
  }

  if (is<AND>(b)) {
    for (auto operand : std::dynamic_pointer_cast<AND>(b)->opnds) {
      operands.insert(operand);
    }
  } else {
    operands.insert(b);
  }

  std::vector<Ref<PrecedencePredicate>> precedencePredicates = filterPrecedencePredicates(operands);

  if (!precedencePredicates.empty()) {
    // interested in the transition with the lowest precedence
    auto predicate = [](Ref<PrecedencePredicate> const& a, Ref<PrecedencePredicate> const& b) {
      return a->precedence < b->precedence;
    };

    auto reduced = std::min_element(precedencePredicates.begin(), precedencePredicates.end(), predicate);
    operands.insert(*reduced);
  }

  std::copy(operands.begin(), operands.end(), std::back_inserter(opnds));
}

std::vector<Ref<SemanticContext>> SemanticContext::AND::getOperands() const {
  return opnds;
}

bool SemanticContext::AND::operator == (const SemanticContext &other) const {
  const AND *context = dynamic_cast<const AND *>(&other);
  if (context == nullptr)
    return false;

  if (this == context) {
    return true;
  }

  return opnds == context->opnds;
}

size_t SemanticContext::AND::hashCode() const {
  return misc::MurmurHash::hashCode(opnds, typeid(AND).hash_code());
}

bool SemanticContext::AND::eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  for (auto opnd : opnds) {
    if (!opnd->eval(parser, parserCallStack)) {
      return false;
    }
  }
  return true;
}

Ref<SemanticContext> SemanticContext::AND::evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  bool differs = false;
  std::vector<Ref<SemanticContext>> operands;
  for (auto context : opnds) {
    Ref<SemanticContext> evaluated = context->evalPrecedence(parser, parserCallStack);
    differs |= (evaluated != context);
    if (evaluated == nullptr) {
      // The AND context is false if any element is false.
      return nullptr;
    } else if (evaluated != NONE) {
      // Reduce the result by skipping true elements.
      operands.push_back(evaluated);
    }
  }

  if (!differs) {
    return shared_from_this();
  }

  if (operands.empty()) {
    // All elements were true, so the AND context is true.
    return NONE;
  }

  Ref<SemanticContext> result = operands[0];
  for (size_t i = 1; i < operands.size(); ++i) {
    result = SemanticContext::And(result, operands[i]);
  }

  return result;
}

std::string SemanticContext::AND::toString() const {
  std::string tmp;
  for (auto var : opnds) {
    tmp += var->toString() + " && ";
  }
  return tmp;
}

//------------------ OR ------------------------------------------------------------------------------------------------

SemanticContext::OR::OR(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b) {
  Set operands;

  if (is<OR>(a)) {
    for (auto operand : std::dynamic_pointer_cast<OR>(a)->opnds) {
      operands.insert(operand);
    }
  } else {
    operands.insert(a);
  }

  if (is<OR>(b)) {
    for (auto operand : std::dynamic_pointer_cast<OR>(b)->opnds) {
      operands.insert(operand);
    }
  } else {
    operands.insert(b);
  }

  std::vector<Ref<PrecedencePredicate>> precedencePredicates = filterPrecedencePredicates(operands);
  if (!precedencePredicates.empty()) {
    // interested in the transition with the highest precedence
    auto predicate = [](Ref<PrecedencePredicate> const& a, Ref<PrecedencePredicate> const& b) {
      return a->precedence < b->precedence;
    };
    auto reduced = std::max_element(precedencePredicates.begin(), precedencePredicates.end(), predicate);
    operands.insert(*reduced);
  }

  std::copy(operands.begin(), operands.end(), std::back_inserter(opnds));
}

std::vector<Ref<SemanticContext>> SemanticContext::OR::getOperands() const {
  return opnds;
}

bool SemanticContext::OR::operator == (const SemanticContext &other) const {
  const OR *context = dynamic_cast<const OR *>(&other);
  if (context == nullptr)
    return false;

  if (this == context) {
    return true;
  }

  return opnds == context->opnds;
}

size_t SemanticContext::OR::hashCode() const {
  return misc::MurmurHash::hashCode(opnds, typeid(OR).hash_code());
}

bool SemanticContext::OR::eval(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  for (auto opnd : opnds) {
    if (opnd->eval(parser, parserCallStack)) {
      return true;
    }
  }
  return false;
}

Ref<SemanticContext> SemanticContext::OR::evalPrecedence(Recognizer *parser, Ref<RuleContext> const& parserCallStack) {
  bool differs = false;
  std::vector<Ref<SemanticContext>> operands;
  for (auto context : opnds) {
    Ref<SemanticContext> evaluated = context->evalPrecedence(parser, parserCallStack);
    differs |= (evaluated != context);
    if (evaluated == NONE) {
      // The OR context is true if any element is true.
      return NONE;
    } else if (evaluated != nullptr) {
      // Reduce the result by skipping false elements.
      operands.push_back(evaluated);
    }
  }

  if (!differs) {
    return shared_from_this();
  }

  if (operands.empty()) {
    // All elements were false, so the OR context is false.
    return nullptr;
  }

  Ref<SemanticContext> result = operands[0];
  for (size_t i = 1; i < operands.size(); ++i) {
    result = SemanticContext::Or(result, operands[i]);
  }

  return result;
}

std::string SemanticContext::OR::toString() const {
  std::string tmp;
  for(auto var : opnds) {
    tmp += var->toString() + " || ";
  }
  return tmp;
}

//------------------ SemanticContext -----------------------------------------------------------------------------------

const Ref<SemanticContext> SemanticContext::NONE = std::make_shared<Predicate>(-1, -1, false);

Ref<SemanticContext> SemanticContext::evalPrecedence(Recognizer * /*parser*/, Ref<RuleContext> const& /*parserCallStack*/) {
  return shared_from_this();
}

Ref<SemanticContext> SemanticContext::And(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b) {
  if (!a || a == NONE) {
    return b;
  }

  if (!b || b == NONE) {
    return a;
  }

  Ref<AND> result = std::make_shared<AND>(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}

Ref<SemanticContext> SemanticContext::Or(Ref<SemanticContext> const& a, Ref<SemanticContext> const& b) {
  if (!a) {
    return b;
  }
  if (!b) {
    return a;
  }

  if (a == NONE || b == NONE) {
    return NONE;
  }

  Ref<OR> result = std::make_shared<OR>(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}

std::vector<Ref<SemanticContext::PrecedencePredicate>> SemanticContext::filterPrecedencePredicates(const Set &collection) {
  std::vector<Ref<SemanticContext::PrecedencePredicate>> result;
  for (auto context : collection) {
    if (antlrcpp::is<PrecedencePredicate>(context)) {
      result.push_back(std::dynamic_pointer_cast<PrecedencePredicate>(context));
    }
  }

  return result;
}
