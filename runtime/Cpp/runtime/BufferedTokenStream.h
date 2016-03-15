#pragma once

#include "Token.h"
#include "Interval.h"
#include "TokenStream.h"
#include "TokenSource.h"
#include "RuleContext.h"
#include "vectorhelper.h"

#include <string>
#include <vector>
#include <set>

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
                /// Buffer all input tokens but do on-demand fetching of new tokens from lexer.
                /// Useful when the parser or lexer has to set context/mode info before proper
                /// lexing of future tokens. The ST template parser needs this, for example,
                /// because it has to constantly flip back and forth between inside/output
                /// templates. E.g., {@code <names:{hi, <it>}>} has to parse names as part of an
                /// expression but {@code "hi, <it>"} as a nested template.
                /// <p/>
                /// You can't use this stream if you pass whitespace or other off-channel tokens
                /// to the parser. The stream can't ignore off-channel tokens.
                /// (<seealso cref="UnbufferedTokenStream"/> is the same way.) Use
                /// <seealso cref="CommonTokenStream"/>.
                /// </summary>
                class BufferedTokenStream : public TokenStream {
                protected:
                    TokenSource *tokenSource;

                    /// <summary>
                    /// Record every single token pulled from the source so we can reproduce
                    /// chunks of it later. This list captures everything so we can access
                    /// complete input text.
                    /// </summary>
                    std::vector<Token*> tokens;

                    /// <summary>
                    /// The index into <seealso cref="#tokens"/> of the current token (next token to
                    /// consume). <seealso cref="#tokens"/>{@code [}<seealso cref="#p"/>{@code ]} should be
                    /// <seealso cref="#LT LT(1)"/>. <seealso cref="#p"/>{@code =-1} indicates need to initialize
                    /// with first token. The constructor doesn't get a token. First call to
                    /// <seealso cref="#LT LT(1)"/> or whatever gets the first token and sets
                    /// <seealso cref="#p"/>{@code =0;}.
                    /// </summary>
                    int p;

                    /// <summary>
                    /// Set to {@code true} when the EOF token is fetched. Do not continue fetching
                    /// tokens after that point, or multiple EOF tokens could end up in the
                    /// <seealso cref="#tokens"/> array.
                    /// </summary>
                    /// <seealso cref= #fetch </seealso>
                    bool fetchedEOF;

                public:
                    BufferedTokenStream(TokenSource *tokenSource);

                    virtual TokenSource *getTokenSource() override;
                    virtual int index() override;
                    virtual int mark() override;

                    virtual void release(int marker) override;

                    virtual void reset();

                    virtual void seek(int index) override;

                    virtual size_t size() override;
                    virtual void consume() override;

                    /// <summary>
                    /// Make sure index {@code i} in tokens has a token.
                    /// </summary>
                    /// <returns> {@code true} if a token is located at index {@code i}, otherwise
                    ///    {@code false}. </returns>
                    /// <seealso cref= #get(int i) </seealso>
                protected:
                    virtual bool sync(int i);

                    /// <summary>
                    /// Add {@code n} elements to buffer.
                    /// </summary>
                    /// <returns> The actual number of elements added to the buffer. </returns>
                    virtual int fetch(int n);

                public:
                    virtual Token *get(int i) override;

                    /// <summary>
                    /// Get all tokens from start..stop inclusively </summary>
                    virtual std::vector<Token*> get(int start, int stop);

                    virtual int LA(int i) override;

                protected:
                    virtual Token *LB(int k);

                public:
                    virtual Token *LT(int k) override;

                    /// <summary>
                    /// Allowed derived classes to modify the behavior of operations which change
                    /// the current stream position by adjusting the target token index of a seek
                    /// operation. The default implementation simply returns {@code i}. If an
                    /// exception is thrown in this method, the current stream index should not be
                    /// changed.
                    /// <p/>
                    /// For example, <seealso cref="CommonTokenStream"/> overrides this method to ensure that
                    /// the seek target is always an on-channel token.
                    /// </summary>
                    /// <param name="i"> The target token index. </param>
                    /// <returns> The adjusted target token index. </returns>
                protected:
                    virtual int adjustSeekIndex(int i);

                    void lazyInit();

                    virtual void setup();

                    /// <summary>
                    /// Reset this token stream by setting its token source. </summary>
                public:
                    virtual void setTokenSource(TokenSource *tokenSource);

                    virtual std::vector<Token*> getTokens();

                    virtual std::vector<Token*> getTokens(int start, int stop);

                    /// <summary>
                    /// Given a start and stop index, return a List of all tokens in
                    ///  the token type BitSet.  Return null if no tokens were found.  This
                    ///  method looks at both on and off channel tokens.
                    /// </summary>
                    virtual std::vector<Token*> getTokens(int start, int stop, std::vector<int> *types);

                    virtual std::vector<Token*> getTokens(int start, int stop, int ttype);

                    /// <summary>
                    /// Given a starting index, return the index of the next token on channel.
                    ///  Return i if tokens[i] is on channel.  Return -1 if there are no tokens
                    ///  on channel between i and EOF.
                    /// </summary>
                protected:
                    virtual int nextTokenOnChannel(int i, int channel);

                    /// <summary>
                    /// Given a starting index, return the index of the previous token on channel.
                    ///  Return i if tokens[i] is on channel. Return -1 if there are no tokens
                    ///  on channel between i and 0.
                    /// </summary>
                    virtual int previousTokenOnChannel(int i, int channel);

                    /// <summary>
                    /// Collect all tokens on specified channel to the right of
                    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
                    ///  EOF. If channel is -1, find any non default channel token.
                    /// </summary>
                public:
                    virtual std::vector<Token*> getHiddenTokensToRight(int tokenIndex, int channel);

                    /// <summary>
                    /// Collect all hidden tokens (any off-default channel) to the right of
                    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL
                    ///  of EOF.
                    /// </summary>
                    virtual std::vector<Token*> getHiddenTokensToRight(int tokenIndex);

                    /// <summary>
                    /// Collect all tokens on specified channel to the left of
                    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
                    ///  If channel is -1, find any non default channel token.
                    /// </summary>
                    virtual std::vector<Token*> getHiddenTokensToLeft(int tokenIndex, int channel);

                    /// <summary>
                    /// Collect all hidden tokens (any off-default channel) to the left of
                    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
                    /// </summary>
                    virtual std::vector<Token*> getHiddenTokensToLeft(int tokenIndex);

                protected:
                    virtual std::vector<Token*> filterForChannel(int from, int to, int channel);

                public:
                    virtual std::string getSourceName() override;
                    virtual std::wstring getText() override;

                    virtual std::wstring getText(misc::Interval *interval) override;

                    virtual std::wstring getText(RuleContext *ctx) override;

                    virtual std::wstring getText(Token *start, Token *stop) override;

                    /// <summary>
                    /// Get all tokens from lexer until EOF </summary>
                    virtual void fill();

                private:
                    void InitializeInstanceFields();
                };

            }
        }
    }
}
