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

#pragma once

#include <algorithm>
#include <assert.h>
#include <codecvt>
#include <chrono>
#include <fstream>
#include <iostream>
#include <iterator>
#include <limits.h>
#include <list>
#include <map>
#include <memory>
#include <set>
#include <stdarg.h>
#include <stdint.h>
#include <stdlib.h>
#include <sstream>
#include <stack>
#include <string>
#include <typeinfo>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>
#include <mutex>
#include <exception>
#include <bitset>

// Defines for the Guid class and other platform dependent stuff.
#ifdef _WIN32
  #pragma warning (disable: 4250) // Class inherits by dominance.

  #define GUID_WINDOWS

  #ifdef _WIN64
    typedef __int64 ssize_t;
  #else
    typedef int ssize_t;
  #endif

  #if _MSC_VER == 1900
    // VS 2015 has a known bug when using std::codecvt_utf8<char32_t>
    // so we have to temporarily use __int32 instead.
    typedef std::basic_string<__int32> i32string;
  #endif

  #ifdef _WINDLL
    #ifdef ANTLR4CPP_EXPORTS
      #define ANTLR4CPP_PUBLIC __declspec(dllexport)
      #define EXPIMP_TEMPLATE
    #else
      #define ANTLR4CPP_PUBLIC __declspec(dllimport)
      #define EXPIMP_TEMPLATE extern
    #endif
  #else
    #define ANTLR4CPP_PUBLIC
    #define EXPIMP_TEMPLATE
  #endif

  EXPIMP_TEMPLATE class ANTLR4CPP_PUBLIC std::exception; // Needed for VS 2015.

#elif __APPLE__
  #define GUID_CFUUID
  #define ANTLR4CPP_PUBLIC
  #define EXPIMP_TEMPLATE
#else
  #define GUID_LIBUUID
  #define ANTLR4CPP_PUBLIC
  #define EXPIMP_TEMPLATE
#endif

#include "support/guid.h"
#include "support/Declarations.h"

#if !defined(HAS_NOEXCEPT)
  #if defined(__clang__)
    #if __has_feature(cxx_noexcept)
      #define HAS_NOEXCEPT
    #endif
  #else
    #if defined(__GXX_EXPERIMENTAL_CXX0X__) && __GNUC__ * 10 + __GNUC_MINOR__ >= 46 || \
      defined(_MSC_FULL_VER) && _MSC_FULL_VER >= 190023026
      #define HAS_NOEXCEPT
    #endif
  #endif

  #ifdef HAS_NOEXCEPT
    #define NOEXCEPT noexcept
  #else
    #define NOEXCEPT
  #endif
#endif

// We have to undefine this symbol as ANTLR will use this name for own members and even
// generated functions. Because EOF is a global macro we cannot use e.g. a namespace scope to disambiguate.
#ifdef EOF
#undef EOF
#endif

template<class T> using Ref = std::shared_ptr<T>;
