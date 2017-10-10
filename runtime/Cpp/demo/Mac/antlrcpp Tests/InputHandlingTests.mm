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

#include "ANTLRInputStream.h"
#include "Exceptions.h"
#include "Interval.h"
#include "UnbufferedTokenStream.h"
#include "StringUtils.h"

using namespace antlrcpp;
using namespace antlr4;
using namespace antlr4::misc;

@interface InputHandlingTests : XCTestCase

@end

@implementation InputHandlingTests

- (void)setUp {
  [super setUp];
  // Put setup code here. This method is called before the invocation of each test method in the class.
}

- (void)tearDown {
  // Put teardown code here. This method is called after the invocation of each test method in the class.
  [super tearDown];
}

- (void)testANTLRInputStreamCreation {
  ANTLRInputStream stream1;
  XCTAssert(stream1.toString().empty());
  XCTAssertEqual(stream1.index(), 0U);

  ANTLRInputStream stream2("To be or not to be");
  XCTAssert(stream2.toString() == "To be or not to be");
  XCTAssertEqual(stream2.index(), 0U);
  XCTAssertEqual(stream2.size(), 18U);

  char data[] = "Lorem ipsum dolor sit amet";
  ANTLRInputStream stream3(data, sizeof(data) / sizeof(data[0]));
  XCTAssert(stream3.toString() == std::string("Lorem ipsum dolor sit amet\0", 27));
  XCTAssertEqual(stream3.index(), 0U);
  XCTAssertEqual(stream3.size(), 27U);

  std::stringstream input("Lorem ipsum dolor sit amet");
  ANTLRInputStream stream4(input);
  std::string content = stream4.toString();
  XCTAssertEqual(content, "Lorem ipsum dolor sit amet"); // Now as utf-8 string.
  XCTAssertEqual(stream4.index(), 0U);
  XCTAssertEqual(stream4.size(), 26U);

  std::string longString(33333, 'a');
  input.str(longString);
  stream4.load(input);
  XCTAssertEqual(stream4.index(), 0U);
  XCTAssertEqual(stream4.size(), 33333U);

  input.clear();
  stream4.load(input);
  XCTAssertEqual(stream4.size(), 0U);
}

- (void)testANTLRInputStreamUse {
  std::string text(u8"🚧Lorem ipsum dolor sit amet🕶");
  std::u32string wtext = utf8_to_utf32(text.c_str(), text.c_str() + text.size()); // Convert to UTF-32.
  ANTLRInputStream stream(text);
  XCTAssertEqual(stream.index(), 0U);
  XCTAssertEqual(stream.size(), wtext.size());

  for (size_t i = 0; i < stream.size(); ++i) {
    stream.consume();
    XCTAssertEqual(stream.index(), i + 1);
  }

  try {
    stream.consume();
    XCTFail();
  } catch (IllegalStateException &e) {
    // Expected.
    std::string message = e.what();
    XCTAssertEqual(message, "cannot consume EOF");
  }

  XCTAssertEqual(stream.index(), wtext.size());
  stream.reset();
  XCTAssertEqual(stream.index(), 0U);

  XCTAssertEqual(stream.LA(0), 0ULL);
  for (size_t i = 1; i < wtext.size(); ++i) {
    XCTAssertEqual(stream.LA(static_cast<ssize_t>(i)), wtext[i - 1]); // LA(1) means: current char.
    XCTAssertEqual(stream.LT(static_cast<ssize_t>(i)), wtext[i - 1]); // LT is mapped to LA.
    XCTAssertEqual(stream.index(), 0U); // No consumption when looking ahead.
  }

  stream.seek(wtext.size() - 1);
  XCTAssertEqual(stream.index(), wtext.size() - 1);

  stream.seek(wtext.size() / 2);
  XCTAssertEqual(stream.index(), wtext.size() / 2);

  stream.seek(wtext.size() - 1);
  for (ssize_t i = 1; i < static_cast<ssize_t>(wtext.size()) - 1; ++i) {
    XCTAssertEqual(stream.LA(-i), wtext[wtext.size() - i - 1]); // LA(-1) means: previous char.
    XCTAssertEqual(stream.LT(-i), wtext[wtext.size() - i - 1]); // LT is mapped to LA.
    XCTAssertEqual(stream.index(), wtext.size() - 1); // No consumption when looking ahead.
  }

  XCTAssertEqual(stream.LA(-10000), IntStream::EOF);

  // Mark and release do nothing.
  stream.reset();
  XCTAssertEqual(stream.index(), 0U);
  ssize_t marker = stream.mark();
  XCTAssertEqual(marker, -1);
  stream.seek(10);
  XCTAssertEqual(stream.index(), 10U);
  XCTAssertEqual(stream.mark(), -1);

  stream.release(marker);
  XCTAssertEqual(stream.index(), 10U);

  misc::Interval interval1(2, 10UL); // From - to, inclusive.
  std::string output = stream.getText(interval1);
  std::string sub = utf32_to_utf8(wtext.substr(2, 9));
  XCTAssertEqual(output, sub);

  misc::Interval interval2(200, 10UL); // Start beyond bounds.
  output = stream.getText(interval2);
  XCTAssert(output.empty());

  misc::Interval interval3(0, 200UL); // End beyond bounds.
  output = stream.getText(interval3);
  XCTAssertEqual(output, text);

  stream.name = "unit tests"; // Quite useless test, as "name" is a public field.
  XCTAssertEqual(stream.getSourceName(), "unit tests");
}

- (void)testUnbufferedTokenSteam {
  //UnbufferedTokenStream stream;
}

@end
