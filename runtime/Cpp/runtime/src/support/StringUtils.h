/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"

namespace antlrcpp {
  // For all conversions utf8 <-> utf32.
  // VS 2015 and VS 2017 have different bugs in std::codecvt_utf8<char32_t> (VS 2013 works fine).
#if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
  static std::wstring_convert<std::codecvt_utf8<__int32>, __int32> utfConverter;
#else
  static std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> utfConverter;
#endif

  //the conversion functions fails in VS2017, so we explicitly use a workaround
  template<typename T>
  inline std::string utf32_to_utf8(T _data)
  {
    #if defined(_MSC_VER) && _MSC_VER > 1900 && _MSC_VER < 2000
      auto p = reinterpret_cast<const int32_t *>(_data.data());
      return antlrcpp::utfConverter.to_bytes(p, p + _data.size());
    #else
      return antlrcpp::utfConverter.to_bytes(_data);
    #endif
  }

  inline auto utf8_to_utf32(const char* first, const char* last)
  {
    #if defined(_MSC_VER) && _MSC_VER > 1900 && _MSC_VER < 2000
      auto r = antlrcpp::utfConverter.from_bytes(first, last);
      std::u32string s = reinterpret_cast<const char32_t *>(r.data());
      return s;
    #else
      return antlrcpp::utfConverter.from_bytes(first, last);
    #endif
  }

  void replaceAll(std::string& str, const std::string& from, const std::string& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  ANTLR4CPP_PUBLIC std::string ws2s(const std::wstring &wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(const std::string &str);
}
