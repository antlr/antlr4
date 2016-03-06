#pragma once

#include <functional>
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
                namespace misc {

                    /// 
                    /// <summary>
                    /// @author Sam Harwell
                    /// </summary>
                    class MurmurHash {

                    private:
                        static const int DEFAULT_SEED = 0;

                        /// <summary>
                        /// Initialize the hash using the default seed value.
                        /// </summary>
                        /// <returns> the intermediate hash value </returns>
                    public:
                        static int initialize();

                        /// <summary>
                        /// Initialize the hash using the specified {@code seed}.
                        /// </summary>
                        /// <param name="seed"> the seed </param>
                        /// <returns> the intermediate hash value </returns>
                        static int initialize(int seed);

                        /// <summary>
                        /// Update the intermediate hash value for the next input {@code value}.
                        /// </summary>
                        /// <param name="hash"> the intermediate hash value </param>
                        /// <param name="value"> the value to add to the current hash </param>
                        /// <returns> the updated intermediate hash value </returns>
                        static int update(int hash, int value);

                        /// <summary>
                        /// Update the intermediate hash value for the next input {@code value}.
                        /// </summary>
                        /// <param name="hash"> the intermediate hash value </param>
                        /// <param name="value"> the value to add to the current hash </param>
                        /// <returns> the updated intermediate hash value </returns>
                        template<typename T>
                        static int update(int hash, T *value)  {
                            std::hash<T> hashFunction;
                            
                            return update(hash, value != nullptr ? (int)hashFunction(*value) : 0);
                        }

                        /// <summary>
                        /// Apply the final computation steps to the intermediate value {@code hash}
                        /// to form the final result of the MurmurHash 3 hash function.
                        /// </summary>
                        /// <param name="hash"> the intermediate hash value </param>
                        /// <param name="numberOfWords"> the number of integer values added to the hash </param>
                        /// <returns> the final hash result </returns>
                        static int finish(int hash, int numberOfWords);

                        /// <summary>
                        /// Utility function to compute the hash code of an array using the
                        /// MurmurHash algorithm.
                        /// </summary>
                        /// @param <T> the array element type </param>
                        /// <param name="data"> the array data </param>
                        /// <param name="seed"> the seed for the MurmurHash algorithm </param>
                        /// <returns> the hash code of the data </returns>

                        template<typename T> // where T is C array type
                        static int hashCode(const T*data, std::size_t size, int seed) {
                            int hash = initialize(seed);
                            for(int i = 0; i < (int)size; i++) {
                                hash = update(hash, data[i]);
                            }
                            
                            hash = finish(hash, (int)size);
                            return hash;
                        }


                    private:
                        MurmurHash();
                    };

                }
            }
        }
    }
}
