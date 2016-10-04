/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  Copyright (c) 2015 Janyou
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


public class TerminalNodeImpl: TerminalNode {
    public var symbol: Token
    public var parent: ParseTree?

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
