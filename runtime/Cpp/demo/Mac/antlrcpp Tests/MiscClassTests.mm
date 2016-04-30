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
#include "Token.h"
#include "Exceptions.h"
#include "Lexer.h"
#include "CPPUtils.h"

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::misc;
using namespace antlrcpp;

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

- (void)testCPPUtils {

  class A { public: virtual ~A() {}; };
  class B : public A { public: virtual ~B() {}; };
  class C : public A { public: virtual ~C() {}; };
  class D : public C { public: virtual ~D() {}; };

  {
    A a; B b; C c; D d;
    const A &cc = d;
    XCTAssert(is<A>(b));
    XCTAssertFalse(is<B>(a));
    XCTAssert(is<A>(c));
    XCTAssertFalse(is<B>(c));
    XCTAssert(is<A>(d));
    XCTAssert(is<C>(d));
    XCTAssertFalse(is<B>(d));
    XCTAssert(is<C>(cc));

    auto isA = [&](const A &aa) { XCTAssert(is<A>(aa)); };
    isA(a);
    isA(b);
    isA(c);
    isA(d);

    auto isC = [&](const A &aa, bool mustBeTrue) {
      if (mustBeTrue)
        XCTAssert(is<C>(aa));
      else
        XCTAssertFalse(is<C>(aa));
    };
    isC(a, false);
    isC(b, false);
    isC(c, true);
    isC(d, true);
  }
  {
    A *a = new A(); B *b = new B(); C *c = new C(); D *d = new D();
    XCTAssert(is<A*>(b));
    XCTAssertFalse(is<B*>(a));
    XCTAssert(is<A*>(c));
    XCTAssertFalse(is<B*>(c));
    XCTAssert(is<A*>(d));
    XCTAssert(is<C*>(d));
    XCTAssertFalse(is<B*>(d));
    delete a; delete b; delete c; delete d;
  }
  {
    Ref<A> a(new A());
    Ref<B> b(new B());
    Ref<C> c(new C());
    Ref<D> d(new D());
    XCTAssert(is<A>(b));
    XCTAssertFalse(is<B>(a));
    XCTAssert(is<A>(c));
    XCTAssertFalse(is<B>(c));
    XCTAssert(is<A>(d));
    XCTAssert(is<C>(d));
    XCTAssertFalse(is<B>(d));
  }
}

- (void)testMurmurHash {
  XCTAssertEqual(MurmurHash::initialize(), 0U);
  XCTAssertEqual(MurmurHash::initialize(31), 31U);

  XCTAssertEqual(MurmurHash::hashCode<size_t>({}, 0), 0U);

  // In absence of real test vectors (64bit) for murmurhash I instead check if I can find duplicate hash values
  // in a deterministic and a random sequence of 100K values each.
  std::set<size_t> hashs;
  for (size_t i = 0; i < 100000; ++i) {
    std::vector<size_t> data = { i, (size_t)(i * M_PI),  arc4random()};
    size_t hash = 0;
    for (auto value : data)
      hash = MurmurHash::update(hash, value);
    hash = MurmurHash::finish(hash, data.size());
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), 100000U, @"At least one duplicate hash found.");

  hashs.clear();
  for (size_t i = 0; i < 100000; ++i) {
    std::vector<size_t> data = { i, (size_t)(i * M_PI)};
    size_t hash = 0;
    for (auto value : data)
      hash = MurmurHash::update(hash, value);
    hash = MurmurHash::finish(hash, data.size());
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), 100000U, @"At least one duplicate hash found.");

  // Another test with fixed input but varying seeds.
  // Note: the higher the seed the less LSDs are in the result (for small input data).
  hashs.clear();
  std::vector<size_t> data = { L'µ', 'a', '@', '1' };
  for (size_t i = 0; i < 100000; ++i) {
    size_t hash = i;
    for (auto value : data)
      hash = MurmurHash::update(hash, value);
    hash = MurmurHash::finish(hash, data.size());
    hashs.insert(hash);
  }
  XCTAssertEqual(hashs.size(), 100000U, @"At least one duplicate hash found.");
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

  XCTAssertEqual(Interval().hashCode(), 22070U);
  XCTAssertEqual(Interval(0, 0).hashCode(), 22103U);
  XCTAssertEqual(Interval(10, 2000).hashCode(), 24413U);

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

  XCTAssert(Interval().toString() == "-1..-2");
  XCTAssert(Interval(10, 10).toString() == "10..10");
  XCTAssert(Interval(1000, 2000).toString() == "1000..2000");
  XCTAssert(Interval(500, INT_MAX).toString() == "500.." + std::to_string(INT_MAX));
}

- (void)testIntervalSet {
  XCTAssertFalse(IntervalSet().isReadOnly());
  XCTAssert(IntervalSet().isEmpty());

  IntervalSet set1;
  set1.setReadOnly(true);
  XCTAssert(set1.isReadOnly());

  XCTAssert(IntervalSet() == IntervalSet::EMPTY_SET);

  std::vector<Interval> intervals = { Interval(), Interval(10, 20), Interval(20, 100), Interval(1000, 2000) };
  IntervalSet set2(intervals);
  XCTAssertFalse(set2.isEmpty());
  XCTAssertFalse(set2.contains(9));
  XCTAssert(set2.contains(10));
  XCTAssert(set2.contains(20));
  XCTAssertTrue(set2.contains(22));
  XCTAssert(set2.contains(1111));
  XCTAssertFalse(set2.contains(10000));
  XCTAssertEqual(set2.getSingleElement(), Token::INVALID_TYPE);
  XCTAssertEqual(set2.getMinElement(), 10);
  XCTAssertEqual(set2.getMaxElement(), 2000);

  IntervalSet set3(set2);
  XCTAssertFalse(set3.isEmpty());
  XCTAssertFalse(set3.contains(9));
  XCTAssert(set3.contains(10));
  XCTAssert(set3.contains(20));
  XCTAssertTrue(set3.contains(22));
  XCTAssert(set3.contains(1111));
  XCTAssertFalse(set3.contains(10000));
  XCTAssertEqual(set3.getSingleElement(), Token::INVALID_TYPE);
  XCTAssertEqual(set3.getMinElement(), 10);
  XCTAssertEqual(set3.getMaxElement(), 2000);

  set3.add(Interval(100, 1000));
  XCTAssertEqual(set3.getMinElement(), 10);
  set3.add(Interval(9, 1000));
  XCTAssertEqual(set3.getMinElement(), 9);
  set3.add(Interval(1, 1));
  XCTAssertEqual(set3.getMinElement(), 1);

  IntervalSet set4;
  set4.add(10);
  XCTAssertEqual(set4.getSingleElement(), 10);
  XCTAssertEqual(set4.getMinElement(), 10);
  XCTAssertEqual(set4.getMaxElement(), 10);

  set4.clear();
  XCTAssert(set4.isEmpty());
  set4.add(Interval(10, 10));
  XCTAssertEqual(set4.getSingleElement(), 10);
  XCTAssertEqual(set4.getMinElement(), 10);
  XCTAssertEqual(set4.getMaxElement(), 10);
  set4.setReadOnly(true);
  try {
    set4.clear();
    XCTFail(@"Expected exception");
  }
  catch (IllegalStateException &e) {
  }

  set4.setReadOnly(false);
  set4 = IntervalSet::of(12345);
  XCTAssertEqual(set4.getSingleElement(), 12345);
  XCTAssertEqual(set4.getMinElement(), 12345);
  XCTAssertEqual(set4.getMaxElement(), 12345);

  IntervalSet set5(10, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50);
  XCTAssertEqual(set5.getMinElement(), 5);
  XCTAssertEqual(set5.getMaxElement(), 50);
  XCTAssertEqual(set5.size(), 10U);
  set5.add(12, 18);
  XCTAssertEqual(set5.size(), 16U); // (15, 15) replaced by (12, 18)
  set5.add(9, 33);
  XCTAssertEqual(set5.size(), 30U); // (10, 10), (12, 18), (20, 20), (25, 25) and (30, 30) replaced by (9, 33)

  XCTAssert(IntervalSet(3, 1, 2, 10).Or(IntervalSet(3, 1, 2, 5)) == IntervalSet(4, 1, 2, 5, 10));
  XCTAssert(IntervalSet({ Interval(2, 10) }).Or(IntervalSet({ Interval(5, 8) })) == IntervalSet({ Interval(2, 10) }));

  XCTAssert(IntervalSet::of(1, 10).complement(IntervalSet::of(7, 55)) == IntervalSet::of(11, 55));
  XCTAssert(IntervalSet::of(1, 10).complement(IntervalSet::of(20, 55)) == IntervalSet::of(20, 55));
  XCTAssert(IntervalSet::of(1, 10).complement(IntervalSet::of(5, 6)) == IntervalSet::EMPTY_SET);
  XCTAssert(IntervalSet::of(15, 20).complement(IntervalSet::of(7, 55)) == IntervalSet({ Interval(7, 14), Interval(21, 55) }));
  XCTAssert(IntervalSet({ Interval(1, 10), Interval(30, 35) }).complement(IntervalSet::of(7, 55)) == IntervalSet({ Interval(11, 29), Interval(36, 55) }));

  XCTAssert(IntervalSet::of(1, 10).And(IntervalSet::of(7, 55)) == IntervalSet::of(7, 10));
  XCTAssert(IntervalSet::of(1, 10).And(IntervalSet::of(20, 55)) == IntervalSet::EMPTY_SET);
  XCTAssert(IntervalSet::of(1, 10).And(IntervalSet::of(5, 6)) == IntervalSet::of(5, 6));
  XCTAssert(IntervalSet::of(15, 20).And(IntervalSet::of(7, 55)) == IntervalSet::of(15, 20));

  XCTAssert(IntervalSet::of(1, 10).subtract(IntervalSet::of(7, 55)) == IntervalSet::of(1, 6));
  XCTAssert(IntervalSet::of(1, 10).subtract(IntervalSet::of(20, 55)) == IntervalSet::of(1, 10));
  XCTAssert(IntervalSet::of(1, 10).subtract(IntervalSet::of(5, 6)) == IntervalSet({ Interval(1, 4), Interval(7, 10) }));
  XCTAssert(IntervalSet::of(15, 20).subtract(IntervalSet::of(7, 55)) == IntervalSet::EMPTY_SET);
}

@end
