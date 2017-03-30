/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"

namespace antlrcpp {
  // For all conversions utf8 <-> utf32.
  // VS 2015 has a bug in std::codecvt_utf8<char32_t> (VS 2013 works fine).
#if defined(_MSC_VER) && _MSC_VER == 1900
  static std::wstring_convert<std::codecvt_utf8<__int32>, __int32> utfConverter;
#else
  static std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> utfConverter;
#endif

  void replaceAll(std::string& str, const std::string& from, const std::string& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  ANTLR4CPP_PUBLIC std::string ws2s(const std::wstring &wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(const std::string &str);
}
