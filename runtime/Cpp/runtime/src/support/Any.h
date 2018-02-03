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

  Any() : _ptr(nullptr) {
  }

  Any(Any& that) : _ptr(that.clone()) {
  }

  Any(Any&& that) : _ptr(that._ptr) {
    that._ptr = nullptr;
  }

  Any(const Any& that) : _ptr(that.clone()) {
  }

  Any(const Any&& that) : _ptr(that.clone()) {
  }

  template<typename U>
  Any(U&& value) : _ptr(new Derived<StorageType<U>, Cloneable<StorageType<U>>::value>(std::forward<U>(value))) {
  }

  template<class U>
  bool is() const {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T, Cloneable<T>::value> *>(_ptr);

    return derived != nullptr;
  }

  template<class U>
  StorageType<U>& as() {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T, Cloneable<T>::value>*>(_ptr);

    if (!derived)
      throw std::bad_cast();

    return derived->value;
  }

  template<class U>
  operator U() {
    return as<StorageType<U>>();
  }

  Any& operator = (const Any& a) {
    if (_ptr == a._ptr)
      return *this;

    auto old_ptr = _ptr;
    _ptr = a.clone();

    if (old_ptr)
      delete old_ptr;

    return *this;
  }

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
  template<typename T>
  struct Cloneable : std::is_copy_constructible<T> {};
  template<typename T, typename A>
  struct Cloneable<std::vector<T, A>> : std::is_copy_constructible<T> {};

  struct Base {
    virtual ~Base() {};
    virtual Base* clone() const = 0;
  };

  template<typename T, bool Cloneable>
  struct Derived;

  template<typename T>
  struct Derived<T, true> : Base
  {
    template<typename U> Derived(U&& value_) : value(std::forward<U>(value_)) {
    }

    T value;

    Base* clone() const {
      return new Derived<T, Cloneable<T>::value>(value);
    }

  };

  template<typename T>
  struct Derived<T, false> : Base
  {
    template<typename U> Derived(U&& value_) : value(std::forward<U>(value_)) {
    }

    T value;

    Base* clone() const {
      return nullptr;
    }

  };

  Base* clone() const
  {
    if (_ptr)
      return _ptr->clone();
    else
      return nullptr;
  }

  Base *_ptr;

};

  template<> inline
  Any::Any(std::nullptr_t&& ) : _ptr(nullptr) {
  }


} // namespace antlrcpp

#ifdef _MSC_VER
#pragma warning(pop)
#endif
