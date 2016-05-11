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

#include "PredictionContext.h"
#include "ATNConfig.h"
#include "ATNSimulator.h"
#include "Exceptions.h"
#include "SemanticContext.h"

#include "ATNConfigSet.h"

using namespace org::antlr::v4::runtime::atn;
using namespace antlrcpp;

size_t SimpleATNConfigHasher::operator()(const Ref<ATNConfig> &k) const {
  size_t hashCode = 7;
  hashCode = 31 * hashCode + (size_t)k->state->stateNumber;
  hashCode = 31 * hashCode + (size_t)k->alt;
  hashCode = 31 * hashCode + k->semanticContext->hashCode();
  return hashCode;
}

bool SimpleATNConfigComparer::operator () (const Ref<ATNConfig> &lhs, const Ref<ATNConfig> &rhs) const {
  return lhs->state->stateNumber == rhs->state->stateNumber && lhs->alt == rhs->alt &&
    lhs->semanticContext == rhs->semanticContext;
}

//------------------ ATNConfigSet --------------------------------------------------------------------------------------

ATNConfigSet::ATNConfigSet(bool fullCtx) : fullCtx(fullCtx) {
  InitializeInstanceFields();
}

ATNConfigSet::ATNConfigSet(Ref<ATNConfigSet> old) : ATNConfigSet(old->fullCtx) {
  addAll(old);
  uniqueAlt = old->uniqueAlt;
  conflictingAlts = old->conflictingAlts;
  hasSemanticContext = old->hasSemanticContext;
  dipsIntoOuterContext = old->dipsIntoOuterContext;
}

ATNConfigSet::~ATNConfigSet() {
}

bool ATNConfigSet::add(Ref<ATNConfig> config) {
  return add(config, nullptr);
}

bool ATNConfigSet::add(Ref<ATNConfig> config, PredictionContextMergeCache *mergeCache) {
  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  if (config->semanticContext != SemanticContext::NONE) {
    hasSemanticContext = true;
  }
  if (config->getOuterContextDepth() > 0) {
    dipsIntoOuterContext = true;
  }
  
  Ref<ATNConfig> existing = configLookup->getOrAdd(config);
  if (existing.get() == config.get()) { // Compare mem addresses, not content (which would happen when we compare the refs).
    _cachedHashCode = 0;
    configs.push_back(config); // track order here

    assert(configLookup->size() == configs.size());
    return true;
  }
  // a previous (s,i,pi,_), merge with it and save result
  bool rootIsWildcard = !fullCtx;
  Ref<PredictionContext> merged = PredictionContext::merge(existing->context, config->context, rootIsWildcard, mergeCache);
  // no need to check for existing.context, config.context in cache
  // since only way to create new graphs is "call rule" and here. We
  // cache at both places.
  existing->reachesIntoOuterContext = std::max(existing->reachesIntoOuterContext, config->reachesIntoOuterContext);

  // make sure to preserve the precedence filter suppression during the merge
  if (config->isPrecedenceFilterSuppressed()) {
    existing->setPrecedenceFilterSuppressed(true);
  }
  
  existing->context = merged; // replace context; no need to alt mapping

  assert(configLookup->size() == configs.size());
  return true;
}

bool ATNConfigSet::addAll(Ref<ATNConfigSet> other) {
  for (auto &c : other->configs) {
    add(c);
  }
  return false;
}

std::vector<Ref<ATNConfig>> ATNConfigSet::elements() {
  return configs;
}

std::vector<ATNState*> ATNConfigSet::getStates() {
  std::vector<ATNState*> states;
  for (auto c : configs) {
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

BitSet ATNConfigSet::getAlts() {
  BitSet alts;
  for (ATNConfig config : configs) {
    alts.set(config.alt);
  }
  return alts;
}

std::vector<Ref<SemanticContext>> ATNConfigSet::getPredicates() {
  std::vector<Ref<SemanticContext>> preds;
  for (auto c : configs) {
    if (c->semanticContext != SemanticContext::NONE) {
      preds.push_back(c->semanticContext);
    }
  }
  return preds;
}

Ref<ATNConfig> ATNConfigSet::get(size_t i) const {
  return configs[i];
}

void ATNConfigSet::optimizeConfigs(ATNSimulator *interpreter) {
  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  if (configLookup->isEmpty()) {
    return;
  }

  for (auto &config : configs) {
    config->context = interpreter->getCachedContext(config->context);
  }
}

bool ATNConfigSet::operator == (const ATNConfigSet &other) {
  if (&other == this) {
    return true;
  }

  bool configEquals = true;
  if (configs.size() == other.configs.size()) {
    for (size_t i = 0; i < configs.size(); i++) {
      if (other.configs.size() < i) {
        configEquals = false;
        break;
      }
      if (configs[i] != other.configs[i]) {
        configEquals = false;
        break;
      }
    }
  } else {
    configEquals = false;
  }

  bool same = configs.size() > 0 && configEquals && fullCtx == other.fullCtx && uniqueAlt == other.uniqueAlt &&
    conflictingAlts == other.conflictingAlts && hasSemanticContext == other.hasSemanticContext &&
    dipsIntoOuterContext == other.dipsIntoOuterContext; // includes stack context

  return same;
}

size_t ATNConfigSet::hashCode() {
  if (isReadonly()) {
    if (_cachedHashCode == 0) {
      _cachedHashCode = std::hash<std::vector<Ref<ATNConfig>>>()(configs);
    }

    return _cachedHashCode;
  }

  return std::hash<std::vector<Ref<ATNConfig>>>()(configs);
}

size_t ATNConfigSet::size() {
  return configs.size();
}

bool ATNConfigSet::isEmpty() {
  return configs.empty();
}

bool ATNConfigSet::contains(Ref<ATNConfig> o) {
  if (configLookup == nullptr) {
    throw UnsupportedOperationException("This method is not implemented for readonly sets.");
  }

  return configLookup->contains(o);
}

void ATNConfigSet::clear() {
  if (_readonly) {
    throw IllegalStateException("This set is readonly");
  }
  configs.clear();
  _cachedHashCode = 0;
  configLookup->clear();
}

bool ATNConfigSet::isReadonly() {
  return _readonly;
}

void ATNConfigSet::setReadonly(bool readonly) {
  _readonly = readonly;
  configLookup.reset();
}

std::wstring ATNConfigSet::toString() {
  std::wstringstream ss;
  ss << L"[";
  for (size_t i = 0; i < elements().size(); i++) {
    ss << configs[i]->toString();
  }
  ss << L"]";

  if (hasSemanticContext) {
    ss << L",hasSemanticContext = " <<  hasSemanticContext;
  }
  if (uniqueAlt != ATN::INVALID_ALT_NUMBER) {
    ss << L",uniqueAlt = " << uniqueAlt;
  }

  if (conflictingAlts.size() > 0) {
    ss << L",conflictingAlts = ";
    ss << conflictingAlts.toString();
  }

  if (dipsIntoOuterContext) {
    ss << L", dipsIntoOuterContext";
  }
  return ss.str();
}

bool ATNConfigSet::remove(void * /*o*/) {
  throw UnsupportedOperationException();
}

void ATNConfigSet::InitializeInstanceFields() {
  configLookup = Ref<ConfigLookup>(new ConfigLookupImpl<SimpleATNConfigHasher, SimpleATNConfigComparer>());
  uniqueAlt = 0;
  hasSemanticContext = false;
  dipsIntoOuterContext = false;

  _readonly = false;
  _cachedHashCode = 0;
}
