/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

class TokenStreamRewriterTests: XCTestCase {
    
    static let allTests = [
        ("testPreservesOrderOfContiguousInserts", testPreservesOrderOfContiguousInserts),
        ("testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder2", testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder2),
        ("testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder", testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder),
        ("testInsertBeforeTokenThenDeleteThatToken", testInsertBeforeTokenThenDeleteThatToken),
        ("testLeaveAloneDisjointInsert2", testLeaveAloneDisjointInsert2),
        ("testLeaveAloneDisjointInsert", testLeaveAloneDisjointInsert),
        ("testDropPrevCoveredInsert", testDropPrevCoveredInsert),
        ("testDropIdenticalReplace", testDropIdenticalReplace),
        ("testOverlappingReplace4", testOverlappingReplace4),
        ("testOverlappingReplace3", testOverlappingReplace3),
        ("testOverlappingReplace2", testOverlappingReplace2),
        ("testOverlappingReplace", testOverlappingReplace),
        ("testDisjointInserts", testDisjointInserts),
        ("testCombineInsertOnLeftWithDelete", testCombineInsertOnLeftWithDelete),
        ("testCombineInsertOnLeftWithReplace", testCombineInsertOnLeftWithReplace),
        ("testCombine3Inserts", testCombine3Inserts),
        ("testCombineInserts", testCombineInserts),
        ("testReplaceSingleMiddleThenOverlappingSuperset", testReplaceSingleMiddleThenOverlappingSuperset),
        ("testReplaceThenReplaceLowerIndexedSuperset", testReplaceThenReplaceLowerIndexedSuperset),
        ("testReplaceThenReplaceSuperset", testReplaceThenReplaceSuperset),
        ("testReplaceSubsetThenFetch", testReplaceSubsetThenFetch),
        ("testReplaceAll", testReplaceAll),
        ("testReplaceRangeThenInsertAfterRightEdge", testReplaceRangeThenInsertAfterRightEdge),
        ("testReplaceRangeThenInsertAtRightEdge", testReplaceRangeThenInsertAtRightEdge),
        ("testReplaceThenInsertAtLeftEdge", testReplaceThenInsertAtLeftEdge),
        ("testReplaceThenInsertAfterLastIndex", testReplaceThenInsertAfterLastIndex),
        ("testInsertThenReplaceLastIndex", testInsertThenReplaceLastIndex),
        ("testReplaceThenInsertBeforeLastIndex", testReplaceThenInsertBeforeLastIndex),
        ("test2InsertThenReplaceIndex0", test2InsertThenReplaceIndex0),
        ("test2InsertMiddleIndex", test2InsertMiddleIndex),
        ("testInsertThenReplaceSameIndex", testInsertThenReplaceSameIndex),
        ("testInsertInPriorReplace", testInsertInPriorReplace),
        ("testReplaceThenDeleteMiddleIndex", testReplaceThenDeleteMiddleIndex),
        ("test2ReplaceMiddleIndex1InsertBefore", test2ReplaceMiddleIndex1InsertBefore),
        ("test2ReplaceMiddleIndex", test2ReplaceMiddleIndex),
        ("testToStringStartStop2", testToStringStartStop2),
        ("testToStringStartStop", testToStringStartStop),
        ("testReplaceMiddleIndex", testReplaceMiddleIndex),
        ("testReplaceLastIndex", testReplaceLastIndex),
        ("testReplaceIndex0", testReplaceIndex0),
        ("test2InsertBeforeAfterMiddleIndex", test2InsertBeforeAfterMiddleIndex),
        ("testInsertAfterLastIndex", testInsertAfterLastIndex),
        ("testInsertBeforeIndex0", testInsertBeforeIndex0)
    ]
    
    func testInsertBeforeIndex0() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "0")
        let result = try tokens.getText()
        let expecting = "0abc"
        XCTAssertEqual(expecting, result)
    }

    func testInsertAfterLastIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertAfter(2, "x")
        let result = try tokens.getText()
        let expecting = "abcx"
        XCTAssertEqual(expecting, result)
    }

    func test2InsertBeforeAfterMiddleIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "x")
        tokens.insertAfter(1, "x")
        let result = try tokens.getText()
        let expecting = "axbxc"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceIndex0() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(0, "x")
        let result = try tokens.getText()
        let expecting = "xbc"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceLastIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, "x")
        let result = try tokens.getText()
        let expecting = "abx"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceMiddleIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, "x")
        let result = try tokens.getText()
        let expecting = "axc"
        XCTAssertEqual(expecting, result)
    }

    func testToStringStartStop() throws {
        // Tokens: 0123456789
        // Input:  x = 3 * 0
        let input = ANTLRInputStream("x = 3 * 0;")
        let lexer = LexerB(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)

        // replace 3 * 0 with 0
        try tokens.replace(4, 8, "0")
        try stream.fill()

        var result = try tokens.getTokenStream().getText()
        var expecting = "x = 3 * 0;"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText()
        expecting = "x = 0;"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(0, 9))
        expecting = "x = 0;"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(4, 8))
        expecting = "0"
        XCTAssertEqual(expecting, result)
    }

    func testToStringStartStop2() throws {
        // Tokens: 012345678901234567
        // Input:  x = 3 * 0 + 2 * 0;
        let input = ANTLRInputStream("x = 3 * 0 + 2 * 0;")
        let lexer = LexerB(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)

        var result = try tokens.getTokenStream().getText()
        var expecting = "x = 3 * 0 + 2 * 0;"
        XCTAssertEqual(expecting, result)

        // replace 3 * 0 with 0
        try tokens.replace(4, 8, "0")
        try stream.fill()

        result = try tokens.getText()
        expecting = "x = 0 + 2 * 0;"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(0, 17))
        expecting = "x = 0 + 2 * 0;"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(4, 8))
        expecting = "0"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(0, 8))
        expecting = "x = 0"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(12, 16))
        expecting = "2 * 0"
        XCTAssertEqual(expecting, result)

        tokens.insertAfter(17, "// comment")
        result = try tokens.getText(Interval.of(12, 18))
        expecting = "2 * 0;// comment"
        XCTAssertEqual(expecting, result)

        result = try tokens.getText(Interval.of(0, 8))
        try stream.fill()
        // try again after insert at end
        expecting = "x = 0"
        XCTAssertEqual(expecting, result)
    }

    func test2ReplaceMiddleIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, "x")
        try tokens.replace(1, "y")
        let result = try tokens.getText()
        let expecting = "ayc"
        XCTAssertEqual(expecting, result)
    }

    func test2ReplaceMiddleIndex1InsertBefore() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "_")
        try tokens.replace(1, "x")
        try tokens.replace(1, "y")
        let result = try tokens.getText()
        let expecting = "_ayc"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceThenDeleteMiddleIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, "x")
        try tokens.delete(1)
        let result = try tokens.getText()
        let expecting = "ac"
        XCTAssertEqual(expecting, result)
    }

    func testInsertInPriorReplace() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(0, 2, "x")
        tokens.insertBefore(1, "0")

        do {
            _ = try tokens.getText()
            XCTFail("Expected exception not thrown.")
        } catch ANTLRError.illegalArgument(let msg) {
            let expecting = "insert op <InsertBeforeOp@[@1,1:1='b',<2>,1:1]:\"0\"> within boundaries of previous <ReplaceOp@[@0,0:0='a',<1>,1:0]..[@2,2:2='c',<3>,1:2]:\"x\">"

            XCTAssertEqual(expecting, msg)
        }
    }

    func testInsertThenReplaceSameIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "0")
        try tokens.replace(0, "x")
        let result = try tokens.getText()
        let expecting = "0xbc"
        XCTAssertEqual(expecting, result)
    }

    func test2InsertMiddleIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "x")
        tokens.insertBefore(1, "y")
        let result = try tokens.getText()
        let expecting = "ayxbc"
        XCTAssertEqual(expecting, result)
    }

    func test2InsertThenReplaceIndex0() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "x")
        tokens.insertBefore(0, "y")
        try tokens.replace(0, "z")
        let result = try tokens.getText()
        let expecting = "yxzbc"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceThenInsertBeforeLastIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, "x")
        tokens.insertBefore(2, "y")
        let result = try tokens.getText()
        let expecting = "abyx"
        XCTAssertEqual(expecting, result)
    }

    func testInsertThenReplaceLastIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(2, "y")
        try tokens.replace(2, "x")
        let result = try tokens.getText()
        let expecting = "abyx"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceThenInsertAfterLastIndex() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, "x")
        tokens.insertAfter(2, "y")
        let result = try tokens.getText()
        let expecting = "abxy"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceThenInsertAtLeftEdge() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "x")
        tokens.insertBefore(2, "y")
        let result = try tokens.getText()
        let expecting = "abyxba"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceRangeThenInsertAtRightEdge() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "x")
        tokens.insertBefore(4, "y")

        do {
            _ = try tokens.getText()
            XCTFail("Expected exception not thrown.")
        } catch ANTLRError.illegalArgument(let msg) {
            let expecting = "insert op <InsertBeforeOp@[@4,4:4='c',<3>,1:4]:\"y\"> within boundaries of previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"x\">"

            XCTAssertEqual(expecting, msg)
        }
    }

    func testReplaceRangeThenInsertAfterRightEdge() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "x")
        tokens.insertAfter(4, "y")
        let result = try tokens.getText()
        let expecting = "abxyba"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceAll() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(0, 6, "x")
        let result = try tokens.getText()
        let expecting = "x"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceSubsetThenFetch() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "xyz")
        let result = try tokens.getText(Interval.of(0, 6))
        let expecting = "abxyzba"
        XCTAssertEqual(expecting, result)
    }

    func testReplaceThenReplaceSuperset() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "xyz")
        try tokens.replace(3, 5, "foo")

        do {
            _ = try tokens.getText()
            XCTFail("Expected exception not thrown.")
        } catch ANTLRError.illegalArgument(let msg) {
            let expecting = "replace op boundaries of <ReplaceOp@[@3,3:3='c',<3>,1:3]..[@5,5:5='b',<2>,1:5]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"xyz\">"
            XCTAssertEqual(expecting, msg)
        }
    }

    func testReplaceThenReplaceLowerIndexedSuperset() throws {
        let input = ANTLRInputStream("abcccba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 4, "xyz")
        try tokens.replace(1, 3, "foo")

        do {
            _ = try tokens.getText()
            XCTFail("Expected exception not thrown.")
        } catch ANTLRError.illegalArgument(let msg) {
            let expecting = "replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@3,3:3='c',<3>,1:3]:\"foo\"> overlap with previous <ReplaceOp@[@2,2:2='c',<3>,1:2]..[@4,4:4='c',<3>,1:4]:\"xyz\">"
            XCTAssertEqual(expecting, msg)
        }
    }

    func testReplaceSingleMiddleThenOverlappingSuperset() throws {
        let input = ANTLRInputStream("abcba")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 2, "xyz")
        try tokens.replace(0, 3, "foo")
        let result = try tokens.getText()
        let expecting = "fooa"
        XCTAssertEqual(expecting, result)
    }

    func testCombineInserts() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "x")
        tokens.insertBefore(0, "y")
        let result = try tokens.getText()
        let expecting = "yxabc"
        XCTAssertEqual(expecting, result)
    }

    func testCombine3Inserts() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "x")
        tokens.insertBefore(0, "y")
        tokens.insertBefore(1, "z")
        let result = try tokens.getText()
        let expecting = "yazxbc"
        XCTAssertEqual(expecting, result)
    }

    func testCombineInsertOnLeftWithReplace() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        // combine with left edge of rewrite
        try tokens.replace(0, 2, "foo")
        tokens.insertBefore(0, "z")
        try stream.fill()
        let result = try tokens.getText()
        let expecting = "zfoo"
        XCTAssertEqual(expecting, result)
    }

    func testCombineInsertOnLeftWithDelete() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        // combine with left edge of rewrite
        try tokens.delete(0, 2)
        tokens.insertBefore(0, "z")
        try stream.fill()
        let result = try tokens.getText()
        let expecting = "z"
        // make sure combo is not znull
        try stream.fill()
        XCTAssertEqual(expecting, result)
    }

    func testDisjointInserts() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "x")
        tokens.insertBefore(2, "y")
        tokens.insertBefore(0, "z")
        try stream.fill()
        let result = try tokens.getText()
        let expecting = "zaxbyc"
        XCTAssertEqual(expecting, result)
    }

    func testOverlappingReplace() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, 2, "foo")
        try tokens.replace(0, 3, "bar")
        try stream.fill()
        // wipes prior nested replace
        let result = try tokens.getText()
        let expecting = "bar"
        XCTAssertEqual(expecting, result)
    }

    func testOverlappingReplace2() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(0, 3, "bar")
        try tokens.replace(1, 2, "foo")
        try stream.fill()
        // cannot split earlier replace

        do {
            _ = try tokens.getText()
            XCTFail("Expected exception not thrown.")
        } catch ANTLRError.illegalArgument(let msg) {
            let expecting = "replace op boundaries of <ReplaceOp@[@1,1:1='b',<2>,1:1]..[@2,2:2='c',<3>,1:2]:\"foo\"> overlap with previous <ReplaceOp@[@0,0:0='a',<1>,1:0]..[@3,3:3='c',<3>,1:3]:\"bar\">"
            XCTAssertEqual(expecting, msg)
        }
    }

    func testOverlappingReplace3() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, 2, "foo")
        try tokens.replace(0, 2, "bar")
        try stream.fill()
        // wipes prior nested replace
        let result = try tokens.getText()
        let expecting = "barc"
        XCTAssertEqual(expecting, result)
    }

    func testOverlappingReplace4() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, 2, "foo")
        try tokens.replace(1, 3, "bar")
        try stream.fill()
        // wipes prior nested replace
        let result = try tokens.getText()
        let expecting = "abar"
        XCTAssertEqual(expecting, result)
    }

    func testDropIdenticalReplace() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(1, 2, "foo")
        try tokens.replace(1, 2, "foo")
        try stream.fill()
        // drop previous, identical
        let result = try tokens.getText()
        let expecting = "afooc"
        XCTAssertEqual(expecting, result)
    }

    func testDropPrevCoveredInsert() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "foo")
        try tokens.replace(1, 2, "foo")
        try stream.fill()
        // kill prev insert
        let result = try tokens.getText()
        let expecting = "afoofoo"
        XCTAssertEqual(expecting, result)
    }

    func testLeaveAloneDisjointInsert() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(1, "x")
        try tokens.replace(2, 3, "foo")
        let result = try tokens.getText()
        let expecting = "axbfoo"
        XCTAssertEqual(expecting, result)
    }

    func testLeaveAloneDisjointInsert2() throws {
        let input = ANTLRInputStream("abcc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        try tokens.replace(2, 3, "foo")
        tokens.insertBefore(1, "x")
        let result = try tokens.getText()
        let expecting = "axbfoo"
        XCTAssertEqual(expecting, result)
    }

    func testInsertBeforeTokenThenDeleteThatToken() throws {
        let input = ANTLRInputStream("abc")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(2, "y")
        try tokens.delete(2)
        let result = try tokens.getText()
        let expecting = "aby"
        XCTAssertEqual(expecting, result)
    }

    func testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder() throws {
        let input = ANTLRInputStream("aa")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "<b>")
        tokens.insertAfter(0, "</b>")
        tokens.insertBefore(1, "<b>")
        tokens.insertAfter(1, "</b>")
        let result = try tokens.getText()
        let expecting = "<b>a</b><b>a</b>" // fails with <b>a<b></b>a</b>"
        XCTAssertEqual(expecting, result)
    }

    func testDistinguishBetweenInsertAfterAndInsertBeforeToPreserverOrder2() throws {
        let input = ANTLRInputStream("aa")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "<p>")
        tokens.insertBefore(0, "<b>")
        tokens.insertAfter(0, "</p>")
        tokens.insertAfter(0, "</b>")
        tokens.insertBefore(1, "<b>")
        tokens.insertAfter(1, "</b>")
        let result = try tokens.getText()
        let expecting = "<b><p>a</p></b><b>a</b>"
        XCTAssertEqual(expecting, result)
    }

    func testPreservesOrderOfContiguousInserts() throws {
        let input = ANTLRInputStream("ab")
        let lexer = LexerA(input)
        let stream = CommonTokenStream(lexer)
        try stream.fill()
        let tokens = TokenStreamRewriter(stream)
        tokens.insertBefore(0, "<p>")
        tokens.insertBefore(0, "<b>")
        tokens.insertBefore(0, "<div>")
        tokens.insertAfter(0, "</p>")
        tokens.insertAfter(0, "</b>")
        tokens.insertAfter(0, "</div>")
        tokens.insertBefore(1, "!")
        let result = try tokens.getText()
        let expecting = "<div><b><p>a</p></b></div>!b"
        XCTAssertEqual(expecting, result)
    }
}
