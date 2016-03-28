/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
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

#import <XCTest/XCTest.h>

#include "MurmurHash.h"
#include "Interval.h"
#include "IntervalSet.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::misc;

@interface MiscClassTests : XCTestCase

@end

@implementation MiscClassTests

- (void)setUp {
  [super setUp];
  // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
  // Put teardown code here. This method is called after the invocation of each test method in the class.
  [super tearDown];
}

- (void)testMurmurHash {
  XCTAssertEqual(MurmurHash::initialize(), (size_t)0);
  XCTAssertEqual(MurmurHash::initialize(31), (size_t)31);

  XCTAssertEqual(MurmurHash::hashCode<size_t>({}, 0, 0), (size_t)0);

  // In absence of real test vectors (64bit) for murmurhash I instead check if I can find duplicate hash values
  // in a deterministic and a random sequence of 100K values each.
  std::set<size_t> hashs;
  for (size_t i = 0; i < 100000; ++i) {
    size_t data[] = { i, (size_t)(i * M_PI),  arc4random()};
    size_t hash = MurmurHash::hashCode(data, 3, 0);
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), (size_t)100000, @"At least one duplicat hash found.");

  hashs.clear();
  for (size_t i = 0; i < 100000; ++i) {
    size_t data[] = { i, (size_t)(i * M_PI)};
    size_t hash = MurmurHash::hashCode(data, 2, 0);
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), (size_t)100000, @"At least one duplicat hash found.");

  // Another test with fixed input but varying seeds.
  // Note: the higher the seed the less LSDs are in the result (for small input data).
  hashs.clear();
  for (size_t i = 0; i < 100000; ++i) {
    size_t data[] = { L'µ', 'a', '@', '1' };
    size_t hash = MurmurHash::hashCode(data, 4, i);
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), (size_t)100000, @"At least one duplicat hash found.");
}

- (void)testInterval {
  // The Interval class contains no error handling (checks for invalid intervals), hence some of the results
  // look strange as we test of course such intervals as well.
  XCTAssertEqual(Interval().length(), 0);
  XCTAssertEqual(Interval(0, 0).length(), 1); // Remember: it's an inclusive interval.
  XCTAssertEqual(Interval(100, 100).length(), 1);
  XCTAssertEqual(Interval(-1, -1).length(), 1); // Unwanted behavior: negative ranges.
  XCTAssertEqual(Interval(-1, -2).length(), 0);
  XCTAssertEqual(Interval(100, 50).length(), 0);

  XCTAssert(Interval() == Interval(-1, -2));
  XCTAssert(Interval(0, 0) == Interval(0, 0));
  XCTAssertFalse(Interval(0, 1) == Interval(1, 2));

  XCTAssertEqual(Interval().hashCode(), (size_t)22070);
  XCTAssertEqual(Interval(0, 0).hashCode(), (size_t)22103);
  XCTAssertEqual(Interval(10, 2000).hashCode(), (size_t)24413);

  // Results for the interval test functions in this order:
  // startsBeforeDisjoint
  // startsBeforeNonDisjoint
  // startsAfter
  // startsAfterDisjoint
  // startsAfterNonDisjoint
  // disjoint
  // adjacent
  // properlyContains

  typedef std::vector<bool> TestResults;
  struct TestEntry { size_t runningNumber; Interval interval1, interval2; TestResults results; };
  std::vector<TestEntry> testData = {
    // Extreme cases + invalid intervals.
    { 0, Interval(), Interval(10, 20), { true, false, false, false, false, true, false, false } },
    { 1, Interval(1, 1), Interval(1, 1), { false, true, false, false, false, false, false, true } },
    { 2, Interval(10000, 10000), Interval(10000, 10000), { false, true, false, false, false, false, false, true } },
    { 3, Interval(100, 10), Interval(100, 10), { false, false, false, true, false, true, false, true } },
    { 4, Interval(100, 10), Interval(10, 100), { false, false, true, false, true, false, false, false } },
    { 5, Interval(10, 100), Interval(100, 10), { false, true, false, false, false, false, false, true } },

    // First starts before second. End varies.
    { 20, Interval(10, 12), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 21, Interval(10, 12), Interval(13, 100), { true, false, false, false, false, true, true, false } },
    { 22, Interval(10, 12), Interval(14, 100), { true, false, false, false, false, true, false, false } },
    { 23, Interval(10, 13), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 24, Interval(10, 14), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 25, Interval(10, 99), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 26, Interval(10, 100), Interval(12, 100), { false, true, false, false, false, false, false, true } },
    { 27, Interval(10, 101), Interval(12, 100), { false, true, false, false, false, false, false, true } },
    { 28, Interval(10, 1000), Interval(12, 100), { false, true, false, false, false, false, false, true } },

    // First and second start equal. End varies.
    { 30, Interval(12, 12), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 31, Interval(12, 12), Interval(13, 100), { true, false, false, false, false, true, true, false } },
    { 32, Interval(12, 12), Interval(14, 100), { true, false, false, false, false, true, false, false } },
    { 33, Interval(12, 13), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 34, Interval(12, 14), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 35, Interval(12, 99), Interval(12, 100), { false, true, false, false, false, false, false, false } },
    { 36, Interval(12, 100), Interval(12, 100), { false, true, false, false, false, false, false, true } },
    { 37, Interval(12, 101), Interval(12, 100), { false, true, false, false, false, false, false, true } },
    { 38, Interval(12, 1000), Interval(12, 100), { false, true, false, false, false, false, false, true } },

    // First starts after second. End varies.
    { 40, Interval(15, 12), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 41, Interval(15, 12), Interval(13, 100), { false, false, true, false, true, false, true, false } },
    { 42, Interval(15, 12), Interval(14, 100), { false, false, true, false, true, false, false, false } },
    { 43, Interval(15, 13), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 44, Interval(15, 14), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 45, Interval(15, 99), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 46, Interval(15, 100), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 47, Interval(15, 101), Interval(12, 100), { false, false, true, false, true, false, false, false } },
    { 48, Interval(15, 1000), Interval(12, 100), { false, false, true, false, true, false, false, false } },

    // First ends before second. Start varies.
    { 50, Interval(10, 90), Interval(20, 100), { false, true, false, false, false, false, false, false } },
    { 51, Interval(19, 90), Interval(20, 100), { false, true, false, false, false, false, false, false } },
    { 52, Interval(20, 90), Interval(20, 100), { false, true, false, false, false, false, false, false } },
    { 53, Interval(21, 90), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 54, Interval(98, 90), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 55, Interval(99, 90), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 56, Interval(100, 90), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 57, Interval(101, 90), Interval(20, 100), { false, false, true, true, false, true, true, false } },
    { 58, Interval(1000, 90), Interval(20, 100), { false, false, true, true, false, true, false, false } },

    // First and second end equal. Start varies.
    { 60, Interval(10, 100), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 61, Interval(19, 100), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 62, Interval(20, 100), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 63, Interval(21, 100), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 64, Interval(98, 100), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 65, Interval(99, 100), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 66, Interval(100, 100), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 67, Interval(101, 100), Interval(20, 100), { false, false, true, true, false, true, true, false } },
    { 68, Interval(1000, 100), Interval(20, 100), { false, false, true, true, false, true, false, false } },

    // First ends after second. Start varies.
    { 70, Interval(10, 1000), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 71, Interval(19, 1000), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 72, Interval(20, 1000), Interval(20, 100), { false, true, false, false, false, false, false, true } },
    { 73, Interval(21, 1000), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 74, Interval(98, 1000), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 75, Interval(99, 1000), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 76, Interval(100, 1000), Interval(20, 100), { false, false, true, false, true, false, false, false } },
    { 77, Interval(101, 1000), Interval(20, 100), { false, false, true, true, false, true, true, false } },
    { 78, Interval(1000, 1000), Interval(20, 100), { false, false, true, true, false, true, false, false } },

    // It's possible to add more tests with borders that touch each other (e.g. first starts before/on/after second
    // and first ends directly before/after second. However, such cases are not handled differently in the Interval class
    // (only adjacent intervals, where first ends directly before second starts and vice versa. So I ommitted them here.
  };

  for (auto &entry : testData) {
    XCTAssert(entry.interval1.startsBeforeDisjoint(entry.interval2) == entry.results[0], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.startsBeforeNonDisjoint(entry.interval2) == entry.results[1], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.startsAfter(entry.interval2) == entry.results[2], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.startsAfterDisjoint(entry.interval2) == entry.results[3], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.startsAfterNonDisjoint(entry.interval2) == entry.results[4], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.disjoint(entry.interval2) == entry.results[5], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.adjacent(entry.interval2) == entry.results[6], @"entry: %zu", entry.runningNumber);
    XCTAssert(entry.interval1.properlyContains(entry.interval2) == entry.results[7], @"entry: %zu", entry.runningNumber);
  }

  XCTAssert(Interval().Union(Interval(10, 100)) == Interval(-1, 100));
  XCTAssert(Interval(10, 10).Union(Interval(10, 100)) == Interval(10, 100));
  XCTAssert(Interval(10, 11).Union(Interval(10, 100)) == Interval(10, 100));
  XCTAssert(Interval(10, 1000).Union(Interval(10, 100)) == Interval(10, 1000));
  XCTAssert(Interval(1000, 30).Union(Interval(10, 100)) == Interval(10, 100));
  XCTAssert(Interval(1000, 2000).Union(Interval(10, 100)) == Interval(10, 2000));
  XCTAssert(Interval(500, 2000).Union(Interval(10, 1000)) == Interval(10, 2000));

  XCTAssert(Interval().intersection(Interval(10, 100)) == Interval(10, -2));
  XCTAssert(Interval(10, 10).intersection(Interval(10, 100)) == Interval(10, 10));
  XCTAssert(Interval(10, 11).intersection(Interval(10, 100)) == Interval(10, 11));
  XCTAssert(Interval(10, 1000).intersection(Interval(10, 100)) == Interval(10, 100));
  XCTAssert(Interval(1000, 30).intersection(Interval(10, 100)) == Interval(1000, 30));
  XCTAssert(Interval(1000, 2000).intersection(Interval(10, 100)) == Interval(1000, 100));
  XCTAssert(Interval(500, 2000).intersection(Interval(10, 1000)) == Interval(500, 1000));

  XCTAssert(Interval().toString() == L"-1..-2");
  XCTAssert(Interval(10, 10).toString() == L"10..10");
  XCTAssert(Interval(1000, 2000).toString() == L"1000..2000");
  XCTAssert(Interval(500, INT_MAX).toString() == L"500.." + std::to_wstring(INT_MAX));
}

@end
