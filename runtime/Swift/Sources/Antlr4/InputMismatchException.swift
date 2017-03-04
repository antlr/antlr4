/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


/// This signifies any kind of mismatched input exceptions such as
/// when the current input does not match the expected token.

public class InputMismatchException: RecognitionException<ParserATNSimulator> {
    public init(_ recognizer: Parser) throws {
        super.init(recognizer, recognizer.getInputStream()!, recognizer._ctx)
        self.setOffendingToken(try recognizer.getCurrentToken())
    }
}
