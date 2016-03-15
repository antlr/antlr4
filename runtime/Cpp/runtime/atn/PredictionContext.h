#pragma once
#include <string>
#include <unordered_map>
#include <vector>
#include <limits.h>
#include <stdint.h>

#include "DoubleKeyMap.h"
#include "Recognizer.h"
#include "Declarations.h"
#include "EqualityComparator.h"
#include "ATN.h"
#include "ATNState.h"


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


                    class PredictionContext {
                        /// <summary>
                        /// Represents {@code $} in local context prediction, which means wildcard.
                        /// {@code *+x = *}.
                        /// </summary>
                    public:
                        static EmptyPredictionContext * EMPTY;

                        /// <summary>
                        /// Represents {@code $} in an array in full context mode, when {@code $}
                        /// doesn't mean wildcard: {@code $ + x = [$,x]}. Here,
                        /// {@code $} = <seealso cref="#EMPTY_RETURN_STATE"/>.
                        /// </summary>
                        static const int EMPTY_RETURN_STATE = INT16_MAX;

                    private:
                        static const int INITIAL_HASH = 1;

                    public:
                        static int globalNodeCount;
                        const int id;

                        /// <summary>
                        /// Stores the computed hash code of this <seealso cref="PredictionContext"/>. The hash
                        /// code is computed in parts to match the following reference algorithm.
                        /// 
                        /// <pre>
                        ///  private int referenceHashCode() {
                        ///      int hash = <seealso cref="MurmurHash#initialize"/>(<seealso cref="#INITIAL_HASH"/>);
                        /// 
                        ///      for (int i = 0; i < <seealso cref="#size()"/>; i++) {
                        ///          hash = <seealso cref="MurmurHash#update"/>(hash, <seealso cref="#getParent"/>(i));
                        ///      }
                        /// 
                        ///      for (int i = 0; i < <seealso cref="#size()"/>; i++) {
                        ///          hash = <seealso cref="MurmurHash#update"/>(hash, <seealso cref="#getReturnState"/>(i));
                        ///      }
                        /// 
                        ///      hash = <seealso cref="MurmurHash#finish"/>(hash, 2 * <seealso cref="#size()"/>);
                        ///      return hash;
                        ///  }
                        /// </pre>
                        /// </summary>
                        const int cachedHashCode;

                    protected:
                        PredictionContext(int cachedHashCode);

                        /// <summary>
                        /// Convert a <seealso cref="RuleContext"/> tree to a <seealso cref="PredictionContext"/> graph.
                        ///  Return <seealso cref="#EMPTY"/> if {@code outerContext} is empty or null.
                        /// </summary>
                    public:
                        static PredictionContext *fromRuleContext(ATN *atn, RuleContext *outerContext);

                        virtual int size();//= 0;

                        virtual PredictionContext *getParent(int index);//= 0;

                        virtual int getReturnState(int index); //  = 0;

                        /// <summary>
                        /// This means only the <seealso cref="#EMPTY"/> context is in set. </summary>
                        virtual bool isEmpty();

                        virtual bool hasEmptyPath();

                        virtual int hashCode()  final;

                        virtual bool equals(void *obj){ // = 0;
                            // This should be abstract but we need to create arrays of it, which will point to
                            // daughters in reality
                            throw new ASSERTException(L"PredictionContext", L"equal should never be called, abstract class");
                            
                        }

                    protected:
                        static int calculateEmptyHashCode();

                        static int calculateHashCode(PredictionContext *parent, int returnState);

                        static int calculateHashCode(std::vector<PredictionContext*> parents, std::vector<int>returnStates);

                        // dispatch
                    public:
                        static PredictionContext *merge(PredictionContext *a, PredictionContext *b, bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache);

                        /// <summary>
                        /// Merge two <seealso cref="SingletonPredictionContext"/> instances.
                        /// 
                        /// <p/>
                        /// 
                        /// Stack tops equal, parents merge is same; return left graph.<br/>
                        /// <embed src="images/SingletonMerge_SameRootSamePar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Same stack top, parents differ; merge parents giving array node, then
                        /// remainders of those graphs. A new root node is created to point to the
                        /// merged parents.<br/>
                        /// <embed src="images/SingletonMerge_SameRootDiffPar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Different stack tops pointing to same parent. Make array node for the
                        /// root where both element in the root point to the same (original)
                        /// parent.<br/>
                        /// <embed src="images/SingletonMerge_DiffRootSamePar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Different stack tops pointing to different parents. Make array node for
                        /// the root where each element points to the corresponding original
                        /// parent.<br/>
                        /// <embed src="images/SingletonMerge_DiffRootDiffPar.svg" type="image/svg+xml"/>
                        /// </summary>
                        /// <param name="a"> the first <seealso cref="SingletonPredictionContext"/> </param>
                        /// <param name="b"> the second <seealso cref="SingletonPredictionContext"/> </param>
                        /// <param name="rootIsWildcard"> {@code true} if this is a local-context merge,
                        /// otherwise false to indicate a full-context merge </param>
                        /// <param name="mergeCache"> </param>
                        static PredictionContext *mergeSingletons(SingletonPredictionContext *a, SingletonPredictionContext *b, bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache);

                        /// <summary>
                        /// Handle case where at least one of {@code a} or {@code b} is
                        /// <seealso cref="#EMPTY"/>. In the following diagrams, the symbol {@code $} is used
                        /// to represent <seealso cref="#EMPTY"/>.
                        /// 
                        /// <h2>Local-Context Merges</h2>
                        /// 
                        /// These local-context merge operations are used when {@code rootIsWildcard}
                        /// is true.
                        /// 
                        /// <p/>
                        /// 
                        /// <seealso cref="#EMPTY"/> is superset of any graph; return <seealso cref="#EMPTY"/>.<br/>
                        /// <embed src="images/LocalMerge_EmptyRoot.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// <seealso cref="#EMPTY"/> and anything is {@code #EMPTY}, so merged parent is
                        /// {@code #EMPTY}; return left graph.<br/>
                        /// <embed src="images/LocalMerge_EmptyParent.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Special case of last merge if local context.<br/>
                        /// <embed src="images/LocalMerge_DiffRoots.svg" type="image/svg+xml"/>
                        /// 
                        /// <h2>Full-Context Merges</h2>
                        /// 
                        /// These full-context merge operations are used when {@code rootIsWildcard}
                        /// is false.
                        /// 
                        /// <p/>
                        /// 
                        /// <embed src="images/FullMerge_EmptyRoots.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Must keep all contexts; <seealso cref="#EMPTY"/> in array is a special value (and
                        /// null parent).<br/>
                        /// <embed src="images/FullMerge_EmptyRoot.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// <embed src="images/FullMerge_SameRoot.svg" type="image/svg+xml"/>
                        /// </summary>
                        /// <param name="a"> the first <seealso cref="SingletonPredictionContext"/> </param>
                        /// <param name="b"> the second <seealso cref="SingletonPredictionContext"/> </param>
                        /// <param name="rootIsWildcard"> {@code true} if this is a local-context merge,
                        /// otherwise false to indicate a full-context merge </param>
                        static PredictionContext *mergeRoot(SingletonPredictionContext *a, SingletonPredictionContext *b, bool rootIsWildcard);

                        /// <summary>
                        /// Merge two <seealso cref="ArrayPredictionContext"/> instances.
                        /// 
                        /// <p/>
                        /// 
                        /// Different tops, different parents.<br/>
                        /// <embed src="images/ArrayMerge_DiffTopDiffPar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Shared top, same parents.<br/>
                        /// <embed src="images/ArrayMerge_ShareTopSamePar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Shared top, different parents.<br/>
                        /// <embed src="images/ArrayMerge_ShareTopDiffPar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Shared top, all shared parents.<br/>
                        /// <embed src="images/ArrayMerge_ShareTopSharePar.svg" type="image/svg+xml"/>
                        /// 
                        /// <p/>
                        /// 
                        /// Equal tops, merge parents and reduce top to
                        /// <seealso cref="SingletonPredictionContext"/>.<br/>
                        /// <embed src="images/ArrayMerge_EqualTop.svg" type="image/svg+xml"/>
                        /// </summary>
                        static PredictionContext *mergeArrays(ArrayPredictionContext *a, ArrayPredictionContext *b, bool rootIsWildcard, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache);

                        /// <summary>
                        /// Make pass over all <em>M</em> {@code parents}; merge any {@code ()}
                        /// ones.
                        /// </summary>
                    protected:
						static void combineCommonParents(std::vector<PredictionContext*>parents);

                    public:
                        static std::wstring toDOTString(PredictionContext *context);

                    private:

                        class ComparatorAnonymousInnerClassHelper : public misc::EqualityComparator<PredictionContext*> {

                        public:
                            ComparatorAnonymousInnerClassHelper();

                            static int compare(PredictionContext *o1, PredictionContext *o2);
                        };
 
                        // From Sam
                    public:
                        static PredictionContext *getCachedContext(PredictionContext *context, PredictionContextCache *contextCache, std::map<PredictionContext*, PredictionContext*> *visited);

                    //	// extra structures, but cut/paste/morphed works, so leave it.
                    //	// seems to do a breadth-first walk
                    //	public static List<PredictionContext> getAllNodes(PredictionContext context) {
                    //		Map<PredictionContext, PredictionContext> visited =
                    //			new IdentityHashMap<PredictionContext, PredictionContext>();
                    //		Deque<PredictionContext> workList = new ArrayDeque<PredictionContext>();
                    //		workList.add(context);
                    //		visited.put(context, context);
                    //		List<PredictionContext> nodes = new ArrayList<PredictionContext>();
                    //		while (!workList.isEmpty()) {
                    //			PredictionContext current = workList.pop();
                    //			nodes.add(current);
                    //			for (int i = 0; i < current.size(); i++) {
                    //				PredictionContext parent = current.getParent(i);
                    //				if ( parent!=null && visited.put(parent, parent) == null) {
                    //					workList.push(parent);
                    //				}
                    //			}
                    //		}
                    //		return nodes;
                    //	}

                        // ter's recursive version of Sam's getAllNodes()
                        static std::vector<PredictionContext*> getAllContextNodes(PredictionContext *context);

                        static void getAllContextNodes_(PredictionContext *context, std::vector<PredictionContext*> &nodes, std::map<PredictionContext*, PredictionContext*> *visited);

                        std::wstring toString();

                        template<typename T1, typename T2>
                        std::wstring toString(Recognizer<T1, T2> *recog)  {
                            return toString();
                            //		return toString(recog, ParserRuleContext.EMPTY);
                        }

                        template<typename T1, typename T2>
                        std::wstring *toStrings(Recognizer<T1, T2> *recognizer, int currentState) {
                            return toStrings(recognizer, EMPTY, currentState);
                        }

                        // FROM SAM
                        template<typename T1, typename T2>
                        std::wstring *toStrings(Recognizer<T1, T2> *recognizer, PredictionContext *stop, int currentState) {
                            std::vector<std::wstring> result = std::vector<std::wstring>();
                            
                            for (int perm = 0; ; perm++) {
                                int offset = 0;
                                bool last = true;
                                PredictionContext *p = this;
                                int stateNumber = currentState;
                                antlrcpp::StringBuilder *localBuffer = new antlrcpp::StringBuilder();
                                localBuffer->append(L"[");
                                while (!p->isEmpty() && p != stop) {
                                    int index = 0;
                                    if (p->size() > 0) {
                                        int bits = 1;
                                        while ((1 << bits) < p->size()) {
                                            bits++;
                                        }
                                        
                                        int mask = (1 << bits) - 1;
                                        index = (perm >> offset) & mask;
                                        last &= index >= p->size() - 1;
                                        if (index >= p->size()) {
                                            goto outerContinue;
                                        }
                                        offset += bits;
                                    }
                                    
                                    if (recognizer != nullptr) {
                                        if (localBuffer->length() > 1) {
                                            // first char is '[', if more than that this isn't the first rule
                                            localBuffer->append(L' ');
                                        }
                                        
                                        ATN *atn = recognizer->getATN();
                                        ATNState *s = atn->states[stateNumber];
                                        std::wstring ruleName = recognizer->getRuleNames()[s->ruleIndex];
                                        localBuffer->append(ruleName);
                                    } else if (p->getReturnState(index) != EMPTY_RETURN_STATE) {
                                        if (!p->isEmpty()) {
                                            if (localBuffer->length() > 1) {
                                                // first char is '[', if more than that this isn't the first rule
                                                localBuffer->append(L' ');
                                            }
                                            
                                            localBuffer->append(p->getReturnState(index));
                                        }
                                    }
                                    stateNumber = p->getReturnState(index);
                                    p = p->getParent(index);
                                }
                                localBuffer->append(L"]");
                                result.push_back(localBuffer->toString());
                                
                                if (last) {
                                    break;
                                }
                            outerContinue:
                                continue;
                            }
                        outerBreak:
                            
                            // TODO: return result.toArray(new std::wstring[result.size()]);
                            return nullptr;
                        }

                    };

                }
            }
        }
    }
}

// Hash function for PredictionContext, used in the MurmurHash::update function
 
namespace std {
    using org::antlr::v4::runtime::atn::PredictionContext;
    
    template <> struct hash<PredictionContext>
    {
        size_t operator()(PredictionContext & x) const
        {
            return x.hashCode();
        }
    };
}


