#pragma once

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
                namespace misc {

                    /// <summary>
                    /// This interface provides an abstract concept of object equality independent of
                    /// <seealso cref="Object#equals"/> (object equality) and the {@code ==} operator
                    /// (reference equality). It can be used to provide algorithm-specific unordered
                    /// comparisons without requiring changes to the object itself.
                    /// 
                    /// @author Sam Harwell
                    /// </summary>
                    template<typename T>
                    class EqualityComparator {

                        /// <summary>
                        /// This method returns a hash code for the specified object.
                        /// </summary>
                        /// <param name="obj"> The object. </param>
                        /// <returns> The hash code for {@code obj}. </returns>
                    public:
                        virtual int hashCode(T obj) {
                            throw new TODOException(L"EqualityComparator::hashCode");
                            return 0;
                        }

                        /// <summary>
                        /// This method tests if two objects are equal.
                        /// </summary>
                        /// <param name="a"> The first object to compare. </param>
                        /// <param name="b"> The second object to compare. </param>
                        /// <returns> {@code true} if {@code a} equals {@code b}, otherwise {@code false}. </returns>
                        virtual bool equals(T a, T b) {
                            new TODOException(L"EqualityComparator::equals");
                            return false;
                        }

                    };

                }
            }
        }
    }
}
