#include "CommonToken.h"
#include "Interval.h"
#include "TokenSource.h"

#include "Strings.h"

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

                std::pair<TokenSource*, CharStream*> *const CommonToken::EMPTY_SOURCE = new std::pair<TokenSource*, CharStream*>(nullptr, nullptr);

                CommonToken::CommonToken(int type) {
                    InitializeInstanceFields();
                    this->type = type;
                }

                CommonToken::CommonToken(std::pair<TokenSource*, CharStream*> *source, int type, int channel, int start, int stop) {
                    InitializeInstanceFields();
                    this->source = source;
                    this->type = type;
                    this->channel = channel;
                    this->start = start;
                    this->stop = stop;
                    if (source->first != nullptr) {
                        this->line = source->first->getLine();
                        this->charPositionInLine = source->first->getCharPositionInLine();
                    }
                }

                CommonToken::CommonToken(int type, const std::wstring &text) {
                    InitializeInstanceFields();
                    this->type = type;
                    this->channel = DEFAULT_CHANNEL;
                    this->text = text;
                    this->source = EMPTY_SOURCE;
                }

                CommonToken::CommonToken(Token *oldToken) {
                    InitializeInstanceFields();
                    text = oldToken->getText();
                    type = oldToken->getType();
                    line = oldToken->getLine();
                    index = oldToken->getTokenIndex();
                    charPositionInLine = oldToken->getCharPositionInLine();
                    channel = oldToken->getChannel();
                    start = oldToken->getStartIndex();
                    stop = oldToken->getStopIndex();

                    if (dynamic_cast<CommonToken*>(oldToken) != nullptr) {
                        source = (static_cast<CommonToken*>(oldToken))->source;
                    } else {
                        source = new std::pair<TokenSource*, CharStream*>(oldToken->getTokenSource(), oldToken->getInputStream());
                    }
                }

                int CommonToken::getType() {
                    return type;
                }

                void CommonToken::setLine(int line) {
                    this->line = line;
                }

                std::wstring CommonToken::getText() {
                    if (text != L"") {
                        return text;
                    }

                    CharStream *input = getInputStream();
                    if (input == nullptr) {
                        return L"";
                    }
                    size_t n = input->size();
                    if ((size_t)start < n && (size_t)stop < n) {
                        return input->getText(misc::Interval::of(start,stop));
                    } else {
                        return L"<EOF>";
                    }
                }

                void CommonToken::setText(const std::wstring &text) {
                    this->text = text;
                }

                int CommonToken::getLine() {
                    return line;
                }

                int CommonToken::getCharPositionInLine() {
                    return charPositionInLine;
                }

                void CommonToken::setCharPositionInLine(int charPositionInLine) {
                    this->charPositionInLine = charPositionInLine;
                }

                int CommonToken::getChannel() {
                    return channel;
                }

                void CommonToken::setChannel(int channel) {
                    this->channel = channel;
                }

                void CommonToken::setType(int type) {
                    this->type = type;
                }

                int CommonToken::getStartIndex() {
                    return start;
                }

                void CommonToken::setStartIndex(int start) {
                    this->start = start;
                }

                int CommonToken::getStopIndex() {
                    return stop;
                }

                void CommonToken::setStopIndex(int stop) {
                    this->stop = stop;
                }

                int CommonToken::getTokenIndex() {
                    return index;
                }

                void CommonToken::setTokenIndex(int index) {
                    this->index = index;
                }

                org::antlr::v4::runtime::TokenSource *CommonToken::getTokenSource() {
                    return source->first;
                }

                org::antlr::v4::runtime::CharStream *CommonToken::getInputStream() {
                    return source->second;
                }

                std::wstring CommonToken::toString() {
                    std::wstring channelStr = L"";
                    if (channel > 0) {
                        channelStr = std::wstring(L",channel=") + std::to_wstring(channel);
                    }
                    std::wstring txt = getText();
                    if (txt != L"") {
                        
                        antlrcpp::replaceAll(txt, L"\n",L"\\n");

                        antlrcpp::replaceAll(txt, L"\r",L"\\r");

                        antlrcpp::replaceAll(txt, L"\t",L"\\t");
                    } else {
                        txt = L"<no text>";
                    }
                    return std::wstring(L"[@") + std::to_wstring(getTokenIndex()) + std::wstring(L",") + std::to_wstring(start) + std::wstring(L":") + std::to_wstring(stop) + std::wstring(L"='") + txt + std::wstring(L"',<") + std::to_wstring(type) + std::wstring(L">") + channelStr + std::wstring(L",") + std::to_wstring(line) + std::wstring(L":") + std::to_wstring(getCharPositionInLine()) + std::wstring(L"]");
                }

                void CommonToken::InitializeInstanceFields() {
                    type = 0;
                    line = 0;
                    charPositionInLine = -1;
                    channel = DEFAULT_CHANNEL;
                    index = -1;
                    start = 0;
                    stop = 0;
                }
            }
        }
    }
}
