#pragma once

#include <set>
#include <string>
#include <algorithm>
#include <vector>

#include "DoubleKeyMap.h"
#include "Declarations.h"
#include "Array2DHashSet.h"
#include "AbstractEqualityComparator.h"
#include "BitSet.h"
#include "Exceptions.h"


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
                    /// <summary>
                    /// Specialized <seealso cref="Set"/>{@code <}<seealso cref="ATNConfig"/>{@code >} that can track
                    /// info about the set, with support for combining similar configurations using a
                    /// graph-structured stack.
                    /// </summary>
					/// TODO: Consider going from std::set to std::vector
                    class ATNConfigSet : public std::set<ATNConfig*> {
                        
                    public:
                        class AbstractConfigHashSet : public misc::Array2DHashSet<ATNConfig*> {

                        public:
                            
                            template<typename T1>
                            AbstractConfigHashSet(misc::AbstractEqualityComparator<T1> *comparator) {}

                            template<typename T1>
                            AbstractConfigHashSet(misc::AbstractEqualityComparator<T1> *comparator, int initialCapacity, int initialBucketCapacity) {}

                        protected:
                            ATNConfig *asElementType(void *o) override;

                            std::vector<std::vector<ATNConfig*>> *createBuckets(int capacity) override;

                            std::vector<ATNConfig*> *createBucket(int capacity) override;

                        };
                        /// <summary>
                        /// The reason that we need this is because we don't want the hash map to use
                        /// the standard hash code and equals. We need all configurations with the same
                        /// {@code (s,i,_,semctx)} to be equal. Unfortunately, this key effectively doubles
                        /// the number of objects associated with ATNConfigs. The other solution is to
                        /// use a hash table that lets us specify the equals/hash code operation.
                        /// </summary>
                    public:
                        class ConfigHashSet : public AbstractConfigHashSet {
                        public:
                            ConfigHashSet();
                        };

                    public:
                        class ConfigEqualityComparator : public misc::AbstractEqualityComparator<ATNConfig*> {
                        public:
                            static ConfigEqualityComparator *const INSTANCE;

                        private:
                            ConfigEqualityComparator();

                        public:
                            int hashCode(ATNConfig *o);

                            bool equals(ATNConfig *a, ATNConfig *b);
                        };

                        /// <summary>
                        /// Indicates that the set of configurations is read-only. Do not
                        ///  allow any code to manipulate the set; DFA states will point at
                        ///  the sets and they must not change. This does not protect the other
                        ///  fields; in particular, conflictingAlts is set after
                        ///  we've made this readonly.
                        /// </summary>
                    protected:
                        bool readonly;

                        /// <summary>
                        /// All configs but hashed by (s, i, _, pi) not including context. Wiped out
                        /// when we go readonly as this set becomes a DFA state.
                        /// </summary>
                    public:
                        AbstractConfigHashSet *configLookup;

                        /// <summary>
                        /// Track the elements as they are added to the set; supports get(i) </summary>
                        std::vector<ATNConfig*> configs;

                        // TODO: these fields make me pretty uncomfortable but nice to pack up info together, saves recomputation
                        // TODO: can we track conflicts as they are added to save scanning configs later?
                        int uniqueAlt;
                  
                        antlrcpp::BitSet *conflictingAlts;

                        // Used in parser and lexer. In lexer, it indicates we hit a pred
                        // while computing a closure operation.  Don't make a DFA state from this.
                    public:
                        bool hasSemanticContext;
                        bool dipsIntoOuterContext;

                        /// <summary>
                        /// Indicates that this configuration set is part of a full context
                        ///  LL prediction. It will be used to determine how to merge $. With SLL
                        ///  it's a wildcard whereas it is not for LL context merge.
                        /// </summary>
                        const bool fullCtx;

                    private:
                        int cachedHashCode;

                    public:
                        ATNConfigSet(bool fullCtx);
                        ATNConfigSet(); //this(true);

                        ATNConfigSet(ATNConfigSet *old); //this(old.fullCtx);

                        virtual bool add(ATNConfig *config);

                        /// <summary>
                        /// Adding a new config means merging contexts with existing configs for
                        /// {@code (s, i, pi, _)}, where {@code s} is the
                        /// <seealso cref="ATNConfig#state"/>, {@code i} is the <seealso cref="ATNConfig#alt"/>, and
                        /// {@code pi} is the <seealso cref="ATNConfig#semanticContext"/>. We use
                        /// {@code (s,i,pi)} as key.
                        /// <p/>
                        /// This method updates <seealso cref="#dipsIntoOuterContext"/> and
                        /// <seealso cref="#hasSemanticContext"/> when necessary.
                        /// </summary>
                        virtual bool add(ATNConfig *config, misc::DoubleKeyMap<PredictionContext*, PredictionContext*, PredictionContext*> *mergeCache);

                        /// <summary>
                        /// Return a List holding list of configs </summary>
                        virtual std::vector<ATNConfig*> elements();

                        virtual std::vector<ATNState*> *getStates();

                        virtual std::vector<SemanticContext*> getPredicates();

                        virtual ATNConfig *get(int i);

                        virtual void optimizeConfigs(ATNSimulator *interpreter);

                        template<typename T1>// where T1 : ATNConfig
                        bool addAll(ATNConfigSet *coll) {
                            for (auto c : *coll) {
                                add(c);
                            }
                            return false;
                        }

                        virtual bool equals(void *o);

                        virtual int hashCode();

                        virtual size_t size();

                        virtual bool isEmpty();

                        virtual bool contains(void *o);

                        virtual bool containsFast(ATNConfig *obj);

                        virtual std::vector<ATNConfig*>::iterator const iterator();

                        virtual void clear();

                        virtual bool isReadonly();

                        virtual void setReadonly(bool readonly);

                        virtual std::wstring toString();

                        // satisfy interface

                        virtual ATNConfig *toArray();

                        template<typename T>
                        T *toArray(T a[])  {
                            return configLookup->toArray(a);
                        }
                        virtual bool remove(void *o);

                        template<typename T1>
                        bool containsAll(std::vector<T1> *c){
                            throw new UnsupportedOperationException();
                        }
                        template<typename T1>
                        bool retainAll(std::vector<T1> *c)  {
                            throw new UnsupportedOperationException();
                        }

                        template<typename T1>
                        bool removeAll(std::vector<T1> *c)  {
                            throw new UnsupportedOperationException();
                        }


                    private:
                        void InitializeInstanceFields();
                    };

                }
            }
        }
    }
}
