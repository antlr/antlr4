#pragma once 

#include "Declarations.h"
#include "Token.h"
#include "Exceptions.h"
#include "assert.h"
#include "TokenSource.h"
#include "Arrays.h"
#include "Interval.h"
#include "RuleContext.h"
#include "WritableToken.h"


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
				UnbufferedTokenStream<T>::UnbufferedTokenStream(TokenSource *tokenSource) { //this(tokenSource, 256);
				}
                
				template<typename T>
				UnbufferedTokenStream<T>::UnbufferedTokenStream(TokenSource *tokenSource, int bufferSize) {
					InitializeInstanceFields();
					this->tokenSource = tokenSource;
					tokens = new std::vector<Token*>(); // TODO do we need to actually new this?
					n = 0;
					fill(1); // prime the pump
				}
                
				template<typename T>
                Token *UnbufferedTokenStream<T>::get(int i)  { // get absolute index
					int bufferStartIndex = getBufferStartIndex();
					if (i < bufferStartIndex || i >= bufferStartIndex + n) {
						throw new IndexOutOfBoundsException(std::wstring(L"get(") + std::to_wstring(i) + std::wstring(L") outside buffer: ") + std::to_wstring(bufferStartIndex) + std::wstring(L"..") + std::to_wstring(bufferStartIndex + n));
					}
					return tokens[i - bufferStartIndex];
				}
                
				template<typename T>
                Token *UnbufferedTokenStream<T>::LT(int i)  {
					if (i == -1) {
						return lastToken;
					}
                    
					sync(i);
					int index = p + i - 1;
					if (index < 0) {
						throw new IndexOutOfBoundsException(std::wstring(L"LT(") + std::to_wstring(i) + std::wstring(L") gives negative index"));
					}
                    
					if (index >= n) {
						assert(n > 0 && tokens[n - 1]->getType() == Token::_EOF);
						return tokens[n - 1];
					}
                    
					return tokens[index];
				}
                
				template<typename T>
                int UnbufferedTokenStream<T>::LA(int i)  {
					return LT(i)->getType();
				}
                
				template<typename T>
                TokenSource *UnbufferedTokenStream<T>::getTokenSource()  {
					return tokenSource;
				}
                
				template<typename T>
                std::wstring UnbufferedTokenStream<T>::getText()  {
					return L"";
				}
                
				template<typename T>
                std::wstring UnbufferedTokenStream<T>::getText(RuleContext *ctx)  {
					return getText(ctx->getSourceInterval());
				}
                
				template<typename T>
                std::wstring UnbufferedTokenStream<T>::getText(Token *start, Token *stop)  {
					return getText(misc::Interval::of(start->getTokenIndex(), stop->getTokenIndex()));
				}
                
				template<typename T>
                void UnbufferedTokenStream<T>::consume()  {
					if (LA(1) == Token::_EOF) {
						throw new IllegalStateException(L"cannot consume EOF");
					}
                    
					// buf always has at least tokens[p==0] in this method due to ctor
					lastToken = tokens[p]; // track last token for LT(-1)
                    
					// if we're at last token and no markers, opportunity to flush buffer
					if (p == n - 1 && numMarkers == 0) {
						n = 0;
						p = -1; // p++ will leave this at 0
						lastTokenBufferStart = lastToken;
					}
                    
					p++;
					currentTokenIndex++;
					sync(1);
				}
                
				/// <summary>
				/// Make sure we have 'need' elements from current position <seealso cref="#p p"/>. Last valid
				///  {@code p} index is {@code tokens.length-1}.  {@code p+need-1} is the tokens index 'need' elements
				///  ahead.  If we need 1 element, {@code (p+1-1)==p} must be less than {@code tokens.length}.
				/// </summary>
				template<typename T>
                void UnbufferedTokenStream<T>::sync(int want) {
					int need = (p + want - 1) - n + 1; // how many more elements we need?
					if (need > 0) {
						fill(need);
					}
				}
                
				/// <summary>
				/// Add {@code n} elements to the buffer. Returns the number of tokens
				/// actually added to the buffer. If the return value is less than {@code n},
				/// then EOF was reached before {@code n} tokens could be added.
				/// </summary>
				template<typename T>
                int UnbufferedTokenStream<T>::fill(int n) {
					for (int i = 0; i < n; i++) {
						if (this->n > 0 && tokens[this->n - 1]->getType() == Token::_EOF) {
							return i;
						}
                        
						Token *t = tokenSource->nextToken();
						add(t);
					}
                    
					return n;
				}
				template<typename T>
                void UnbufferedTokenStream<T>::add(Token *t) {
					if (n >= tokens.size()) {
						tokens = Arrays::copyOf(tokens, tokens.size() * 2);
					}
                    
					if (dynamic_cast<WritableToken*>(t) != nullptr) {
						(static_cast<WritableToken*>(t))->setTokenIndex(getBufferStartIndex() + n);
					}
                    
					tokens[n++] = t;
				}
                
				/// <summary>
				/// Return a marker that we can release later.
				/// <p/>
				/// The specific marker value used for this class allows for some level of
				/// protection against misuse where {@code seek()} is called on a mark or
				/// {@code release()} is called in the wrong order.
				/// </summary>
				template<typename T>
                int UnbufferedTokenStream<T>::mark()  {
					if (numMarkers == 0) {
						lastTokenBufferStart = lastToken;
					}
                    
					int mark = -numMarkers - 1;
					numMarkers++;
					return mark;
				}
                
				template<typename T>
                void UnbufferedTokenStream<T>::release(int marker) {
					int expectedMark = -numMarkers;
					if (marker != expectedMark) {
						throw IllegalStateException(L"release() called with an invalid marker.");
					}
                    
					numMarkers--;
					if (numMarkers == 0) { // can we release buffer?
						if (p > 0) {
							// Copy tokens[p]..tokens[n-1] to tokens[0]..tokens[(n-1)-p], reset ptrs
							// p is last valid token; move nothing if p==n as we have no valid char
							arraycopy(tokens, p, tokens, 0, n - p); // shift n-p tokens from p to 0
							n = n - p;
							p = 0;
						}
                        
						lastTokenBufferStart = lastToken;
					}
				}
				template<typename T>
                int UnbufferedTokenStream<T>::index()  {
					return currentTokenIndex;
				}
                
				template<typename T>
                void UnbufferedTokenStream<T>::seek(int index) { // seek to absolute index
					if (index == currentTokenIndex) {
						return;
					}
                    
					if (index > currentTokenIndex) {
						sync(index - currentTokenIndex);
						index = std::min(index, getBufferStartIndex() + n - 1);
					}
                    
					int bufferStartIndex = getBufferStartIndex();
					int i = index - bufferStartIndex;
					if (i < 0) {
						throw new IllegalArgumentException(std::wstring(L"cannot seek to negative index ") + std::to_wstring(index));
					}
					else if (i >= n) {
						throw new UnsupportedOperationException(std::wstring(L"seek to index outside buffer: ") + std::to_wstring(index) + std::wstring(L" not in ") + std::to_wstring(bufferStartIndex) + std::wstring(L"..") + std::to_wstring(bufferStartIndex + n));
					}
                    
					p = i;
					currentTokenIndex = index;
					if (p == 0) {
						lastToken = lastTokenBufferStart;
					}
					else {
						lastToken = tokens[p - 1];
					}
				}
                
				template<typename T>
                size_t UnbufferedTokenStream<T>::size()  {
					throw new UnsupportedOperationException(L"Unbuffered stream cannot know its size");
				}
                
				template<typename T>
                std::string UnbufferedTokenStream<T>::getSourceName()  {
					return tokenSource->getSourceName();
				}
                
				template<typename T>
                std::wstring UnbufferedTokenStream<T>::getText(misc::Interval *interval)  {
					int bufferStartIndex = getBufferStartIndex();
					int bufferStopIndex = bufferStartIndex + tokens.size() - 1;
                    
					int start = interval->a;
					int stop = interval->b;
					if (start < bufferStartIndex || stop > bufferStopIndex) {
						throw new UnsupportedOperationException(std::wstring(L"interval ") + interval->toString() + std::wstring(L" not in token buffer window: ") + std::to_wstring(bufferStartIndex) + std::wstring(L"..") + std::to_wstring(bufferStopIndex));
					}
                    
					int a = start - bufferStartIndex;
					int b = stop - bufferStartIndex;
                    
					StringBuilder *buf = new StringBuilder();
					for (int i = a; i <= b; i++) {
						Token *t = tokens[i];
						buf->append(t->getText());
					}
                    
					return buf->toString();
				}
                
				template<typename T>
				int UnbufferedTokenStream<T>::getBufferStartIndex() {
					return currentTokenIndex - p;
				}
                
				template<typename T>
				void UnbufferedTokenStream<T>::InitializeInstanceFields() {
					n = 0;
					p = 0;
					numMarkers = 0;
					currentTokenIndex = 0;
				}
                
                
			}
            
        }
    }
}
