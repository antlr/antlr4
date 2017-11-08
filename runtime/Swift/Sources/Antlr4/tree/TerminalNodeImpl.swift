/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


public class TerminalNodeImpl: TerminalNode {
    public var symbol: Token
    public weak var parent: ParseTree?

    public init(_ symbol: Token) {
        self.symbol = symbol
    }


    public func getChild(_ i: Int) -> Tree? {
        return nil
    }

    open subscript(index: Int) -> ParseTree {
        preconditionFailure("Index out of range (TerminalNode never has children)")
    }

    public func getSymbol() -> Token? {
        return symbol
    }

    public func getParent() -> Tree? {
        return parent
    }

    public func setParent(_ parent: RuleContext) {
        self.parent = parent
    }

    public func getPayload() -> AnyObject {
        return symbol
    }

    public func getSourceInterval() -> Interval {
        //if   symbol == nil   { return Interval.INVALID; }

        let tokenIndex: Int = symbol.getTokenIndex()
        return Interval(tokenIndex, tokenIndex)
    }

    public func getChildCount() -> Int {
        return 0
    }


    public func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        return visitor.visitTerminal(self)
    }

    public func getText() -> String {
        return (symbol.getText())!
    }

    public func toStringTree(_ parser: Parser) -> String {
        return description
    }

    public var description: String {
        //TODO: symbol == nil?
        //if    symbol == nil   {return "<nil>"; }
        if symbol.getType() == CommonToken.EOF {
            return "<EOF>"
        }
        return symbol.getText()!
    }

    public var debugDescription: String {
        return description
    }

    public func toStringTree() -> String {
        return description
    }
}
