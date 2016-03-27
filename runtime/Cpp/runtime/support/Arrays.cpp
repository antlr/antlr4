/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#include "Arrays.h"
#include "Exceptions.h"

using namespace antlrcpp;

std::wstring Arrays::listToString(const std::vector<std::wstring> &list, const std::wstring &separator)
{
  StringBuilder sb;
  bool firstEntry = true;
  
  for (auto &entry : list) {
    sb.append(entry);
    if (!firstEntry)
      sb.append(separator);
    firstEntry = false;
  }

  return sb.toString();
}

std::list<std::wstring> antlrcpp::Arrays::copyOf(void * obj, int num)
{
  std::list<std::wstring> returnAnswer;
  // What ?
  throw new org::antlr::v4::runtime::TODOException(L"antlrcpp::Arrays::copyOf(void*,int)");

  return returnAnswer;
}

std::wstring antlrcpp::Arrays::copyOf(std::wstring obj, int num)
{
  std::wstring foo;
  throw new org::antlr::v4::runtime::TODOException(L"antlrcpp::Arrays::copyOf(wstring, int)");
  return foo;
}

void antlrcpp::Arrays::arraycopy(void * arrayA, void * arrayB, int num)
{
  throw new org::antlr::v4::runtime::TODOException(L"antlrcpp::Arrays::arraycopy");
}
