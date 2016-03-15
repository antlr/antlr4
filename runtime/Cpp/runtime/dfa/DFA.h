#pragma once

#include <string>
#include <vector>
#include <map>

#include "Declarations.h"

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
                namespace dfa {



                    class DFA {
                        /// <summary>
                        /// A set of all DFA states. Use <seealso cref="Map"/> so we can get old state back
                        ///  (<seealso cref="Set"/> only allows you to see if it's there).
                        /// </summary>
                    public:
                        /// <summary>
                        /// From which ATN state did we create this DFA? </summary>
                        atn::DecisionState *const atnStartState;
                        std::map<DFAState*, DFAState*> *const states;
                        DFAState *s0;
                        const int decision;

                        DFA(atn::DecisionState *atnStartState); //this(atnStartState, 0);

                        DFA(atn::DecisionState *atnStartState, int decision);

                        /// <summary>
                        /// Return a list of all states in this DFA, ordered by state number.
                        /// </summary>
                        virtual std::vector<DFAState*> getStates();

                    private:
                        class ComparatorAnonymousInnerClassHelper {
                        private:
                            DFA *const outerInstance;

                        public:
                            ComparatorAnonymousInnerClassHelper(DFA *outerInstance);

                            static int compare(DFAState *o1, DFAState *o2);
                        };

                    public:
                        virtual std::wstring toString();

                        virtual std::wstring toString(const std::vector<std::wstring>& tokenNames);
                        virtual std::wstring toLexerString();

                    };

                }
            }
        }
    }
}
