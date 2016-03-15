/*
 * [The "BSD license"]
 *  Copyright (c) 2015 Dan McLaughlin
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

#include "UUID.h"

// Terence thinks this is for some version control stuff or something
// or other - probably not important. So be nice in this function, be quiet and
// hope nobody complains

namespace antlrcpp {
    UUID::UUID(long long mostSigBits, long long leastSigBits) {
//        throw new std::exception();
    }
    UUID::UUID(const UUID &other) {
//        throw new std::exception();
    };
    
    UUID::~UUID() {
//        throw new std::exception();
    };
    
    const UUID &UUID::operator=(const UUID &rhs) {
//        throw new std::exception();
        return rhs;
    };
    bool UUID::operator==(const UUID &rhs) const {
//        throw new std::exception();
        return true;
    };
    bool UUID::equals(const UUID &rhs) const {
//        throw new std::exception();
        return true;
    };
    bool UUID::equals(const UUID *rhs) const {
//        throw new std::exception();
        return true;
    };
    
    UUID *UUID::fromString(const std::wstring &name) {
//        throw new std::exception();
        return new UUID(0,0);
    };
    
    std::wstring UUID::toString() const {
//        throw new std::exception();
        return L"UUID_UNIMPLEMENTED";
    };
    
    long UUID::getLeastSignificantBits() const {
//        throw new std::exception();
        return 0;
    };
    long UUID::getMostSignificantBits() const {
//        throw new std::exception();
        return 0;
    };
}