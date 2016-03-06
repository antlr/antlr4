#pragma once
#include <vector>
#include <string>
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
                    /// A generic set of ints.
                    /// </summary>
                    ///  <seealso cref= IntervalSet </seealso>
                    class IntSet {
                        /// <summary>
                        /// Add an element to the set </summary>
                    public:
                        virtual void add(int el) = 0;

                        /// <summary>
                        /// Add all elements from incoming set to this set.  Can limit
                        ///  to set of its own type. Return "this" so we can chain calls.
                        /// </summary>
                        virtual IntSet *addAll(IntSet *set) = 0;

                        /// <summary>
                        /// Return the intersection of this set with the argument, creating
                        ///  a new set.
                        /// </summary>
                        virtual IntSet *And(IntSet *a) = 0;

                        virtual IntSet *complement(IntSet *elements) = 0;

                        virtual IntSet *Or(IntSet *a) = 0;

                        virtual IntSet *subtract(IntSet *a) = 0;

                        /// <summary>
                        /// Return the size of this set (not the underlying implementation's
                        ///  allocated memory size, for example).
                        /// </summary>
                        virtual int size() = 0;

                        virtual bool isNil() = 0;

                        virtual bool equals(void *obj) = 0;

                        virtual int getSingleElement() = 0;

                        virtual bool contains(int el) = 0;

                        /// <summary>
                        /// remove this element from this set </summary>
                        virtual void remove(int el) = 0;

                        virtual std::vector<int> toList() = 0;

                        virtual std::wstring toString() = 0;
                    };

                }
            }
        }
    }
}
