/// Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

class ThreadingTests: XCTestCase {
    static let allTests = [
        ("testParallelExecution", testParallelExecution),
    ]

    ///
    /// This test verifies parallel execution of the parser
    ///
    func testParallelExecution() throws {
        let expectation = expectation(description: "Waiting on async-task")
        expectation.expectedFulfillmentCount = 100
        for _ in 0...100 {
            DispatchQueue.global().async {
                let lexer = ThreadingLexer(ANTLRInputStream("1+1"))
                let tokenStream = CommonTokenStream(lexer)
                let parser = try? ThreadingParser(tokenStream)

                let _ = try? parser?.operation()

                expectation.fulfill()
            }
        }

        waitForExpectations(timeout: 30.0) { (_) in
            print("Completed")
        }
    }
}