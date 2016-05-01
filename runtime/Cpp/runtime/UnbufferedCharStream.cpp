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

#include "Interval.h"
#include "Exceptions.h"

#include "UnbufferedCharStream.h"

using namespace org::antlr::v4::runtime;

UnbufferedCharStream::UnbufferedCharStream(std::wistream &input, size_t bufferSize) : input(input) {
  InitializeInstanceFields();

  // The vector's size is what used to be n in Java code, while the vector's capacity is the allocated
  // buffer length in Java.
  data.reserve(bufferSize);
  fill(1); // prime
}

void UnbufferedCharStream::consume() {
  if (LA(1) == EOF) {
    throw IllegalStateException("cannot consume EOF");
  }

  // buf always has at least data[p==0] in this method due to ctor
  lastChar = data[p]; // track last char for LA(-1)

  if (p == data.size() - 1 && numMarkers == 0) {
    size_t capacity = data.capacity();
    data.clear();
    data.reserve(capacity);

    p = 0;
    lastCharBufferStart = lastChar;
  } else {
    p++;
  }

  currentCharIndex++;
  sync(1);
}

void UnbufferedCharStream::sync(size_t want) {
  size_t need = (p + want - 1) - data.size() + 1; // how many more elements we need?
  if (need > 0) {
    fill(need);
  }
}

size_t UnbufferedCharStream::fill(size_t n) {
  for (size_t i = 0; i < n; i++) {
    if (data.size() > 0 && data.back() == static_cast<wchar_t>(EOF)) {
      return i;
    }

    try {
      size_t c = nextChar();
      add(c);
    } catch (IOException &ioe) {
#if defined(_MSC_FULL_VER) && _MSC_FULL_VER < 190023026
      // throw_with_nested is not available before VS 2015.
      throw ioe;
#else
      std::throw_with_nested(RuntimeException());
#endif
    }
  }

  return n;
}

size_t UnbufferedCharStream::nextChar()  {
  return (size_t)input.get();
}

void UnbufferedCharStream::add(size_t c) {
  data.push_back(static_cast<wchar_t>(c));
}

ssize_t UnbufferedCharStream::LA(ssize_t i) {
  if (i == -1) { // special case
    return lastChar;
  }
  sync((size_t)i);
  ssize_t index = (ssize_t)p + i - 1;
  if (index < 0) {
    throw IndexOutOfBoundsException();
  }

  if ((size_t)index >= data.size()) {
    return EOF;
  }

  ssize_t c = data[(size_t)index];
  if (c == EOF) {
    return EOF;
  }
  return c;
}

ssize_t UnbufferedCharStream::mark() {
  if (numMarkers == 0) {
    lastCharBufferStart = lastChar;
  }

  ssize_t mark = -(ssize_t)numMarkers - 1;
  numMarkers++;
  return mark;
}

void UnbufferedCharStream::release(ssize_t marker) {
  ssize_t expectedMark = -(ssize_t)numMarkers;
  if (marker != expectedMark) {
    throw IllegalStateException("release() called with an invalid marker.");
  }

  numMarkers--;
  if (numMarkers == 0 && p > 0) {
    data.erase(0, p);
    p = 0;
    lastCharBufferStart = lastChar;
  }
}

size_t UnbufferedCharStream::index() {
  return currentCharIndex;
}

void UnbufferedCharStream::seek(size_t index) {
  if (index == currentCharIndex) {
    return;
  }

  if (index > currentCharIndex) {
    sync(index - currentCharIndex);
    index = std::min(index, getBufferStartIndex() + data.size() - 1);
  }

  // index == to bufferStartIndex should set p to 0
  ssize_t i = (ssize_t)index - (ssize_t)getBufferStartIndex();
  if (i < 0) {
    throw IllegalArgumentException(std::string("cannot seek to negative index ") + std::to_string(index));
  } else if (i >= (ssize_t)data.size()) {
    throw UnsupportedOperationException(std::string("seek to index outside buffer: ") + std::to_string(index) +
                                        std::string(" not in ") + std::to_string(getBufferStartIndex()) + ".." +
                                        std::to_string(getBufferStartIndex() + data.size()));
  }

  p = (size_t)i;
  currentCharIndex = index;
  if (p == 0) {
    lastChar = lastCharBufferStart;
  } else {
    lastChar = data[p - 1];
  }
}

size_t UnbufferedCharStream::size() {
  throw UnsupportedOperationException("Unbuffered stream cannot know its size");
}

std::string UnbufferedCharStream::getSourceName() const {
  if (name.empty()) {
    return UNKNOWN_SOURCE_NAME;
  }
  
  return name;
}

std::wstring UnbufferedCharStream::getText(const misc::Interval &interval) {
  if (interval.a < 0 || interval.b < interval.a - 1) {
    throw IllegalArgumentException("invalid interval");
  }

  size_t bufferStartIndex = getBufferStartIndex();
  if (!data.empty() && data.back() == WCHAR_MAX) {
    if ((size_t)(interval.a + interval.length()) > bufferStartIndex + data.size()) {
      throw IllegalArgumentException("the interval extends past the end of the stream");
    }
  }

  if ((size_t)interval.a < bufferStartIndex || (size_t)interval.b >= bufferStartIndex + data.size()) {
    throw UnsupportedOperationException(std::string("interval ") + interval.toString() + " outside buffer: " +
                                        std::to_string(bufferStartIndex) + ".." + std::to_string(bufferStartIndex + data.size() - 1));
  }
  // convert from absolute to local index
  size_t i = (size_t)interval.a - bufferStartIndex;
  return std::wstring(data, i, (size_t)interval.length());
}

size_t UnbufferedCharStream::getBufferStartIndex() const {
  return currentCharIndex - p;
}

void UnbufferedCharStream::InitializeInstanceFields() {
  p = 0;
  numMarkers = 0;
  lastChar = -1;
  lastCharBufferStart = 0;
  currentCharIndex = 0;
}
