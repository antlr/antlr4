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

#include "EmptyPredictionContext.h"
#include "MurmurHash.h"
#include "ArrayPredictionContext.h"
#include "RuleContext.h"
#include "RuleTransition.h"
#include "stringconverter.h"
#include "PredictionContextCache.h"
#include "DoubleKeyMap.h"

#include "PredictionContext.h"

using namespace org::antlr::v4::runtime::misc;
using namespace org::antlr::v4::runtime::atn;

int PredictionContext::globalNodeCount = 0;
EmptyPredictionContext * PredictionContext::EMPTY;
const int PredictionContext::EMPTY_RETURN_STATE;
const int PredictionContext::INITIAL_HASH;

PredictionContext::PredictionContext(size_t cachedHashCode) : id(globalNodeCount++), cachedHashCode(cachedHashCode)  {
}

PredictionContext *PredictionContext::fromRuleContext(const ATN &atn, RuleContext *outerContext) {
  if (outerContext == nullptr) {
    outerContext = (RuleContext*)RuleContext::EMPTY;
  }

  // if we are in RuleContext of start rule, s, then PredictionContext
  // is EMPTY. Nobody called us. (if we are empty, return empty)
  if (outerContext->parent == nullptr || outerContext == (RuleContext*)RuleContext::EMPTY) {
    return PredictionContext::EMPTY;
  }

  // If we have a parent, convert it to a PredictionContext graph
  PredictionContext *parent = EMPTY;
  parent = PredictionContext::fromRuleContext(atn, outerContext->parent);

  ATNState *state = atn.states[(size_t)outerContext->invokingState];
  RuleTransition *transition = (RuleTransition *)state->transition(0);//static_cast<RuleTransition*>(state->transition(0));
  return SingletonPredictionContext::create(parent, transition->followState->stateNumber);
}

bool PredictionContext::isEmpty() const {
  return this == EMPTY;
}

bool PredictionContext::hasEmptyPath() const {
  return getReturnState(size() - 1) == EMPTY_RETURN_STATE;
}

size_t PredictionContext::hashCode() const {
  return cachedHashCode;
}

size_t PredictionContext::calculateEmptyHashCode() {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);
  hash = MurmurHash::finish(hash, 0);
  return hash;
}

size_t PredictionContext::calculateHashCode(PredictionContext *parent, int returnState) {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);
  hash = MurmurHash::update(hash, (size_t)parent);
  hash = MurmurHash::update(hash, (size_t)returnState);
  hash = MurmurHash::finish(hash, 2);
  return hash;
}

size_t PredictionContext::calculateHashCode(const std::vector<PredictionContext*> &parents, const std::vector<int> &returnStates) {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);

  for (auto parent : parents) {
    hash = MurmurHash::update(hash, (size_t)parent);
  }

  for (auto returnState : returnStates) {
    hash = MurmurHash::update(hash, (size_t)returnState);
  }

  return MurmurHash::finish(hash, parents.size() + returnStates.size());
}

PredictionContext *PredictionContext::merge(PredictionContext *a, PredictionContext *b,
                                                 bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache) {
  if (a != nullptr && b != nullptr) {
    throw new ASSERTException(L"PredictionContext::merge",
                              L"a != nullptr && b != nullptr");
  };

  // share same graph if both same
  if (a == b) {
    return a;
  }

  if (dynamic_cast<SingletonPredictionContext*>(a) != nullptr && dynamic_cast<SingletonPredictionContext*>(b) != nullptr) {
    return mergeSingletons(static_cast<SingletonPredictionContext*>(a), static_cast<SingletonPredictionContext*>(b), rootIsWildcard, mergeCache);
  }

  // At least one of a or b is array
  // If one is $ and rootIsWildcard, return $ as * wildcard
  if (rootIsWildcard) {
    if (dynamic_cast<EmptyPredictionContext*>(a) != nullptr) {
      return a;
    }
    if (dynamic_cast<EmptyPredictionContext*>(b) != nullptr) {
      return b;
    }
  }

  // convert singleton so both are arrays to normalize
  if (dynamic_cast<SingletonPredictionContext*>(a) != nullptr) {
    a = (PredictionContext *)new ArrayPredictionContext(static_cast<SingletonPredictionContext*>(a));
  }
  if (dynamic_cast<SingletonPredictionContext*>(b) != nullptr) {
    b = (PredictionContext *)new ArrayPredictionContext(static_cast<SingletonPredictionContext*>(b));
  }
  return mergeArrays(static_cast<ArrayPredictionContext*>(a), static_cast<ArrayPredictionContext*>(b), rootIsWildcard, mergeCache);
}

PredictionContext *PredictionContext::mergeSingletons(SingletonPredictionContext *a, SingletonPredictionContext *b, bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache) {
  if (mergeCache != nullptr) {
    PredictionContext *previous = mergeCache->get(a,b);
    if (previous != nullptr) {
      return previous;
    }
    previous = mergeCache->get(b,a);
    if (previous != nullptr) {
      return previous;
    }
  }

  PredictionContext *rootMerge = mergeRoot(a, b, rootIsWildcard);
  if (rootMerge != nullptr) {
    if (mergeCache != nullptr) {
      mergeCache->put((PredictionContext *)a, (PredictionContext *)b, rootMerge);
    }
    return rootMerge;
  }

  if (a->returnState == b->returnState) { // a == b
    PredictionContext *parent = merge(a->parent, b->parent, rootIsWildcard, mergeCache);
    // if parent is same as existing a or b parent or reduced to a parent, return it
    if (parent == a->parent) { // ax + bx = ax, if a=b
      return (PredictionContext *)a;
    }
    if (parent == b->parent) { // ax + bx = bx, if a=b
      return (PredictionContext *)b;
    }
    // else: ax + ay = a'[x,y]
    // merge parents x and y, giving array node with x,y then remainders
    // of those graphs.  dup a, a' points at merged array
    // new joined parent so create new singleton pointing to it, a'
    PredictionContext *a_ = (PredictionContext *)SingletonPredictionContext::create(parent, a->returnState);
    if (mergeCache != nullptr) {
      mergeCache->put((PredictionContext *)a, (PredictionContext *)b, a_);
    }
    return a_;
  }
  else { // a != b payloads differ
         // see if we can collapse parents due to $+x parents if local ctx
    PredictionContext *singleParent = nullptr;
    if (a == b || (a->parent != nullptr && a->parent == b->parent)) { // ax + bx = [a,b]x
      singleParent = a->parent;
    }
    if (singleParent != nullptr) { // parents are same
                                   // sort payloads and use same parent
      std::vector<int> payloads = { a->returnState, b->returnState };
      if (a->returnState > b->returnState) {
        payloads[0] = b->returnState;
        payloads[1] = a->returnState;
      }
      std::vector<PredictionContext *> parents = { singleParent, singleParent };
      PredictionContext *a_ = new ArrayPredictionContext(parents, payloads);
      if (mergeCache != nullptr) {
        mergeCache->put(a, b, a_);
      }
      return a_;
    }
    // parents differ and can't merge them. Just pack together
    // into array; can't merge.
    // ax + by = [ax,by]
    PredictionContext *a_;
    if (a->returnState > b->returnState) { // sort by payload
      std::vector<int> payloads = { b->returnState, a->returnState };
      std::vector<PredictionContext *> parents = { b->parent, a->parent };
      a_ = new ArrayPredictionContext(parents, payloads);
    } else {
      std::vector<int> payloads = {a->returnState, b->returnState};
      std::vector<PredictionContext *> parents = { a->parent, b->parent };
      a_ = new ArrayPredictionContext(parents, payloads);
    }

    if (mergeCache != nullptr) {
      mergeCache->put(a, b, a_);
    }
    return a_;
  }
}

PredictionContext *PredictionContext::mergeRoot(SingletonPredictionContext *a, SingletonPredictionContext *b, bool rootIsWildcard) {
  if (rootIsWildcard) {
    if (a == EMPTY) { // * + b = *
      return (PredictionContext *)EMPTY;
    }
    if (b == EMPTY) { // a + * = *
      return (PredictionContext *)EMPTY;
    }
  } else {
    if (a == EMPTY && b == EMPTY) { // $ + $ = $
      return (PredictionContext *)EMPTY;
    }
    if (a == EMPTY) { // $ + x = [$,x]
      std::vector<int> payloads = { b->returnState, EMPTY_RETURN_STATE };
      std::vector<PredictionContext *> parents = { b->parent, EMPTY };
      PredictionContext *joined = new ArrayPredictionContext(parents, payloads);
      return joined;
    }
    if (b == EMPTY) { // x + $ = [$,x] ($ is always first if present)
      std::vector<int> payloads = { a->returnState, EMPTY_RETURN_STATE };
      std::vector<PredictionContext *> parents = { a->parent, EMPTY };
      PredictionContext *joined = new ArrayPredictionContext(parents, payloads);
      return joined;
    }
  }
  return nullptr;
}

PredictionContext *PredictionContext::mergeArrays(ArrayPredictionContext *a, ArrayPredictionContext *b, bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache) {
  if (mergeCache != nullptr) {
    PredictionContext *previous = mergeCache->get(a,b);
    if (previous != nullptr) {
      return previous;
    }
    previous = mergeCache->get(b,a);
    if (previous != nullptr) {
      return previous;
    }
  }

  // merge sorted payloads a + b => M
  std::vector<int>::size_type i = 0; // walks a
  std::vector<int>::size_type j = 0; // walks b
  std::vector<int>::size_type k = 0; // walks target M array

  std::vector<int> mergedReturnStates;
  std::vector<PredictionContext*> mergedParents;

  // walk and merge to yield mergedParents, mergedReturnStates
  while (i < a->returnStates.size() && j < b->returnStates.size()) {
    PredictionContext *a_parent = a->parents[i];
    PredictionContext *b_parent = b->parents[j];
    if (a->returnStates[i] == b->returnStates[j]) {
      // same payload (stack tops are equal), must yield merged singleton
      int payload = a->returnStates[i];
      // $+$ = $
      bool both$ = payload == EMPTY_RETURN_STATE && a_parent == nullptr && b_parent == nullptr;
      bool ax_ax = (a_parent != nullptr && b_parent != nullptr) && a_parent == b_parent; // ax+ax -> ax
      if (both$ || ax_ax) {
        mergedParents[k] = a_parent; // choose left
        mergedReturnStates[k] = payload;
      }
      else { // ax+ay -> a'[x,y]
        PredictionContext *mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache);
        mergedParents[k] = mergedParent;
        mergedReturnStates[k] = payload;
      }
      i++; // hop over left one as usual
      j++; // but also skip one in right side since we merge
    } else if (a->returnStates[i] < b->returnStates[j]) { // copy a[i] to M
      mergedParents[k] = a_parent;
      mergedReturnStates[k] = a->returnStates[i];
      i++;
    }
    else { // b > a, copy b[j] to M
      mergedParents[k] = b_parent;
      mergedReturnStates[k] = b->returnStates[j];
      j++;
    }
    k++;
  }

  // copy over any payloads remaining in either array
  if (i < a->returnStates.size()) {
    for (std::vector<int>::size_type p = i; p < a->returnStates.size(); p++) {
      mergedParents[k] = a->parents[p];
      mergedReturnStates[k] = a->returnStates[p];
      k++;
    }
  } else {
    for (std::vector<int>::size_type p = j; p < b->returnStates.size(); p++) {
      mergedParents[k] = b->parents[p];
      mergedReturnStates[k] = b->returnStates[p];
      k++;
    }
  }

  // trim merged if we combined a few that had same stack tops
  if (k < sizeof(mergedParents) / sizeof(mergedParents[0])) { // write index < last position; trim
    if (k == 1) { // for just one merged element, return singleton top
      PredictionContext *a_ = SingletonPredictionContext::create(mergedParents[0], mergedReturnStates[0]);
      if (mergeCache != nullptr) {
        mergeCache->put(a,b,a_);
      }
      return a_;
    }
    // TODO: mergedParents = Arrays::copyOf(mergedParents, k);
    // TODO: mergedReturnStates = Arrays::copyOf(mergedReturnStates, k);
  }

  PredictionContext *M = nullptr;
  // TODO: PredictionContext *M = new ArrayPredictionContext(mergedParents, mergedReturnStates);

  // if we created same array as a or b, return that instead
  // TODO: track whether this is possible above during merge sort for speed
  if (M == a) {
    if (mergeCache != nullptr) {
      mergeCache->put(a,b,a);
    }
    return a;
  }
  if (M == b) {
    if (mergeCache != nullptr) {
      mergeCache->put(a,b,b);
    }
    return b;
  }

  combineCommonParents(mergedParents);

  if (mergeCache != nullptr) {
    mergeCache->put(a,b,M);
  }
  return M;
}

void PredictionContext::combineCommonParents(std::vector<PredictionContext*> parents) {
  std::unordered_map<PredictionContext*, PredictionContext*> uniqueParents = std::unordered_map<PredictionContext*, PredictionContext*>();

  for (size_t p = 0; p < parents.size(); p++) {
    PredictionContext *parent = parents[p];
    if (uniqueParents.find(parent) == uniqueParents.end()) { // don't replace
      uniqueParents[parent] = parent;
    }
  }

  for (size_t p = 0; p < parents.size(); p++) {
    parents[p] = uniqueParents.at(parents[p]);
  }
}

std::wstring PredictionContext::toDOTString(PredictionContext *context) {
  if (context == nullptr) {
    return L"";
  }
  antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
  buf->append(L"digraph G {\n");
  buf->append(L"rankdir=LR;\n");

  std::vector<PredictionContext*> nodes = getAllContextNodes(context);

  std::sort(nodes.begin(), nodes.end(), [](PredictionContext *o1, PredictionContext *o2) {
    return o1->id - o2->id;
  });

  for (auto current : nodes) {
    if (dynamic_cast<SingletonPredictionContext*>(current) != nullptr) {
      std::wstring s = antlrcpp::StringConverterHelper::toString(current->id);
      buf->append(L"  s").append(s);
      std::wstring returnState = antlrcpp::StringConverterHelper::toString(current->getReturnState(0));
      if (dynamic_cast<EmptyPredictionContext*>(current) != nullptr) {
        returnState = L"$";
      }
      buf->append(L" [label=\"").append(returnState).append(L"\"];\n");
      continue;
    }
    ArrayPredictionContext *arr = static_cast<ArrayPredictionContext*>(current);
    buf->append(L"  s").append(arr->id);
    buf->append(L" [shape=box, label=\"");
    buf->append(L"[");
    bool first = true;
    for (auto inv : arr->returnStates) {
      if (!first) {
        buf->append(L", ");
      }
      if (inv == EMPTY_RETURN_STATE) {
        buf->append(L"$");
      } else {
        buf->append(inv);
      }
      first = false;
    }
    buf->append(L"]");
    buf->append(L"\"];\n");
  }

  for (auto current : nodes) {
    if (current == EMPTY) {
      continue;
    }
    for (size_t i = 0; i < current->size(); i++) {
      if (current->getParent(i) == nullptr) {
        continue;
      }
      std::wstring s = antlrcpp::StringConverterHelper::toString(current->id);
      buf->append(L"  s").append(s);
      buf->append(L"->");
      buf->append(L"s");
      buf->append(current->getParent(i)->id);
      if (current->size() > 1) {
        buf->append(std::wstring(L" [label=\"parent[") + antlrcpp::StringConverterHelper::toString(i) + std::wstring(L"]\"];\n"));
      } else {
        buf->append(L";\n");
      }
    }
  }

  buf->append(L"}\n");
  return buf->toString();
}

PredictionContext *PredictionContext::getCachedContext(PredictionContext *context, PredictionContextCache *contextCache,
                                                            std::map<PredictionContext*, PredictionContext*> *visited) {
  if (context->isEmpty()) {
    return context;
  }

  PredictionContext *existing = (*visited)[context];
  if (existing != nullptr) {
    return existing;
  }

  existing = contextCache->get(context);
  if (existing != nullptr) {
    std::pair<PredictionContext*, PredictionContext*> thePair(context, existing);
    visited->insert(thePair);

    return existing;
  }

  bool changed = false;

  std::vector<PredictionContext*> parents;

  for (size_t i = 0; i < sizeof(parents) / sizeof(parents[0]); i++) {
    PredictionContext *parent = getCachedContext(context->getParent(i), contextCache, visited);
    if (changed || parent != context->getParent(i)) {
      if (!changed) {
        parents = std::vector<PredictionContext*>();
        for (size_t j = 0; j < context->size(); j++) {
          parents[j] = context->getParent(j);
        }

        changed = true;
      }

      parents[i] = parent;
    }
  }

  if (!changed) {
    contextCache->add(context);
    std::pair<PredictionContext*, PredictionContext*> thePair(context,context);
    visited->insert(thePair);

    return context;
  }

  PredictionContext *updated;
  if (sizeof(parents) / sizeof(parents[0]) == 0) {
    updated = EMPTY;
  } else if (sizeof(parents) / sizeof(parents[0]) == 1) {
    updated = SingletonPredictionContext::create(parents[0], context->getReturnState(0));
  } else {
    ArrayPredictionContext *arrayPredictionContext = static_cast<ArrayPredictionContext*>(context);
    updated = new ArrayPredictionContext(parents,
                                         arrayPredictionContext->returnStates);
  }

  contextCache->add(updated);

  std::pair<PredictionContext*, PredictionContext*> thePair(updated, updated);
  visited->insert(thePair);

  std::pair<PredictionContext*, PredictionContext*> otherPair(context, updated);
  visited->insert(otherPair);

  return updated;
}

std::vector<PredictionContext*> PredictionContext::getAllContextNodes(PredictionContext *context) {
  std::vector<PredictionContext*> nodes = std::vector<PredictionContext*>();
  std::map<PredictionContext*, PredictionContext*> *visited = new std::map<PredictionContext*, PredictionContext*>();
  getAllContextNodes_(context, nodes, visited);
  return nodes;
}


void PredictionContext::getAllContextNodes_(PredictionContext *context, std::vector<PredictionContext*> &nodes, std::map<PredictionContext*, PredictionContext*> *visited) {

  if (context == nullptr || visited->at(context)) {
    return;
  }

  std::pair<PredictionContext*, PredictionContext*> thePair(context, context);
  visited->insert(thePair);

  nodes.push_back(context);

  for (size_t i = 0; i < context->size(); i++) {
    getAllContextNodes_(context->getParent(i), nodes, visited);
  }
}

std::wstring PredictionContext::toString() {
  //TODO: what should this return?  (Return empty string
  // for now.)
  return L"TODO PredictionContext::toString()";
}
