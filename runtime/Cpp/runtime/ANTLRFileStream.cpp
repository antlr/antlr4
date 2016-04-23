/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Dan McLaughlin
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

#include "Exceptions.h"
#include "Strings.h"

#include "ANTLRFileStream.h"

using namespace org::antlr::v4::runtime;

ANTLRFileStream::ANTLRFileStream(const std::string &fileName, const std::string &encoding) {
  _fileName = fileName;
  load(fileName, encoding);
}

void ANTLRFileStream::load(const std::string &fileName, const std::string &encoding) {
  if (_fileName.empty()) {
    return;
  }

  enum Encoding { ANSI, UTF8, UTF16LE } encodingType = ANSI;

#ifdef _WIN32
  std::ifstream stream(antlrcpp::s2ws(fileName).c_str(), std::ios::binary);
#else
  std::ifstream stream(_fileName.c_str(), std::ifstream::binary);
#endif

  std::stringstream ss;

  if (!stream.is_open() || stream.eof())
    return;

  int ch1 = stream.get();
  int ch2 = stream.get();
  if (ch1 == 0xff && ch2 == 0xfe)
    encodingType = UTF16LE;
  else
    if (ch1 == 0xfe && ch2 == 0xff)
      return; // UTF-16BE not supported;
    else
    {
      int ch3 = stream.get();
      if (ch1 == 0xef && ch2 == 0xbb && ch3 == 0xbf)
        encodingType = UTF8;
      else
        stream.seekg(0);
    }

  ss << stream.rdbuf() << '\0';
  switch (encodingType)
  {
    case UTF16LE:
      data = (wchar_t *)ss.str().c_str();
    default:
      data = antlrcpp::s2ws(ss.str());
  }
}

std::string ANTLRFileStream::getSourceName() const {
  return _fileName;
}
