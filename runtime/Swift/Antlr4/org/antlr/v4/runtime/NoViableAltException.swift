/* Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */


/** Indicates that the parser could not decide which of two or more paths
 *  to take based upon the remaining input. It tracks the starting token
 *  of the offending input and also knows where the parser was
 *  in the various paths when the error. Reported by reportNoViableAlternative()
 */

public class NoViableAltException: RecognitionException<ParserATNSimulator> {
    /** Which configurations did we try at input.index() that couldn't match input.LT(1)? */

    private final var deadEndConfigs: ATNConfigSet?

    /** The token object at the start index; the input stream might
     * 	not be buffering tokens so get a reference to it. (At the
     *  time the error occurred, of course the stream needs to keep a
     *  buffer all of the tokens but later we might not have access to those.)
     */

    private final var startToken: Token

    public convenience init(_ recognizer: Parser?) throws {
        // LL(1) error
        self.init(recognizer,
                recognizer!.getInputStream()!,
                try recognizer!.getCurrentToken(),
                try recognizer!.getCurrentToken(),
                nil,
                recognizer!._ctx)
    }

    public init(_ recognizer: Parser?,
                _ input: IntStream,
                _ startToken: Token,
                _ offendingToken: Token,
                _ deadEndConfigs: ATNConfigSet?,
                _ ctx: ParserRuleContext?) {

        self.deadEndConfigs = deadEndConfigs
        self.startToken = startToken

        // as? Recognizer<AnyObject, ATNSimulator>
        super.init(recognizer, input, ctx)
        self.setOffendingToken(offendingToken)
    }


    public func getStartToken() -> Token {
        return startToken
    }


    public func getDeadEndConfigs() -> ATNConfigSet? {
        return deadEndConfigs
    }

}
