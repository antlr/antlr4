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

#include "misc/Interval.h"
#include "Exceptions.h"
#include "support/StringUtils.h"

#include "UnbufferedCharStream.h"

using namespace antlrcpp;
using namespace antlr4;

UnbufferedCharStream::UnbufferedCharStream(std::wistream &input) : _input(input) {
  InitializeInstanceFields();

  // The vector's size is what used to be n in Java code.
  fill(1); // prime
}

void UnbufferedCharStream::consume() {
  if (LA(1) == EOF) {
    throw IllegalStateException("cannot consume EOF");
  }

  // buf always has at least data[p==0] in this method due to ctor
  _lastChar = _data[_p]; // track last char for LA(-1)

  if (_p == _data.size() - 1 && _numMarkers == 0) {
    size_t capacity = _data.capacity();
    _data.clear();
    _data.reserve(capacity);

    _p = 0;
    _lastCharBufferStart = _lastChar;
  } else {
    _p++;
  }

  _currentCharIndex++;
  sync(1);
}

void UnbufferedCharStream::sync(size_t want) {
  size_t need = (_p + want - 1) - _data.size() + 1; // how many more elements we need?
  if (need > 0) {
    fill(need);
  }
}

size_t UnbufferedCharStream::fill(size_t n) {
  for (size_t i = 0; i < n; i++) {
    if (_data.size() > 0 && _data.back() == static_cast<char32_t>(EOF)) { // TODO: we cannot encode -1 as this is not a valid code point
      return i;
    }

    try {
      char32_t c = nextChar();
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

char32_t UnbufferedCharStream::nextChar()  {
  wchar_t result = EOF;
  _input >> result;
  return result;
}

void UnbufferedCharStream::add(char32_t c) {
  _data += c;
}

ssize_t UnbufferedCharStream::LA(ssize_t i) {
  if (i == -1) { // special case
    return _lastChar;
  }
  sync((size_t)i);
  ssize_t index = (ssize_t)_p + i - 1;
  if (index < 0) {
    throw IndexOutOfBoundsException();
  }

  if ((size_t)index >= _data.size()) {
    return EOF;
  }

  ssize_t c = _data[(size_t)index];
  if (c == EOF) {
    return EOF;
  }
  return c;
}

ssize_t UnbufferedCharStream::mark() {
  if (_numMarkers == 0) {
    _lastCharBufferStart = _lastChar;
  }

  ssize_t mark = -(ssize_t)_numMarkers - 1;
  _numMarkers++;
  return mark;
}

void UnbufferedCharStream::release(ssize_t marker) {
  ssize_t expectedMark = -(ssize_t)_numMarkers;
  if (marker != expectedMark) {
    throw IllegalStateException("release() called with an invalid marker.");
  }

  _numMarkers--;
  if (_numMarkers == 0 && _p > 0) {
    _data.erase(0, _p);
    _p = 0;
    _lastCharBufferStart = _lastChar;
  }
}

size_t UnbufferedCharStream::index() {
  return _currentCharIndex;
}

void UnbufferedCharStream::seek(size_t index) {
  if (index == _currentCharIndex) {
    return;
  }

  if (index > _currentCharIndex) {
    sync(index - _currentCharIndex);
    index = std::min(index, getBufferStartIndex() + _data.size() - 1);
  }

  // index == to bufferStartIndex should set p to 0
  ssize_t i = (ssize_t)index - (ssize_t)getBufferStartIndex();
  if (i < 0) {
    throw IllegalArgumentException(std::string("cannot seek to negative index ") + std::to_string(index));
  } else if (i >= (ssize_t)_data.size()) {
    throw UnsupportedOperationException("Seek to index outside buffer: " + std::to_string(index) +
                                        " not in " + std::to_string(getBufferStartIndex()) + ".." +
                                        std::to_string(getBufferStartIndex() + _data.size()));
  }

  _p = (size_t)i;
  _currentCharIndex = index;
  if (_p == 0) {
    _lastChar = _lastCharBufferStart;
  } else {
    _lastChar = _data[_p - 1];
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

std::string UnbufferedCharStream::getText(const misc::Interval &interval) {
  if (interval.a < 0 || interval.b < interval.a - 1) {
    throw IllegalArgumentException("invalid interval");
  }

  size_t bufferStartIndex = getBufferStartIndex();
  if (!_data.empty() && _data.back() == 0xFFFF) {
    if ((size_t)(interval.a + interval.length()) > bufferStartIndex + _data.size()) {
      throw IllegalArgumentException("the interval extends past the end of the stream");
    }
  }

  if ((size_t)interval.a < bufferStartIndex || (size_t)interval.b >= bufferStartIndex + _data.size()) {
    throw UnsupportedOperationException("interval " + interval.toString() + " outside buffer: " +
      std::to_string(bufferStartIndex) + ".." + std::to_string(bufferStartIndex + _data.size() - 1));
  }
  // convert from absolute to local index
  size_t i = (size_t)interval.a - bufferStartIndex;
  return utfConverter.to_bytes(_data.substr(i, (size_t)interval.length()));
}

size_t UnbufferedCharStream::getBufferStartIndex() const {
  return _currentCharIndex - _p;
}

void UnbufferedCharStream::InitializeInstanceFields() {
  _p = 0;
  _numMarkers = 0;
  _lastChar = -1;
  _lastCharBufferStart = 0;
  _currentCharIndex = 0;
}
