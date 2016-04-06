/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Dan McLaughlin
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

#include "CPPUtils.h"

namespace antlrcpp {

std::wstring join(std::vector<std::wstring> strings, const std::wstring &separator) {
  std::wstring str;
  bool firstItem = true;
  for (std::wstring s : strings) {
    if (!firstItem) {
      str.append(separator);
    }
    firstItem = false;
    str.append(s);
  }
  return str;
}

std::map<std::wstring, int> toMap(const std::vector<std::wstring> &keys) {
  std::map<std::wstring, int> result;
  for (size_t i = 0; i < keys.size(); ++i) {
    result.insert({ keys[i], i });
  }
  return result;
}

std::wstring escapeWhitespace(std::wstring str, bool escapeSpaces) {
  std::wstring result;
  for (auto c : str) {
    switch (c) {
      case L' ':
        if (escapeSpaces) {
          result += '0xB7';
          break;
        } else {
          // fall through
        }

      case L'\n':
        result += L"\\n";
        break;

      case L'\r':
        result += L"\\r";
        break;

      case L'\t':
        result += L"\\t";
        break;

      default:
        result += c;
    }
  }

  return result;
}

std::wstring toHexString(const int t){
		std::wstringstream stream;
		stream << std::uppercase << std::hex << t;
		return stream.str();
}

std::wstring arrayToString(const std::vector<std::wstring> &data) {
  std::wstring answer;
  for (auto sub: data) {
    answer += sub;
  }
  return answer;
}

} // namespace antlrcpp