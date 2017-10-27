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


    public override func getChild(_ i: Int) -> Tree? {
        return nil
    }

    override
    public func getSymbol() -> Token? {
        return symbol
    }

    override
    public func getParent() -> Tree? {
        return parent
    }

    override
    public func setParent(_ parent: RuleContext) {
        self.parent = parent
    }

    override
    public func getPayload() -> AnyObject {
        return symbol
    }

    override
    public func getSourceInterval() -> Interval {
        //if   symbol == nil   { return Interval.INVALID; }

        let tokenIndex: Int = symbol.getTokenIndex()
        return Interval(tokenIndex, tokenIndex)
    }

    override
    public func getChildCount() -> Int {
        return 0
    }


    override
    public func accept<T>(_ visitor: ParseTreeVisitor<T>) -> T? {
        return visitor.visitTerminal(self)
    }

    override
    public func getText() -> String {
        return (symbol.getText())!
    }

    override
    public func toStringTree(_ parser: Parser) -> String {
        return description
    }

    override
    public var description: String {
        //TODO: symbol == nil?
        //if    symbol == nil   {return "<nil>"; }
        if symbol.getType() == CommonToken.EOF {
            return "<EOF>"
        }
        return symbol.getText()!
    }
    public override var debugDescription: String {
        return description
    }
    override
    public func toStringTree() -> String {
        return description
    }
}
