/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import Foundation
import XCTest
import Antlr4

class ParseTreePatternMatcherTests: XCTestCase {

    func testSplit() throws {
        try doSplitTest("", [TextChunk("")])
        try doSplitTest("Foo", [TextChunk("Foo")])
        try doSplitTest("<ID> = <e:expr> ;",
                        [TagChunk("ID"), TextChunk(" = "), TagChunk("e", "expr"), TextChunk(" ;")])
        try doSplitTest("\\<ID\\> = <e:expr> ;",
                        [TextChunk("<ID> = "), TagChunk("e", "expr"), TextChunk(" ;")])
    }
}

private func doSplitTest(_ input: String, _ expected: [Chunk]) throws {
    let matcher = try makeMatcher()
    XCTAssertEqual(try matcher.split(input), expected)
}

private func makeMatcher() throws -> ParseTreePatternMatcher {
    // The lexer and parser here aren't actually used.  They're just here
    // so that ParseTreePatternMatcher can be constructed, but in this file
    // we're currently only testing methods that don't depend on them.
    let lexer = Lexer()
    let ts = BufferedTokenStream(lexer)
    let parser = try Parser(ts)
    return ParseTreePatternMatcher(lexer, parser)
}
