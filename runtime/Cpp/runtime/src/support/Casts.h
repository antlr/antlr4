/* Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <cassert>
#include <type_traits>

namespace antlrcpp {

template <typename To, typename From>
To downCast(From* from) {
  static_assert(std::is_pointer<To>::value, "Target type not a pointer.");
  static_assert((std::is_base_of<From, typename std::remove_pointer<To>::type>::value), "Target type not derived from source type.");
#if !defined(__GNUC__) || defined(__GXX_RTTI)
  assert(from == nullptr || dynamic_cast<To>(from) != nullptr);
#endif
  return static_cast<To>(from);
}

template <typename To, typename From>
To downCast(From& from) {
  static_assert(std::is_lvalue_reference<To>::value, "Target type not a lvalue reference.");
  static_assert((std::is_base_of<From, typename std::remove_reference<To>::type>::value), "Target type not derived from source type.");
#if !defined(__GNUC__) || defined(__GXX_RTTI)
  assert(dynamic_cast<typename std::add_pointer<typename std::remove_reference<To>::type>::type>(&from) != nullptr);
#endif
  return static_cast<To>(from);
}

}
