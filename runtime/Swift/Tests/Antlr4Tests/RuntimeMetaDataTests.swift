/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import Foundation
import XCTest
import Antlr4

class RuntimeMetaDataTests: XCTestCase {

    func testGetMajorMinorVersion() {
        doGetMajorMinorVersionTest("", "")
        doGetMajorMinorVersionTest("4", "4")
        doGetMajorMinorVersionTest("4.", "4.")
        doGetMajorMinorVersionTest("4.7", "4.7")
        doGetMajorMinorVersionTest("4.7.1", "4.7")
        doGetMajorMinorVersionTest("4.7.2", "4.7")
        doGetMajorMinorVersionTest("4.8", "4.8")
        doGetMajorMinorVersionTest("4.9", "4.9")
        doGetMajorMinorVersionTest("4.9.1", "4.9")
        doGetMajorMinorVersionTest("4.9.2", "4.9")
        doGetMajorMinorVersionTest("4.9.3", "4.9")
        doGetMajorMinorVersionTest("4-SNAPSHOT", "4")
        doGetMajorMinorVersionTest("4.-SNAPSHOT", "4.")
        doGetMajorMinorVersionTest("4.7-SNAPSHOT", "4.7")
        doGetMajorMinorVersionTest("4.7.1-SNAPSHOT", "4.7")
        doGetMajorMinorVersionTest("4.7.2-SNAPSHOT", "4.7")
        doGetMajorMinorVersionTest("4.9.1-SNAPSHOT", "4.9")
        doGetMajorMinorVersionTest("4.9.2-SNAPSHOT", "4.9")
        doGetMajorMinorVersionTest("4.9.3-SNAPSHOT", "4.9")
        doGetMajorMinorVersionTest("4.10-SNAPSHOT", "4.10")
    }
}

private func doGetMajorMinorVersionTest(_ input: String, _ expected: String) {
    XCTAssertEqual(RuntimeMetaData.getMajorMinorVersion(input), expected)
}
