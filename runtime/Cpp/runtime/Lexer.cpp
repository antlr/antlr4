#include "Lexer.h"

#include "LexerATNSimulator.h"
#include "Exceptions.h"
#include "ANTLRErrorListener.h"
#include "Interval.h"
#include "StringBuilder.h"
#include "CommonTokenFactory.h"
#include "LexerNoViableAltException.h"

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

namespace org {
    namespace antlr {
        namespace v4 {
            namespace runtime {


                Lexer::Lexer() {
                    InitializeInstanceFields();
                }

                Lexer::Lexer(CharStream *input) {
                    InitializeInstanceFields();
                    this->_input = input;
                    this->_tokenFactorySourcePair = new std::pair<TokenSource*, CharStream*>(this, input);
                }

                void Lexer::reset() {
                    // wack Lexer state variables
                    if (_input != nullptr) {
                        _input->seek(0); // rewind the input
                    }
                    delete _token;
                    _type = Token::INVALID_TYPE;
                    _channel = Token::DEFAULT_CHANNEL;
                    _tokenStartCharIndex = -1;
                    _tokenStartCharPositionInLine = -1;
                    _tokenStartLine = -1;
                    _text = L"";

                    _hitEOF = false;
                    _mode = Lexer::DEFAULT_MODE;
                    _modeStack.clear();

                    getInterpreter()->reset();
                }

                Token *Lexer::nextToken() {
                    if (_input == nullptr) {
                        throw new IllegalStateException(L"nextToken requires a non-null input stream.");
                    }

                    // Mark start location in char stream so unbuffered streams are
                    // guaranteed at least have text of current token
                    int tokenStartMarker = _input->mark();
                    try {
                        while (true) {
                            outerContinue:
                            if (_hitEOF) {
                                emitEOF();
                                return _token;
                            }

                            delete _token;
                            _channel = Token::DEFAULT_CHANNEL;
                            _tokenStartCharIndex = _input->index();
                            _tokenStartCharPositionInLine = getInterpreter()->getCharPositionInLine();
                            _tokenStartLine = getInterpreter()->getLine();
                            _text = L"";
                            do {
                                _type = Token::INVALID_TYPE;
                                //				System.out.println("nextToken line "+tokenStartLine+" at "+((char)input.LA(1))+
                                //								   " in mode "+mode+
                                //								   " at index "+input.index());
                                int ttype;
                                try {
                                    ttype = getInterpreter()->match(_input, _mode);
                                } catch (LexerNoViableAltException *e) {
                                    notifyListeners(e); // report error
                                    recover(e);
                                    ttype = SKIP;
                                }
                                if (_input->LA(1) == IntStream::_EOF) {
                                    _hitEOF = true;
                                }
                                if (_type == Token::INVALID_TYPE) {
                                    _type = ttype;
                                }
                                if (_type == SKIP) {
                                    goto outerContinue;
                                }
                            } while (_type == MORE);
                            if (_token == nullptr) {
                                emit();
                            }
                            return _token;
                        }
                        
                    }
                    catch(...) {
#ifdef TODO
                        // Do something intelligent here for once
#endif
                    }
                    
                    // make sure we release marker after match or
                    // unbuffered char stream will keep buffering
                    _input->release(tokenStartMarker);
                    return nullptr;
                }

                void Lexer::skip() {
                    _type = SKIP;
                }

                void Lexer::more() {
                    _type = MORE;
                }

                void Lexer::mode(int m) {
                    _mode = m;
                }

                void Lexer::pushMode(int m) {
                    if (atn::LexerATNSimulator::debug) {
                        std::wcout << std::wstring(L"pushMode ") << m << std::endl;
                    }
                    _modeStack.push_back(_mode);
                    mode(m);
                }

                int Lexer::popMode() {
                    if (_modeStack.empty()) {
                        throw EmptyStackException();
                    }
                    if (atn::LexerATNSimulator::debug) {
                        std::wcout << std::wstring(L"popMode back to ") << _modeStack.back() << std::endl;
                    }
                    mode(_modeStack.back());
                    _modeStack.pop_back();
                    return _mode;
                }
                

                TokenFactory<CommonToken*> *Lexer::getTokenFactory() {
                    return _factory;
                }

                void Lexer::setInputStream(IntStream *input) {
                    delete this->_input;
                    this->_tokenFactorySourcePair = new std::pair<TokenSource*, CharStream*>(this, _input);
                    reset();
                    this->_input = static_cast<CharStream*>(input);
                    this->_tokenFactorySourcePair = new std::pair<TokenSource*, CharStream*>(this, _input);
                }

                std::string Lexer::getSourceName() {
                    return _input->getSourceName();
                }

                CharStream *Lexer::getInputStream() {
                    return _input;
                }

                void Lexer::emit(Token *token) {
                    //System.err.println("emit "+token);
                    this->_token = token;
                }

                Token *Lexer::emit() {
                    Token *t = (Token*)_factory->create(_tokenFactorySourcePair, _type, _text, _channel, _tokenStartCharIndex, getCharIndex() - 1, _tokenStartLine, _tokenStartCharPositionInLine);
                    emit(t);
                    return t;
                }

                Token *Lexer::emitEOF() {
                    int cpos = getCharPositionInLine();
                    // The character position for EOF is one beyond the position of
                    // the previous token's last character
                    if (_token != nullptr) {
                        int n = _token->getStopIndex() - _token->getStartIndex() + 1;
                        cpos = _token->getCharPositionInLine() + n;
                    }
                    Token *eof = (Token*)_factory->create(_tokenFactorySourcePair, Token::_EOF, L"", Token::DEFAULT_CHANNEL, _input->index(), _input->index() - 1, getLine(), cpos);
                    emit(eof);
                    return eof;
                }

                int Lexer::getLine() {
                    return getInterpreter()->getLine();
                }

                int Lexer::getCharPositionInLine() {
                    return getInterpreter()->getCharPositionInLine();
                }

                void Lexer::setLine(int line) {
                    getInterpreter()->setLine(line);
                }

                void Lexer::setCharPositionInLine(int charPositionInLine) {
                    getInterpreter()->setCharPositionInLine(charPositionInLine);
                }

                int Lexer::getCharIndex() {
                    return _input->index();
                }

                std::wstring Lexer::getText() {
                    if (_text != L"") {
                        return _text;
                    }
                    return getInterpreter()->getText(_input);
                }

                void Lexer::setText(const std::wstring &text) {
                    this->_text = text;
                }

                Token *Lexer::getToken() {
                    return _token;
                }

                void Lexer::setToken(Token *_token) {
                    this->_token = _token;
                }

                void Lexer::setType(int ttype) {
                    _type = ttype;
                }

                int Lexer::getType() {
                    return _type;
                }

                void Lexer::setChannel(int channel) {
                    _channel = channel;
                }

                int Lexer::getChannel() {
                    return _channel;
                }

                std::vector<Token*> Lexer::getAllTokens() {
                    std::vector<Token*> tokens = std::vector<Token*>();
                    Token *t = nextToken();
                    while (t->getType() != Token::_EOF) {
                        tokens.push_back(t);
                        t = nextToken();
                    }
                    return tokens;
                }

                void Lexer::recover(LexerNoViableAltException *e) {
                    if (_input->LA(1) != IntStream::_EOF) {
                        // skip a char and try again
                        getInterpreter()->consume(_input);
                    }
                }

                void Lexer::notifyListeners(LexerNoViableAltException *e) {
                    std::wstring text = _input->getText(misc::Interval::of(_tokenStartCharIndex, _input->index()));
                    std::wstring msg = std::wstring(L"token recognition error at: '") + getErrorDisplay(text) + std::wstring(L"'");

                    ANTLRErrorListener *listener = getErrorListenerDispatch();
                    listener->syntaxError(this, nullptr, _tokenStartLine, _tokenStartCharPositionInLine, msg, e);
                }

                std::wstring Lexer::getErrorDisplay(const std::wstring &s) {
                    antlrcpp::StringBuilder *buf = new antlrcpp::StringBuilder();
                    
                    for (size_t i = 0; i < s.length(); i++) {
                        char c = ((char*)s.c_str())[i];
                        buf->append(getErrorDisplay(c));
                    }
                    /*
                    for (auto c : s.toCharArray()) {
                        buf->append(getErrorDisplay(c));
                    }*/

                    return buf->toString();
                }

                std::wstring Lexer::getErrorDisplay(int c) {
                    std::wstring s = antlrcpp::StringConverterHelper::toString(static_cast<wchar_t>(c));
                    switch (c) {
                        case Token::_EOF :
                            s = L"<EOF>";
                            break;
                        case L'\n' :
                            s = L"\\n";
                            break;
                        case L'\t' :
                            s = L"\\t";
                            break;
                        case L'\r' :
                            s = L"\\r";
                            break;
                    }
                    return s;
                }

                std::wstring Lexer::getCharErrorDisplay(int c) {
                    std::wstring s = getErrorDisplay(c);
                    return std::wstring(L"'") + s + std::wstring(L"'");
                }

                void Lexer::recover(RecognitionException *re) {
                    //System.out.println("consuming char "+(char)input.LA(1)+" during recovery");
                    //re.printStackTrace();
                    // TODO: Do we lose character or line position information?
                    _input->consume();
                }

                void Lexer::InitializeInstanceFields() {
                    _factory = CommonTokenFactory::DEFAULT;
                    _tokenStartCharIndex = -1;
                    _tokenStartLine = 0;
                    _tokenStartCharPositionInLine = 0;
                    _hitEOF = false;
                    _channel = 0;
                    _type = 0;
                    _mode = Lexer::DEFAULT_MODE;
                }
            }
        }
    }
}
