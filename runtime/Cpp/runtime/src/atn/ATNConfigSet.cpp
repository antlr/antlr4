/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "atn/PredictionContext.h"
#include "atn/ATNConfig.h"
#include "atn/ATNSimulator.h"
#include "Exceptions.h"
#include "atn/SemanticContext.h"
#include "misc/MurmurHash.h"
#include "support/Arrays.h"

#include "atn/ATNConfigSet.h"

using namespace antlr4::atn;
using namespace antlr4::misc;
using namespace antlrcpp;

namespace {

  bool atnConfigEqual(const Ref<ATNConfig> &lhs, const Ref<ATNConfig> &rhs) {
    return *lhs == *rhs;
  }

}

ATNConfigSet::ATNConfigSet() : ATNConfigSet(ATNType::PARSER, true) {}

ATNConfigSet::ATNConfigSet(ATNType type) : ATNConfigSet(type, true) {}

ATNConfigSet::ATNConfigSet(bool fullCtx) : ATNConfigSet(ATNType::PARSER, fullCtx) {}

ATNConfigSet::ATNConfigSet(const ATNConfigSet &other)
    : fullCtx(other.fullCtx), _type(other._type),
      _configLookup(std::unordered_set<ATNConfig*>().bucket_count(), ATNConfigHasher{_type}, ATNConfigComparer{_type}) {
  addAll(other);
  uniqueAlt = other.uniqueAlt;
  conflictingAlts = other.conflictingAlts;
  hasSemanticContext = other.hasSemanticContext;
  dipsIntoOuterContext = other.dipsIntoOuterContext;
}

ATNConfigSet::ATNConfigSet(ATNType type, bool fullCtx)
    : fullCtx(fullCtx), _type(type),
      _configLookup(std::unordered_set<ATNConfig*>().bucket_count(), ATNConfigHasher{_type}, ATNConfigComparer{_type}) {}

void ATNConfigSet::add(const Ref<ATNConfig> &config) {
  add(config, nullptr);
}

void ATNConfigSet::add(const Ref<ATNConfig> &config, PredictionContextMergeCache *mergeCache) {
  assert(config);

  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  if (config->semanticContext != SemanticContext::NONE) {
    hasSemanticContext = true;
  }
  if (config->getOuterContextDepth() > 0) {
    dipsIntoOuterContext = true;
  }

  auto [existing, inserted] = _configLookup.insert(config.get());
  if (inserted) {
    _cachedHashCode = 0;
    configs.push_back(config); // track order here
    return;
  }

  // a previous (s,i,pi,_), merge with it and save result
  bool rootIsWildcard = !fullCtx;
  Ref<const PredictionContext> merged = PredictionContext::merge((*existing)->context, config->context, rootIsWildcard, mergeCache);
  // no need to check for existing.context, config.context in cache
  // since only way to create new graphs is "call rule" and here. We
  // cache at both places.
  (*existing)->reachesIntoOuterContext = std::max((*existing)->reachesIntoOuterContext, config->reachesIntoOuterContext);

  // make sure to preserve the precedence filter suppression during the merge
  if (config->isPrecedenceFilterSuppressed()) {
    (*existing)->setPrecedenceFilterSuppressed(true);
  }

  (*existing)->context = std::move(merged); // replace context; no need to alt mapping
}

void ATNConfigSet::addAll(const ATNConfigSet &other) {
  for (const auto &config : other.configs) {
    add(config);
  }
}

std::vector<ATNState*> ATNConfigSet::getStates() const {
  std::vector<ATNState*> states;
  states.reserve(configs.size());
  for (const auto &c : configs) {
    states.push_back(c->state);
  }
  return states;
}

/**
 * Gets the complete set of represented alternatives for the configuration
 * set.
 *
 * @return the set of represented alternatives in this configuration set
 *
 * @since 4.3
 */

BitSet ATNConfigSet::getAlts() const {
  BitSet alts;
  for (const auto &config : configs) {
    alts.set(config->alt);
  }
  return alts;
}

std::vector<Ref<const SemanticContext>> ATNConfigSet::getPredicates() const {
  std::vector<Ref<const SemanticContext>> preds;
  preds.reserve(configs.size());
  for (const auto &c : configs) {
    if (c->semanticContext != SemanticContext::NONE) {
      preds.push_back(c->semanticContext);
    }
  }
  return preds;
}

const Ref<ATNConfig>& ATNConfigSet::get(size_t i) const {
  return configs[i];
}

void ATNConfigSet::optimizeConfigs(ATNSimulator *interpreter) {
  assert(interpreter);

  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  if (_configLookup.empty()) {
    return;
  }
  for (const auto &config : configs) {
    config->context = interpreter->getCachedContext(config->context);
  }
}

bool ATNConfigSet::equals(const ATNConfigSet &other) const {
  if (this == std::addressof(other)) {
    return true;
  }
  if (configs.size() != other.configs.size()) {
    return false;
  }
  return fullCtx == other.fullCtx && uniqueAlt == other.uniqueAlt &&
         hasSemanticContext == other.hasSemanticContext &&
         dipsIntoOuterContext == other.dipsIntoOuterContext &&
         conflictingAlts == other.conflictingAlts &&
         std::equal(configs.begin(), configs.end(), other.configs.begin(), atnConfigEqual);
}

size_t ATNConfigSet::hashCode() const {
  bool readOnly = isReadonly();
  size_t hash = readOnly ? _cachedHashCode.load(std::memory_order_relaxed) : 0;
  if (hash == 0) {
    hash = MurmurHash::initialize();
    hash = MurmurHash::update(hash, fullCtx ? 1 : 0);
    hash = MurmurHash::update(hash, uniqueAlt);
    hash = MurmurHash::update(hash, std::hash<antlrcpp::BitSet>{}(conflictingAlts));
    hash = MurmurHash::update(hash, hasSemanticContext ? 1 : 0);
    hash = MurmurHash::update(hash, dipsIntoOuterContext ? 1 : 0);
    for (const auto &config : configs) {
      hash = MurmurHash::update(hash, config);
    }
    hash = MurmurHash::finish(hash, 5 + configs.size());
    if (hash == 0) {
      hash = std::numeric_limits<size_t>::max();
    }
    if (readOnly) {
      _cachedHashCode.store(hash, std::memory_order_relaxed);
    }
  }
  return hash;
}

size_t ATNConfigSet::size() const {
  return configs.size();
}

bool ATNConfigSet::isEmpty() const {
  return configs.empty();
}

void ATNConfigSet::clear() {
  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  configs.clear();
  _cachedHashCode = 0;
  _configLookup.clear();
}

bool ATNConfigSet::isReadonly() const {
  return _readonly;
}

void ATNConfigSet::setReadonly(bool readonly) {
  _readonly = readonly;
  Container(std::unordered_set<ATNConfig*>().bucket_count(), ATNConfigHasher{_type}, ATNConfigComparer{_type}).swap(_configLookup);
}

std::string ATNConfigSet::toString() const {
  std::stringstream ss;
  ss << "[";
  for (size_t i = 0; i < configs.size(); i++) {
    ss << configs[i]->toString();
  }
  ss << "]";

  if (hasSemanticContext) {
    ss << ",hasSemanticContext = " <<  hasSemanticContext;
  }
  if (uniqueAlt != ATN::INVALID_ALT_NUMBER) {
    ss << ",uniqueAlt = " << uniqueAlt;
  }

  if (conflictingAlts.size() > 0) {
    ss << ",conflictingAlts = ";
    ss << conflictingAlts.toString();
  }

  if (dipsIntoOuterContext) {
    ss << ", dipsIntoOuterContext";
  }
  return ss.str();
}

size_t ATNConfigSet::ATNConfigHasher::operator()(const ATNConfig *atnConfig) const {
  assert(atnConfig != nullptr);
  switch (type) {
    case ATNType::LEXER:
      return atnConfig->hashCode();
    case ATNType::PARSER: {
      size_t hash = MurmurHash::initialize();
      hash = MurmurHash::update(hash, atnConfig->state->stateNumber);
      hash = MurmurHash::update(hash, atnConfig->alt);
      hash = MurmurHash::update(hash, atnConfig->semanticContext);
      return MurmurHash::finish(hash, 3);
    }
  }
  throw IllegalStateException("unreachable");
}

bool ATNConfigSet::ATNConfigComparer::operator()(const ATNConfig *lhs, const ATNConfig *rhs) const {
  assert(lhs != nullptr);
  assert(rhs != nullptr);
  if (lhs == rhs) {
    return true;
  }
  switch (type) {
    case ATNType::LEXER:
      return *lhs == *rhs;
    case ATNType::PARSER:
      return lhs->state->stateNumber == rhs->state->stateNumber &&
             lhs->alt == rhs->alt &&
             *lhs->semanticContext == *rhs->semanticContext;
  }
  throw IllegalStateException("unreachable");
}
