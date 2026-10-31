/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

private let a = SemanticContext.Predicate(0, 0, false)
private let prec2 = SemanticContext.PrecedencePredicate(2)
private let prec5 = SemanticContext.PrecedencePredicate(5)

class SemanticContextTests: XCTestCase {

    // An operand order that followed the per-process hash seed would put any
    // one pair in the expected order by chance, so these tests check many pairs.
    private let others = (1...100).map { SemanticContext.Predicate(0, $0, false) }

    func testAndIgnoresOperandOrder() {
        for b in others {
            XCTAssertEqual(SemanticContext.and(a, b), SemanticContext.and(b, a))
            XCTAssertEqual(SemanticContext.and(a, b).hashValue, SemanticContext.and(b, a).hashValue)

            XCTAssertEqual("{0:0}?&&{0:\(b.predIndex)}?", SemanticContext.and(b, a).description)
        }
    }

    func testOrIgnoresOperandOrder() {
        for b in others {
            XCTAssertEqual(SemanticContext.or(a, b), SemanticContext.or(b, a))
            XCTAssertEqual(SemanticContext.or(a, b).hashValue, SemanticContext.or(b, a).hashValue)
        }
    }

    func testAndKeepsLowestPrecedenceOrKeepsHighest() {
        XCTAssertEqual("{0:0}?&&{2>=prec}?", SemanticContext.and(SemanticContext.and(prec5, a), prec2).description)

        XCTAssertEqual("{0:0}?||{5>=prec}?", SemanticContext.or(SemanticContext.or(prec2, a), prec5).description)
    }

    func testOrOrdersAndsOperandByOperand() {
        let b = SemanticContext.Predicate(0, 2, false)
        let c = SemanticContext.Predicate(0, 3, false)
        XCTAssertEqual("{0:0}?&&{0:2}?||{0:0}?&&{0:3}?",
                       SemanticContext.or(SemanticContext.and(a, c), SemanticContext.and(a, b)).description)
    }
}
