/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"

namespace antlrcpp {
  std::string utf32_to_utf8(const UTF32String &data);
  UTF32String utf8_to_utf32(const char* first, const char* last);

  void replaceAll(std::string &str, std::string const& from, std::string const& to);

  // string <-> wstring conversion (UTF-16), e.g. for use with Window's wide APIs.
  ANTLR4CPP_PUBLIC std::string ws2s(std::wstring const& wstr);
  ANTLR4CPP_PUBLIC std::wstring s2ws(std::string const& str);
}
