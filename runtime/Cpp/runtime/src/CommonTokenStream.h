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

#include "BufferedTokenStream.h"

namespace antlr4 {

  /**
   * This class extends {@link BufferedTokenStream} with functionality to filter
   * token streams to tokens on a particular channel (tokens where
   * {@link Token#getChannel} returns a particular value).
   *
   * <p>
   * This token stream provides access to all tokens by index or when calling
   * methods like {@link #getText}. The channel filtering is only used for code
   * accessing tokens via the lookahead methods {@link #LA}, {@link #LT}, and
   * {@link #LB}.</p>
   *
   * <p>
   * By default, tokens are placed on the default channel
   * ({@link Token#DEFAULT_CHANNEL}), but may be reassigned by using the
   * {@code ->channel(HIDDEN)} lexer command, or by using an embedded action to
   * call {@link Lexer#setChannel}.
   * </p>
   *
   * <p>
   * Note: lexer rules which use the {@code ->skip} lexer command or call
   * {@link Lexer#skip} do not produce tokens at all, so input text matched by
   * such a rule will not be available as part of the token stream, regardless of
   * channel.</p>
   */
  class ANTLR4CPP_PUBLIC CommonTokenStream : public BufferedTokenStream {
  protected:
    /**
     * Specifies the channel to use for filtering tokens.
     *
     * <p>
     * The default value is {@link Token#DEFAULT_CHANNEL}, which matches the
     * default channel assigned to tokens created by the lexer.</p>
     */
    size_t channel;

  public:
    /**
     * Constructs a new {@link CommonTokenStream} using the specified token
     * source and the default token channel ({@link Token#DEFAULT_CHANNEL}).
     *
     * @param tokenSource The token source.
     */
    CommonTokenStream(TokenSource *tokenSource);

    /**
     * Constructs a new {@link CommonTokenStream} using the specified token
     * source and filtering tokens to the specified channel. Only tokens whose
     * {@link Token#getChannel} matches {@code channel} or have the
     * {@link Token#getType} equal to {@link Token#EOF} will be returned by the
     * token stream lookahead methods.
     *
     * @param tokenSource The token source.
     * @param channel The channel to use for filtering tokens.
     */
    CommonTokenStream(TokenSource *tokenSource, int channel);

  protected:
    virtual ssize_t adjustSeekIndex(size_t i) override;

    virtual Token* LB(size_t k) override;

  public:
    virtual Token* LT(ssize_t k) override;

    /// Count EOF just once.
    virtual int getNumberOfOnChannelTokens();

  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
