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

#include "misc/MurmurHash.h"
#include "Lexer.h"
#include "Exceptions.h"
#include "Vocabulary.h"

#include "misc/IntervalSet.h"

using namespace antlr4;
using namespace antlr4::misc;

IntervalSet const IntervalSet::COMPLETE_CHAR_SET = []() {
  IntervalSet complete = IntervalSet::of(Lexer::MIN_CHAR_VALUE, Lexer::MAX_CHAR_VALUE);
  complete.setReadOnly(true);
  return complete;
}();

IntervalSet const IntervalSet::EMPTY_SET = []() {
  IntervalSet empty;
  empty.setReadOnly(true);
  return empty;
}();

IntervalSet::IntervalSet() {
  InitializeInstanceFields();
}

IntervalSet::IntervalSet(const std::vector<Interval> &intervals) : IntervalSet() {
  _intervals = intervals;
}

IntervalSet::IntervalSet(const IntervalSet &set) : IntervalSet() {
  addAll(set);
}

IntervalSet::IntervalSet(int n, ...) : IntervalSet() {
  va_list vlist;
  va_start(vlist, n);

  for (int i = 0; i < n; i++) {
    add(va_arg(vlist, int));
  }
}

IntervalSet IntervalSet::of(int a) {
  return IntervalSet({ Interval(a, a) });
}

IntervalSet IntervalSet::of(int a, int b) {
  return IntervalSet({ Interval(a, b) });
}

void IntervalSet::clear() {
  if (_readonly) {
    throw IllegalStateException("can't alter read only IntervalSet");
  }
  _intervals.clear();
}

void IntervalSet::add(int el) {
  if (_readonly) {
    throw IllegalStateException("can't alter read only IntervalSet");
  }
  add(el, el);
}

void IntervalSet::add(int a, int b) {
  add(Interval(a, b));
}

void IntervalSet::add(const Interval &addition) {
  if (_readonly) {
    throw IllegalStateException("can't alter read only IntervalSet");
  }

  if (addition.b < addition.a) {
    return;
  }

  // find position in list
  for (auto iterator = _intervals.begin(); iterator != _intervals.end(); ++iterator) {
    Interval r = *iterator;
    if (addition == r) {
      return;
    }

    if (addition.adjacent(r) || !addition.disjoint(r)) {
      // next to each other, make a single larger interval
      Interval bigger = addition.Union(r);
      *iterator = bigger;

      // make sure we didn't just create an interval that
      // should be merged with next interval in list
      while (iterator + 1 != _intervals.end()) {
        Interval next = *++iterator;
        if (!bigger.adjacent(next) && bigger.disjoint(next)) {
          break;
        }

        // if we bump up against or overlap next, merge
        _intervals.erase(iterator);// remove this one
        --iterator; // move backwards to what we just set
        *iterator = bigger.Union(next); // set to 3 merged ones
        // ml: no need to advance iterator, we do that in the next round anyway. ++iterator; // first call to next after previous duplicates the result
      }
      return;
    }

    if (addition.startsBeforeDisjoint(r)) {
      // insert before r
      //--iterator;
      _intervals.insert(iterator, addition);
      return;
    }

    // if disjoint and after r, a future iteration will handle it
  }

  // ok, must be after last interval (and disjoint from last interval)
  // just add it
  _intervals.push_back(addition);
}

IntervalSet IntervalSet::Or(const std::vector<IntervalSet> &sets) {
  IntervalSet result;
  for (auto &s : sets) {
    result.addAll(s);
  }
  return result;
}

IntervalSet& IntervalSet::addAll(const IntervalSet &set) {
  // walk set and add each interval
  for (auto &interval : set._intervals) {
    add(interval);
  }
  return *this;
}

IntervalSet IntervalSet::complement(int minElement, int maxElement) const {
  return complement(IntervalSet::of(minElement, maxElement));
}

IntervalSet IntervalSet::complement(const IntervalSet &vocabulary) const {
  return vocabulary.subtract(*this);
}

IntervalSet IntervalSet::subtract(const IntervalSet &other) const {
  return subtract(*this, other);
}

IntervalSet IntervalSet::subtract(const IntervalSet &left, const IntervalSet &right) {
  if (left.isEmpty()) {
    return IntervalSet();
  }

  if (right.isEmpty()) {
    // right set has no elements; just return the copy of the current set
    return left;
  }

  IntervalSet result(left);
  size_t resultI = 0;
  size_t rightI = 0;
  while (resultI < result._intervals.size() && rightI < right._intervals.size()) {
    Interval &resultInterval = result._intervals[resultI];
    const Interval &rightInterval = right._intervals[rightI];

    // operation: (resultInterval - rightInterval) and update indexes

    if (rightInterval.b < resultInterval.a) {
      rightI++;
      continue;
    }

    if (rightInterval.a > resultInterval.b) {
      resultI++;
      continue;
    }

    Interval beforeCurrent;
    Interval afterCurrent;
    if (rightInterval.a > resultInterval.a) {
      beforeCurrent = Interval(resultInterval.a, rightInterval.a - 1);
    }

    if (rightInterval.b < resultInterval.b) {
      afterCurrent = Interval(rightInterval.b + 1, resultInterval.b);
    }

    if (beforeCurrent.a > -1) { // -1 is the default value
      if (afterCurrent.a > -1) {
        // split the current interval into two
        result._intervals[resultI] = beforeCurrent;
        result._intervals.insert(result._intervals.begin() + resultI + 1, afterCurrent);
        resultI++;
        rightI++;
      } else {
        // replace the current interval
        result._intervals[resultI] = beforeCurrent;
        resultI++;
      }
    } else {
      if (afterCurrent.a > -1) {
        // replace the current interval
        result._intervals[resultI] = afterCurrent;
        rightI++;
      } else {
        // remove the current interval (thus no need to increment resultI)
        result._intervals.erase(result._intervals.begin() + resultI);
      }
    }
  }

  // If rightI reached right.intervals.size(), no more intervals to subtract from result.
  // If resultI reached result.intervals.size(), we would be subtracting from an empty set.
  // Either way, we are done.
  return result;
}

IntervalSet IntervalSet::Or(const IntervalSet &a) const {
  IntervalSet result;
  result.addAll(*this);
  result.addAll(a);
  return result;
}

IntervalSet IntervalSet::And(const IntervalSet &other) const {
  IntervalSet intersection;
  size_t i = 0;
  size_t j = 0;

  // iterate down both interval lists looking for nondisjoint intervals
  while (i < _intervals.size() && j < other._intervals.size()) {
    Interval mine = _intervals[i];
    Interval theirs = other._intervals[j];

    if (mine.startsBeforeDisjoint(theirs)) {
      // move this iterator looking for interval that might overlap
      i++;
    } else if (theirs.startsBeforeDisjoint(mine)) {
      // move other iterator looking for interval that might overlap
      j++;
    } else if (mine.properlyContains(theirs)) {
      // overlap, add intersection, get next theirs
      intersection.add(mine.intersection(theirs));
      j++;
    } else if (theirs.properlyContains(mine)) {
      // overlap, add intersection, get next mine
      intersection.add(mine.intersection(theirs));
      i++;
    } else if (!mine.disjoint(theirs)) {
      // overlap, add intersection
      intersection.add(mine.intersection(theirs));

      // Move the iterator of lower range [a..b], but not
      // the upper range as it may contain elements that will collide
      // with the next iterator. So, if mine=[0..115] and
      // theirs=[115..200], then intersection is 115 and move mine
      // but not theirs as theirs may collide with the next range
      // in thisIter.
      // move both iterators to next ranges
      if (mine.startsAfterNonDisjoint(theirs)) {
        j++;
      } else if (theirs.startsAfterNonDisjoint(mine)) {
        i++;
      }
    }
  }

  return intersection;
}

bool IntervalSet::contains(int el) const {
  if (_intervals.empty())
    return false;
  if (el < _intervals[0].a) // list is sorted and el is before first interval; not here
    return false;

  for (auto &interval : _intervals) {
    if (el >= interval.a && el <= interval.b) {
      return true; // found in this interval
    }
  }
  return false;
}

bool IntervalSet::isEmpty() const {
  return _intervals.empty();
}

int IntervalSet::getSingleElement() const {
  if (_intervals.size() == 1) {
    if (_intervals[0].a == _intervals[0].b) {
      return _intervals[0].a;
    }
  }

  return Token::INVALID_TYPE; // XXX: this value is 0, but 0 is a valid interval range, how can that work?
}

int IntervalSet::getMaxElement() const {
  if (_intervals.empty()) {
    return Token::INVALID_TYPE;
  }

  return _intervals.back().b;
}

int IntervalSet::getMinElement() const {
  if (_intervals.empty()) {
    return Token::INVALID_TYPE;
  }

  return _intervals[0].a;
}

std::vector<Interval> IntervalSet::getIntervals() const {
  return _intervals;
}

size_t IntervalSet::hashCode() const {
  size_t hash = MurmurHash::initialize();
  for (auto &interval : _intervals) {
    hash = MurmurHash::update(hash, (size_t)interval.a);
    hash = MurmurHash::update(hash, (size_t)interval.b);
  }

  return MurmurHash::finish(hash, _intervals.size() * 2);
}

bool IntervalSet::operator == (const IntervalSet &other) const {
  if (_intervals.empty() && other._intervals.empty())
    return true;

  if (_intervals.empty() || other._intervals.empty())
    return false;

  return std::equal(_intervals.begin(), _intervals.end(), other._intervals.begin());
}

std::string IntervalSet::toString() const {
  return toString(false);
}

std::string IntervalSet::toString(bool elemAreChar) const {
  if (_intervals.empty()) {
    return "{}";
  }

  std::stringstream ss;
  size_t effectiveSize = size();
  if (effectiveSize > 1) {
    ss << "{";
  }

  bool firstEntry = true;
  for (auto &interval : _intervals) {
    if (!firstEntry)
      ss << ", ";
    firstEntry = false;

    int a = interval.a;
    int b = interval.b;
    if (a == b) {
      if (a == Token::EOF) {
        ss << "<EOF>";
      } else if (elemAreChar) {
        ss << "'" << static_cast<char>(a) << "'";
      } else {
        ss << a;
      }
    } else {
      if (elemAreChar) {
        ss << "'" << static_cast<char>(a) << "'..'" << static_cast<char>(b) << "'";
      } else {
        ss << a << ".." << b;
      }
    }
  }
  if (effectiveSize > 1) {
    ss << "}";
  }

  return ss.str();
}

std::string IntervalSet::toString(const std::vector<std::string> &tokenNames) const {
  return toString(dfa::Vocabulary::fromTokenNames(tokenNames));
}

std::string IntervalSet::toString(const dfa::Vocabulary &vocabulary) const {
  if (_intervals.empty()) {
    return "{}";
  }

  std::stringstream ss;
  size_t effectiveSize = size();
  if (effectiveSize > 1) {
    ss << "{";
  }

  bool firstEntry = true;
  for (auto &interval : _intervals) {
    if (!firstEntry)
      ss << ", ";
    firstEntry = false;

    ssize_t a = (ssize_t)interval.a;
    ssize_t b = (ssize_t)interval.b;
    if (a == b) {
      ss << elementName(vocabulary, a);
    } else {
      for (ssize_t i = a; i <= b; i++) {
        if (i > a) {
          ss << ", ";
        }
        ss << elementName(vocabulary, i);
      }
    }
  }
  if (effectiveSize > 1) {
    ss << "}";
  }

  return ss.str();
}

std::string IntervalSet::elementName(const std::vector<std::string> &tokenNames, ssize_t a) const {
  return elementName(dfa::Vocabulary::fromTokenNames(tokenNames), a);
}

std::string IntervalSet::elementName(const dfa::Vocabulary &vocabulary, ssize_t a) const {
  if (a == Token::EOF) {
    return "<EOF>";
  } else if (a == Token::EPSILON) {
    return "<EPSILON>";
  } else {
    return vocabulary.getDisplayName(a);
  }
}

size_t IntervalSet::size() const {
  size_t result = 0;
  for (auto &interval : _intervals) {
    result += (size_t)(interval.b - interval.a + 1);
  }
  return result;
}

std::vector<int> IntervalSet::toList() const {
  std::vector<int> result;
  for (auto &interval : _intervals) {
    size_t a = (size_t)interval.a;
    size_t b = (size_t)interval.b;
    for (size_t v = a; v <= b; v++) {
      result.push_back((int)v);
    }
  }
  return result;
}

std::set<int> IntervalSet::toSet() const {
  std::set<int> result;
  for (auto &interval : _intervals) {
    size_t a = (size_t)interval.a;
    size_t b = (size_t)interval.b;
    for (size_t v = a; v <= b; v++) {
      result.insert((int)v);
    }
  }
  return result;
}

int IntervalSet::get(int i) const {
  size_t index = 0;
  for (auto &interval : _intervals) {
    size_t a = (size_t)interval.a;
    size_t b = (size_t)interval.b;
    for (size_t v = a; v <= b; v++) {
      if (index == (size_t)i) {
        return (int)v;
      }
      index++;
    }
  }
  return -1;
}

void IntervalSet::remove(int el) {
  if (_readonly) {
    throw IllegalStateException("can't alter read only IntervalSet");
  }

  for (size_t i = 0; i < _intervals.size(); ++i) {
    Interval &interval = _intervals[i];
    size_t a = (size_t)interval.a;
    size_t b = (size_t)interval.b;
    if ((size_t)el < a) {
      break; // list is sorted and el is before this interval; not here
    }

    // if whole interval x..x, rm
    if ((size_t)el == a && (size_t)el == b) {
      _intervals.erase(_intervals.begin() + (long)i);
      break;
    }
    // if on left edge x..b, adjust left
    if ((size_t)el == a) {
      interval.a++;
      break;
    }
    // if on right edge a..x, adjust right
    if ((size_t)el == b) {
      interval.b--;
      break;
    }
    // if in middle a..x..b, split interval
    if ((size_t)el > a && (size_t)el < b) { // found in this interval
      size_t oldb = (size_t)interval.b;
      interval.b = el - 1; // [a..x-1]
      add(el + 1, (int)oldb); // add [x+1..b]

      break; // ml: not in the Java code but I believe we also should stop searching here, as we found x.
    }
  }
}

bool IntervalSet::isReadOnly() const {
  return _readonly;
}

void IntervalSet::setReadOnly(bool readonly) {
  if (_readonly && !readonly)
    throw IllegalStateException("Can't alter readonly IntervalSet");
  _readonly = readonly;
}

void IntervalSet::InitializeInstanceFields() {
  _readonly = false;
}
