/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import Foundation
import XCTest
@testable import Antlr4

class StringExtensionTests: XCTestCase {

    func testLastIndex() {
        doLastIndexTest("", "", nil)
        doLastIndexTest("a", "", nil)
        doLastIndexTest("a", "a", 0)
        doLastIndexTest("aba", "a", 2)
        doLastIndexTest("aba", "b", 1)
        doLastIndexTest("abc", "d", nil)
    }

}

private func doLastIndexTest(_ str: String, _ target: String, _ expectedOffset: Int?) {
    let expectedIdx: String.Index?
    if let expectedOffset = expectedOffset {
        expectedIdx = str.index(str.startIndex, offsetBy: expectedOffset)
    }
    else {
        expectedIdx = nil
    }
    XCTAssertEqual(str.lastIndex(of: target), expectedIdx)
}
