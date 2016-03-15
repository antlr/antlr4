#pragma once

#include <map>

#include "Declarations.h"
#include "EmptyPredictionContext.h"

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

#include "PredictionContext.h"

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                namespace atn {
                    
                    /// <summary>
                    /// Used to cache <seealso cref="PredictionContext"/> objects. Its used for the
                    /// shared
                    ///  context cash associated with contexts in DFA states. This cache
                    ///  can be used for both lexers and parsers.
                    /// </summary>
                    class PredictionContextCache {
                    protected:
                        std::map<PredictionContext *, PredictionContext *> *cache;
                        
                        /// <summary>
                        /// Add a context to the cache and return it. If the context already exists,
                        ///  return that one instead and do not add a new context to the cache.
                        ///  Protect shared cache from unsafe thread access.
                        /// </summary>
                    public:
                        virtual PredictionContext *add(PredictionContext *ctx);
                        
                        virtual PredictionContext *get(PredictionContext *ctx);
                        
                        virtual size_t size();
                        
                    private:
                        void InitializeInstanceFields();
                        
                    public:
                        PredictionContextCache() { InitializeInstanceFields(); }
                    };
                    
                }  // namespace atn
            }  // namespace runtime
        }  // namespace v4
    }  // namespace antlr
}  // namespace org
