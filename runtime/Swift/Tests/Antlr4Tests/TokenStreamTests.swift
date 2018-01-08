/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

class TokenStreamTests: XCTestCase {
    
    static let allTests = [
        ("testBufferedTokenStreamClearFetchEOFWithNewSource", testBufferedTokenStreamClearFetchEOFWithNewSource)
    ]

    /// Test fetchEOF reset after setTokenSource
    func testBufferedTokenStreamClearFetchEOFWithNewSource() throws {
        let inputStream1 = ANTLRInputStream("A")
        let tokenStream = CommonTokenStream(VisitorBasicLexer(inputStream1))

        try tokenStream.fill();
        XCTAssertEqual(2, tokenStream.size())
        XCTAssertEqual(VisitorBasicLexer.A, try tokenStream.get(0).getType())
        XCTAssertEqual(Lexer.EOF, try tokenStream.get(1).getType())

        let inputStream2 = ANTLRInputStream("AA");
        tokenStream.setTokenSource(VisitorBasicLexer(inputStream2));
        try tokenStream.fill();
        XCTAssertEqual(3, tokenStream.size())
        XCTAssertEqual(VisitorBasicLexer.A, try tokenStream.get(0).getType())
        XCTAssertEqual(VisitorBasicLexer.A, try tokenStream.get(1).getType())
        XCTAssertEqual(Lexer.EOF, try tokenStream.get(2).getType())
    }

}
