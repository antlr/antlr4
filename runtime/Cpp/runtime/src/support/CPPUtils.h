/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Dan McLaughlin
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

  std::string join(std::vector<std::string> strings, const std::string &separator);
  std::map<std::string, size_t> toMap(const std::vector<std::string> &keys);
  std::string escapeWhitespace(std::string str, bool escapeSpaces);
  std::string toHexString(const int t);
  std::string arrayToString(const std::vector<std::string> &data);
  std::string replaceString(const std::string &s, const std::string &from, const std::string &to);
  std::vector<std::string> split(const std::string &s, const std::string &sep, int count);
  std::string indent(const std::string &s, const std::string &indentation, bool includingFirst = true);

  // Using RAII + a lambda to implement a "finally" replacement.
  struct FinalAction {
    FinalAction(std::function<void ()> f) : _cleanUp { f } {}
    FinalAction(FinalAction &&other) {
      _cleanUp = other._cleanUp;
      _enabled = other._enabled;
      other._enabled = false; // Don't trigger the lambda after ownership has moved.
    }
    ~FinalAction() { if (_enabled) _cleanUp(); }

    void disable() { _enabled = false; };
  private:
    std::function<void ()> _cleanUp;
    bool _enabled {true};
  };

  ANTLR4CPP_PUBLIC FinalAction finally(std::function<void ()> f);

  // Convenience functions to avoid lengthy dynamic_cast() != nullptr checks in many places.
  template <typename T1, typename T2>
  inline bool is(T2 *obj) { // For pointer types.
    return dynamic_cast<typename std::add_const<T1>::type>(obj) != nullptr;
  }

  template <typename T1, typename T2>
  inline bool is(Ref<T2> const& obj) { // For shared pointers.
    return dynamic_cast<T1 *>(obj.get()) != nullptr;
  }

  template <typename T>
  std::string toString(const T &o) {
    std::stringstream ss;
    // typeid gives the mangled class name, but that's all what's possible
    // in a portable way.
    ss << typeid(o).name() << "@" << std::hex << (size_t)&o;
    return ss.str();
  }

  // Get the error text from an exception pointer or the current exception.
  std::string what(std::exception_ptr eptr = std::current_exception());

} // namespace antlrcpp

namespace std {
  // Comparing weak and shared pointers.
  template <typename T>
  bool operator == (const std::weak_ptr<T> &lhs, const std::weak_ptr<T> &rhs) {
    if (lhs.expired() && rhs.expired())
      return true;

    if (lhs.expired() || rhs.expired())
      return false;

    return (lhs.lock() == rhs.lock());
  }

  template <typename T>
  bool operator == (const Ref<T> &lhs, const Ref<T> &rhs) {
    if (!lhs && !rhs)
      return true;

    if (!lhs || !rhs)
      return false;

    return (*lhs == *rhs);
  }
} // namespace std
