/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.

import XCTest
import Antlr4

class VisitorTests: XCTestCase {
    static let allTests = [
        ("testCalculatorVisitor", testCalculatorVisitor),
        ("testShouldNotVisitTerminal", testShouldNotVisitTerminal),
        ("testShouldNotVisitEOF", testShouldNotVisitEOF),
        ("testVisitErrorNode", testVisitErrorNode),
        ("testVisitTerminalNode", testVisitTerminalNode)
    ]
    
    ///
    /// This test verifies the basic behavior of visitors, with an emphasis on
    /// {@link AbstractParseTreeVisitor#visitTerminal}.
    ///
    func testVisitTerminalNode() throws {
        let lexer = VisitorBasicLexer(ANTLRInputStream("A"))
        let parser = try VisitorBasicParser(CommonTokenStream(lexer))

        let context = try parser.s()
        XCTAssertEqual("(s A <EOF>)", context.toStringTree(parser))

        class Visitor: VisitorBasicBaseVisitor<String> {
            override func visitTerminal(_ node: TerminalNode) -> String? {
                return "\(node.getSymbol()!)\n"
            }

            override func defaultResult() -> String? {
                return ""
            }

            override func aggregateResult(_ aggregate: String?, _ nextResult: String?) -> String? {
                return aggregate! + nextResult!
            }
        }

        let visitor = Visitor()
        let result = visitor.visit(context)
        let expected =
        "[@0,0:0='A',<1>,1:0]\n" +
        "[@1,1:0='<EOF>',<-1>,1:1]\n"
        XCTAssertEqual(expected, result)
    }

    ///
    /// This test verifies the basic behavior of visitors, with an emphasis on
    /// {@link AbstractParseTreeVisitor#visitErrorNode}.
    ///
    func testVisitErrorNode() throws {
        let lexer = VisitorBasicLexer(ANTLRInputStream(""))
        let parser = try VisitorBasicParser(CommonTokenStream(lexer))

        class ErrorListener: BaseErrorListener {
            override init() {
                super.init()
            }

            var errors = [String]()

            override func syntaxError<T>(_ recognizer: Recognizer<T>,
                                         _ offendingSymbol: AnyObject?,
                                         _ line: Int, _ charPositionInLine: Int,
                                         _ msg: String, _ e: AnyObject?) {
                errors.append("line \(line):\(charPositionInLine) \(msg)")
            }
        }

        parser.removeErrorListeners()
        let errorListener = ErrorListener()
        parser.addErrorListener(errorListener)

        let context = try parser.s()
        let errors = errorListener.errors
        XCTAssertEqual("(s <missing 'A'> <EOF>)", context.toStringTree(parser))
        XCTAssertEqual(1, errors.count)
        XCTAssertEqual("line 1:0 missing 'A' at '<EOF>'", errors[0])

        class Visitor: VisitorBasicBaseVisitor<String> {
            override func visitErrorNode(_ node: ErrorNode) -> String? {
                return "Error encountered: \(node.getSymbol()!)"
            }

            override func defaultResult() -> String? {
                return ""
            }

            override func aggregateResult(_ aggregate: String?, _ nextResult: String?) -> String? {
                return aggregate! + nextResult!
            }
        }

        let visitor = Visitor()
        let result = visitor.visit(context)
        let expected = "Error encountered: [@-1,-1:-1='<missing 'A'>',<1>,1:0]"
        XCTAssertEqual(expected, result)
    }

    ///
    /// This test verifies that {@link AbstractParseTreeVisitor#visitChildren} does not call
    /// {@link ParseTreeVisitor#visit} after {@link AbstractParseTreeVisitor#shouldVisitNextChild} returns
    /// {@code false}.
    ///
    func testShouldNotVisitEOF() throws {
        let input = "A"
        let lexer = VisitorBasicLexer(ANTLRInputStream(input))
        let parser = try VisitorBasicParser(CommonTokenStream(lexer))

        let context = try parser.s()
        XCTAssertEqual("(s A <EOF>)", context.toStringTree(parser))

        class Visitor: VisitorBasicBaseVisitor<String> {
            override func visitTerminal(_ node: TerminalNode) -> String? {
                return "\(node.getSymbol()!)\n"
            }

            override func shouldVisitNextChild(_ node: RuleNode, _ currentResult: String?) -> Bool {
                return currentResult == nil || currentResult!.isEmpty
            }
        }

        let visitor = Visitor()
        let result = visitor.visit(context)
        let expected = "[@0,0:0='A',<1>,1:0]\n"
        XCTAssertEqual(expected, result)
    }

    ///
    /// This test verifies that {@link AbstractParseTreeVisitor#shouldVisitNextChild} is called before visiting the first
    /// child. It also verifies that {@link AbstractParseTreeVisitor#defaultResult} provides the default return value for
    /// visiting a tree.
    ///
    func testShouldNotVisitTerminal() throws {
        let input = "A"
        let lexer = VisitorBasicLexer(ANTLRInputStream(input))
        let parser = try VisitorBasicParser(CommonTokenStream(lexer))

        let context = try parser.s()
        XCTAssertEqual("(s A <EOF>)", context.toStringTree(parser))

        class Visitor: VisitorBasicBaseVisitor<String> {
            override func visitTerminal(_ node: TerminalNode) -> String? {
                XCTFail()
                return nil
            }

            override func defaultResult() -> String? {
                return "default result"
            }

            override func shouldVisitNextChild(_ node: RuleNode, _ currentResult: String?) -> Bool {
                return false
            }
        }

        let visitor = Visitor()
        let result = visitor.visit(context)
        let expected = "default result"
        XCTAssertEqual(expected, result)
    }

    ///
    /// This test verifies that the visitor correctly dispatches calls for labeled outer alternatives.
    ///
    func testCalculatorVisitor() throws {
        let input = "2 + 8 / 2"
        let lexer = VisitorCalcLexer(ANTLRInputStream(input))
        let parser = try VisitorCalcParser(CommonTokenStream(lexer))

        let context = try parser.s()
        XCTAssertEqual("(s (expr (expr 2) + (expr (expr 8) / (expr 2))) <EOF>)", context.toStringTree(parser))

        class Visitor: VisitorCalcBaseVisitor<Int> {
            override func visitS(_ ctx: VisitorCalcParser.SContext) -> Int? {
                return visit(ctx.expr()!)
            }

            override func visitNumber(_ ctx: VisitorCalcParser.NumberContext) -> Int? {
                return Int((ctx.INT()?.getText())!)
            }

            override func visitMultiply(_ ctx: VisitorCalcParser.MultiplyContext) -> Int? {
                let left = visit(ctx.expr(0)!)!
                let right = visit(ctx.expr(1)!)!
                if ctx.MUL() != nil {
                    return left * right
                }
                else {
                    return left / right
                }
            }

            override func visitAdd(_ ctx: VisitorCalcParser.AddContext) -> Int? {
                let left = visit(ctx.expr(0)!)!
                let right = visit(ctx.expr(1)!)!
                if ctx.ADD() != nil {
                    return left + right
                }
                else {
                    return left - right
                }
            }
        }

        let visitor = Visitor()
        let result = visitor.visit(context)
        let expected = 6
        XCTAssertEqual(expected, result!)
    }
}
