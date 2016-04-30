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

#include "TokenStream.h"

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {

  /// Buffer all input tokens but do on-demand fetching of new tokens from lexer.
  /// Useful when the parser or lexer has to set context/mode info before proper
  /// lexing of future tokens. The ST template parser needs this, for example,
  /// because it has to constantly flip back and forth between inside/output
  /// templates. E.g., <names:{hi, <it>}> has to parse names as part of an
  /// expression but "hi, <it>" as a nested template.
  ///
  /// You can't use this stream if you pass whitespace or other off-channel tokens
  /// to the parser. The stream can't ignore off-channel tokens.
  /// (UnbufferedTokenStream is the same way.) Use CommonTokenStream.
  class BufferedTokenStream : public TokenStream {
  public:
    BufferedTokenStream(TokenSource *tokenSource);

    virtual TokenSource* getTokenSource() const override;
    virtual size_t index() override;
    virtual ssize_t mark() override;

    virtual void release(ssize_t marker) override;
    virtual void reset();
    virtual void seek(size_t index) override;

    virtual size_t size() override;
    virtual void consume() override;

    virtual Ref<Token> get(size_t i) const override;

    /// Get all tokens from start..stop inclusively.
    virtual std::vector<Ref<Token>> get(size_t start, size_t stop);

    virtual ssize_t LA(ssize_t i) override;
    virtual Ref<Token> LT(ssize_t k) override;

    /// Reset this token stream by setting its token source.
    virtual void setTokenSource(TokenSource *tokenSource);
    virtual std::vector<Ref<Token>> getTokens();
    virtual std::vector<Ref<Token>> getTokens(int start, int stop);

    /// <summary>
    /// Given a start and stop index, return a List of all tokens in
    ///  the token type BitSet.  Return null if no tokens were found.  This
    ///  method looks at both on and off channel tokens.
    /// </summary>
    virtual std::vector<Ref<Token>> getTokens(int start, int stop, const std::vector<int> &types);
    virtual std::vector<Ref<Token>> getTokens(int start, int stop, int ttype);

    /// Collect all tokens on specified channel to the right of
    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL or
    ///  EOF. If channel is -1, find any non default channel token.
    virtual std::vector<Ref<Token>> getHiddenTokensToRight(size_t tokenIndex, int channel);

    /// <summary>
    /// Collect all hidden tokens (any off-default channel) to the right of
    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL
    ///  of EOF.
    /// </summary>
    virtual std::vector<Ref<Token>> getHiddenTokensToRight(size_t tokenIndex);

    /// <summary>
    /// Collect all tokens on specified channel to the left of
    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
    ///  If channel is -1, find any non default channel token.
    /// </summary>
    virtual std::vector<Ref<Token>> getHiddenTokensToLeft(size_t tokenIndex, int channel);

    /// <summary>
    /// Collect all hidden tokens (any off-default channel) to the left of
    ///  the current token up until we see a token on DEFAULT_TOKEN_CHANNEL.
    /// </summary>
    virtual std::vector<Ref<Token>> getHiddenTokensToLeft(size_t tokenIndex);

    virtual std::string getSourceName() const override;
    virtual std::wstring getText() override;
    virtual std::wstring getText(const misc::Interval &interval) override;
    virtual std::wstring getText(RuleContext *ctx) override;
    virtual std::wstring getText(Ref<Token> start, Ref<Token> stop) override;

    /// <summary>
    /// Get all tokens from lexer until EOF </summary>
    virtual void fill();

  protected:
    TokenSource *_tokenSource;

    /// Record every single token pulled from the source so we can reproduce
    /// chunks of it later. This list captures everything so we can access
    /// complete input text.
    // ml: we own the tokens produced by the token factory.
    std::vector<Ref<Token>> _tokens;

    /// <summary>
    /// The index into <seealso cref="#tokens"/> of the current token (next token to
    /// consume). <seealso cref="#tokens"/>{@code [}<seealso cref="#p"/>{@code ]} should be
    /// <seealso cref="#LT LT(1)"/>. <seealso cref="#p"/>{@code =-1} indicates need to initialize
    /// with first token. The constructor doesn't get a token. First call to
    /// <seealso cref="#LT LT(1)"/> or whatever gets the first token and sets
    /// <seealso cref="#p"/>{@code =0;}.
    /// </summary>
    size_t _p;

    /// <summary>
    /// Set to {@code true} when the EOF token is fetched. Do not continue fetching
    /// tokens after that point, or multiple EOF tokens could end up in the
    /// <seealso cref="#tokens"/> array.
    /// </summary>
    /// <seealso cref= #fetch </seealso>
    bool _fetchedEOF;
    
    /// <summary>
    /// Make sure index {@code i} in tokens has a token.
    /// </summary>
    /// <returns> {@code true} if a token is located at index {@code i}, otherwise
    ///    {@code false}. </returns>
    /// <seealso cref= #get(int i) </seealso>
    virtual bool sync(size_t i);

    /// <summary>
    /// Add {@code n} elements to buffer.
    /// </summary>
    /// <returns> The actual number of elements added to the buffer. </returns>
    virtual size_t fetch(size_t n);
    
    virtual Ref<Token> LB(size_t k);

    /// Allowed derived classes to modify the behavior of operations which change
    /// the current stream position by adjusting the target token index of a seek
    /// operation. The default implementation simply returns {@code i}. If an
    /// exception is thrown in this method, the current stream index should not be
    /// changed.
    /// <p/>
    /// For example, <seealso cref="CommonTokenStream"/> overrides this method to ensure that
    /// the seek target is always an on-channel token.
    ///
    /// <param name="i"> The target token index. </param>
    /// <returns> The adjusted target token index. </returns>
    virtual ssize_t adjustSeekIndex(size_t i);
    void lazyInit();
    virtual void setup();

    /// Given a starting index, return the index of the next token on channel.
    /// Return i if tokens[i] is on channel.  Return -1 if there are no tokens
    /// on channel between i and EOF.
    virtual ssize_t nextTokenOnChannel(size_t i, int channel);

    /// Given a starting index, return the index of the previous token on channel.
    /// Return i if tokens[i] is on channel. Return -1 if there are no tokens
    /// on channel between i and 0.
    virtual ssize_t previousTokenOnChannel(ssize_t i, int channel) const;
    
    virtual std::vector<Ref<Token>> filterForChannel(size_t from, size_t to, int channel);
  private:
    bool _needSetup;
    void InitializeInstanceFields();
  };

} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
