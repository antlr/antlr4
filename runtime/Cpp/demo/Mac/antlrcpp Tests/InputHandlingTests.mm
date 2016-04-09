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

using namespace org::antlr::v4::runtime;
using namespace org::antlr::v4::runtime::misc;

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

  ANTLRInputStream stream2(L"To be or not to be");
  XCTAssert(stream2.toString() == L"To be or not to be");
  XCTAssertEqual(stream2.index(), 0U);
  XCTAssertEqual(stream2.size(), 18U);

  wchar_t data[] = L"Lorem ipsum dolor sit amet";
  ANTLRInputStream stream3(data, sizeof(data) / sizeof(data[0]));
  XCTAssert(stream3.toString() == std::wstring(L"Lorem ipsum dolor sit amet\0", 27));
  XCTAssertEqual(stream3.index(), 0U);
  XCTAssertEqual(stream3.size(), 27U);

  std::wstringstream input(data, sizeof(data) / sizeof(data[0]));
  ANTLRInputStream stream4(input);
  XCTAssertEqual(stream4.index(), 0U);
  XCTAssertEqual(stream4.size(), 26U);

  std::wstring longString(33333, L'a');
  input.str(longString);
  stream4.load(input, 0);
  XCTAssertEqual(stream4.index(), 0U);
  XCTAssertEqual(stream4.size(), 26U); // Nothing changed as the stream is still at eof.

  input.clear();
  stream4.load(input, 0);
  XCTAssertEqual(stream4.size(), 33333U);
}

- (void)testANTLRInputStreamUse {
  std::wstring text(L"🚧Lorem ipsum dolor sit amet🕶");
  ANTLRInputStream stream(text);
  XCTAssertEqual(stream.index(), 0U);
  XCTAssertEqual(stream.size(), text.size());

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

  XCTAssertEqual(stream.index(), text.size());
  stream.reset();
  XCTAssertEqual(stream.index(), 0U);

  XCTAssertEqual(stream.LA(0), 0);
  for (size_t i = 1; i < text.size(); ++i) {
    XCTAssertEqual(stream.LA((ssize_t)i), text[i - 1]); // LA(1) means: current char.
    XCTAssertEqual(stream.LT((ssize_t)i), text[i - 1]); // LT is mapped to LA.
    XCTAssertEqual(stream.index(), 0U); // No consumption when looking ahead.
  }

  stream.seek(text.size() - 1);
  XCTAssertEqual(stream.index(), text.size() - 1);

  stream.seek(text.size() / 2);
  XCTAssertEqual(stream.index(), text.size() / 2);

  stream.seek(text.size() - 1);
  for (size_t i = 1; i < text.size() - 1; ++i) {
    XCTAssertEqual(stream.LA((ssize_t)-i), text[text.size() - i - 1]); // LA(-1) means: previous char.
    XCTAssertEqual(stream.LT((ssize_t)-i), text[text.size() - i - 1]); // LT is mapped to LA.
    XCTAssertEqual(stream.index(), text.size() - 1); // No consumption when looking ahead.
  }

  XCTAssertEqual((int)stream.LA(-10000), EOF);

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

  misc::Interval interval1(2, 10); // From - to, inclusive.
  std::wstring output = stream.getText(interval1);
  XCTAssertEqual(output, text.substr(2, 9));

  misc::Interval interval2(200, 10); // Start beyond bounds.
  output = stream.getText(interval2);
  XCTAssert(output.empty());

  misc::Interval interval3(0, 200); // End beyond bounds.
  output = stream.getText(interval3);
  XCTAssertEqual(output, text);

  stream.name = "unit tests"; // Quite useless test, as "name" is a public field.
  XCTAssertEqual(stream.getSourceName(), "unit tests");
}

- (void)testUnbufferedTokenSteam {
  //UnbufferedTokenStream stream;
}

@end
