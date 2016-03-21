/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

// A standard C++ class loosely modeled after boost::Any.

#pragma once

#include <type_traits>

#include "MurmurHash.h"

using namespace std;
using namespace org::antlr::v4::runtime;

template<class T>
using StorageType = typename decay<typename remove_reference<T>::type>::type;

struct Any
{
  bool isNull() const { return !_ptr; }
  bool isNotNull() const { return _ptr; }

  Any() : _ptr(nullptr) {
    updateHashCode();
  }

  Any(Any& that) : _ptr(that.clone()) {
    updateHashCode();
  }

  Any(Any&& that) : _ptr(that._ptr) {
    that._ptr = nullptr;
    updateHashCode();
  }

  Any(const Any& that) : _ptr(that.clone()) {
    updateHashCode();
  }

  Any(const Any&& that) : _ptr(that.clone()) {
    updateHashCode();
  }
  
  template<typename U> Any(U&& value) : _ptr(new Derived<StorageType<U>>(forward<U>(value))) {
    updateHashCode();
  }

  template<class U> bool is() const {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T> *>(_ptr);

    return derived;
  }

  template<class U>
  StorageType<U>& as() {
    typedef StorageType<U> T;

    auto derived = dynamic_cast<Derived<T>*>(_ptr);

    if (!derived)
      throw bad_cast();

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
    updateHashCode();

    if (old_ptr)
      delete old_ptr;

    return *this;
  }

  Any& operator = (Any&& a) {
    if (_ptr == a._ptr)
      return *this;

    swap(_ptr, a._ptr);
    updateHashCode();

    return *this;
  }

  ~Any() {
    delete _ptr;
  }

  virtual int hashCode() const {
    return _hashCode;
  }

  virtual bool equals(Any other) const {
    return _ptr == other._ptr;
  }

private:
  int _hashCode;

  void updateHashCode() {
    _hashCode = misc::MurmurHash::initialize(7);
    _hashCode = misc::MurmurHash::update(_hashCode, _ptr);
    _hashCode = misc::MurmurHash::finish(_hashCode, 4);
  }

  struct Base {
    virtual ~Base() { }
    virtual Base* clone() const = 0;
  };

  template<typename T>
  struct Derived : Base
  {
    template<typename U> Derived(U&& value) : value(forward<U>(value)) {
    }

    T value;

    Base* clone() const {
      return new Derived<T>(value);
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

  friend struct hash<Base>;
};

template <>
struct hash<Any::Base>
{
  std::size_t operator()(const Any::Base &k) const {
    return (size_t)&k;
  }
};
