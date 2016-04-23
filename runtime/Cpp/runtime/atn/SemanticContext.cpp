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

#include "MurmurHash.h"
#include "CPPUtils.h"

#include "SemanticContext.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::atn;
using namespace antlrcpp;

SemanticContext::Predicate::Predicate() : Predicate(-1, -1, false) {
}

SemanticContext::Predicate::Predicate(int ruleIndex, int predIndex, bool isCtxDependent)
  : ruleIndex(ruleIndex), predIndex(predIndex), isCtxDependent(isCtxDependent) {
}

size_t SemanticContext::Predicate::hashCode() {
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

std::wstring SemanticContext::Predicate::toString() const {
  return std::wstring(L"{") + std::to_wstring(ruleIndex) + std::wstring(L":") + std::to_wstring(predIndex) + std::wstring(L"}?");
}

SemanticContext::PrecedencePredicate::PrecedencePredicate() : precedence(0) {
}

SemanticContext::PrecedencePredicate::PrecedencePredicate(int precedence) : precedence(precedence) {
}

int SemanticContext::PrecedencePredicate::compareTo(PrecedencePredicate *o) {
  return precedence - o->precedence;
}

size_t SemanticContext::PrecedencePredicate::hashCode() {
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

std::wstring SemanticContext::PrecedencePredicate::toString() const {
  return L"Precedence: " + std::to_wstring(precedence);
}


SemanticContext::AND::AND(SemanticContext::Ref a, SemanticContext::Ref b) {

  if (is<AND>(a)) {
    const std::vector<SemanticContext::Ref> op = ((AND*)a.get())->opnds;
    for (auto var : op) {
      opnds.push_back(var);
    }
  } else {
    opnds.push_back(a);
  }

  if (is<AND>(b)) {
    const std::vector<SemanticContext::Ref> op = ((AND*)b.get())->opnds;
    for (auto var : op) {
      opnds.push_back(var);
    }
  } else {
    opnds.push_back(b);
  }

  std::vector<std::shared_ptr<PrecedencePredicate>> precedencePredicates = filterPrecedencePredicates(opnds);

  if (!precedencePredicates.empty()) {
    // interested in the transition with the lowest precedence
    auto predicate = [](std::shared_ptr<PrecedencePredicate> a, std::shared_ptr<PrecedencePredicate> b) {
      return a->precedence < b->precedence;
    };
    auto reduced = std::min_element(precedencePredicates.begin(), precedencePredicates.end(), predicate);
    opnds.push_back(*reduced);
  }

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


size_t SemanticContext::AND::hashCode() {
  return misc::MurmurHash::hashCode(opnds, typeid(AND).hash_code());
}



std::wstring SemanticContext::AND::toString() const {
  std::wstring tmp;
  for (auto var : opnds) {
    tmp += var->toString() + L" && ";
  }
  return tmp;
}

SemanticContext::OR::OR(SemanticContext::Ref a, SemanticContext::Ref b){

  if (is<OR>(a)) {
    const std::vector<SemanticContext::Ref> op = ((OR*)a.get())->opnds;
    for (auto var : op) {
      opnds.push_back(var);
    }
  } else {
    opnds.push_back(a);
  }

  if (is<OR>(b)) {
    const std::vector<SemanticContext::Ref> op = ((OR*)b.get())->opnds;
    for (auto var : op) {
      opnds.push_back(var);
    }
  } else {
    opnds.push_back(b);
  }

  std::vector<std::shared_ptr<PrecedencePredicate>> precedencePredicates = filterPrecedencePredicates(opnds);
  if (!precedencePredicates.empty()) {
    // interested in the transition with the highest precedence
    auto predicate = [](std::shared_ptr<PrecedencePredicate> a, std::shared_ptr<PrecedencePredicate> b) {
      return a->precedence > b->precedence;
    };
    auto reduced = std::min_element(precedencePredicates.begin(), precedencePredicates.end(), predicate);
    opnds.push_back(*reduced);
  }
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

size_t SemanticContext::OR::hashCode() {
  return misc::MurmurHash::hashCode(opnds, typeid(OR).hash_code());
}

bool SemanticContext::OR::eval(Recognizer *parser, RuleContext::Ref outerContext) {
  for (auto opnd : opnds) {
    if (opnd->eval(parser, outerContext)) {
      return true;
    }
  }
  return false;
}

std::wstring SemanticContext::OR::toString() const {
  std::wstring tmp;
  for(auto var : opnds) {
    tmp += var->toString() + L" || ";
  }
  return tmp;
}

const SemanticContext::Ref SemanticContext::NONE = std::make_shared<SemanticContext::Predicate>(-1, -1, false);

SemanticContext::Ref SemanticContext::And(SemanticContext::Ref a, SemanticContext::Ref b) {
  if (!a || a == NONE) {
    return b;
  }

  if (!b || b == NONE) {
    return a;
  }

  std::shared_ptr<AND> result = std::make_shared<AND>(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}

SemanticContext::Ref SemanticContext::Or(SemanticContext::Ref a, SemanticContext::Ref b) {
  if (!a) {
    return b;
  }
  if (!b) {
    return a;
  }

  if (a == NONE || b == NONE) {
    return NONE;
  }

  std::shared_ptr<OR> result = std::make_shared<OR>(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}
