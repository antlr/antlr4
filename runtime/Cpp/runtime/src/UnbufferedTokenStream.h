/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include <memory>
#include <vector>
#include <string>
#include <cstddef>
#include "antlr4-common.h"
#include "misc/Interval.h"
#include "Token.h"
#include "TokenStream.h"

namespace antlr4 {

  class ANTLR4CPP_PUBLIC UnbufferedTokenStream : public TokenStream {
  public:
    UnbufferedTokenStream(TokenSource *tokenSource);
    UnbufferedTokenStream(TokenSource *tokenSource, int bufferSize);
    UnbufferedTokenStream(const UnbufferedTokenStream& other) = delete;
    ~UnbufferedTokenStream() override;

    UnbufferedTokenStream& operator = (const UnbufferedTokenStream& other) = delete;

    Token* get(size_t i) const override;
    Token* LT(ssize_t i) override;
    size_t LA(ssize_t i) override;

    TokenSource* getTokenSource() const override;

    std::string getText(const misc::Interval &interval) override;
    std::string getText() override;
    std::string getText(RuleContext *ctx) override;
    std::string getText(Token *start, Token *stop) override;

    void consume() override;

    /// <summary>
    /// Return a marker that we can release later.
    /// <p/>
    /// The specific marker value used for this class allows for some level of
    /// protection against misuse where {@code seek()} is called on a mark or
    /// {@code release()} is called in the wrong order.
    /// </summary>
    ssize_t mark() override;
    void release(ssize_t marker) override;
    size_t index() override;
    void seek(size_t index) override;
    size_t size() override;
    std::string getSourceName() const override;

  protected:
    /// Make sure we have 'need' elements from current position p. Last valid
    /// p index is tokens.length - 1.  p + need - 1 is the tokens index 'need' elements
    /// ahead.  If we need 1 element, (p+1-1)==p must be less than tokens.length.
    TokenSource *_tokenSource;

    /// <summary>
    /// A moving window buffer of the data being scanned. While there's a marker,
    /// we keep adding to buffer. Otherwise, <seealso cref="#consume consume()"/> resets so
    /// we start filling at index 0 again.
    /// </summary>

    std::vector<std::unique_ptr<Token>> _tokens;

    /// <summary>
    /// 0..n-1 index into <seealso cref="#tokens tokens"/> of next token.
    /// <p/>
    /// The {@code LT(1)} token is {@code tokens[p]}. If {@code p == n}, we are
    /// out of buffered tokens.
    /// </summary>
    size_t _p;

    /// <summary>
    /// Count up with <seealso cref="#mark mark()"/> and down with
    /// <seealso cref="#release release()"/>. When we {@code release()} the last mark,
    /// {@code numMarkers} reaches 0 and we reset the buffer. Copy
    /// {@code tokens[p]..tokens[n-1]} to {@code tokens[0]..tokens[(n-1)-p]}.
    /// </summary>
    int _numMarkers;

    /// <summary>
    /// This is the {@code LT(-1)} token for the current position.
    /// </summary>
    Token *_lastToken;

    /// <summary>
    /// When {@code numMarkers > 0}, this is the {@code LT(-1)} token for the
    /// first token in <seealso cref="#tokens"/>. Otherwise, this is {@code null}.
    /// </summary>
    Token *_lastTokenBufferStart;

    /// <summary>
    /// Absolute token index. It's the index of the token about to be read via
    /// {@code LT(1)}. Goes from 0 to the number of tokens in the entire stream,
    /// although the stream size is unknown before the end is reached.
    /// <p/>
    /// This value is used to set the token indexes if the stream provides tokens
    /// that implement <seealso cref="WritableToken"/>.
    /// </summary>
    size_t _currentTokenIndex;

    virtual void sync(ssize_t want);

    /// <summary>
    /// Add {@code n} elements to the buffer. Returns the number of tokens
    /// actually added to the buffer. If the return value is less than {@code n},
    /// then EOF was reached before {@code n} tokens could be added.
    /// </summary>
    virtual size_t fill(size_t n);
    virtual void add(std::unique_ptr<Token> t);

    size_t getBufferStartIndex() const;

  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
