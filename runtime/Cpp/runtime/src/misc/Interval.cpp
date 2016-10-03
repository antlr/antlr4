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

using namespace antlr4::misc;


size_t antlr4::misc::numericToSymbol(ssize_t v) {
  return (size_t)v;
}

ssize_t antlr4::misc::symbolToNumeric(size_t v) {
  return (ssize_t)v;
}

Interval const Interval::INVALID;

Interval::Interval() : Interval((ssize_t)-1, -2) { // Need an explicit cast here for VS.
}

Interval::Interval(size_t a_, size_t b_) : Interval(symbolToNumeric(a_), symbolToNumeric(b_)) {
}

Interval::Interval(ssize_t a_, ssize_t b_, bool autoExtend) {
  a = a_;
  b = b_;

  // XXX: temporary hack to make the full Unicode range available.
  if (autoExtend && b == 0xFFFF) {
    b = 0x10FFFF;
  }
}

size_t Interval::length() const {
  if (b < a) {
    return 0;
  }
  return size_t(b - a + 1);
}

bool Interval::operator == (const Interval &other) const {
  return a == other.a && b == other.b;
}

size_t Interval::hashCode() const {
  size_t hash = 23;
  hash = hash * 31 + (size_t)a;
  hash = hash * 31 + (size_t)b;
  return hash;
}

bool Interval::startsBeforeDisjoint(const Interval &other) const {
  return a < other.a && b < other.a;
}

bool Interval::startsBeforeNonDisjoint(const Interval &other) const {
  return a <= other.a && b >= other.a;
}

bool Interval::startsAfter(const Interval &other) const {
  return a > other.a;
}

bool Interval::startsAfterDisjoint(const Interval &other) const {
  return a > other.b;
}

bool Interval::startsAfterNonDisjoint(const Interval &other) const {
  return a > other.a && a <= other.b; // b >= other.b implied
}

bool Interval::disjoint(const Interval &other) const {
  return startsBeforeDisjoint(other) || startsAfterDisjoint(other);
}

bool Interval::adjacent(const Interval &other) const {
  return a == other.b + 1 || b == other.a - 1;
}

bool Interval::properlyContains(const Interval &other) const {
  return other.a >= a && other.b <= b;
}

Interval Interval::Union(const Interval &other) const {
  return Interval(std::min(a, other.a), std::max(b, other.b));
}

Interval Interval::intersection(const Interval &other) const {
  return Interval(std::max(a, other.a), std::min(b, other.b));
}

std::string Interval::toString() const {
  return std::to_string(a) + ".." + std::to_string(b);
}
