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

#include "support/CPPUtils.h"

namespace antlrcpp {

  std::string join(std::vector<std::string> strings, const std::string &separator) {
    std::string str;
    bool firstItem = true;
    for (std::string s : strings) {
      if (!firstItem) {
        str.append(separator);
      }
      firstItem = false;
      str.append(s);
    }
    return str;
  }

  std::map<std::string, size_t> toMap(const std::vector<std::string> &keys) {
    std::map<std::string, size_t> result;
    for (size_t i = 0; i < keys.size(); ++i) {
      result.insert({ keys[i], i });
    }
    return result;
  }

  std::string escapeWhitespace(std::string str, bool escapeSpaces) {
    std::string result;
    for (auto c : str) {
      switch (c) {
        case ' ':
          if (escapeSpaces) {
            result += (char)'0xB7';
            break;
          } else {
            // fall through
          }

        case '\n':
          result += "\\n";
          break;

        case '\r':
          result += "\\r";
          break;

        case '\t':
          result += "\\t";
          break;

        default:
          result += c;
      }
    }

    return result;
  }

  std::string toHexString(const int t){
    std::stringstream stream;
    stream << std::uppercase << std::hex << t;
    return stream.str();
  }

  std::string arrayToString(const std::vector<std::string> &data) {
    std::string answer;
    for (auto sub: data) {
      answer += sub;
    }
    return answer;
  }
  
  std::string replaceString(const std::string &s, const std::string &from, const std::string &to) {
    std::string::size_type p;
    std::string ss, res;

    ss = s;
    p = ss.find(from);
    while (p != std::string::npos)
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
  
  std::vector<std::string> split(const std::string &s, const std::string &sep, int count) {
    std::vector<std::string> parts;
    std::string ss = s;

    std::string::size_type p;

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
  std::string indent(const std::string &s, const std::string &indentation, bool includingFirst) {
    std::vector<std::string> parts = split(s, "\n", -1);
    for (size_t i = 0; i < parts.size(); ++i) {
      if (i == 0 && !includingFirst)
        continue;
      parts[i].insert(0, indentation);
    }

    return join(parts, "\n");
  }

  //--------------------------------------------------------------------------------------------------

  // Recursively get the error from a, possibly nested, exception.
  template <typename T>
  std::exception_ptr get_nested(const T &e)
  {
    try
    {
#if defined(_MSC_FULL_VER) && _MSC_FULL_VER < 190023026
      return nullptr; // No nested exceptions before VS 2015.
#else
      auto nested = dynamic_cast<const std::nested_exception&>(e);
      return nested.nested_ptr();
#endif
    }
    catch (const std::bad_cast &)
    { return nullptr; }
  }

  std::string what(std::exception_ptr eptr)
  {
    if (!eptr) {
      throw std::bad_exception();
    }

    std::string result;
    std::size_t nestCount = 0;

  next:
    {
      try
      {
        std::exception_ptr yeptr;
        std::swap(eptr, yeptr);
        std::rethrow_exception(yeptr);
      }
      catch (const std::exception &e) {
        result += e.what();
        eptr = get_nested(e);
      }
      catch (const std::string &e) {
        result += e;
      }
      catch (const char *e) {
        result += e;
      }
      catch (...) {
        result += "cannot be determined";
      }

      if (eptr) {
        result += " (";
        ++nestCount;
        goto next;
      }
    }
    result += std::string(nestCount, ')');
    return result;
  }

  FinalAction finally(std::function<void ()> f) {
    return FinalAction(f);
  }

} // namespace antlrcpp
