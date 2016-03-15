#pragma once

#include "CharStream.h"
#include "Declarations.h"
#include <string>
#include <stdexcept>
#include <fstream>
#include <algorithm>

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

                /// <summary>
                /// Do not buffer up the entire char stream. It does keep a small buffer
                ///  for efficiency and also buffers while a mark exists (set by the
                ///  lookahead prediction in parser). "Unbuffered" here refers to fact
                ///  that it doesn't buffer all data, not that's it's on demand loading of char.
                /// </summary>
                class UnbufferedCharStream : public CharStream {
                  protected:
                    /// <summary>
                    /// A moving window buffer of the data being scanned. While there's a marker,
                    /// we keep adding to buffer. Otherwise, <seealso cref="#consume consume()"/> resets so
                    /// we start filling at index 0 again.
                    /// </summary>
                    std::wstring data;

                    /// <summary>
                    /// The number of characters currently in <seealso cref="#data data"/>.
                    /// <p/>
                    /// This is not the buffer capacity, that's {@code data.length}.
                    /// </summary>
                       int n;

                    /// <summary>
                    /// 0..n-1 index into <seealso cref="#data data"/> of next character.
                    /// <p/>
                    /// The {@code LA(1)} character is {@code data[p]}. If {@code p == n}, we are
                    /// out of buffered characters.
                    /// </summary>
                       int p;

                    /// <summary>
                    /// Count up with <seealso cref="#mark mark()"/> and down with
                    /// <seealso cref="#release release()"/>. When we {@code release()} the last mark,
                    /// {@code numMarkers} reaches 0 and we reset the buffer. Copy
                    /// {@code data[p]..data[n-1]} to {@code data[0]..data[(n-1)-p]}.
                    /// </summary>
                    int numMarkers;

                    /// <summary>
                    /// This is the {@code LA(-1)} character for the current position.
                    /// </summary>
                    int lastChar;

                    /// <summary>
                    /// When {@code numMarkers > 0}, this is the {@code LA(-1)} character for the
                    /// first character in <seealso cref="#data data"/>. Otherwise, this is unspecified.
                    /// </summary>
                    int lastCharBufferStart;

                    /// <summary>
                    /// Absolute character index. It's the index of the character about to be
                    /// read via {@code LA(1)}. Goes from 0 to the number of characters in the
                    /// entire stream, although the stream size is unknown before the end is
                    /// reached.
                    /// </summary>
                    int currentCharIndex;

                    std::ifstream *input;

                    /// <summary>
                    /// The name or source of this char stream. </summary>
                public:
                    std::string name;

                    /// <summary>
                    /// Useful for subclasses that pull char from other than this.input. </summary>

                    UnbufferedCharStream(); //this(256);

                    /// <summary>
                    /// Useful for subclasses that pull char from other than this.input. </summary>
                    UnbufferedCharStream(int bufferSize);


                    UnbufferedCharStream(std::ifstream *input); //this(input, 256);

//JAVA TO C++ CONVERTER TODO TASK: Calls to same-class constructors are not supported in C++ prior to C++11:
					UnbufferedCharStream(std::ifstream *input, int bufferSize); //this(bufferSize);

                    virtual void consume() override;

                    /// <summary>
                    /// Make sure we have 'need' elements from current position <seealso cref="#p p"/>.
                    /// Last valid {@code p} index is {@code data.length-1}. {@code p+need-1} is
                    /// the char index 'need' elements ahead. If we need 1 element,
                    /// {@code (p+1-1)==p} must be less than {@code data.length}.
                    /// </summary>
                protected:
                    virtual void sync(int want);

                    /// <summary>
                    /// Add {@code n} characters to the buffer. Returns the number of characters
                    /// actually added to the buffer. If the return value is less than {@code n},
                    /// then EOF was reached before {@code n} characters could be added.
                    /// </summary>
                    virtual int fill(int n);

                    /// <summary>
                    /// Override to provide different source of characters than
                    /// <seealso cref="#input input"/>.
                    /// </summary>
                    virtual int nextChar();

                    virtual void add(int c);

                public:
                    virtual int LA(int i) override;

                    /// <summary>
                    /// Return a marker that we can release later.
                    /// <p/>
                    /// The specific marker value used for this class allows for some level of
                    /// protection against misuse where {@code seek()} is called on a mark or
                    /// {@code release()} is called in the wrong order.
                    /// </summary>
                    virtual int mark() override;

                    /// <summary>
                    /// Decrement number of markers, resetting buffer if we hit 0. </summary>
                    /// <param name="marker"> </param>
                    virtual void release(int marker) override;

                    virtual int index() override;

                    /// <summary>
                    /// Seek to absolute character index, which might not be in the current
                    ///  sliding window.  Move {@code p} to {@code index-bufferStartIndex}.
                    /// </summary>
                    virtual void seek(int index) override;

                    virtual size_t size() override;

                    virtual std::string getSourceName() override;

                    virtual std::wstring getText(misc::Interval *interval) override;

                protected:
                    int getBufferStartIndex();

                private:
                    void InitializeInstanceFields();
                };

            }
        }
    }
}
