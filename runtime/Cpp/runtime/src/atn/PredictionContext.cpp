/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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

size_t PredictionContext::globalNodeCount = 0;
const Ref<PredictionContext> PredictionContext::EMPTY = std::make_shared<EmptyPredictionContext>();

//----------------- PredictionContext ----------------------------------------------------------------------------------

PredictionContext::PredictionContext(size_t cachedHashCode) : id(globalNodeCount++), cachedHashCode(cachedHashCode)  {
}

PredictionContext::~PredictionContext() {
}

Ref<PredictionContext> PredictionContext::fromRuleContext(const ATN &atn, RuleContext *outerContext) {
  if (outerContext == nullptr) {
    return PredictionContext::EMPTY;
  }

  // if we are in RuleContext of start rule, s, then PredictionContext
  // is EMPTY. Nobody called us. (if we are empty, return empty)
  if (outerContext->parent == nullptr || outerContext == &ParserRuleContext::EMPTY) {
    return PredictionContext::EMPTY;
  }

  // If we have a parent, convert it to a PredictionContext graph
  Ref<PredictionContext> parent = PredictionContext::fromRuleContext(atn, dynamic_cast<RuleContext *>(outerContext->parent));

  ATNState *state = atn.states.at(outerContext->invokingState);
  RuleTransition *transition = (RuleTransition *)state->transitions[0];
  return SingletonPredictionContext::create(parent, transition->followState->stateNumber);
}

bool PredictionContext::isEmptyContext() const {
  return this == EMPTY.get();
}

bool PredictionContext::hasEmptyPath() const {
  // since EMPTY_RETURN_STATE can only appear in the last position, we check last one
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

size_t PredictionContext::calculateHashCode(Ref<PredictionContext> parent, size_t returnState) {
  size_t hash = MurmurHash::initialize(INITIAL_HASH);
  hash = MurmurHash::update(hash, parent);
  hash = MurmurHash::update(hash, returnState);
  hash = MurmurHash::finish(hash, 2);
  return hash;
}

size_t PredictionContext::calculateHashCode(const std::vector<PredictionContextItem> &contexts) {
    size_t hash = MurmurHash::initialize(INITIAL_HASH);

    for (const PredictionContextItem& context : contexts) {
        hash = MurmurHash::update(hash, context.parent);
    }

    for (const PredictionContextItem& context : contexts) {
        hash = MurmurHash::update(hash, context.returnState);
    }

    return MurmurHash::finish(hash, 2 * contexts.size());
}

Ref<PredictionContext> PredictionContext::merge(const Ref<PredictionContext> &a,
  const Ref<PredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {
  assert(a && b);

  // share same graph if both same
  if (a == b || *a == *b) {
    return a;
  }

  if (a->isSingletonContext() && b->isSingletonContext()) {
    return mergeSingletons(std::static_pointer_cast<SingletonPredictionContext>(a),
                           std::static_pointer_cast<SingletonPredictionContext>(b), rootIsWildcard, mergeCache);
  }

  // At least one of a or b is array.
  // If one is $ and rootIsWildcard, return $ as * wildcard.
  if (rootIsWildcard) {
    if (a->isEmptyContext()) {
      return a;
    }
    if (b->isEmptyContext()) {
      return b;
    }
  }

  // This is slightly different from Java, use a different merge function for singleton into
  // array and array merge. In Java a temporary ArrayPredictionContext is created containing
  // only one item and then the same merge function is used.
  if (a->isSingletonContext()) {
      return mergeSingletonIntoArray(std::static_pointer_cast<SingletonPredictionContext>(a),
                                     std::static_pointer_cast<ArrayPredictionContext>(b),
                                     rootIsWildcard,
                                     mergeCache);
  }
  if (b->isSingletonContext()) {
      return mergeSingletonIntoArray(std::static_pointer_cast<SingletonPredictionContext>(b),
                                     std::static_pointer_cast<ArrayPredictionContext>(a),
                                     rootIsWildcard,
                                     mergeCache);
  }
  return mergeArrays(std::static_pointer_cast<ArrayPredictionContext>(a),
                     std::static_pointer_cast<ArrayPredictionContext>(b),
                     rootIsWildcard,
                     mergeCache);
}

Ref<PredictionContext> PredictionContext::mergeSingletons(const Ref<SingletonPredictionContext> &a,
  const Ref<SingletonPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {

  if (mergeCache != nullptr) { // Can be null if not given to the ATNState from which this call originates.
    // This is different to the Java runtime. Merge cache lookups are symmetric.
    if (const Ref<PredictionContext>& existing = mergeCache->get(a, b)) {
      return existing;
    }
  }

  Ref<PredictionContext> rootMerge = mergeRoot(a, b, rootIsWildcard);
  if (rootMerge) {
    if (mergeCache != nullptr) {
      mergeCache->put(a, b, rootMerge);
    }
    return rootMerge;
  }

  if (a->returnState == b->returnState) { // a == b
    Ref<PredictionContext> parent = merge(a->parent, b->parent, rootIsWildcard, mergeCache);

    // If parent is same object as existing a or b parent or reduced to a parent, return it.
    if (parent == a->parent) { // ax + bx = ax, if a=b
      return a;
    }
    if (parent == b->parent) { // ax + bx = bx, if a=b
      return b;
    }

    // else: ax + ay = a'[x,y]
    // merge parents x and y, giving array node with x,y then remainders
    // of those graphs.  dup a, a' points at merged array
    // new joined parent so create new singleton pointing to it, a'
    Ref<PredictionContext> a_ = SingletonPredictionContext::create(parent, a->returnState);
    if (mergeCache != nullptr) {
      mergeCache->put(a, b, a_);
    }
    return a_;
  } else {
    // a != b payloads differ
    // see if we can collapse parents due to $+x parents if local ctx
    const Ref<PredictionContext>& parentA = a->parent;
    Ref<PredictionContext> parentB = b->parent;
    if (parentA == parentB || *parentA == *parentB) {
        parentB = parentA; // modified the following so that ax + bx = [a,b]x
    }
    // ax + by = [ax,by] (or see above)
    Ref<PredictionContext> a_;
    if (a->returnState > b->returnState) {
      std::vector<PredictionContextItem> contexts = { PredictionContextItem(parentB, b->returnState), PredictionContextItem(parentA, a->returnState) };
      a_ = std::make_shared<ArrayPredictionContext>(contexts);
    } else {
      std::vector<PredictionContextItem> contexts = { PredictionContextItem(parentA, a->returnState), PredictionContextItem(parentB, b->returnState) };
      a_ = std::make_shared<ArrayPredictionContext>(contexts);
    }
    if (mergeCache != nullptr) {
      mergeCache->put(a, b, a_);
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
      std::vector<PredictionContextItem> contexts = {PredictionContextItem(b->parent, b->returnState), PredictionContextItem(nullptr, EMPTY_RETURN_STATE)};
      Ref<PredictionContext> joined = std::make_shared<ArrayPredictionContext>(contexts);
      return joined;
    }
    if (b == EMPTY) { // x + $ = [$,x] ($ is always first if present)
      std::vector<PredictionContextItem> contexts = { PredictionContextItem(a->parent, a->returnState), PredictionContextItem(nullptr, EMPTY_RETURN_STATE)};
      Ref<PredictionContext> joined = std::make_shared<ArrayPredictionContext>(contexts);
      return joined;
    }
  }
  return nullptr;
}

Ref<PredictionContext> PredictionContext::mergeArrays(const Ref<ArrayPredictionContext> &a,
  const Ref<ArrayPredictionContext> &b, bool rootIsWildcard, PredictionContextMergeCache *mergeCache) {

  if (mergeCache != nullptr) {
    // This is different to the Java runtime. Merge cache lookups are symmetric.
    if (const Ref<PredictionContext>& existing = mergeCache->get(a, b)) {
      return existing;
    }
  }

  // merge sorted payloads a + b => M
  std::vector<PredictionContextItem> contexts;
  contexts.reserve(a->contexts.size() + b->contexts.size());
  std::vector<PredictionContextItem>::const_iterator i = a->contexts.begin(), // walks a
                                                     iEnd = a->contexts.end();
  std::vector<PredictionContextItem>::const_iterator j = b->contexts.begin(), // walks b
                                                     jEnd = b->contexts.end();
  while (i != iEnd && j != jEnd) {
    if (i->returnState == j->returnState) {
      // same payload (stack tops are equal), must yield merged singleton
      size_t payload = i->returnState;
      // $+$ = $
      if (i->parent && j->parent && (payload == EMPTY_RETURN_STATE || *i->parent == *j->parent)) {
        contexts.emplace_back(i->parent, i->returnState);
      } else { // ax+ay -> a'[x,y]
        contexts.emplace_back(merge(i->parent, j->parent, rootIsWildcard, mergeCache), i->returnState);
      }
      ++i;
      ++j;
    } else if (i->returnState < j->returnState) { // copy a[i] to M
      contexts.emplace_back(i->parent, i->returnState);
      ++i;
    } else { // b > a, copy b[j] to M
      contexts.emplace_back(j->parent, j->returnState);
      ++j;
    }
  }
  // copy over any payloads remaining in either array
  if (i != iEnd) {
    do {
      contexts.emplace_back(i->parent, i->returnState);
      ++i;
    } while (i != iEnd);
  } else {
    while (j != jEnd) {
      contexts.emplace_back(j->parent, j->returnState);
      ++j;
    }
  }

  // if we created same array as the contents of a or b, return a or b instead
  // TO_DO: track whether this is possible above during merge sort for speed
  if (antlrcpp::Arrays::equals(contexts, a->contexts)) {
    if (mergeCache != nullptr) {
      mergeCache->put(a, b, a);
    }
    return a;
  }
  if (antlrcpp::Arrays::equals(contexts, b->contexts)) {
    if (mergeCache != nullptr) {
      mergeCache->put(a, b, b);
    }
    return b;
  }

  // This is different to the Java runtime. Construction of the returned object has been deferred
  // until we know it will be the return value.
  Ref<ArrayPredictionContext> M = std::make_shared<ArrayPredictionContext>(std::move(contexts));
  if (mergeCache != nullptr) {
    mergeCache->put(a, b, M);
  }
  return M;
}

Ref<PredictionContext> PredictionContext::mergeSingletonIntoArray(const Ref<SingletonPredictionContext> &a,
                                                                  const Ref<ArrayPredictionContext> &b,
                                                                  bool rootIsWildcard,
                                                                  PredictionContextMergeCache *mergeCache) {

  if (mergeCache != nullptr) {
    // This is different to the Java runtime. Merge cache lookups are symmetric.
    if (const Ref<PredictionContext>& existing = mergeCache->get(a, b)) {
      return existing;
    }
  }

  // merge sorted payloads a + b => M
  std::vector<PredictionContextItem> contexts(b->contexts.begin(), b->contexts.end());
  std::vector<PredictionContextItem>::iterator jEnd = contexts.end(),
    j = std::lower_bound(contexts.begin(), jEnd, PredictionContextItem(a->parent, a->returnState),
      [] (const PredictionContextItem& lhs, const PredictionContextItem& rhs) {
        return lhs.returnState < rhs.returnState;
      });
  if (j != jEnd && j->returnState == a->returnState) {
    // same payload (stack tops are equal), must yield merged singleton
    size_t payload = j->returnState;
    // $+$ = $
    if (a->parent && j->parent && (payload == EMPTY_RETURN_STATE || *j->parent == *a->parent)) {
      // a was already a member of b so a+b = b
      return b;
    } else { // ax+ay -> a'[x,y]
      j->parent = merge(a->parent, j->parent, rootIsWildcard, mergeCache);
    }
  } else { // insert a into M in order.
    contexts.emplace(j, PredictionContextItem(a->parent, a->returnState));
  }

  // Compared to mergeArrays the test for a merge constructing an input of a or b is impossible.
  // If that happened it happened at return b; above.

  // This is different to the Java runtime. Construction of the returned object has been deferred
  // until we know it will be the return value.
  Ref<ArrayPredictionContext> M = std::make_shared<ArrayPredictionContext>(std::move(contexts));
  if (mergeCache != nullptr) {
    mergeCache->put(a, b, M);
  }
  return M;
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
    for (const auto& ctx : arr->contexts) {
      if (!first) {
       ss << ", ";
      }
      if (ctx.returnState == EMPTY_RETURN_STATE) {
        ss << "$";
      } else {
        ss << ctx.returnState;
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
      if (!current->getParent(i)) {
        continue;
      }
      ss << "  s" << current->id << "->" << "s" << current->getParent(i)->id;
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
  if (context->isEmptyContext()) {
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

  std::vector<Ref<PredictionContext>> parents(context->size());
  for (size_t i = 0; i < parents.size(); i++) {
    Ref<PredictionContext> parent = getCachedContext(context->getParent(i), contextCache, visited);
    if (changed || parent != context->getParent(i)) {
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
    contextCache.insert(updated);
  } else {
    std::vector<PredictionContextItem> contexts;
    for (size_t i = 0; i < parents.size(); ++i) {
        contexts.push_back(PredictionContextItem(parents[i], std::static_pointer_cast<ArrayPredictionContext>(context)->contexts[i].returnState));
    }
    updated = std::make_shared<ArrayPredictionContext>(contexts);
    contextCache.insert(updated);
  }

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
    getAllContextNodes_(context->getParent(i), nodes, visited);
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
    size_t stateNumber = currentState;

    std::stringstream ss;
    ss << "[";
    bool outerContinue = false;
    while (!p->isEmptyContext() && p != stop.get()) {
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
        ATNState *s = atn.states[stateNumber];
        std::string ruleName = recognizer->getRuleNames()[s->ruleIndex];
        ss << ruleName;
      } else if (p->getReturnState(index) != EMPTY_RETURN_STATE) {
        if (!p->isEmptyContext()) {
          if (ss.tellp() > 1) {
            // first char is '[', if more than that this isn't the first rule
            ss << ' ';
          }

          ss << p->getReturnState(index);
        }
      }
      stateNumber = p->getReturnState(index);
      p = p->getParent(index).get();
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

//----------------- PredictionContextMergeCache ------------------------------------------------------------------------

bool PredictionContextPair::operator == (const PredictionContextPair& o) const {
    return (*lhs == *o.lhs && *rhs == *o.rhs)
        || (*lhs == *o.rhs && *rhs == *o.lhs);
}

void PredictionContextMergeCache::put(Ref<PredictionContext> const& key1,
                                      Ref<PredictionContext> const& key2,
                                      Ref<PredictionContext> const& value) {
  PredictionContextPair entry({ key1, key2 });
  _data.insert(std::make_pair(entry, value));
}

const Ref<PredictionContext>& PredictionContextMergeCache::get(Ref<PredictionContext> const& key1,
                                                               Ref<PredictionContext> const& key2) const {
  PredictionContextPair entry = { key1, key2 };
  auto i =  _data.find(entry);
  if (i == _data.end()) {
      return _missing;
  }
  return i->second;
}

void PredictionContextMergeCache::clear() {
  _data.clear();
}

std::string PredictionContextMergeCache::toString() const {
  std::string result;
  for (const auto& pair : _data)
    result += pair.second->toString() + "\n";
  return result;
}

size_t PredictionContextMergeCache::count() const {
  return _data.size();
}

