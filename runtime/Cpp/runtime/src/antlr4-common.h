/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <algorithm>
#include <any>
#include <atomic>
#include <cassert>
#include <climits>
#include <cstddef>
#include <cstdint>
#include <cstdlib>
#include <exception>
#include <fstream>
#include <iostream>
#include <iterator>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <set>
#include <sstream>
#include <string>
#include <string_view>
#include <type_traits>
#include <typeinfo>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

// Defines for the Guid class and other platform dependent stuff.
#ifdef _WIN32
#ifdef _MSC_VER
#pragma warning(disable : 4250) // Class inherits by dominance.
#pragma warning(disable : 4512) // assignment operator could not be generated

#if _MSC_VER < 1900
// Before VS 2015 code like "while (true)" will create a (useless) warning in level 4.
#pragma warning(disable : 4127) // conditional expression is constant
#endif
#endif

#define GUID_WINDOWS

#ifdef ANTLR4CPP_EXPORTS
#define ANTLR4CPP_PUBLIC __declspec(dllexport)
#else
#ifdef ANTLR4CPP_STATIC
#define ANTLR4CPP_PUBLIC
#else
#define ANTLR4CPP_PUBLIC __declspec(dllimport)
#endif
#endif

#elif defined(__APPLE__)
#define GUID_CFUUID
#if __GNUC__ >= 4
#define ANTLR4CPP_PUBLIC __attribute__((visibility("default")))
#else
#define ANTLR4CPP_PUBLIC
#endif
#else
#define GUID_LIBUUID
#if __GNUC__ >= 6
#define ANTLR4CPP_PUBLIC __attribute__((visibility("default")))
#else
#define ANTLR4CPP_PUBLIC
#endif
#endif

#include "support/Declarations.h"
#include "support/Guid.h"

namespace antlr4 {

#undef INVALID_INDEX
  inline constexpr size_t INVALID_INDEX = std::numeric_limits<size_t>::max();

// We have to undefine this symbol as ANTLR will use this name for own members and even
// generated functions. Because EOF is a global macro we cannot use e.g. a namespace scope to
// disambiguate.
#undef EOF
  inline constexpr size_t EOF = std::numeric_limits<size_t>::max();

  template <typename T>
  using Ref = std::shared_ptr<T>;

  using ssize_t = std::make_signed_t<size_t>;

} // namespace antlr4
