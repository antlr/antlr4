#include <assert.h>
#include <algorithm>

#include "BufferedTokenStream.h"
#include "WritableToken.h"
#include "Lexer.h"
#include "Exceptions.h"
#include "StringBuilder.h"

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

                BufferedTokenStream::BufferedTokenStream(TokenSource *tokenSource) {
                    InitializeInstanceFields();
                    if (tokenSource == nullptr) {
                        throw new NullPointerException(L"tokenSource cannot be null");
                    }
                    this->tokenSource = tokenSource;
                }

                TokenSource *BufferedTokenStream::getTokenSource() {
                    return tokenSource;
                }

                int BufferedTokenStream::index() {
                    return p;
                }

                int BufferedTokenStream::mark() {
                    return 0;
                }

                void BufferedTokenStream::release(int marker) {
                    // no resources to release
                }

                void BufferedTokenStream::reset() {
                    seek(0);
                }

                void BufferedTokenStream::seek(int index) {
                    lazyInit();
                    p = adjustSeekIndex(index);
                }

                size_t BufferedTokenStream::size() {
                    return tokens.size();
                }

                void BufferedTokenStream::consume() {
                    if (LA(1) == _EOF) {
                        throw new IllegalStateException(L"cannot consume EOF");
                    }

                    if (sync(p + 1)) {
                        p = adjustSeekIndex(p + 1);
                    }
                }

                bool BufferedTokenStream::sync(int i) {
                    assert(i >= 0);
                    size_t n = i - tokens.size() + 1; // how many more elements we need?
                    //System.out.println("sync("+i+") needs "+n);
                    if (n > 0) {
                        size_t fetched = fetch((int)n);
                        return fetched >= n;
                    }

                    return true;
                }

                int BufferedTokenStream::fetch(int n) {
                    if (fetchedEOF) {
                        return 0;
                    }

                    for (int i = 0; i < n; i++) {
                        Token *t = tokenSource->nextToken();
                        if (dynamic_cast<WritableToken*>(t) != nullptr) {
                            (static_cast<WritableToken*>(t))->setTokenIndex((int)tokens.size());
                        }
                        tokens.push_back(t);
                        if (t->getType() == Token::_EOF) {
                            fetchedEOF = true;
                            return i + 1;
                        }
                    }

                    return n;
                }

                Token *BufferedTokenStream::get(int i) {
                    if (i < 0 || i >= (int)tokens.size()) {
                        throw IndexOutOfBoundsException(std::wstring(L"token index ") +
                                                        std::to_wstring(i) +
                                                        std::wstring(L" out of range 0..") +
                                                        std::to_wstring(tokens.size() - 1));
                    }
                    return tokens[i];
                }

                std::vector<Token*> BufferedTokenStream::get(int start, int stop) {
                    if (start < 0 || stop < 0) {
                        return std::vector<Token*>();
                    }
                    lazyInit();
                    std::vector<Token*> subset = std::vector<Token*>();
                    if (stop >= (int)tokens.size()) {
                        stop = (int)tokens.size() - 1;
                    }
                    for (int i = start; i <= stop; i++) {
                        Token *t = tokens[i];
                        if (t->getType() == Token::_EOF) {
                            break;
                        }
                        subset.push_back(t);
                    }
                    return subset;
                }

                int BufferedTokenStream::LA(int i) {
                    return LT(i)->getType();
                }

                Token *BufferedTokenStream::LB(int k) {
                    if ((p - k) < 0) {
                        return nullptr;
                    }
                    return tokens[p - k];
                }

                Token *BufferedTokenStream::LT(int k) {
                    lazyInit();
                    if (k == 0) {
                        return nullptr;
                    }
                    if (k < 0) {
                        return LB(-k);
                    }

                    int i = p + k - 1;
                    sync(i);
                    if (i >= (int)tokens.size()) { // return EOF token
                        // EOF must be last token
                        return tokens[tokens.size() - 1];
                    }
                                //		if ( i>range ) range = i;
                    return tokens[i];
                }

                int BufferedTokenStream::adjustSeekIndex(int i) {
                    return i;
                }

                void BufferedTokenStream::lazyInit() {
                    if (p == -1) {
                        setup();
                    }
                }

                void BufferedTokenStream::setup() {
                    sync(0);
                    p = adjustSeekIndex(0);
                }

                void BufferedTokenStream::setTokenSource(TokenSource *tokenSource) {
                    this->tokenSource = tokenSource;
                    tokens.clear();
                    p = -1;
                }

                std::vector<Token*> BufferedTokenStream::getTokens() {
                    return tokens;
                }

                std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop) {
                    return getTokens(start, stop, nullptr);
                }

                std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop, std::vector<int> *types) {
                    lazyInit();
		        if (start < 0 || stop >= (int)tokens.size() || stop < 0 || (int)start >= (int)tokens.size()) {
                        throw new IndexOutOfBoundsException(std::wstring(L"start ") +
                                                            std::to_wstring(start) +
                                                            std::wstring(L" or stop ") +
                                                            std::to_wstring(stop) +
                                                            std::wstring(L" not in 0..") +
                                                            std::to_wstring(tokens.size() - 1));
                    }
                    if (start > stop) {
                        return std::vector<Token*>();
                    }

                    // list = tokens[start:stop]:{T t, t.getType() in types}
                    std::vector<Token*> filteredTokens = std::vector<Token*>();
                    for (int i = start; i <= stop; i++) {
                        Token *tok = tokens[i];
                        
                        if (types == nullptr) {
                            filteredTokens.push_back(tok);
                        } else {
                            if (types == nullptr || std::find(types->begin(), types->end(), tok->getType()) != types->end()) {
                                filteredTokens.push_back(tok);
                            }
                        }
                    }
                    if (filteredTokens.empty()) {
                        filteredTokens.clear();
                    }
                    return filteredTokens;
                }

                std::vector<Token*> BufferedTokenStream::getTokens(int start, int stop, int ttype) {
                    std::vector<int> *s = new std::vector<int>();
                    s->insert(s->begin(), ttype);
                    return getTokens(start,stop, s);
                }

                int BufferedTokenStream::nextTokenOnChannel(int i, int channel) {
                    sync(i);
                    Token *token = tokens[i];
					if (i >= (int)size()) {
                        return -1;
                    }
                    while (token->getChannel() != channel) {
                        if (token->getType() == Token::_EOF) {
                            return -1;
                        }
                        i++;
                        sync(i);
                        token = tokens[i];
                    }
                    return i;
                }

                int BufferedTokenStream::previousTokenOnChannel(int i, int channel) {
                    while (i >= 0 && tokens[i]->getChannel() != channel) {
                        i--;
                    }
                    return i;
                }

                std::vector<Token*> BufferedTokenStream::getHiddenTokensToRight(int tokenIndex, int channel) {
                    lazyInit();
					if (tokenIndex < 0 || tokenIndex >= (int)tokens.size()) {
                        throw new IndexOutOfBoundsException(std::to_wstring(tokenIndex) +
                                                            std::wstring(L" not in 0..") +
                                                            std::to_wstring(tokens.size() - 1));
                    }

                    int nextOnChannel = nextTokenOnChannel(tokenIndex + 1, Lexer::DEFAULT_TOKEN_CHANNEL);
                    int to;
                    int from = tokenIndex + 1;
                    // if none onchannel to right, nextOnChannel=-1 so set to = last token
                    if (nextOnChannel == -1) {
                        to = (int)size() - 1;
                    } else {
                        to = nextOnChannel;
                    }

                    return filterForChannel(from, to, channel);
                }

                std::vector<Token*> BufferedTokenStream::getHiddenTokensToRight(int tokenIndex) {
                    return getHiddenTokensToRight(tokenIndex, -1);
                }

                std::vector<Token*> BufferedTokenStream::getHiddenTokensToLeft(int tokenIndex, int channel) {
                    lazyInit();
					if (tokenIndex < 0 || tokenIndex >= (int)tokens.size()) {
                        throw new IndexOutOfBoundsException(std::to_wstring(tokenIndex) +
                                                            std::wstring(L" not in 0..") +
                                                            std::to_wstring(tokens.size() - 1));
                    }

                    int prevOnChannel = previousTokenOnChannel(tokenIndex - 1, Lexer::DEFAULT_TOKEN_CHANNEL);
                    if (prevOnChannel == tokenIndex - 1) {
                        return std::vector<Token*>();
                    }
                    // if none onchannel to left, prevOnChannel=-1 then from=0
                    int from = prevOnChannel + 1;
                    int to = tokenIndex - 1;

                    return filterForChannel(from, to, channel);
                }

                std::vector<Token*> BufferedTokenStream::getHiddenTokensToLeft(int tokenIndex) {
                    return getHiddenTokensToLeft(tokenIndex, -1);
                }

                std::vector<Token*> BufferedTokenStream::filterForChannel(int from, int to, int channel) {
                    std::vector<Token*> hidden = std::vector<Token*>();
                    for (int i = from; i <= to; i++) {
                        Token *t = tokens[i];
                        if (channel == -1) {
                            if (t->getChannel() != Lexer::DEFAULT_TOKEN_CHANNEL) {
                                hidden.push_back(t);
                            }
                        } else {
                            if (t->getChannel() == channel) {
                                hidden.push_back(t);
                            }
                        }
                    }
                    if (hidden.empty()) {
                        return std::vector<Token*>();
                    }
                    return hidden;
                }

                /**
                 * Get the text of all tokens in this buffer.
                 */
                std::string BufferedTokenStream::getSourceName()
                {
                    return tokenSource->getSourceName();
                }

                std::wstring BufferedTokenStream::getText() {
                    lazyInit();
                    fill();
                    return getText(misc::Interval::of(0, (int)size() - 1));
                }

                std::wstring BufferedTokenStream::getText(misc::Interval *interval) {
                    int start = interval->a;
                    int stop = interval->b;
                    if (start < 0 || stop < 0) {
                        return L"";
                    }
                    lazyInit();
					if (stop >= (int)tokens.size()) {
                        stop = (int)tokens.size() - 1;
                    }

                    antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                    for (int i = start; i <= stop; i++) {
                        Token *t = tokens[i];
                        if (t->getType() == Token::_EOF) {
                            break;
                        }
                        buf->append(t->getText());
                    }
                    return buf->toString();
                }

                std::wstring BufferedTokenStream::getText(RuleContext *ctx) {
                    return getText(ctx->getSourceInterval());
                }

                std::wstring BufferedTokenStream::getText(Token *start, Token *stop) {
                    if (start != nullptr && stop != nullptr) {
                        return getText(misc::Interval::of(start->getTokenIndex(), stop->getTokenIndex()));
                    }

                    return L"";
                }

                void BufferedTokenStream::fill() {
                    lazyInit();
                    const int blockSize = 1000;
                    while (true) {
                        int fetched = fetch(blockSize);
                        if (fetched < blockSize) {
                            return;
                        }
                    }
                }

                void BufferedTokenStream::InitializeInstanceFields() {
                    tokens = antlrcpp::VectorHelper::VectorWithReservedSize<Token*>(100);
                    p = -1;
                    fetchedEOF = false;
                }
            }
        }
    }
}
