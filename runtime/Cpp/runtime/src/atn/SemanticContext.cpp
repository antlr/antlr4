/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include <algorithm>
#include <memory>
#include <utility>
#include <vector>
#include <string>
#include <cstddef>

#include "misc/MurmurHash.h"
#include "antlr4-common.h"
#include "support/Casts.h"
#include "support/CPPUtils.h"
#include "support/Arrays.h"

#include "SemanticContext.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

namespace {

  template <typename T>
  int compareValues(const T &lhs, const T &rhs) {
    return lhs < rhs ? -1 : (rhs < lhs ? 1 : 0);
  }

  int kind(const SemanticContext &context) {
    switch (context.getContextType()) {
      case SemanticContextType::PREDICATE:
        return 0;
      case SemanticContextType::PRECEDENCE:
        return 1;
      case SemanticContextType::AND:
        return 2;
      case SemanticContextType::OR:
        return 3;
      default:
        return 4;
    }
  }

  // Orders contexts by kind and then by their fields, so an operator holds
  // its operands in one order whichever order they were combined in.
  int compare(const SemanticContext &a, const SemanticContext &b) {
    int c = compareValues(kind(a), kind(b));
    if (c != 0) {
      return c;
    }
    switch (a.getContextType()) {
      case SemanticContextType::PREDICATE: {
        const auto &p = downCast<const SemanticContext::Predicate&>(a);
        const auto &q = downCast<const SemanticContext::Predicate&>(b);
        if (p.ruleIndex != q.ruleIndex) {
          return compareValues(p.ruleIndex, q.ruleIndex);
        }
        if (p.predIndex != q.predIndex) {
          return compareValues(p.predIndex, q.predIndex);
        }
        return compareValues(p.isCtxDependent, q.isCtxDependent);
      }
      case SemanticContextType::PRECEDENCE:
        return compareValues(downCast<const SemanticContext::PrecedencePredicate&>(a).precedence,
                             downCast<const SemanticContext::PrecedencePredicate&>(b).precedence);
      case SemanticContextType::AND:
      case SemanticContextType::OR: {
        const auto &x = downCast<const SemanticContext::Operator&>(a).getOperands();
        const auto &y = downCast<const SemanticContext::Operator&>(b).getOperands();
        if (x.size() != y.size()) {
          return compareValues(x.size(), y.size());
        }
        for (size_t i = 0; i < x.size(); ++i) {
          c = compare(*x[i], *y[i]);
          if (c != 0) {
            return c;
          }
        }
        return 0;
      }
      default:
        return compareValues(a.hashCode(), b.hashCode());
    }
  }

  std::pair<const Ref<const SemanticContext>*, const Ref<const SemanticContext>*> operandsOf(
    const Ref<const SemanticContext> &context, SemanticContextType type) {
    if (context->getContextType() == type) {
      const auto &operands = downCast<const SemanticContext::Operator&>(*context).getOperands();
      return {operands.data(), operands.data() + operands.size()};
    }
    return {&context, &context + 1};
  }

  // Merges the operands of a and b, each already in compare order, into one
  // list in that order without duplicates.
  void mergeOperands(const Ref<const SemanticContext> &a, const Ref<const SemanticContext> &b, SemanticContextType type,
                     std::vector<Ref<const SemanticContext>> &merged) {
    auto [x, xEnd] = operandsOf(a, type);
    auto [y, yEnd] = operandsOf(b, type);
    merged.reserve(static_cast<size_t>((xEnd - x) + (yEnd - y)));
    while (x != xEnd && y != yEnd) {
      int c = compare(**x, **y);
      // compare orders contexts of other kinds by hash code, so it can tie two unequal ones
      if (c < 0 || (c == 0 && **x != **y)) {
        merged.push_back(*x++);
      } else if (c > 0) {
        merged.push_back(*y++);
      } else {
        merged.push_back(*x++);
        ++y;
      }
    }
    merged.insert(merged.end(), x, xEnd);
    merged.insert(merged.end(), y, yEnd);
  }

  // Precedence predicates sort next to each other by precedence, so the first
  // has the lowest and the last the highest.
  void keepOnePrecedencePredicate(std::vector<Ref<const SemanticContext>> &operands, bool lowest) {
    auto isPrecedencePredicate = [](const Ref<const SemanticContext> &operand) {
      return SemanticContext::PrecedencePredicate::is(*operand);
    };
    auto first = std::find_if(operands.begin(), operands.end(), isPrecedencePredicate);
    auto last = std::find_if_not(first, operands.end(), isPrecedencePredicate);
    if (first == last) {
      return;
    }
    if (!lowest) {
      std::iter_swap(first, last - 1);
    }
    operands.erase(first + 1, last);
  }

}

//------------------ Predicate -----------------------------------------------------------------------------------------

SemanticContext::Predicate::Predicate(size_t ruleIndex, size_t predIndex, bool isCtxDependent)
    : SemanticContext(SemanticContextType::PREDICATE), ruleIndex(ruleIndex), predIndex(predIndex), isCtxDependent(isCtxDependent) {}

bool SemanticContext::Predicate::eval(Recognizer *parser, RuleContext *parserCallStack) const {
  RuleContext *localctx = nullptr;
  if (isCtxDependent) {
    localctx = parserCallStack;
  }
  return parser->sempred(localctx, ruleIndex, predIndex);
}

size_t SemanticContext::Predicate::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize();
  hashCode = misc::MurmurHash::update(hashCode, static_cast<size_t>(getContextType()));
  hashCode = misc::MurmurHash::update(hashCode, ruleIndex);
  hashCode = misc::MurmurHash::update(hashCode, predIndex);
  hashCode = misc::MurmurHash::update(hashCode, isCtxDependent ? 1 : 0);
  hashCode = misc::MurmurHash::finish(hashCode, 4);
  return hashCode;
}

bool SemanticContext::Predicate::equals(const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }
  if (getContextType() != other.getContextType()) {
    return false;
  }
  const Predicate &p = downCast<const Predicate&>(other);
  return ruleIndex == p.ruleIndex && predIndex == p.predIndex && isCtxDependent == p.isCtxDependent;
}

std::string SemanticContext::Predicate::toString() const {
  return std::string("{") + std::to_string(ruleIndex) + std::string(":") + std::to_string(predIndex) + std::string("}?");
}

//------------------ PrecedencePredicate -------------------------------------------------------------------------------

SemanticContext::PrecedencePredicate::PrecedencePredicate(int precedence) : SemanticContext(SemanticContextType::PRECEDENCE), precedence(precedence) {}

bool SemanticContext::PrecedencePredicate::eval(Recognizer *parser, RuleContext *parserCallStack) const {
  return parser->precpred(parserCallStack, precedence);
}

Ref<const SemanticContext> SemanticContext::PrecedencePredicate::evalPrecedence(Recognizer *parser,
  RuleContext *parserCallStack) const {
  if (parser->precpred(parserCallStack, precedence)) {
    return SemanticContext::Empty::Instance;
  }
  return nullptr;
}

size_t SemanticContext::PrecedencePredicate::hashCode() const {
  size_t hashCode = misc::MurmurHash::initialize();
  hashCode = misc::MurmurHash::update(hashCode, static_cast<size_t>(getContextType()));
  hashCode = misc::MurmurHash::update(hashCode, static_cast<size_t>(precedence));
  return misc::MurmurHash::finish(hashCode, 2);
}

bool SemanticContext::PrecedencePredicate::equals(const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }
  if (getContextType() != other.getContextType()) {
    return false;
  }
  const PrecedencePredicate &predicate = downCast<const PrecedencePredicate&>(other);
  return precedence == predicate.precedence;
}

std::string SemanticContext::PrecedencePredicate::toString() const {
  return "{" + std::to_string(precedence) + ">=prec}?";
}

//------------------ AND -----------------------------------------------------------------------------------------------

SemanticContext::AND::AND(Ref<const SemanticContext> a, Ref<const SemanticContext> b) : Operator(SemanticContextType::AND) {
  mergeOperands(a, b, SemanticContextType::AND, _opnds);
  // interested in the transition with the lowest precedence
  keepOnePrecedencePredicate(_opnds, true);
}

const std::vector<Ref<const SemanticContext>>& SemanticContext::AND::getOperands() const {
  return _opnds;
}

bool SemanticContext::AND::equals(const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }
  if (getContextType() != other.getContextType()) {
    return false;
  }
  const AND &context = downCast<const AND&>(other);
  return Arrays::equals(getOperands(), context.getOperands());
}

size_t SemanticContext::AND::hashCode() const {
  size_t hash = misc::MurmurHash::initialize();
  hash = misc::MurmurHash::update(hash, static_cast<size_t>(getContextType()));
  return misc::MurmurHash::hashCode(getOperands(), hash);
}

bool SemanticContext::AND::eval(Recognizer *parser, RuleContext *parserCallStack) const {
  for (const auto &opnd : getOperands()) {
    if (!opnd->eval(parser, parserCallStack)) {
      return false;
    }
  }
  return true;
}

Ref<const SemanticContext> SemanticContext::AND::evalPrecedence(Recognizer *parser, RuleContext *parserCallStack) const {
  bool differs = false;
  std::vector<Ref<const SemanticContext>> operands;
  for (const auto &context : getOperands()) {
    auto evaluated = context->evalPrecedence(parser, parserCallStack);
    differs |= (evaluated != context);
    if (evaluated == nullptr) {
      // The AND context is false if any element is false.
      return nullptr;
    }
    if (evaluated != Empty::Instance) {
      // Reduce the result by skipping true elements.
      operands.push_back(std::move(evaluated));
    }
  }

  if (!differs) {
    return shared_from_this();
  }

  if (operands.empty()) {
    // All elements were true, so the AND context is true.
    return Empty::Instance;
  }

  Ref<const SemanticContext> result = std::move(operands[0]);
  for (size_t i = 1; i < operands.size(); ++i) {
    result = SemanticContext::And(std::move(result), std::move(operands[i]));
  }

  return result;
}

std::string SemanticContext::AND::toString() const {
  std::string tmp;
  for (const auto &var : getOperands()) {
    tmp += var->toString() + " && ";
  }
  return tmp;
}

//------------------ OR ------------------------------------------------------------------------------------------------

SemanticContext::OR::OR(Ref<const SemanticContext> a, Ref<const SemanticContext> b) : Operator(SemanticContextType::OR) {
  mergeOperands(a, b, SemanticContextType::OR, _opnds);
  // interested in the transition with the highest precedence
  keepOnePrecedencePredicate(_opnds, false);
}

const std::vector<Ref<const SemanticContext>>& SemanticContext::OR::getOperands() const {
  return _opnds;
}

bool SemanticContext::OR::equals(const SemanticContext &other) const {
  if (this == &other) {
    return true;
  }
  if (getContextType() != other.getContextType()) {
    return false;
  }
  const OR &context = downCast<const OR&>(other);
  return Arrays::equals(getOperands(), context.getOperands());
}

size_t SemanticContext::OR::hashCode() const {
  size_t hash = misc::MurmurHash::initialize();
  hash = misc::MurmurHash::update(hash, static_cast<size_t>(getContextType()));
  return misc::MurmurHash::hashCode(getOperands(), hash);
}

bool SemanticContext::OR::eval(Recognizer *parser, RuleContext *parserCallStack) const {
  for (const auto &opnd : getOperands()) {
    if (opnd->eval(parser, parserCallStack)) {
      return true;
    }
  }
  return false;
}

Ref<const SemanticContext> SemanticContext::OR::evalPrecedence(Recognizer *parser, RuleContext *parserCallStack) const {
  bool differs = false;
  std::vector<Ref<const SemanticContext>> operands;
  for (const auto &context : getOperands()) {
    auto evaluated = context->evalPrecedence(parser, parserCallStack);
    differs |= (evaluated != context);
    if (evaluated == Empty::Instance) {
      // The OR context is true if any element is true.
      return Empty::Instance;
    }
    if (evaluated != nullptr) {
      // Reduce the result by skipping false elements.
      operands.push_back(std::move(evaluated));
    }
  }

  if (!differs) {
    return shared_from_this();
  }

  if (operands.empty()) {
    // All elements were false, so the OR context is false.
    return nullptr;
  }

  Ref<const SemanticContext> result = std::move(operands[0]);
  for (size_t i = 1; i < operands.size(); ++i) {
    result = SemanticContext::Or(std::move(result), std::move(operands[i]));
  }

  return result;
}

std::string SemanticContext::OR::toString() const {
  std::string tmp;
  for(const auto &var : getOperands()) {
    tmp += var->toString() + " || ";
  }
  return tmp;
}

//------------------ SemanticContext -----------------------------------------------------------------------------------

const Ref<const SemanticContext> SemanticContext::Empty::Instance = std::make_shared<Predicate>(INVALID_INDEX, INVALID_INDEX, false);

Ref<const SemanticContext> SemanticContext::evalPrecedence(Recognizer * /*parser*/, RuleContext * /*parserCallStack*/) const {
  return shared_from_this();
}

Ref<const SemanticContext> SemanticContext::And(Ref<const SemanticContext> a, Ref<const SemanticContext> b) {
  if (!a || a == Empty::Instance) {
    return b;
  }

  if (!b || b == Empty::Instance) {
    return a;
  }

  Ref<AND> result = std::make_shared<AND>(std::move(a), std::move(b));
  if (result->getOperands().size() == 1) {
    return result->getOperands()[0];
  }

  return result;
}

Ref<const SemanticContext> SemanticContext::Or(Ref<const SemanticContext> a, Ref<const SemanticContext> b) {
  if (!a) {
    return b;
  }
  if (!b) {
    return a;
  }

  if (a == Empty::Instance || b == Empty::Instance) {
    return Empty::Instance;
  }

  Ref<OR> result = std::make_shared<OR>(std::move(a), std::move(b));
  if (result->getOperands().size() == 1) {
    return result->getOperands()[0];
  }

  return result;
}
