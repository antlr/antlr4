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

#include "Token.h"
#include "Exceptions.h"
#include "assert.h"
#include "TokenSource.h"
#include "Arrays.h"
#include "Interval.h"
#include "RuleContext.h"
#include "WritableToken.h"

#include "UnbufferedTokenStream.h"

using namespace org::antlr::v4::runtime;

UnbufferedTokenStream::UnbufferedTokenStream(TokenSource* tokenSource) : UnbufferedTokenStream(tokenSource, 256) {
}

UnbufferedTokenStream::UnbufferedTokenStream(TokenSource* tokenSource, int bufferSize)
{
  InitializeInstanceFields();
  this->tokenSource = tokenSource;
  fill(1); // prime the pump
}

Token* UnbufferedTokenStream::get(size_t i) const
{ // get absolute index
  size_t bufferStartIndex = getBufferStartIndex();
  if (i < bufferStartIndex || i >= bufferStartIndex + tokens.size()) {
    throw IndexOutOfBoundsException(std::string("get(") + std::to_string(i) + std::string(") outside buffer: ")
      + std::to_string(bufferStartIndex) + std::string("..") + std::to_string(bufferStartIndex + tokens.size()));
  }
  return tokens[i - bufferStartIndex];
}

Token* UnbufferedTokenStream::LT(ssize_t i)
{
  if (i == -1) {
    return lastToken;
  }

  sync(i);
  ssize_t index = (ssize_t)p + i - 1;
  if (index < 0) {
    throw IndexOutOfBoundsException(std::string("LT(") + std::to_string(i) + std::string(") gives negative index"));
  }

  if (index >= (ssize_t)tokens.size()) {
    assert(tokens.size() > 0 && tokens.back()->getType() == EOF);
    return tokens.back();
  }

  return tokens[(size_t)index];
}

ssize_t UnbufferedTokenStream::LA(ssize_t i)
{
  return LT(i)->getType();
}

TokenSource* UnbufferedTokenStream::getTokenSource() const
{
  return tokenSource;
}

std::wstring UnbufferedTokenStream::getText()
{
  return L"";
}

std::wstring UnbufferedTokenStream::getText(RuleContext* ctx)
{
  return getText(ctx->getSourceInterval());
}

std::wstring UnbufferedTokenStream::getText(Token* start, Token* stop)
{
  return getText(misc::Interval(start->getTokenIndex(), stop->getTokenIndex()));
}

void UnbufferedTokenStream::consume()
{
  if (LA(1) == EOF) {
    throw IllegalStateException("cannot consume EOF");
  }

  // buf always has at least tokens[p==0] in this method due to ctor
  lastToken = tokens[p]; // track last token for LT(-1)

  // if we're at last token and no markers, opportunity to flush buffer
  if (p == tokens.size() - 1 && numMarkers == 0) {
    tokens.clear();
    p = 0;
    lastTokenBufferStart = lastToken;
  } else {
    ++p;
  }

  ++currentTokenIndex;
  sync(1);
}

/// <summary>
/// Make sure we have 'need' elements from current position <seealso cref="#p p"/>. Last valid
///  {@code p} index is {@code tokens.length-1}.  {@code p+need-1} is the tokens index 'need' elements
///  ahead.  If we need 1 element, {@code (p+1-1)==p} must be less than {@code tokens.length}.
/// </summary>
void UnbufferedTokenStream::sync(ssize_t want)
{
  ssize_t need = ((ssize_t)p + want - 1) - (ssize_t)tokens.size() + 1; // how many more elements we need?
  if (need > 0) {
    fill((size_t)need);
  }
}

/// <summary>
/// Add {@code n} elements to the buffer. Returns the number of tokens
/// actually added to the buffer. If the return value is less than {@code n},
/// then EOF was reached before {@code n} tokens could be added.
/// </summary>
size_t UnbufferedTokenStream::fill(size_t n)
{
  for (size_t i = 0; i < n; i++) {
    if (tokens.size() > 0 && tokens.back()->getType() == EOF) {
      return i;
    }

    Token* t = tokenSource->nextToken();
    add(t);
  }

  return n;
}

void UnbufferedTokenStream::add(Token* t)
{
  WritableToken *writable = dynamic_cast<WritableToken *>(t);
  if (writable != nullptr) {
    writable->setTokenIndex(int(getBufferStartIndex() + tokens.size()));
  }

  tokens.push_back(t);
}

/// <summary>
/// Return a marker that we can release later.
/// <p/>
/// The specific marker value used for this class allows for some level of
/// protection against misuse where {@code seek()} is called on a mark or
/// {@code release()} is called in the wrong order.
/// </summary>
ssize_t UnbufferedTokenStream::mark()
{
  if (numMarkers == 0) {
    lastTokenBufferStart = lastToken;
  }

  int mark = -numMarkers - 1;
  numMarkers++;
  return mark;
}

void UnbufferedTokenStream::release(ssize_t marker)
{
  ssize_t expectedMark = -numMarkers;
  if (marker != expectedMark) {
    throw IllegalStateException("release() called with an invalid marker.");
  }

  numMarkers--;
  if (numMarkers == 0) { // can we release buffer?
    if (p > 0) {
      // Copy tokens[p]..tokens[n-1] to tokens[0]..tokens[(n-1)-p], reset ptrs
      // p is last valid token; move nothing if p==n as we have no valid char
      std::vector<Token *>(tokens.begin() + (ssize_t)p, tokens.end()).swap(tokens);
      p = 0;
    }

    lastTokenBufferStart = lastToken;
  }
}

size_t UnbufferedTokenStream::index()
{
  return currentTokenIndex;
}

void UnbufferedTokenStream::seek(size_t index)
{ // seek to absolute index
  if (index == currentTokenIndex) {
    return;
  }

  if (index > currentTokenIndex) {
    sync(ssize_t(index - currentTokenIndex));
    index = std::min(index, getBufferStartIndex() + tokens.size() - 1);
  }

  size_t bufferStartIndex = getBufferStartIndex();
  if (bufferStartIndex > index) {
    throw IllegalArgumentException(std::string("cannot seek to negative index ") + std::to_string(index));
  }

  size_t i = index - bufferStartIndex;
  if (i >= tokens.size()) {
    throw UnsupportedOperationException(std::string("seek to index outside buffer: ") + std::to_string(index) +
      " not in " + std::to_string(bufferStartIndex) + ".." + std::to_string(bufferStartIndex + tokens.size()));
  }

  p = i;
  currentTokenIndex = index;
  if (p == 0) {
    lastToken = lastTokenBufferStart;
  } else {
    lastToken = tokens[p - 1];
  }
}

size_t UnbufferedTokenStream::size()
{
  throw UnsupportedOperationException("Unbuffered stream cannot know its size");
}

std::string UnbufferedTokenStream::getSourceName() const
{
  return tokenSource->getSourceName();
}

std::wstring UnbufferedTokenStream::getText(const misc::Interval &interval)
{
  size_t bufferStartIndex = getBufferStartIndex();
  size_t bufferStopIndex = bufferStartIndex + tokens.size() - 1;

  size_t start = (size_t)interval.a;
  size_t stop = (size_t)interval.b;
  if (start < bufferStartIndex || stop > bufferStopIndex) {
    throw UnsupportedOperationException(std::string("interval ") + interval.toString() +
      " not in token buffer window: " + std::to_string(bufferStartIndex) + ".." + std::to_string(bufferStopIndex));
  }

  size_t a = start - bufferStartIndex;
  size_t b = stop - bufferStartIndex;

  std::wstringstream ss;
  for (size_t i = a; i <= b; i++) {
    Token *t = tokens[i];
    if (i > 0)
      ss << L", ";
    ss << t->getText();
  }

  return ss.str();
}

size_t UnbufferedTokenStream::getBufferStartIndex() const
{
  return currentTokenIndex - p;
}

void UnbufferedTokenStream::InitializeInstanceFields()
{
  p = 0;
  numMarkers = 0;
  currentTokenIndex = 0;
}
