///
/// Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
///


public class LexerException: RecognitionException {
    internal let startIndex: Int
    internal let length: Int

    public init(_ lexer: Lexer?,
                _ input: CharStream,
                _ startIndex: Int,
                _ length: Int) {
        let ctx: ParserRuleContext? = nil
        self.startIndex = startIndex
        self.length = length
        super.init(lexer, input as IntStream, ctx)
    }

    public func getStartIndex() -> Int {
        return startIndex
    }

    public func getLength() -> Int {
        return length
    }

    public func getErrorMessage(_ input: String) -> String {
        return input
    }
}
