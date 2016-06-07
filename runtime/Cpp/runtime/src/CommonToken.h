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

#include "WritableToken.h"

namespace antlr4 {

  class ANTLR4CPP_PUBLIC CommonToken : public WritableToken {
  protected:
    /**
     * An empty {@link Pair} which is used as the default value of
     * {@link #source} for tokens that do not have a source.
     */
    static const std::pair<TokenSource *, CharStream *> EMPTY_SOURCE;

    /**
     * This is the backing field for {@link #getType} and {@link #setType}.
     */
    int _type;

    /**
     * This is the backing field for {@link #getLine} and {@link #setLine}.
     */
    int _line;

    /**
     * This is the backing field for {@link #getCharPositionInLine} and
     * {@link #setCharPositionInLine}.
     */
    int _charPositionInLine; // set to invalid position

    /**
     * This is the backing field for {@link #getChannel} and
     * {@link #setChannel}.
     */
    size_t _channel;

    /**
     * This is the backing field for {@link #getTokenSource} and
     * {@link #getInputStream}.
     *
     * <p>
     * These properties share a field to reduce the memory footprint of
     * {@link CommonToken}. Tokens created by a {@link CommonTokenFactory} from
     * the same source and input stream share a reference to the same
     * {@link Pair} containing these values.</p>
     */
    
    std::pair<TokenSource *, CharStream *> _source; // ml: pure references, usually from statically allocated classes.

    /**
     * This is the backing field for {@link #getText} when the token text is
     * explicitly set in the constructor or via {@link #setText}.
     *
     * @see #getText()
     */
    std::string _text;

    /**
     * This is the backing field for {@link #getTokenIndex} and
     * {@link #setTokenIndex}.
     */
    int _index;

    /**
     * This is the backing field for {@link #getStartIndex} and
     * {@link #setStartIndex}.
     */
    int _start;

    /**
     * This is the backing field for {@link #getStopIndex} and
     * {@link #setStopIndex}.
     */
    int _stop;

  public:
    /**
     * Constructs a new {@link CommonToken} with the specified token type.
     *
     * @param type The token type.
     */
    CommonToken(int type);
    CommonToken(std::pair<TokenSource*, CharStream*> source, int type, int channel, int start, int stop);

    /**
     * Constructs a new {@link CommonToken} with the specified token type and
     * text.
     *
     * @param type The token type.
     * @param text The text of the token.
     */
    CommonToken(int type, const std::string &text);

    /**
     * Constructs a new {@link CommonToken} as a copy of another {@link Token}.
     *
     * <p>
     * If {@code oldToken} is also a {@link CommonToken} instance, the newly
     * constructed token will share a reference to the {@link #text} field and
     * the {@link Pair} stored in {@link #source}. Otherwise, {@link #text} will
     * be assigned the result of calling {@link #getText}, and {@link #source}
     * will be constructed from the result of {@link Token#getTokenSource} and
     * {@link Token#getInputStream}.</p>
     *
     * @param oldToken The token to copy.
     */
    CommonToken(Token *oldToken);

    virtual int getType() const override;

    /**
     * Explicitly set the text for this token. If {code text} is not
     * {@code null}, then {@link #getText} will return this value rather than
     * extracting the text from the input.
     *
     * @param text The explicit text of the token, or {@code null} if the text
     * should be obtained from the input along with the start and stop indexes
     * of the token.
     */
    virtual void setText(const std::string &text) override;
    virtual std::string getText() const override;

    virtual void setLine(int line) override;
    virtual int getLine() const override;

    virtual int getCharPositionInLine() const override;
    virtual void setCharPositionInLine(int charPositionInLine) override;

    virtual size_t getChannel() const override;
    virtual void setChannel(int channel) override;

    virtual void setType(int type) override;

    virtual int getStartIndex() const override;
    virtual void setStartIndex(int start);

    virtual int getStopIndex() const override;
    virtual void setStopIndex(int stop);

    virtual int getTokenIndex() const override;
    virtual void setTokenIndex(int index) override;

    virtual TokenSource *getTokenSource() const override;
    virtual CharStream *getInputStream() const override;

    virtual std::string toString() const override;
    
  private:
    void InitializeInstanceFields();
  };

} // namespace antlr4
