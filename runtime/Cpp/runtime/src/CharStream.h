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

#include "IntStream.h"
#include "misc/Interval.h"

namespace antlr4 {

  /// A source of characters for an ANTLR lexer.
  class ANTLR4CPP_PUBLIC CharStream : public IntStream {
  public:
    virtual ~CharStream() = 0;

    /// This method returns the text for a range of characters within this input
    /// stream. This method is guaranteed to not throw an exception if the
    /// specified interval lies entirely within a marked range. For more
    /// information about marked ranges, see IntStream::mark.
    ///
    /// <param name="interval"> an interval within the stream </param>
    /// <returns> the text of the specified interval
    /// </returns>
    /// <exception cref="NullPointerException"> if {@code interval} is {@code null} </exception>
    /// <exception cref="IllegalArgumentException"> if {@code interval.a < 0}, or if
    /// {@code interval.b < interval.a - 1}, or if {@code interval.b} lies at or
    /// past the end of the stream </exception>
    /// <exception cref="UnsupportedOperationException"> if the stream does not support
    /// getting the text of the specified interval </exception>
    virtual std::string getText(const misc::Interval &interval) = 0;

    virtual std::string toString() const = 0;
  };

} // namespace antlr4
