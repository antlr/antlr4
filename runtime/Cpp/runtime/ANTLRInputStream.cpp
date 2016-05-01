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

#include "Exceptions.h"
#include "Interval.h"
#include "IntStream.h"

#include "Arrays.h"
#include "CPPUtils.h"

#include "ANTLRInputStream.h"

using namespace org::antlr::v4::runtime;
using namespace antlrcpp;

using misc::Interval;

ANTLRInputStream::ANTLRInputStream(const std::wstring &input) : data(input) {
  InitializeInstanceFields();
}

ANTLRInputStream::ANTLRInputStream(const wchar_t data[], size_t numberOfActualCharsInArray)
  : ANTLRInputStream(std::wstring(data, numberOfActualCharsInArray)) {
}

ANTLRInputStream::ANTLRInputStream(std::wistream &stream) : ANTLRInputStream(stream, READ_BUFFER_SIZE) {
}

ANTLRInputStream::ANTLRInputStream(std::wistream &stream, std::streamsize readChunkSize) : ANTLRInputStream() {
  load(stream, readChunkSize);
}

void ANTLRInputStream::load(std::wistream &stream, std::streamsize readChunkSize) {
  stream.seekg(0, stream.beg);
  if (!stream.good()) // No fail, bad or EOF.
    return;

  data.clear();

  if (readChunkSize == 0) {
    readChunkSize = READ_BUFFER_SIZE;
  }

  wchar_t *buffer = new wchar_t[readChunkSize]; /* mem check: freed in finally block */
  auto onExit = finally([buffer] {
    delete[] buffer;
  });

  while (!stream.eof()) {
    stream.read(buffer, readChunkSize);
    data.append(buffer, (size_t)std::min(stream.gcount(), readChunkSize));
  }
}

void ANTLRInputStream::reset() {
  p = 0;
}

void ANTLRInputStream::consume() {
  if (p >= data.size()) {
    assert(LA(1) == EOF);
    throw IllegalStateException("cannot consume EOF");
  }

  if (p < data.size()) {
    p++;
  }
}

ssize_t ANTLRInputStream::LA(ssize_t i) {
  if (i == 0) {
    return 0; // undefined
  }

  ssize_t position = (ssize_t)p;
  if (i < 0) {
    i++; // e.g., translate LA(-1) to use offset i=0; then data[p+0-1]
    if ((position + i - 1) < 0) {
      return EOF; // invalid; no char before first char
    }
  }

  if ((position + i - 1) >= (ssize_t)data.size()) {
    return EOF;
  }

  return data[(size_t)(position + i - 1)];
}

ssize_t ANTLRInputStream::LT(ssize_t i) {
  return LA(i);
}

size_t ANTLRInputStream::index() {
  return p;
}

size_t ANTLRInputStream::size() {
  return data.size();
}

// Mark/release do nothing. We have entire buffer.
ssize_t ANTLRInputStream::mark() {
  return -1;
}

void ANTLRInputStream::release(ssize_t marker) {
}

void ANTLRInputStream::seek(size_t index) {
  if (index <= p) {
    p = index; // just jump; don't update stream state (line, ...)
    return;
  }
  // seek forward, consume until p hits index or n (whichever comes first)
  index = std::min(index, data.size());
  while (p < index) {
    consume();
  }
}

std::wstring ANTLRInputStream::getText(const Interval &interval) {
  size_t start = (size_t)interval.a;
  size_t stop = (size_t)interval.b;

  if (stop >= data.size()) {
    stop = data.size() - 1;
  }

  size_t count = stop - start + 1;
  if (start >= data.size()) {
    return L"";
  }

  return data.substr(start, count);
}

std::string ANTLRInputStream::getSourceName() const {
  if (name.empty()) {
    return IntStream::UNKNOWN_SOURCE_NAME;
  }
  return name;
}

std::wstring ANTLRInputStream::toString() const {
  return data;
}

void ANTLRInputStream::InitializeInstanceFields() {
  p = 0;
}
