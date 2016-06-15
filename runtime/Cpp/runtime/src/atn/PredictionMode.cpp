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

#include "atn/RuleStopState.h"
#include "atn/ATNConfigSet.h"
#include "atn/ATNConfig.h"
#include "misc/MurmurHash.h"
#include "SemanticContext.h"

#include "PredictionMode.h"

using namespace antlr4;
using namespace antlr4::atn;
using namespace antlrcpp;

struct AltAndContextConfigHasher
{
  /**
   * The hash code is only a function of the {@link ATNState#stateNumber}
   * and {@link ATNConfig#context}.
   */
  size_t operator () (ATNConfig *o) const {
    size_t hashCode = misc::MurmurHash::initialize(7);
    hashCode = misc::MurmurHash::update(hashCode, (size_t)o->state->stateNumber);
    hashCode = misc::MurmurHash::update(hashCode, o->context);
    return misc::MurmurHash::finish(hashCode, 2);
  }
};

struct AltAndContextConfigComparer {
  bool operator()(ATNConfig *a, ATNConfig *b) const
  {
    if (a == b) {
      return true;
    }
    return a->state->stateNumber == b->state->stateNumber && a->context == b->context;
  }
};

bool PredictionModeClass::hasSLLConflictTerminatingPrediction(PredictionMode mode, ATNConfigSet *configs) {
  /* Configs in rule stop states indicate reaching the end of the decision
   * rule (local context) or end of start rule (full context). If all
   * configs meet this condition, then none of the configurations is able
   * to match additional input so we terminate prediction.
   */
  if (allConfigsInRuleStopStates(configs)) {
    return true;
  }

  bool heuristic;

  // Pure SLL mode parsing or SLL+LL if:
  // Don't bother with combining configs from different semantic
  // contexts if we can fail over to full LL; costs more time
  // since we'll often fail over anyway.
  if (mode == PredictionMode::SLL || !configs->hasSemanticContext) {
    std::vector<antlrcpp::BitSet> altsets = getConflictingAltSubsets(configs);
    heuristic = hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
  } else {
    // dup configs, tossing out semantic predicates
    ATNConfigSet dup(true);
    for (auto &config : configs->configs) {
      Ref<ATNConfig> c = std::make_shared<ATNConfig>(config, SemanticContext::NONE);
      dup.add(c);
    }
    std::vector<antlrcpp::BitSet> altsets = getConflictingAltSubsets(&dup);
    heuristic = hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(&dup);
  }

  return heuristic;
}

bool PredictionModeClass::hasConfigInRuleStopState(ATNConfigSet *configs) {
  for (auto &c : configs->configs) {
    if (is<RuleStopState *>(c->state)) {
      return true;
    }
  }

  return false;
}

bool PredictionModeClass::allConfigsInRuleStopStates(ATNConfigSet *configs) {
  for (auto &config : configs->configs) {
    if (!is<RuleStopState*>(config->state)) {
      return false;
    }
  }

  return true;
}

int PredictionModeClass::resolvesToJustOneViableAlt(const std::vector<antlrcpp::BitSet>& altsets) {
  return getSingleViableAlt(altsets);
}

bool PredictionModeClass::allSubsetsConflict(const std::vector<antlrcpp::BitSet>& altsets) {
  return !hasNonConflictingAltSet(altsets);
}

bool PredictionModeClass::hasNonConflictingAltSet(const std::vector<antlrcpp::BitSet>& altsets) {
  for (antlrcpp::BitSet alts : altsets) {
    if (alts.count() == 1) {
      return true;
    }
  }
  return false;
}

bool PredictionModeClass::hasConflictingAltSet(const std::vector<antlrcpp::BitSet>& altsets) {
  for (antlrcpp::BitSet alts : altsets) {
    if (alts.count() > 1) {
      return true;
    }
  }
  return false;
}

bool PredictionModeClass::allSubsetsEqual(const std::vector<antlrcpp::BitSet>& altsets) {
  if (altsets.empty()) {
    return true;
  }

  const antlrcpp::BitSet& first = *altsets.begin();
  for (const antlrcpp::BitSet& alts : altsets) {
    if (alts != first) {
      return false;
    }
  }
  return true;
}

int PredictionModeClass::getUniqueAlt(const std::vector<antlrcpp::BitSet>& altsets) {
  antlrcpp::BitSet all = getAlts(altsets);
  if (all.count() == 1) {
    return all.nextSetBit(0);
  }
  return ATN::INVALID_ALT_NUMBER;
}

antlrcpp::BitSet PredictionModeClass::getAlts(const std::vector<antlrcpp::BitSet>& altsets) {
  antlrcpp::BitSet all;
  for (antlrcpp::BitSet alts : altsets) {
    all |= alts;
  }

  return all;
}

antlrcpp::BitSet PredictionModeClass::getAlts(ATNConfigSet *configs) {
  antlrcpp::BitSet alts;
  for (auto &config : configs->configs) {
    alts.set(config->alt);
  }
  return alts;
}

std::vector<antlrcpp::BitSet> PredictionModeClass::getConflictingAltSubsets(ATNConfigSet *configs) {
  std::unordered_map<ATNConfig *, antlrcpp::BitSet, AltAndContextConfigHasher, AltAndContextConfigComparer> configToAlts;
  for (auto &config : configs->configs) {
    configToAlts[config.get()].set(config->alt);
  }
  std::vector<antlrcpp::BitSet> values;
  for (auto it : configToAlts) {
    values.push_back(it.second);
  }
  return values;
}

std::map<ATNState*, antlrcpp::BitSet> PredictionModeClass::getStateToAltMap(ATNConfigSet *configs) {
  std::map<ATNState*, antlrcpp::BitSet> m;
  for (auto &c : configs->configs) {
    m[c->state].set((size_t)c->alt);
  }
  return m;
}

bool PredictionModeClass::hasStateAssociatedWithOneAlt(ATNConfigSet *configs) {
  std::map<ATNState*, antlrcpp::BitSet> x = getStateToAltMap(configs);
  for (std::map<ATNState*, antlrcpp::BitSet>::iterator it = x.begin(); it != x.end(); it++){
    if (it->second.count() == 1) return true;
  }
  return false;
}

int PredictionModeClass::getSingleViableAlt(const std::vector<antlrcpp::BitSet>& altsets) {
  antlrcpp::BitSet viableAlts;
  for (antlrcpp::BitSet alts : altsets) {
    int minAlt = alts.nextSetBit(0);

    viableAlts.set((size_t)minAlt);
    if (viableAlts.count() > 1)  // more than 1 viable alt
    {
      return ATN::INVALID_ALT_NUMBER;
    }
  }

  return viableAlts.nextSetBit(0);
}
