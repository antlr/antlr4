#pragma once

#include "PredictionContext.h"
#include "SingletonPredictionContext.h"
#include <string>
#include <vector>

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


                    class ArrayPredictionContext : public PredictionContext {
                        /// <summary>
                        /// Parent can be null only if full ctx mode and we make an array
                        ///  from <seealso cref="#EMPTY"/> and non-empty. We merge <seealso cref="#EMPTY"/> by using null parent and
                        ///  returnState == <seealso cref="#EMPTY_RETURN_STATE"/>.
                        /// </summary>
                    public:
                        const std::vector<PredictionContext*> *parents;

                        /// <summary>
                        /// Sorted for merge, no duplicates; if present,
                        ///  <seealso cref="#EMPTY_RETURN_STATE"/> is always last.
                        /// </summary>
                        const std::vector<int> returnStates;

                        ArrayPredictionContext(SingletonPredictionContext *a); //this(new PredictionContext[] {a.parent}, new int[] {a.returnState});

                        ArrayPredictionContext(PredictionContext *parents, int returnStates[]);
                        ArrayPredictionContext(std::vector<PredictionContext *>parents,
                                               const std::vector<int> returnStates);
                        
                        virtual bool isEmpty() override;

                        virtual int size() override;

                        virtual PredictionContext *getParent(int index) override;

                        virtual int getReturnState(int index) override;

                    //	@Override
                    //	public int findReturnState(int returnState) {
                    //		return Arrays.binarySearch(returnStates, returnState);
                    //	}

                        virtual bool equals(void *o) override;

                        virtual std::wstring toString();
                    };

                }
            }
        }
    }
}
