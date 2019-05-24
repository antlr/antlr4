/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/// Indicates that the parser could not decide which of two or more paths
/// to take based upon the remaining input. It tracks the starting token
/// of the offending input and also knows where the parser was
/// in the various paths when the error. Reported by reportNoViableAlternative()
/// 

public class NoViableAltException: RecognitionException {
    /// Which configurations did we try at input.index() that couldn't match input.LT(1)?

    private let deadEndConfigs: ATNConfigSet?

    /// The token object at the start index; the input stream might
    /// not be buffering tokens so get a reference to it. (At the
    /// time the error occurred, of course the stream needs to keep a
    /// buffer all of the tokens but later we might not have access to those.)
    /// 
    private let startToken: Token

    public convenience init(_ recognizer: Parser) {
        // LL(1) error
        let token = try! recognizer.getCurrentToken()
        self.init(recognizer,
                recognizer.getInputStream()!,
                token,
                token,
                nil,
                recognizer._ctx)
    }

    public init(_ recognizer: Parser?,
                _ input: IntStream,
                _ startToken: Token,
                _ offendingToken: Token?,
                _ deadEndConfigs: ATNConfigSet?,
                _ ctx: ParserRuleContext?) {

        self.deadEndConfigs = deadEndConfigs
        self.startToken = startToken

        super.init(recognizer, input, ctx)
        if let offendingToken = offendingToken {
            setOffendingToken(offendingToken)
        }
    }


    public func getStartToken() -> Token {
        return startToken
    }


    public func getDeadEndConfigs() -> ATNConfigSet? {
        return deadEndConfigs
    }

}
