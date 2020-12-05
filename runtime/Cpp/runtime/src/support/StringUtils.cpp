/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#include "support/StringUtils.h"
#ifdef USE_UTF8_INSTEAD_OF_CODECVT
#   include "utf8.h"
#else
#   include <codecvt>
#endif

namespace antlrcpp {

void replaceAll(std::string& str, std::string const& from, std::string const& to)
{
  if (from.empty())
    return;

  size_t start_pos = 0;
  while ((start_pos = str.find(from, start_pos)) != std::string::npos) {
    str.replace(start_pos, from.length(), to);
    start_pos += to.length(); // In case 'to' contains 'from', like replacing 'x' with 'yx'.
  }
}

std::string ws2s(std::wstring const& wstr) {
#ifndef USE_UTF8_INSTEAD_OF_CODECVT
  std::wstring_convert<std::codecvt_utf8_utf16<wchar_t>> converter;
  std::string narrow = converter.to_bytes(wstr);
#else
  std::string narrow;
  utf8::utf32to8(wstr.begin(), wstr.end(), std::back_inserter(narrow));
#endif

  return narrow;
}

std::wstring s2ws(const std::string &str) {
#ifndef USE_UTF8_INSTEAD_OF_CODECVT
  std::wstring_convert<std::codecvt_utf8_utf16<wchar_t>> converter;
  std::wstring wide = converter.from_bytes(str);
#else
  std::wstring wide;
  utf8::utf8to32(str.begin(), str.end(), std::back_inserter(wide));
#endif

  return wide;
}

// For all conversions utf8 <-> utf32.
// I wouldn't prefer wstring_convert because: according to
// https://en.cppreference.com/w/cpp/locale/wstring_convert,
// wstring_convert is deprecated in C++17.
// utfcpp (https://github.com/nemtrif/utfcpp) is a substitution.
#ifndef USE_UTF8_INSTEAD_OF_CODECVT
    // VS 2015 and VS 2017 have different bugs in std::codecvt_utf8<char32_t> (VS 2013 works fine).
#if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
    typedef std::wstring_convert<std::codecvt_utf8<__int32>, __int32> UTF32Converter;
#else
    typedef std::wstring_convert<std::codecvt_utf8<char32_t>, char32_t> UTF32Converter;
#endif
#endif

UTF32String utf8_to_utf32(const char* first, const char* last)
{
#ifndef USE_UTF8_INSTEAD_OF_CODECVT
    thread_local UTF32Converter converter;

  #if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
    auto r = converter.from_bytes(first, last);
    i32string s = reinterpret_cast<const int32_t *>(r.data());
    return s;
  #else
    std::u32string s = converter.from_bytes(first, last);
    return s;
  #endif
#else
    UTF32String wide;
    utf8::utf8to32(first, last, std::back_inserter(wide));
    return wide;
#endif
}

// The conversion functions fails in VS2017, so we explicitly use a workaround.
std::string utf32_to_utf8(const std::u32string &data)
{
#ifndef USE_UTF8_INSTEAD_OF_CODECVT
    // Don't make the converter static or we have to serialize access to it.
  thread_local UTF32Converter converter;

  #if defined(_MSC_VER) && _MSC_VER >= 1900 && _MSC_VER < 2000
    const auto p = reinterpret_cast<const int32_t *>(data.data());
    return converter.to_bytes(p, p + data.size());
  #else
    return converter.to_bytes(data);
  #endif
#else
    std::string narrow;
    utf8::utf32to8(data.begin(), data.end(), std::back_inserter(narrow));
    return narrow;
#endif
}

} // namespace antrlcpp
