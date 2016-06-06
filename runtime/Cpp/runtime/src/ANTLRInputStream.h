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

#pragma once

#include "CharStream.h"

namespace antlr4 {

  // Vacuum all input from a stream and then treat it
  // like a string. Can also pass in a string or char[] to use.
  // Input is expected to be encoded in UTF-8 and converted to UTF-32 internally.
  class ANTLR4CPP_PUBLIC ANTLRInputStream : public CharStream {
  protected:
    /// The data being scanned.
    std::u32string data; // UTF-32

    /// 0..n-1 index into string of next char </summary>
    size_t p;

  public:
    /// What is name or source of this char stream?
    std::string name;

    ANTLRInputStream(const std::string &input = "");
    ANTLRInputStream(const char data[], size_t numberOfActualCharsInArray);
    ANTLRInputStream(std::wistream &stream);

    virtual void load(const std::string &input);
    virtual void load(std::wistream &stream);

    /// Reset the stream so that it's in the same state it was
    /// when the object was created *except* the data array is not
    /// touched.
    virtual void reset();
    virtual void consume() override;
    virtual ssize_t LA(ssize_t i) override;
    virtual ssize_t LT(ssize_t i);

    /// <summary>
    /// Return the current input symbol index 0..n where n indicates the
    ///  last symbol has been read.  The index is the index of char to
    ///  be returned from LA(1).
    /// </summary>
    virtual size_t index() override;
    virtual size_t size() override;

    /// <summary>
    /// mark/release do nothing; we have entire buffer </summary>
    virtual ssize_t mark() override;
    virtual void release(ssize_t marker) override;

    /// <summary>
    /// consume() ahead until p==index; can't just set p=index as we must
    ///  update line and charPositionInLine. If we seek backwards, just set p
    /// </summary>
    virtual void seek(size_t index) override;
    virtual std::string getText(const misc::Interval &interval) override;
    virtual std::string getSourceName() const override;
    virtual std::string toString() const override;

  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
