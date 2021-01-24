/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import Foundation

import XCTest
import Antlr4

class ANTLRInputStreamTests: XCTestCase {
    func testASCIICharactersString() {
        let inputStream = ANTLRInputStream("Cat")
        XCTAssertEqual(inputStream.LA(1), 0x0043)
        XCTAssertEqual(inputStream.LA(2), 0x0061)
        XCTAssertEqual(inputStream.LA(3), 0x0074)
    }

    func testBasicMultilingualPlaneCharactersString() {
        // Three Japanese hiragana characters.
        let inputStream = ANTLRInputStream("\u{3053}\u{306D}\u{3053}")
        XCTAssertEqual(inputStream.LA(1), 0x3053)
        XCTAssertEqual(inputStream.LA(2), 0x306D)
        XCTAssertEqual(inputStream.LA(3), 0x3053)
    }

    func testSupplementaryMultilingualPlaneCharactersString() {
        // Three "Cat", "Cat Face", and "Grinning Cat with Smiling Eyes" emojis
        let inputStream = ANTLRInputStream("\u{1F408}\u{1F431}\u{1F638}")
        XCTAssertEqual(inputStream.LA(1), 0x1F408)
        XCTAssertEqual(inputStream.LA(2), 0x1F431)
        XCTAssertEqual(inputStream.LA(3), 0x1F638)
    }

    func testGraphemeCharactersString() {
        // One "Family (Man, Woman, Girl, Boy)" emoji
        let inputStream = ANTLRInputStream("\u{1F468}\u{200D}\u{1F469}\u{200D}\u{1F467}\u{200D}\u{1F466}")
        XCTAssertEqual(inputStream.LA(1), 0x1F468)
        XCTAssertEqual(inputStream.LA(2), 0x200D)
        XCTAssertEqual(inputStream.LA(3), 0x1F469)
        XCTAssertEqual(inputStream.LA(4), 0x200D)
        XCTAssertEqual(inputStream.LA(5), 0x1F467)
        XCTAssertEqual(inputStream.LA(6), 0x200D)
        XCTAssertEqual(inputStream.LA(7), 0x1F466)
    }
}
