/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"

namespace antlrcpp {

  // For all conversions utf8 <-> utf32.
  // I wouldn't prefer wstring_convert because: according to
  // https://en.cppreference.com/w/cpp/locale/wstring_convert,
  // wstring_convert is deprecated in C++17.
  // utfcpp (https://github.com/nemtrif/utfcpp) is a substitution.
#ifdef USE_CODECVT_INSTEAD_OF_UTF8
  // VS 2015 and VS 2017 have different bugs in std::codecvt_utf8<char32_t> (VS 2013 works fine).
  #if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
    typedef std::wstring_convert<std::codecvt_utf8<__int32>, __int32> UTF32Converter;
  #else
    typedef std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> UTF32Converter;
  #endif
#endif

  void replaceAll(std::string &str, std::string const& from, std::string const& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  std::string utf32_to_utf8(UTF32String const& data);
  UTF32String utf8_to_utf32(const char* first, const char* last);
  ANTLR4CPP_PUBLIC std::string ws2s(std::wstring const& wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(std::string const& str);
}
