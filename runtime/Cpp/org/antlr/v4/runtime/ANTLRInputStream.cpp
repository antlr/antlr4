#include "ANTLRInputStream.h"
#include "Exceptions.h"
#include "Interval.h"
#include "assert.h"
#include "Arrays.h"

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
                using misc::Interval;

                ANTLRInputStream::ANTLRInputStream() {
                    InitializeInstanceFields();
                }

                ANTLRInputStream::ANTLRInputStream(const std::wstring &input) {
                    InitializeInstanceFields();
                    this->data = input;
                    this->n = (int)input.length();
                }

                ANTLRInputStream::ANTLRInputStream(wchar_t data[], int numberOfActualCharsInArray) {
                    InitializeInstanceFields();
                    this->data = data;
                    this->n = numberOfActualCharsInArray;
                }


                ANTLRInputStream::ANTLRInputStream(std::wifstream *r) {
                }


                ANTLRInputStream::ANTLRInputStream(std::wifstream *r, int initialSize)  {
                }

                ANTLRInputStream::ANTLRInputStream(std::wifstream *r, int initialSize, int readChunkSize) {
                    InitializeInstanceFields();
                    load(r, initialSize, readChunkSize);
                }


                void ANTLRInputStream::load(std::wifstream *r, int size, int readChunkSize) {
                    if (r == nullptr) {
                        return;
                    }
                    if (size <= 0) {
                        size = INITIAL_BUFFER_SIZE;
                    }
                    if (readChunkSize <= 0) {
                        readChunkSize = READ_BUFFER_SIZE;
                    }
                       // System.out.println("load "+size+" in chunks of "+readChunkSize);
                       try {
                           // alloc initial buffer size.
                           data = new wchar_t[size];
                           // read all the data in chunks of readChunkSize
                           int numRead = 0;
                           int p = 0;
                           do {
			      if (p + readChunkSize > (int)data.length()) { // overflow?
                                   // System.out.println("### overflow p="+p+", data.length="+data.length);
                                   data = antlrcpp::Arrays::copyOf(data, (int)data.length() * 2);
                               }
                               r->read(new wchar_t[100], p);

                               // System.out.println("read "+numRead+" chars; p was "+p+" is now "+(p+numRead));
                               p += numRead;
                           } while (numRead != -1); // while not EOF
                           // set the actual size of the data available;
                           // EOF subtracted one above in p+=numRead; add one back
                           n = p + 1;
                           //System.out.println("n="+n);
                       }
                       catch (void *){
                          r->close();
                       }
    
                }

                void ANTLRInputStream::reset() {
                    p = 0;
                }

                void ANTLRInputStream::consume() {
                    if (p >= n) {
                        assert(LA(1) == IntStream::_EOF);
                        throw IllegalStateException(L"cannot consume EOF");
                    }

                    //System.out.println("prev p="+p+", c="+(char)data[p]);
                    if (p < n) {
                        p++;
                        //System.out.println("p moves to "+p+" (c='"+(char)data[p]+"')");
                    }
                }

                int ANTLRInputStream::LA(int i) {
                    if (i == 0) {
                        return 0; // undefined
                    }
                    if (i < 0) {
                        i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
                        if ((p + i - 1) < 0) {
                            return IntStream::_EOF; // invalid; no char before first char
                        }
                    }

                    if ((p + i - 1) >= n) {
                        //System.out.println("char LA("+i+")=EOF; p="+p);
                        return IntStream::_EOF;
                    }
                    //System.out.println("char LA("+i+")="+(char)data[p+i-1]+"; p="+p);
                    //System.out.println("LA("+i+"); p="+p+" n="+n+" data.length="+data.length);
                    return data[p + i - 1];
                }

                int ANTLRInputStream::LT(int i) {
                    return LA(i);
                }

                int ANTLRInputStream::index() {
                    return p;
                }

                size_t ANTLRInputStream::size() {
                    return n;
                }

                int ANTLRInputStream::mark() {
                    return -1;
                }

                void ANTLRInputStream::release(int marker) {
                }

                void ANTLRInputStream::seek(int index) {
                    if (index <= p) {
                        p = index; // just jump; don't update stream state (line, ...)
                        return;
                    }
                    // seek forward, consume until p hits index
                    while (p < index && index < n) {
                        consume();
                    }
                }

                std::wstring ANTLRInputStream::getText(Interval *interval) {
                    int start = interval->a;
                    int stop = interval->b;
                    if (stop >= n) {
                        stop = n - 1;
                    }
                    int count = stop - start + 1;
                    if (start >= n) {
                        return L"";
                    }
                                //		System.err.println("data: "+Arrays.toString(data)+", n="+n+
                                //						   ", start="+start+
                                //						   ", stop="+stop);
                    return std::wstring(data, start, count);
                }

                std::string ANTLRInputStream::getSourceName() {
                    return name;
                }

                std::wstring ANTLRInputStream::toString() {
                    return std::wstring(data);
                }

                void ANTLRInputStream::InitializeInstanceFields() {
                    n = 0;
                    p = 0;
                }
            }
        }
    }
}
