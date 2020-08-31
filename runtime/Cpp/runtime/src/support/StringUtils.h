/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"
#include "utf8.h"

namespace antlrcpp {

  // I wouldn't prefer wstring_convert for two reasons:
  // 1. According to https://en.cppreference.com/w/cpp/locale/wstring_convert,
  //    wstring_convert is deprecated in C++17.
  // 2. GCC 4.9 doesn't support codecvt header. And many projects still use
  //    GCC 4.9 as compiler.
  // utfcpp (https://github.com/nemtrif/utfcpp) is a substitution.

  // The conversion functions fails in VS2017, so we explicitly use a workaround.
  template<typename T>
  inline std::string utf32_to_utf8(T const& data)
  {
    std::string narrow;
    utf8::utf32to8(data.begin(), data.end(), std::back_inserter(narrow));

    return narrow;
  }

  inline UTF32String utf8_to_utf32(const char* first, const char* last)
  {
    UTF32String wide;
    utf8::utf8to32(first, last, std::back_inserter(wide));

    return wide;
  }

  void replaceAll(std::string &str, std::string const& from, std::string const& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  ANTLR4CPP_PUBLIC std::string ws2s(std::wstring const& wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(std::string const& str);
}
