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

  std::map<std::wstring, size_t> toMap(const std::vector<std::wstring> &keys) {
    std::map<std::wstring, size_t> result;
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
            result += (wchar_t)'0xB7';
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
  
  std::wstring replaceString(const std::wstring &s, const std::wstring &from, const std::wstring &to) {
    std::wstring::size_type p;
    std::wstring ss, res;

    ss = s;
    p = ss.find(from);
    while (p != std::wstring::npos)
    {
      if (p > 0)
        res.append(ss.substr(0, p)).append(to);
      else
        res.append(to);
      ss = ss.substr(p + from.size());
      p = ss.find(from);
    }
    res.append(ss);

    return res;
  }

  //--------------------------------------------------------------------------------------------------
  
  std::vector<std::wstring> split(const std::wstring &s, const std::wstring &sep, int count) {
    std::vector<std::wstring> parts;
    std::wstring ss = s;

    std::wstring::size_type p;

    if (s.empty())
      return parts;

    if (count == 0)
      count= -1;

    p = ss.find(sep);
    while (!ss.empty() && p != std::string::npos && (count < 0 || count > 0))
    {
      parts.push_back(ss.substr(0, p));
      ss= ss.substr(p+sep.size());

      --count;
      p= ss.find(sep);
    }
    parts.push_back(ss);

    return parts;
  }

  //--------------------------------------------------------------------------------------------------

  // Debugging helper. Adds indentation to all lines in the given string.
  std::wstring indent(const std::wstring &s, const std::wstring &indentation, bool includingFirst) {
    std::vector<std::wstring> parts = split(s, L"\n", -1);
    for (size_t i = 0; i < parts.size(); ++i) {
      if (i == 0 && !includingFirst)
        continue;
      parts[i].insert(0, indentation);
    }

    return join(parts, L"\n");
  }
} // namespace antlrcpp
