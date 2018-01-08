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
  typedef std::wstring_convert<std::codecvt_utf8<__int32>, __int32> UTF32Converter;
#else
  typedef std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> UTF32Converter;
#endif
  
  // The conversion functions fails in VS2017, so we explicitly use a workaround.
  template<typename T>
  inline std::string utf32_to_utf8(T const& data)
  {
    // Don't make the converter static or we have to serialize access to it.
    UTF32Converter converter;

    #if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
      auto p = reinterpret_cast<const int32_t *>(data.data());
      return converter.to_bytes(p, p + data.size());
    #else
      return converter.to_bytes(data);
    #endif
  }

  inline UTF32String utf8_to_utf32(const char* first, const char* last)
  {
    UTF32Converter converter;

    #if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
      auto r = converter.from_bytes(first, last);
      i32string s = reinterpret_cast<const int32_t *>(r.data());
    #else
      std::u32string s = converter.from_bytes(first, last);
    #endif
    
    return s;
  }

  void replaceAll(std::string &str, std::string const& from, std::string const& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  ANTLR4CPP_PUBLIC std::string ws2s(std::wstring const& wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(std::string const& str);
}
