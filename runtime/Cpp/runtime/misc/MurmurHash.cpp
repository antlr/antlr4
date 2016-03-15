#include "MurmurHash.h"

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

                    int MurmurHash::initialize() {
                        return initialize(DEFAULT_SEED);
                    }

                    int MurmurHash::initialize(int seed) {
                        return seed;
                    }
                    
                    int MurmurHash::update(int hash, int value) {
                        const int c1 = 0xCC9E2D51;
                        const int c2 = 0x1B873593;
                        const int r1 = 15;
                        const int r2 = 13;
                        const int m = 5;
                        const int n = 0xE6546B64;

                        int k = value;
                        k = k * c1;
                        k = (k << r1) | (static_cast<int>(static_cast<unsigned int>(k) >> (32 - r1)));
                        k = k * c2;

                        hash = hash ^ k;
                        hash = (hash << r2) | (static_cast<int>(static_cast<unsigned int>(hash) >> (32 - r2)));
                        hash = hash * m + n;

                        return hash;
                    }


                    int MurmurHash::finish(int hash, int numberOfWords) {
                        hash = hash ^ (numberOfWords * 4);
                        hash = hash ^ (static_cast<int>(static_cast<unsigned int>(hash) >> 16));
                        hash = hash * 0x85EBCA6B;
                        hash = hash ^ (static_cast<int>(static_cast<unsigned int>(hash) >> 13));
                        hash = hash * 0xC2B2AE35;
                        hash = hash ^ (static_cast<int>(static_cast<unsigned int>(hash) >> 16));
                        return hash;
                    }

                    MurmurHash::MurmurHash() {
                    }
                }
            }
        }
    }
}
