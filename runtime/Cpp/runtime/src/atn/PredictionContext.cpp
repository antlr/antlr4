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

#include "atn/EmptyPredictionContext.h"
#include "misc/MurmurHash.h"
#include "atn/ArrayPredictionContext.h"
#include "RuleContext.h"
#include "ParserRuleContext.h"
#include "atn/RuleTransition.h"
#include "support/Arrays.h"
#include "support/CPPUtils.h"

#include "atn/PredictionContext.h"

using namespace antlr4;
using namespace antlr4::misc;
using namespace antlr4::atn;

using namespace antlrcpp;

int PredictionContext::globalNodeCount = 0;
const Ref<PredictionContext> PredictionContext::EMPTY = std::make_shared<EmptyPredictionContext>();

PredictionContext::PredictionContext(size_t cachedHashCode) : id(globalNodeCount++), cachedHashCode(cachedHashCode)  {
}

PredictionContext::~PredictionContext() {
}

Ref<PredictionContext> PredictionContext::fromRuleContext(const ATN &atn, const Ref<RuleContext> &outerContext) {
  if (outerContext == nullptr) {
    return PredictionContext::EMPTY;
  }

  // if we are in RuleContext of start rule, s, then PredictionContext
  // is EMPTY. Nobody called us. (if we are empty, return empty)
  if (outerContext->parent.expired() || outerContext == ParserRuleContext::EMPTY) {
    return PredictionContext::EMPTY;
  }

  // If we have a parent, convert it to a PredictionContext graph
  Ref<PredictionContext> parent = PredictionContext::fromRuleContext(atn, outerContext->parent.lock());

  ATNState *state = atn.states.at((size_t)outerContext->invokingState);
  RuleTransition *transition = (RuleTransition *)state->transition(0);
  return SingletonPredictionContext::create(parent, transition->followState->stateNumber);
}

bool PredictionContext::operator != (const PredictionContext &o) const {
  return !(*this == o);
}

bool PredictionContext::isEmpty() const {
  return this == EMPTY.get();
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

size_t PredictionContext::calculateHashCode(std::weak_ptr<PredictionContext> parent, int returnState) {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);
  hash = MurmurHash::update(hash, parent.lock());
  hash = MurmurHash::update(hash, (size_t)returnState);
  hash = MurmurHash::finish(hash, 2);
  return hash;
}

size_t PredictionContext::calculateHashCode(const std::vector<std::weak_ptr<PredictionContext>> &parents,
                                            const std::vector<int> &returnStates) {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);

  for (auto parent : parents) {
    if (parent.expired())
      hash = MurmurHash::update(hash, 0);
    else
      hash = MurmurHash::update(hash, parent.lock());
  }

  for (auto returnState : returnStates) {
    hash = MurmurHash::update(hash, (size_t)returnState);
  }

  return MurmurHash::finish(hash, parents.size() + returnStates.size());
}

Ref<PredictionContext> PredictionContext::merge(const Ref<PredictionContext> &a,
  const Ref<PredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {
  
  assert(a && b);

  // share same graph if both same
  if (a == b) {
    return a;
  }

  if (is<SingletonPredictionContext>(a) && is<SingletonPredictionContext>(b)) {
    return mergeSingletons(std::dynamic_pointer_cast<SingletonPredictionContext>(a),
                           std::dynamic_pointer_cast<SingletonPredictionContext>(b), rootIsWildcard, mergeCache);
  }

  // At least one of a or b is array
  // If one is $ and rootIsWildcard, return $ as * wildcard
  if (rootIsWildcard) {
    if (is<EmptyPredictionContext>(a)) {
      return a;
    }
    if (is<EmptyPredictionContext>(b)) {
      return b;
    }
  }

  // convert singleton so both are arrays to normalize
  Ref<ArrayPredictionContext> left;
  if (is<SingletonPredictionContext>(a)) {
    left = std::make_shared<ArrayPredictionContext>(std::dynamic_pointer_cast<SingletonPredictionContext>(a));
  } else {
    left = std::dynamic_pointer_cast<ArrayPredictionContext>(a);
  }
  Ref<ArrayPredictionContext> right;
  if (is<SingletonPredictionContext>(b)) {
    right = std::make_shared<ArrayPredictionContext>(std::dynamic_pointer_cast<SingletonPredictionContext>(b));
  } else {
    right = std::dynamic_pointer_cast<ArrayPredictionContext>(b);
  }
  return mergeArrays(left, right, rootIsWildcard, mergeCache);
}

Ref<PredictionContext> PredictionContext::mergeSingletons(const Ref<SingletonPredictionContext> &a,
  const Ref<SingletonPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {

  if (mergeCache != nullptr) { // Can be null if not given to the ATNState from which this call originates.
    auto iterator = mergeCache->find({ a.get(), b.get() });
    if (iterator != mergeCache->end()) {
      return iterator->second;
    }
    iterator = mergeCache->find({ b.get(), a.get() });
    if (iterator != mergeCache->end()) {
      return iterator->second;
    }
  }

  Ref<PredictionContext> rootMerge = mergeRoot(a, b, rootIsWildcard);
  if (rootMerge) {
    if (mergeCache != nullptr) {
      (*mergeCache)[{ a.get(), b.get() }] = rootMerge;
    }
    return rootMerge;
  }

  Ref<PredictionContext> parentA = a->parent;//.lock();
  Ref<PredictionContext> parentB = b->parent;//.lock();
  if (a->returnState == b->returnState) { // a == b
    Ref<PredictionContext> parent = merge(parentA, parentB, rootIsWildcard, mergeCache);

    // If parent is same as existing a or b parent or reduced to a parent, return it.
    if (parent == parentA) { // ax + bx = ax, if a=b
      return a;
    }
    if (parent == parentB) { // ax + bx = bx, if a=b
      return b;
    }

    // else: ax + ay = a'[x,y]
    // merge parents x and y, giving array node with x,y then remainders
    // of those graphs.  dup a, a' points at merged array
    // new joined parent so create new singleton pointing to it, a'
    Ref<PredictionContext> a_ = SingletonPredictionContext::create(parent, a->returnState);
    if (mergeCache != nullptr) {
      (*mergeCache)[{ a.get(), b.get() }] = a_;
    }
    return a_;
  } else {
    // a != b payloads differ
    // see if we can collapse parents due to $+x parents if local ctx
    std::weak_ptr<PredictionContext> singleParent;
    if (a == b || (parentA && parentA == parentB)) { // ax + bx = [a,b]x
      singleParent = a->parent;
    }
    if (!singleParent.expired()) { // parents are same, sort payloads and use same parent
      std::vector<int> payloads = { a->returnState, b->returnState };
      if (a->returnState > b->returnState) {
        payloads[0] = b->returnState;
        payloads[1] = a->returnState;
      }
      std::vector<std::weak_ptr<PredictionContext>> parents = { singleParent, singleParent };
      Ref<PredictionContext> a_ = std::make_shared<ArrayPredictionContext>(parents, payloads);
      if (mergeCache != nullptr) {
        (*mergeCache)[{ a.get(), b.get() }] = a_;
      }
      return a_;
    }

    // parents differ and can't merge them. Just pack together
    // into array; can't merge.
    // ax + by = [ax,by]
    Ref<PredictionContext> a_;
    if (a->returnState > b->returnState) { // sort by payload
      std::vector<int> payloads = { b->returnState, a->returnState };
      std::vector<std::weak_ptr<PredictionContext>> parents = { b->parent, a->parent };
      a_ = std::make_shared<ArrayPredictionContext>(parents, payloads);
    } else {
      std::vector<int> payloads = {a->returnState, b->returnState};
      std::vector<std::weak_ptr<PredictionContext>> parents = { a->parent, b->parent };
      a_ = std::make_shared<ArrayPredictionContext>(parents, payloads);
    }

    if (mergeCache != nullptr) {
      (*mergeCache)[{ a.get(), b.get() }] = a_;
    }
    return a_;
  }
}

Ref<PredictionContext> PredictionContext::mergeRoot(const Ref<SingletonPredictionContext> &a,
  const Ref<SingletonPredictionContext> &b, bool rootIsWildcard) {
  if (rootIsWildcard) {
    if (a == EMPTY) { // * + b = *
      return EMPTY;
    }
    if (b == EMPTY) { // a + * = *
      return EMPTY;
    }
  } else {
    if (a == EMPTY && b == EMPTY) { // $ + $ = $
      return EMPTY;
    }
    if (a == EMPTY) { // $ + x = [$,x]
      std::vector<int> payloads = { b->returnState, EMPTY_RETURN_STATE };
      std::vector<std::weak_ptr<PredictionContext>> parents = { b->parent, EMPTY };
      Ref<PredictionContext> joined = std::make_shared<ArrayPredictionContext>(parents, payloads);
      return joined;
    }
    if (b == EMPTY) { // x + $ = [$,x] ($ is always first if present)
      std::vector<int> payloads = { a->returnState, EMPTY_RETURN_STATE };
      std::vector<std::weak_ptr<PredictionContext>> parents = { a->parent, EMPTY };
      Ref<PredictionContext> joined = std::make_shared<ArrayPredictionContext>(parents, payloads);
      return joined;
    }
  }
  return nullptr;
}

Ref<PredictionContext> PredictionContext::mergeArrays(const Ref<ArrayPredictionContext> &a,
  const Ref<ArrayPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {

  if (mergeCache != nullptr) {
    auto iterator = mergeCache->find({ a.get(), b.get() });
    if (iterator != mergeCache->end()) {
      return iterator->second;
    }
    iterator = mergeCache->find({ b.get(), a.get() });
    if (iterator != mergeCache->end()) {
      return iterator->second;
    }
  }

  // merge sorted payloads a + b => M
  size_t i = 0; // walks a
  size_t j = 0; // walks b
  size_t k = 0; // walks target M array

  std::vector<int> mergedReturnStates(a->returnStates.size() + b->returnStates.size());
  std::vector<std::weak_ptr<PredictionContext>> mergedParents(a->returnStates.size() + b->returnStates.size());

  // walk and merge to yield mergedParents, mergedReturnStates
  while (i < a->returnStates.size() && j < b->returnStates.size()) {
    Ref<PredictionContext> a_parent = a->parents[i];
    Ref<PredictionContext> b_parent = b->parents[j];
    if (a->returnStates[i] == b->returnStates[j]) {
      // same payload (stack tops are equal), must yield merged singleton
      int payload = a->returnStates[i];
      // $+$ = $
      bool both$ = payload == EMPTY_RETURN_STATE && a_parent && b_parent;
      bool ax_ax = (a_parent && b_parent) && a_parent == b_parent; // ax+ax -> ax
      if (both$ || ax_ax) {
        mergedParents[k] = a_parent; // choose left
        mergedReturnStates[k] = payload;
      }
      else { // ax+ay -> a'[x,y]
        Ref<PredictionContext> mergedParent = merge(a_parent, b_parent, rootIsWildcard, mergeCache);
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
  if (k < mergedParents.size()) { // write index < last position; trim
    if (k == 1) { // for just one merged element, return singleton top
      Ref<PredictionContext> a_ = SingletonPredictionContext::create(mergedParents[0].lock(), mergedReturnStates[0]);
      if (mergeCache != nullptr) {
        (*mergeCache)[{ a.get(), b.get() }] = a_;
      }
      return a_;
    }
    //mergedParents = Arrays::copyOf(mergedParents, k);
    mergedParents.resize(k);
    //mergedReturnStates = Arrays::copyOf(mergedReturnStates, k);
    mergedReturnStates.resize(k);
  }

  Ref<ArrayPredictionContext> M = std::make_shared<ArrayPredictionContext>(mergedParents, mergedReturnStates);

  // if we created same array as a or b, return that instead
  // TO_DO: track whether this is possible above during merge sort for speed
  if (M == a) {
    if (mergeCache != nullptr) {
      (*mergeCache)[{ a.get(), b.get() }] = a;
    }
    return a;
  }
  if (M == b) {
    if (mergeCache != nullptr) {
      (*mergeCache)[{ a.get(), b.get() }] = b;
    }
    return b;
  }

  // This part differs from Java code. We have to recreate the context as the parents array is copied on creation.
  if (combineCommonParents(mergedParents))
    M = std::make_shared<ArrayPredictionContext>(mergedParents, mergedReturnStates);

  if (mergeCache != nullptr) {
    (*mergeCache)[{ a.get(), b.get() }] = M;
  }
  return M;
}

bool PredictionContext::combineCommonParents(std::vector<std::weak_ptr<PredictionContext>> &parents) {
  std::unordered_set<Ref<PredictionContext>, PredictionContextHasher, PredictionContextComparer> uniqueParents;

  for (size_t p = 0; p < parents.size(); ++p) {
    Ref<PredictionContext> parent = parents[p].lock();
    if (uniqueParents.find(parent) == uniqueParents.end()) { // don't replace
      uniqueParents.insert(parent);
    }
  }

  if (uniqueParents.size() == parents.size())
    return false;

  // Don't resize the parents array, just update the content.
  for (size_t p = 0; p < parents.size(); ++p) {
    parents[p] = *uniqueParents.find(parents[p].lock());
  }
  return true;
}

std::string PredictionContext::toDOTString(const Ref<PredictionContext> &context) {
  if (context == nullptr) {
    return "";
  }

  std::stringstream ss;
  ss << "digraph G {\n" << "rankdir=LR;\n";

  std::vector<Ref<PredictionContext>> nodes = getAllContextNodes(context);
  std::sort(nodes.begin(), nodes.end(), [](const Ref<PredictionContext> &o1, const Ref<PredictionContext> &o2) {
    return o1->id - o2->id;
  });

  for (auto current : nodes) {
    if (is<SingletonPredictionContext>(current)) {
      std::string s = std::to_string(current->id);
      ss << "  s" << s;
      std::string returnState = std::to_string(current->getReturnState(0));
      if (is<EmptyPredictionContext>(current)) {
        returnState = "$";
      }
      ss << " [label=\"" << returnState << "\"];\n";
      continue;
    }
    Ref<ArrayPredictionContext> arr = std::static_pointer_cast<ArrayPredictionContext>(current);
    ss << "  s" << arr->id << " [shape=box, label=\"" << "[";
    bool first = true;
    for (auto inv : arr->returnStates) {
      if (!first) {
       ss << ", ";
      }
      if (inv == EMPTY_RETURN_STATE) {
        ss << "$";
      } else {
        ss << inv;
      }
      first = false;
    }
    ss << "]";
    ss << "\"];\n";
  }

  for (auto current : nodes) {
    if (current == EMPTY) {
      continue;
    }
    for (size_t i = 0; i < current->size(); i++) {
      if (current->getParent(i).expired()) {
        continue;
      }
      ss << "  s" << current->id << "->" << "s" << current->getParent(i).lock()->id;
      if (current->size() > 1) {
        ss << " [label=\"parent[" << i << "]\"];\n";
      } else {
        ss << ";\n";
      }
    }
  }

  ss << "}\n";
  return ss.str();
}

// The "visited" map is just a temporary structure to control the retrieval process (which is recursive).
Ref<PredictionContext> PredictionContext::getCachedContext(const Ref<PredictionContext> &context,
  PredictionContextCache &contextCache, std::map<Ref<PredictionContext>, Ref<PredictionContext>> &visited) {
  if (context->isEmpty()) {
    return context;
  }

  {
    auto iterator = visited.find(context);
    if (iterator != visited.end())
      return iterator->second; // Not necessarly the same as context.
  }

  auto iterator = contextCache.find(context);
  if (iterator != contextCache.end()) {
    visited[context] = *iterator;

    return *iterator;
  }

  bool changed = false;

  std::vector<std::weak_ptr<PredictionContext>> parents(context->size());
  for (size_t i = 0; i < parents.size(); i++) {
    std::weak_ptr<PredictionContext> parent = getCachedContext(context->getParent(i).lock(), contextCache, visited);
    if (changed || parent.lock() != context->getParent(i).lock()) {
      if (!changed) {
        parents.clear();
        for (size_t j = 0; j < context->size(); j++) {
          parents.push_back(context->getParent(j));
        }

        changed = true;
      }

      parents[i] = parent;
    }
  }

  if (!changed) {
    contextCache.insert(context);
    visited[context] = context;

    return context;
  }

  Ref<PredictionContext> updated;
  if (parents.empty()) {
    updated = EMPTY;
  } else if (parents.size() == 1) {
    updated = SingletonPredictionContext::create(parents[0], context->getReturnState(0));
  } else {
    updated = std::make_shared<ArrayPredictionContext>(parents, std::dynamic_pointer_cast<ArrayPredictionContext>(context)->returnStates);
  }

  contextCache.insert(updated);
  visited[updated] = updated;
  visited[context] = updated;

  return updated;
}

std::vector<Ref<PredictionContext>> PredictionContext::getAllContextNodes(const Ref<PredictionContext> &context) {
  std::vector<Ref<PredictionContext>> nodes;
  std::set<PredictionContext *> visited;
  getAllContextNodes_(context, nodes, visited);
  return nodes;
}


void PredictionContext::getAllContextNodes_(const Ref<PredictionContext> &context, std::vector<Ref<PredictionContext>> &nodes,
  std::set<PredictionContext *> &visited) {

  if (visited.find(context.get()) != visited.end()) {
    return; // Already done.
  }

  visited.insert(context.get());
  nodes.push_back(context);

  for (size_t i = 0; i < context->size(); i++) {
    getAllContextNodes_(context->getParent(i).lock(), nodes, visited);
  }
}

std::string PredictionContext::toString() const {
  
  return antlrcpp::toString(this);
}

std::string PredictionContext::toString(Recognizer * /*recog*/) const {
  return toString();
}

std::vector<std::string> PredictionContext::toStrings(Recognizer *recognizer, int currentState) {
  return toStrings(recognizer, EMPTY, currentState);
}

std::vector<std::string> PredictionContext::toStrings(Recognizer *recognizer, const Ref<PredictionContext> &stop, int currentState) {

  std::vector<std::string> result;

  for (size_t perm = 0; ; perm++) {
    size_t offset = 0;
    bool last = true;
    PredictionContext *p = this;
    int stateNumber = currentState;

    std::stringstream ss;
    ss << "[";
    bool outerContinue = false;
    while (!p->isEmpty() && p != stop.get()) {
      size_t index = 0;
      if (p->size() > 0) {
        size_t bits = 1;
        while ((1ULL << bits) < p->size()) {
          bits++;
        }

        size_t mask = (1 << bits) - 1;
        index = (perm >> offset) & mask;
        last &= index >= p->size() - 1;
        if (index >= p->size()) {
          outerContinue = true;
          break;
        }
        offset += bits;
      }

      if (recognizer != nullptr) {
        if (ss.tellp() > 1) {
          // first char is '[', if more than that this isn't the first rule
          ss << ' ';
        }

        const ATN &atn = recognizer->getATN();
        ATNState *s = atn.states[(size_t)stateNumber];
        std::string ruleName = recognizer->getRuleNames()[(size_t)s->ruleIndex];
        ss << ruleName;
      } else if (p->getReturnState(index) != EMPTY_RETURN_STATE) {
        if (!p->isEmpty()) {
          if (ss.tellp() > 1) {
            // first char is '[', if more than that this isn't the first rule
            ss << ' ';
          }

          ss << p->getReturnState(index);
        }
      }
      stateNumber = p->getReturnState(index);
      p = p->getParent(index).lock().get();
    }

    if (outerContinue)
      continue;

    ss << "]";
    result.push_back(ss.str());

    if (last) {
      break;
    }
  }

  return result;
}
