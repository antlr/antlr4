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

struct Object
{
  virtual ~Object();
};
struct ANTLR4CPP_PUBLIC Any final
{
  bool isNull() const { return _ptr == nullptr; }
  bool isNotNull() const { return _ptr != nullptr; }

  Any() = delete;
  Any(const Any &) = delete;
  Any(Object *ptr) : _ptr(ptr) {
  }
  Any(Any&& that) : _ptr(that._ptr) {
    that._ptr = nullptr;
  }

  template<class U>
  bool is() const {
    auto derived = dynamic_cast<U>(_ptr);

    return derived != nullptr;
  }

  template<class U>
  U as() const {
    auto object = dynamic_cast<U>(_ptr);
    if (!object)
        throw std::bad_cast();
    return object;
  }

  template<class U>
  U get() {
    auto object = dynamic_cast<U>(_ptr);
    if (!object)
        throw std::bad_cast();
    _ptr = nullptr;

    return object;
  }
  Object *object() {
    return _ptr;
  }
  const Object *object() const {
    return _ptr;
  }

  Any& operator = (const Any &a) = delete;
  Any& operator = (Any&& a) {
    if (_ptr == a._ptr)
      return *this;

    std::swap(_ptr, a._ptr);

    return *this;
  }

  ~Any();

  bool equals(const Any &other) const {
    return _ptr == other._ptr;
  }

private:
  Object *_ptr;

};

} // namespace antlrcpp

#ifdef _MSC_VER
#pragma warning(pop)
#endif
