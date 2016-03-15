#include <functional>

#include "ATN.h"
#include "ATNState.h"
#include "ATNConfig.h"
#include "ATNConfigSet.h"
#include "SemanticContext.h"
#include "PredictionContext.h"
#include "StringBuilder.h"

/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {

                    atn::ATNConfig *ATNConfigSet::AbstractConfigHashSet::asElementType(void *o) {
                        if (!(static_cast<ATNConfig*>(o) != nullptr)) {
                            return nullptr;
                        }

                        return static_cast<ATNConfig*>(o);
                    }

                    std::vector<std::vector<ATNConfig*>> *
                    ATNConfigSet::AbstractConfigHashSet::createBuckets(int capacity) {
                        return new std::vector<std::vector<ATNConfig*>>();
                    }

                    std::vector<ATNConfig*> *ATNConfigSet::AbstractConfigHashSet::createBucket(int capacity) {
                        return new std::vector<ATNConfig*>();
                    }

                    ATNConfigSet::ConfigHashSet::ConfigHashSet() : AbstractConfigHashSet(ConfigEqualityComparator::INSTANCE) {
                    }

                    ATNConfigSet::ConfigEqualityComparator *const ATNConfigSet::ConfigEqualityComparator::INSTANCE = new ATNConfigSet::ConfigEqualityComparator();

                    ATNConfigSet::ConfigEqualityComparator::ConfigEqualityComparator() {
                        
                    }

                    int ATNConfigSet::ConfigEqualityComparator::hashCode(ATNConfig *o) {
                        int hashCode = 7;
                        hashCode = 31 * hashCode + o->state->stateNumber;
                        hashCode = 31 * hashCode + o->alt;
                        hashCode = 31 * hashCode + o->semanticContext->hashCode();
                        return hashCode;
                    }

                    bool ATNConfigSet::ConfigEqualityComparator::equals(ATNConfig *a, ATNConfig *b) {
                        if (a == b) {
                            return true;
                        }
                        if (a == nullptr || b == nullptr) {
                            return false;
                        }
                        return a->state->stateNumber == b->state->stateNumber && a->alt == b->alt && a->semanticContext->equals(b->semanticContext);
                    }

                    ATNConfigSet::ATNConfigSet(bool fullCtx) : fullCtx(fullCtx), configs(*new std::vector<ATNConfig*>(/*7*/)) {
                        InitializeInstanceFields();
                        configLookup = new ConfigHashSet();
                    }

                    ATNConfigSet::ATNConfigSet() : fullCtx(nullptr), configs(*new std::vector<ATNConfig*>(/*7*/)) {
                    }

                    ATNConfigSet::ATNConfigSet(ATNConfigSet *old) : fullCtx(nullptr), configs(*new std::vector<ATNConfig*>(/*7*/)) {
                        this->addAll<ATNConfigSet*>(old);
                        this->uniqueAlt = old->uniqueAlt;
                        this->conflictingAlts = old->conflictingAlts;
                        this->hasSemanticContext = old->hasSemanticContext;
                        this->dipsIntoOuterContext = old->dipsIntoOuterContext;
                    }

                    bool ATNConfigSet::add(ATNConfig *config) {
                        return add(config, nullptr);
                    }

                    bool ATNConfigSet::add(ATNConfig *config, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache) {
                        if (readonly) {
                            throw new IllegalStateException(L"This set is readonly");
                        }
                        if (config->semanticContext != SemanticContext::NONE) {
                            hasSemanticContext = true;
                        }
                        if (config->reachesIntoOuterContext > 0) {
                            dipsIntoOuterContext = true;
                        }
                        ATNConfig *existing = configLookup->getOrAdd(config);
                        if (existing == config) { // we added this new one
                            cachedHashCode = -1;
                            configs.insert(configs.end(), config); // track order here
                            
                            return true;
                        }
                        // a previous (s,i,pi,_), merge with it and save result
                        bool rootIsWildcard = !fullCtx;
                        PredictionContext *merged = PredictionContext::merge(existing->context, config->context, rootIsWildcard, mergeCache);
                        // no need to check for existing.context, config.context in cache
                        // since only way to create new graphs is "call rule" and here. We
                        // cache at both places.
                        existing->reachesIntoOuterContext = std::max(existing->reachesIntoOuterContext, config->reachesIntoOuterContext);
                        existing->context = merged; // replace context; no need to alt mapping
                        return true;
                    }

                    std::vector<ATNConfig*> ATNConfigSet::elements() {
                        return configs;
                    }

                    std::vector<ATNState*> *ATNConfigSet::getStates() {
                        std::vector<ATNState*> *states = new std::vector<ATNState*>();
                        for (auto c : configs) {
                            states->push_back(c->state);
                        }
                        return states;
                    }

                    std::vector<SemanticContext*> ATNConfigSet::getPredicates() {
                        std::vector<SemanticContext*> preds = std::vector<SemanticContext*>();
                        for (auto c : configs) {
                            if (c->semanticContext != SemanticContext::NONE) {
                                preds.push_back(c->semanticContext);
                            }
                        }
                        return preds;
                    }

                    org::antlr::v4::runtime::atn::ATNConfig *ATNConfigSet::get(int i) {
                        return configs[i];
                    }

                    void ATNConfigSet::optimizeConfigs(ATNSimulator *interpreter) {
                        if (readonly) {
                            throw IllegalStateException(L"This set is readonly");
                        }
                        if (configLookup->isEmpty()) {
                            return;
                        }

                        for (auto config : configs) {
                                        //			int before = PredictionContext.getAllContextNodes(config.context).size();
                            config->context = interpreter->getCachedContext(config->context);
                                        //			int after = PredictionContext.getAllContextNodes(config.context).size();
                                        //			System.out.println("configs "+before+"->"+after);
                        }
                    }


                    bool ATNConfigSet::equals(void *o) {
                        if (o == this) {
                            return true;
                        } else if (!(static_cast<ATNConfigSet*>(o) != nullptr)) {
                            return false;
                        }

                                        //		System.out.print("equals " + this + ", " + o+" = ");
                        ATNConfigSet *other = static_cast<ATNConfigSet*>(o);
                        
                        bool configEquals = true;
                        
                        if (configs.size() == other->configs.size()) {
                            for (int i = 0; i < (int)configs.size(); i++) {
                                if ((int)other->configs.size() < i) {
                                    configEquals = false;
                                    break;
                                }
                                if (configs.at(i) != other->configs.at(i)) {
                                    configEquals = false;
                                    break;
                                }
                            }
                        } else {
                            configEquals = false;
                        }
                        
                        bool same = configs.size() > 0 && configEquals && this->fullCtx == other->fullCtx && this->uniqueAlt == other->uniqueAlt && this->conflictingAlts == other->conflictingAlts && this->hasSemanticContext == other->hasSemanticContext && this->dipsIntoOuterContext == other->dipsIntoOuterContext; // includes stack context

                                        //		System.out.println(same);
                        return same;
                    }

                    int ATNConfigSet::hashCode() {
                        // TODO - revisit and check this usage, including the seed
                        int hash = misc::MurmurHash::hashCode(configs.data(), configs.size(), 123456);
                        if (isReadonly()) {
                            if (cachedHashCode == -1) {
                                cachedHashCode = hash;
                            }

                            return cachedHashCode;
                        }

                        return hash;
                    }

                    size_t ATNConfigSet::size() {
                        return configs.size();
                    }

                    bool ATNConfigSet::isEmpty() {
                        return configs.empty();
                    }

                    bool ATNConfigSet::contains(void *o) {
                        if (configLookup == nullptr) {
                            throw UnsupportedOperationException(L"This method is not implemented for readonly sets.");
                        }

                        return configLookup->contains(o);
                    }

                    bool ATNConfigSet::containsFast(ATNConfig *obj) {
                        if (configLookup == nullptr) {
                            throw UnsupportedOperationException(L"This method is not implemented for readonly sets.");
                        }

                        return configLookup->containsFast(obj);
                    }

                    std::vector<ATNConfig*>::iterator const ATNConfigSet::iterator() {
                        return configs.begin();

                    }

                    void ATNConfigSet::clear() {
                        if (readonly) {
                            throw new IllegalStateException(L"This set is readonly");
                        }
                        configs.clear();
                        cachedHashCode = -1;
                        configLookup->clear();
                    }

                    bool ATNConfigSet::isReadonly() {
                        return readonly;
                    }

                    void ATNConfigSet::setReadonly(bool readonly) {
                        this->readonly = readonly;
                        delete configLookup; // can't mod, no need for lookup cache
                    }

                    std::wstring ATNConfigSet::toString() {
                        antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                        for (int i = 0; i < (int)elements().size(); i++) {
                            buf->append(elements().at(i)->toString());
                        }
                        
                        if (hasSemanticContext) {
                            buf->append(L",hasSemanticContext=").append(hasSemanticContext);
                        }
                        if (uniqueAlt != ATN::INVALID_ALT_NUMBER) {
                            buf->append(L",uniqueAlt=").append(uniqueAlt);
                        }
                        if (conflictingAlts != nullptr) {
                            buf->append(L",conflictingAlts=");
                            buf->append(conflictingAlts->toString());
                        }
                        if (dipsIntoOuterContext) {
                            buf->append(L",dipsIntoOuterContext");
                        }
                        return buf->toString();
                    }

                    ATNConfig *ATNConfigSet::toArray() {
                        return (ATNConfig*)configLookup->toArray();
                    }


                    bool ATNConfigSet::remove(void *o) {
                        throw UnsupportedOperationException();
                    }


                    void ATNConfigSet::InitializeInstanceFields() {
                        readonly = false;
                        uniqueAlt = 0;
                        hasSemanticContext = false;
                        dipsIntoOuterContext = false;
                        cachedHashCode = -1;
                    }
                }
            }
        }
    }
}
