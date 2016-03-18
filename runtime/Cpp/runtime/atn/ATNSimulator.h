#pragma once
#include <string>
#include <vector>
#include <mutex>

#include "Declarations.h"
#include "ATNDeserializer.h"

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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {
                    class ATNSimulator {
                    public:
						
						//Mutex to manage synchronized access for multithreading
						std::mutex mtx;
                        ATNSimulator();

                        /// <summary>
                        /// This is the current serialized UUID. </summary>
                        /// @deprecated Use <seealso cref="ATNDeserializer#checkCondition(boolean)"/> instead.
#ifdef TODO
                        constexpr static UUID *const SERIALIZED_UUID = ATNDeserializer::SERIALIZED_UUID;
#endif
                        /// <summary>
                        /// Must distinguish between missing edge and edge we know leads nowhere </summary>
                        static dfa::DFAState * ERROR;
                        ATN * atn;

                        /// <summary>
                        /// The context cache maps all PredictionContext objects that are equals()
                        ///  to a single cached copy. This cache is shared across all contexts
                        ///  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
                        ///  to use only cached nodes/graphs in addDFAState(). We don't want to
                        ///  fill this during closure() since there are lots of contexts that
                        ///  pop up but are not used ever again. It also greatly slows down closure().
                        ///  <p/>
                        ///  This cache makes a huge difference in memory and a little bit in speed.
                        ///  For the Java grammar on java.*, it dropped the memory requirements
                        ///  at the end from 25M to 16M. We don't store any of the full context
                        ///  graphs in the DFA because they are limited to local context only,
                        ///  but apparently there's a lot of repetition there as well. We optimize
                        ///  the config contexts before storing the config set in the DFA states
                        ///  by literally rebuilding them with cached subgraphs only.
                        ///  <p/>
                        ///  I tried a cache for use during closure operations, that was
                        ///  whacked after each adaptivePredict(). It cost a little bit
                        ///  more time I think and doesn't save on the overall footprint
                        ///  so it's not worth the complexity.
                        /// </summary>
                    protected:
                        PredictionContextCache * sharedContextCache;


                    public:
                        ATNSimulator(ATN *atn, PredictionContextCache *sharedContextCache);

                        virtual void reset() = 0;

                        virtual PredictionContextCache *getSharedContextCache();

                        virtual PredictionContext *getCachedContext(PredictionContext *context);

                        /// @deprecated Use <seealso cref="ATNDeserializer#deserialize"/> instead. 
                        static ATN *deserialize(wchar_t data[]);

                        /// @deprecated Use <seealso cref="ATNDeserializer#checkCondition(boolean)"/> instead. 
                        static void checkCondition(bool condition);

                        /// @deprecated Use <seealso cref="ATNDeserializer#checkCondition(boolean, String)"/> instead. 
                        static void checkCondition(bool condition, const std::wstring &message);

                        /// @deprecated Use <seealso cref="ATNDeserializer#edgeFactory"/> instead. 
                        static Transition *edgeFactory(ATN *atn, int type, int src, int trg, int arg1, int arg2, int arg3, std::vector<misc::IntervalSet*> &sets);

                        /// @deprecated Use <seealso cref="ATNDeserializer#stateFactory"/> instead. 
                        static ATNState *stateFactory(int type, int ruleIndex);

                    /*
                    	public static void dump(DFA dfa, Grammar g) {
                    		DOTGenerator dot = new DOTGenerator(g);
                    		String output = dot.getDOT(dfa, false);
                    		System.out.println(output);
                    	}
                    
                    	public static void dump(DFA dfa) {
                    		dump(dfa, null);
                    	}
                    	 */
                    };

                }
            }
        }
    }
}
