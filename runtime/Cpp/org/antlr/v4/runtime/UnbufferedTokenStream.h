#pragma once

#include "TokenStream.h"
#include "Declarations.h"

#include <string>
#include <vector>

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


                template<typename T>
                class UnbufferedTokenStream : public TokenStream {
                protected:
                    TokenSource *tokenSource;

                    /// <summary>
                    /// A moving window buffer of the data being scanned. While there's a marker,
                    /// we keep adding to buffer. Otherwise, <seealso cref="#consume consume()"/> resets so
                    /// we start filling at index 0 again.
                    /// </summary>

                    std::vector<Token *>tokens;

                    /// <summary>
                    /// The number of tokens currently in <seealso cref="#tokens tokens"/>.
                    /// <p/>
                    /// This is not the buffer capacity, that's {@code tokens.length}.
                    /// </summary>
                    int n;

                    /// <summary>
                    /// 0..n-1 index into <seealso cref="#tokens tokens"/> of next token.
                    /// <p/>
                    /// The {@code LT(1)} token is {@code tokens[p]}. If {@code p == n}, we are
                    /// out of buffered tokens.
                    /// </summary>
                    int p;

                    /// <summary>
                    /// Count up with <seealso cref="#mark mark()"/> and down with
                    /// <seealso cref="#release release()"/>. When we {@code release()} the last mark,
                    /// {@code numMarkers} reaches 0 and we reset the buffer. Copy
                    /// {@code tokens[p]..tokens[n-1]} to {@code tokens[0]..tokens[(n-1)-p]}.
                    /// </summary>
                    int numMarkers;

                    /// <summary>
                    /// This is the {@code LT(-1)} token for the current position.
                    /// </summary>
                    Token *lastToken;

                    /// <summary>
                    /// When {@code numMarkers > 0}, this is the {@code LT(-1)} token for the
                    /// first token in <seealso cref="#tokens"/>. Otherwise, this is {@code null}.
                    /// </summary>
                    Token *lastTokenBufferStart;

                    /// <summary>
                    /// Absolute token index. It's the index of the token about to be read via
                    /// {@code LT(1)}. Goes from 0 to the number of tokens in the entire stream,
                    /// although the stream size is unknown before the end is reached.
                    /// <p/>
                    /// This value is used to set the token indexes if the stream provides tokens
                    /// that implement <seealso cref="WritableToken"/>.
                    /// </summary>
                    int currentTokenIndex;

                public:
                    UnbufferedTokenStream(TokenSource *tokenSource);

                    UnbufferedTokenStream(TokenSource *tokenSource, int bufferSize);

                    virtual Token *get(int i) override;

                    virtual Token *LT(int i) override ;

                    virtual int LA(int i) override ;

                    virtual TokenSource *getTokenSource() override;

                    virtual std::wstring getText() override ;

                    virtual std::wstring getText(RuleContext *ctx) override;

                    virtual std::wstring getText(Token *start, Token *stop) override;

                    virtual void consume() override;

                    /// <summary>
                    /// Make sure we have 'need' elements from current position <seealso cref="#p p"/>. Last valid
                    ///  {@code p} index is {@code tokens.length-1}.  {@code p+need-1} is the tokens index 'need' elements
                    ///  ahead.  If we need 1 element, {@code (p+1-1)==p} must be less than {@code tokens.length}.
                    /// </summary>
                protected:
                    virtual void sync(int want);

                    /// <summary>
                    /// Add {@code n} elements to the buffer. Returns the number of tokens
                    /// actually added to the buffer. If the return value is less than {@code n},
                    /// then EOF was reached before {@code n} tokens could be added.
                    /// </summary>
                    virtual int fill(int n);

                    virtual void add(Token *t);

                    /// <summary>
                    /// Return a marker that we can release later.
                    /// <p/>
                    /// The specific marker value used for this class allows for some level of
                    /// protection against misuse where {@code seek()} is called on a mark or
                    /// {@code release()} is called in the wrong order.
                    /// </summary>
                public:
                    virtual int mark() override;

                    virtual void release(int marker) override;

                    virtual int index() override;

                    virtual void seek(int index) override;

                    virtual size_t size() override;

                    virtual std::string getSourceName() override;

                    virtual std::wstring getText(misc::Interval *interval) ;

                protected:
                    int getBufferStartIndex();

                private:
                    void InitializeInstanceFields();
                };

            }
        }
    }
}

#include "UnbufferedTokenStream.inl"
