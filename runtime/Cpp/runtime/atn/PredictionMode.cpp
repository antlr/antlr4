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

#include "PredictionMode.h"

#include <assert.h>
#include "AbstractEqualityComparator.h"

namespace org {
	namespace antlr {
		namespace v4 {
			namespace runtime {
				namespace atn {

					class AltAndContextConfigEqualityComparator
						: misc::AbstractEqualityComparator < ATNConfig > {
					public:
						int hashCode(ATNConfig* o);
						bool equals(ATNConfig* a, ATNConfig* b);

					private:
						AltAndContextConfigEqualityComparator() {}
					};

					// TODO -- Determine if we need this hash function.
					int AltAndContextConfigEqualityComparator::hashCode(ATNConfig* o) {
						int hashCode = runtime::misc::MurmurHash::initialize(7);
						hashCode = runtime::misc::MurmurHash::update(hashCode, o->state->stateNumber);
						hashCode = runtime::misc::MurmurHash::update(hashCode, o->context);
						return runtime::misc::MurmurHash::finish(hashCode, 2);
					}

					// TODO -- Determine if we need this comparator.
					bool AltAndContextConfigEqualityComparator::equals(ATNConfig* a, ATNConfig* b) {
						if (a == b) {
							return true;
						}
						if (a == nullptr || b == nullptr) {
							return false;
						}
						return a->state->stateNumber == b->state->stateNumber &&
							a->context->equals(b->context);
					}

					/// <summary>
					/// A Map that uses just the state and the stack context as the key. </summary>
					class AltAndContextMap : public std::unordered_map < ATNConfig, antlrcpp::BitSet, ATNConfig::ATNConfigHasher> {
					public:
						AltAndContextMap() {}
					};


					bool PredictionModeClass::hasSLLConflictTerminatingPrediction(PredictionMode* mode,
						ATNConfigSet* configs) {
						/* Configs in rule stop states indicate reaching the end of the decision
						 * rule (local context) or end of start rule (full context). If all
						 * configs meet this condition, then none of the configurations is able
						 * to match additional input so we terminate prediction.
						 */
						if (allConfigsInRuleStopStates(configs)) {
							return true;
						}

						// pure SLL mode parsing
						if (*mode == PredictionMode::SLL) {
							// Don't bother with combining configs from different semantic
							// contexts if we can fail over to full LL; costs more time
							// since we'll often fail over anyway.
							if (configs->hasSemanticContext) {
								// dup configs, tossing out semantic predicates
								ATNConfigSet* dup = new ATNConfigSet();
								for (ATNConfig config : *configs) {
									ATNConfig* c = new ATNConfig(&config, SemanticContext::NONE);
									dup->add(c);
								}
								configs = dup;
							}
							// now we have combined contexts for configs with dissimilar preds
						}

						// pure SLL or combined SLL+LL mode parsing
						std::vector<antlrcpp::BitSet> altsets = getConflictingAltSubsets(configs);
						bool heuristic =
							hasConflictingAltSet(altsets) && !hasStateAssociatedWithOneAlt(configs);
						return heuristic;
					}

					bool PredictionModeClass::hasConfigInRuleStopState(ATNConfigSet* configs) {
						for (ATNConfig c : *configs) {
							if (dynamic_cast<RuleStopState*>(c.state) != NULL) {
								return true;
							}
						}

						return false;
					}

					bool PredictionModeClass::allConfigsInRuleStopStates(ATNConfigSet* configs) {
						for (ATNConfig config : *configs) {
							if (dynamic_cast<RuleStopState*>(config.state) == NULL) {
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
						if (altsets.size() == 0) {
							// TODO -- Determine if this should return true or false when there are no
							// sets available based on the original code.
							return true;
						}
						const antlrcpp::BitSet& first = *altsets.begin();
						for (const antlrcpp::BitSet& alts : altsets) {
							if (alts.data != first.data) {
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
							all.data |= alts.data;
						}

						return all;
					}

					std::vector<antlrcpp::BitSet> PredictionModeClass::getConflictingAltSubsets(ATNConfigSet* configs) {
						AltAndContextMap configToAlts;
						for (const ATNConfig& c : *configs) {
							configToAlts[c].set(c.alt);
						}
						std::vector<antlrcpp::BitSet> values;
						for (auto it : configToAlts) {
							values.push_back(it.second);
						}
						return values;
					}

					std::map<ATNState*, antlrcpp::BitSet> PredictionModeClass::getStateToAltMap(ATNConfigSet* configs) {
						std::map<ATNState*, antlrcpp::BitSet> m;
						for (ATNConfig c : *configs) {
							m[c.state].set(c.alt);
						}
						return m;
					}

					bool PredictionModeClass::hasStateAssociatedWithOneAlt(ATNConfigSet* configs) {
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

							assert(minAlt != -1);  // TODO -- Remove this after verification.
							viableAlts.set(minAlt);
							if (viableAlts.count() > 1)  // more than 1 viable alt
							{
								return ATN::INVALID_ALT_NUMBER;
							}
						}

						return viableAlts.nextSetBit(0);
					}

				}  // namespace atn
			}  // namespace runtime
		}  // namespace v4
	}  // namespace antlr
}  // namespace org
