#include <iostream>
#include <fstream>
#include <sstream>
#include <cstring>

#include "ANTLRFileStream.h"
#include "Exceptions.h"

/*
 * [The "BSD license"]
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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {
                ANTLRFileStream::ANTLRFileStream(const std::string &fileName)  {
                }

                ANTLRFileStream::ANTLRFileStream(const std::string &fileName, const std::string &encoding) {
                    this->fileName = fileName;
                    load(fileName, encoding);
                }

                /*
                 Issue: is seems that file name in C++ are considered to be not
                 wide for the reason that not all systems support wchar file
                 names. Win32 is good about this but not all others. 
                 TODO: this could be a place to have platform specific build
                 flags
                 */
                void ANTLRFileStream::load(const std::string &fileName, const std::string &encoding) {
                    if (fileName == "") {
                        return;
                    }
                    
                    std::stringstream ss;
                    std::wifstream f;
                    
                    // Open as a byte stream
                    f.open(fileName, std::ios::binary);
                    ss<<f.rdbuf();
                    
                    std::string const &s = ss.str();
                    if (s.size() % sizeof(wchar_t) != 0)
                    {
                        throw new IOException(L"file not the right size");
                    }
                    
                    std::wstring ws;
                    ws.resize(s.size()/sizeof(wchar_t));
                    std::memcpy(&ws[0],s.c_str(),s.size()); // copy data into wstring
                    data=ws;
                }

                std::string ANTLRFileStream::getSourceName() {
                    return fileName;
                }
            }
        }
    }
}
