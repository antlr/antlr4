/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

#include "antlr4-common.h"

namespace antlrcpp {

  class ANTLR4CPP_PUBLIC BitSet : public std::bitset<1024> {
  public:
    int nextSetBit(size_t pos) const {
      for (size_t i = pos; i < size(); i++){
        if (test(i)) {
          return (int)i;
        }
      }

      return -1;
    }

    // Prints a list of every index for which the bitset contains a bit in true.
    friend std::wostream& operator << (std::wostream& os, const BitSet& obj)
    {
      os << "{";
      size_t total = obj.count();
      for (size_t i = 0; i < obj.size(); i++){
        if (obj.test(i)){
          os << i;
          --total;
          if (total > 1){
            os << ", ";
          }
        }
      }

      os << "}";
      return os;
    }

    static std::string subStringRepresentation(const std::vector<BitSet>::iterator &begin,
                                                const std::vector<BitSet>::iterator &end) {
      std::string result;
      std::vector<BitSet>::iterator vectorIterator;

      for (vectorIterator = begin; vectorIterator != end; vectorIterator++) {
        result += vectorIterator->toString();
      }
      // Grab the end
      result += end->toString();

      return result;
    }

    std::string toString(){
      std::stringstream stream;
      stream << "{";
      bool valueAdded = false;
      for (size_t i = 0; i < size(); ++i){
        if (test(i)){
          if (valueAdded) {
            stream << ", ";
          }
          stream << i;
          valueAdded = true;
        }
      }

      stream << "}";
      return stream.str();
    }

  };
}
