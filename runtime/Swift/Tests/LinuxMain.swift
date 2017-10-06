#if os(Linux)

import XCTest
@testable import Antlr4Tests

XCTMain([
    // Antlr4Tests
    testCase(TokenStreamTests.allTests),
    testCase(TokenStreamRewriterTests.allTests),
    testCase(VisitorTests.allTests)
])

#endif
