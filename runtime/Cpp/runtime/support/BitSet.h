#pragma once

#include <bitset>
#include <iostream>
#include <sstream>
#include <vector>

/*
 * [The "BSD license"]
 *  Copyright (c) 2015 Ana Maria Rodriguez Reyes
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

namespace antlrcpp {
    
    class BitSet {
        
    public:
        BitSet() {}
        
        BitSet(const BitSet &other) {
            data = other.data;
        }
        
        static const int BITSET_SIZE = 1024;
        std::bitset<BITSET_SIZE> data;
        
        void assign(size_t count, const BitSet & value ) {
            
        }
        
        int nextSetBit(const int & pos) {
            for (size_t i = pos; i < data.size(); i++){
                if (data.test(i)) return (int)i;
            }
            
            return -1;
        }
        
        void set(size_t pos){
            data.set(pos);
        }
        
        void set(){
            data.set();
        }
        
        size_t count(){
            return data.count();
        }
        
        size_t size(){
            return data.size();
        }
        
        // Prints a list of every index for which the bitset contains a bit in true.
        friend std::wostream& operator<<(std::wostream& os, const BitSet& obj)
        {
            
            os << L"{";
            size_t total = obj.data.count();
            for (size_t i = 0; i < obj.data.size(); i++){
                if (obj.data.test(i)){
                    os << i;
                    --total;
                    if (total > 1){
                        os << L", ";
                    }
                }
            }
            
            os << L"}";
            return os;
        }
        
        static std::wstring subStringRepresentation(const std::vector<BitSet>::iterator &begin,
                                                    const std::vector<BitSet>::iterator &end) {
            std::wstring result;
            std::vector<BitSet>::iterator vectorIterator;
            
            for (vectorIterator = begin; vectorIterator != end; vectorIterator++) {
                result += vectorIterator->toString();
            }
            // Grab the end
            result += end->toString();
            
            return result;
        }
        std::wstring toString(){
            std::wstringstream stream;
            stream << L"{";
            size_t total = data.count();
            for (size_t i = 0; i < data.size(); i++){
                if (data.test(i)){
                    stream << i;
                    --total;
                    if (total > 1){
                        stream << L", ";
                    }
                }
            }
            
            stream << L"}";
            return stream.str();
        }
        
    };
}
