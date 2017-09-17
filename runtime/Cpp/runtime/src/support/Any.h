/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

// A standard C++ class loosely modeled after boost::Any.

#pragma once

#include "antlr4-common.h"

#ifdef _MSC_VER
  #pragma warning(push)
  #pragma warning(disable: 4521) // 'antlrcpp::Any': multiple copy constructors specified
#endif

namespace antlrcpp {

template<class T>
  using StorageType = typename std::decay<T>::type;

struct ANTLR4CPP_PUBLIC Any
{
  bool isNull() const { return _ptr == nullptr; }
  bool isNotNull() const { return _ptr != nullptr; }

  Any() = delete;

  Any(Any& that) = delete;

  Any(Any&& that) : _ptr(that._ptr) {
    that._ptr = nullptr;
  }

  Any(const Any& that) = delete;

  Any(const Any&& that) = delete;

  template<typename U>
  Any(U&& value) : _ptr(new Derived<StorageType<U>>(std::forward<U>(value))) {
  }

  template<class U>
  bool is() const {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T> *>(_ptr);

    return derived != nullptr;
  }

  template<class U>
  StorageType<U>& as() {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T>*>(_ptr);

    if (!derived)
      throw std::bad_cast();

    return derived->value;
  }

  template<class U>
  operator U() {
    return std::move(as<StorageType<U>>());
  }

  Any& operator = (const Any& a) = delete;

  Any& operator = (Any&& a) {
    if (_ptr == a._ptr)
      return *this;

    std::swap(_ptr, a._ptr);

    return *this;
  }

  virtual ~Any();

  virtual bool equals(Any other) const {
    return _ptr == other._ptr;
  }

private:
  struct Base {
    virtual ~Base();
  };

  template<typename T>
  struct Derived : Base
  {
    template<typename U> Derived(U&& value_) : value(std::forward<U>(value_)) {
    }

    T value;

  };

  Base *_ptr;

};

  template<> inline
  Any::Any(std::nullptr_t&& ) : _ptr(nullptr) {
  }


} // namespace antlrcpp

#ifdef _MSC_VER
#pragma warning(pop)
#endif
