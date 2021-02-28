/// Copyright (c) 2021 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

class InterpreterDataTests: XCTestCase {

    // https://stackoverflow.com/a/57713176
    let sourceDir = URL(fileURLWithPath:#file).deletingLastPathComponent()

    func testLexerA() throws {
        let input = ANTLRInputStream("abc")
        let interpPath = sourceDir.appendingPathComponent("gen/LexerA.interp").path
        let data = try InterpreterDataReader(interpPath)
        let lexer = try data.createLexer(input:input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let result = try stream.getText()
        let expecting = "abc"
        XCTAssertEqual(expecting, result)
    }

    func testLexerB() throws {
        let input = ANTLRInputStream("x = 3 * 0 + 2 * 0;")
        let interpPath = sourceDir.appendingPathComponent("gen/LexerB.interp").path
        let data = try InterpreterDataReader(interpPath)
        let lexer = try data.createLexer(input:input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let result = try stream.getText()
        let expecting = "x = 3 * 0 + 2 * 0;"
        XCTAssertEqual(expecting, result)
    }

    func testCalculator() throws {
        let input = "2 + 8 / 2"
        let lexerInterpPath = sourceDir.appendingPathComponent("gen/VisitorCalcLexer.interp").path
        let lexerInterpData = try InterpreterDataReader(lexerInterpPath)
        let lexer = try lexerInterpData.createLexer(input:ANTLRInputStream(input))
        let parserInterpPath = sourceDir.appendingPathComponent("gen/VisitorCalc.interp").path
        let parserInterpData = try InterpreterDataReader(parserInterpPath)
        let parser = try parserInterpData.createParser(input:CommonTokenStream(lexer))

        let context = try parser.parse(parser.getRuleIndex("s"))
        XCTAssertEqual("(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)", context.toStringTree(parser))
    }

}
