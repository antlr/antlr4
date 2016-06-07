/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#pragma once

#include "tree/Tree.h"

namespace antlrcpp {
  
  class ANTLR4CPP_PUBLIC Arrays {
  public:

    static std::string listToString(const std::vector<std::string> &list, const std::string &separator);

    template <typename T>
    static bool equals(const std::vector<T> &a, const std::vector<T> &b) {
      if (a.size() != b.size())
        return false;

      for (size_t i = 0; i < a.size(); ++i)
        if (a[i] != b[i]) // Requires that the != operator is supported by the template type.
          return false;
      
      return true;
    }

    template <typename T>
    static bool equals(const std::vector<T *> &a, const std::vector<T *> &b) {
      if (a.size() != b.size())
        return false;

      for (size_t i = 0; i < a.size(); ++i)
        if (*a[i] != *b[i])
          return false;

      return true;
    }
    
    template <typename T>
    static bool equals(const std::vector<std::weak_ptr<T>> &a, const std::vector<std::weak_ptr<T>> &b) {
      if (a.size() != b.size())
        return false;

      for (size_t i = 0; i < a.size(); ++i)
        if (*a[i].lock() != *b[i].lock())
          return false;

      return true;
    }
    /*
    template <typename T>
    static std::vector<T> copyOf(const std::vector<T> &source, size_t count) {
      std::vector<T> result(std::min(source.size(), count));
      std::copy(source.begin(), source.end(), result.begin());
      return result;
    }
    */

    template <typename T>
    static std::string toString(const std::vector<T> &source) {
      std::string result = "[";
      bool firstEntry = true;
      for (auto &value : source) {
        result += value.toString();
        if (firstEntry) {
          result += ", ";
          firstEntry = false;
        }
      }
      return result + "]";
    }

    template <typename T>
    static std::string toString(const std::vector<Ref<T>> &source) {
      std::string result = "[";
      bool firstEntry = true;
      for (auto &value : source) {
        result += value->toString();
        if (firstEntry) {
          result += ", ";
          firstEntry = false;
        }
      }
      return result + "]";
    }

    template <typename T>
    static std::string toString(const std::vector<T*> &source) {
      std::string result = "[";
      bool firstEntry = true;
      for (auto value : source) {
        result += value->toString();
        if (firstEntry) {
          result += ", ";
          firstEntry = false;
        }
      }
      return result + "]";
    }

  };

  template <>
  std::string Arrays::toString(const std::vector<antlr4::tree::Tree*> &source);
}
