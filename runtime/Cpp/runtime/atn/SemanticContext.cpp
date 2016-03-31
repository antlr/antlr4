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

#include "SemanticContext.h"

using namespace org::antlr::v4::runtime::atn;

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
  if (this == &other) {
    return true;
  }

  const Predicate *p = dynamic_cast<const Predicate*>(&other);
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
  if (this == &other) {
    return true;
  }

  const PrecedencePredicate *predicate = dynamic_cast<const PrecedencePredicate *>(&other);
  return precedence == predicate->precedence;
}

std::wstring SemanticContext::PrecedencePredicate::toString() const {
  return L"Precedence: " + std::to_wstring(precedence);
}


SemanticContext::AND::AND(SemanticContext *a, SemanticContext *b) {
  std::vector<SemanticContext*> *operands = new std::vector<SemanticContext*>();

  if (dynamic_cast<AND*>(a) != nullptr) {
    const std::vector<SemanticContext*> op = ((AND*)a)->opnds;
    for (auto var : op) {
      operands->insert(op.end(), var);
    }
  } else {
    operands->insert(operands->end(), a);
  }
  if (dynamic_cast<AND*>(b) != nullptr) {
    const std::vector<SemanticContext*> op = ((AND*)b)->opnds;
    for (auto var : op) {
      operands->insert(op.end(), var);
    }
  } else {
    operands->insert(operands->end(), b);
  }

  std::vector<PrecedencePredicate*> precedencePredicates = filterPrecedencePredicates<SemanticContext*>(operands);

  if (!precedencePredicates.empty()) {
    // interested in the transition with the lowest precedence
    PrecedencePredicate *reduced = std::min_element(*precedencePredicates.begin(),
                                                    *precedencePredicates.end(),
                                                    (*SemanticContext::PrecedencePredicate::lessThan));
    operands->insert(operands->end(), reduced);
  }

  for (auto op : *operands) {
    opnds.insert(opnds.end(), op);
  }

}

bool SemanticContext::AND::operator == (const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }

  const AND *context = dynamic_cast<const AND *>(&other);
  return opnds == context->opnds;
}


size_t SemanticContext::AND::hashCode() {
  return misc::MurmurHash::hashCode(opnds.data(), opnds.size(), typeid(AND).hash_code());
}



std::wstring SemanticContext::AND::toString() const {
  std::wstring tmp;
  for (auto var : opnds) {
    tmp += var->toString() + L" && ";
  }
  return tmp;
}

SemanticContext::OR::OR(SemanticContext *a, SemanticContext *b){
  std::vector<SemanticContext*> *operands = new std::vector<SemanticContext*>();

  //opnds = operands::toArray(new SemanticContext[operands->size()]);

  if (dynamic_cast<OR*>(a) != nullptr) {
    const std::vector<SemanticContext*> op = ((OR*)a)->opnds;
    for (auto var : op) {
      operands->insert(op.end(), var);
    }
  } else {
    operands->insert(operands->end(), a);
  }
  if (dynamic_cast<OR*>(b) != nullptr) {
    const std::vector<SemanticContext*> op = ((OR*)b)->opnds;
    for (auto var : op) {
      operands->insert(op.end(), var);
    }
  } else {
    operands->insert(operands->end(), b);
  }

  std::vector<PrecedencePredicate*> precedencePredicates = filterPrecedencePredicates(operands);
  if (!precedencePredicates.empty()) {
    // interested in the transition with the highest precedence
    PrecedencePredicate *reduced = std::max_element(*precedencePredicates.begin(),
                                                    *precedencePredicates.end(),
                                                    (*SemanticContext::PrecedencePredicate::greaterThan));
    operands->insert(operands->end(), reduced);
  }
  for (auto op : *operands) {
    opnds.insert(opnds.end(), op);
  }
}

bool SemanticContext::OR::operator == (const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }

  const OR *context = dynamic_cast<const OR *>(&other);

  return opnds == context->opnds;
}

size_t SemanticContext::OR::hashCode() {
  return misc::MurmurHash::hashCode(opnds.data(), opnds.size(), typeid(OR).hash_code());
}


std::wstring SemanticContext::OR::toString() const {
  std::wstring tmp;
  for(auto var : opnds) {
    tmp += var->toString() + L" || ";
  }
  return tmp;
}

SemanticContext *const SemanticContext::NONE = new Predicate();

SemanticContext *SemanticContext::And(SemanticContext *a, SemanticContext *b) {
  if (a == nullptr || a == NONE) {
    return b;
  }
  if (b == nullptr || b == NONE) {
    return a;
  }
  AND *result = new AND(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}

SemanticContext *SemanticContext::Or(SemanticContext *a, SemanticContext *b) {
  if (a == nullptr) {
    return b;
  }
  if (b == nullptr) {
    return a;
  }
  if (a == NONE || b == NONE) {
    return NONE;
  }
  OR *result = new OR(a, b);
  if (result->opnds.size() == 1) {
    return result->opnds[0];
  }

  return result;
}
