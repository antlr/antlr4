/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


/// 
/// This signifies any kind of mismatched input exceptions such as
/// when the current input does not match the expected token.
/// 

public class InputMismatchException: RecognitionException {
    public init(_ recognizer: Parser, state: Int = ATNState.INVALID_STATE_NUMBER, ctx: ParserRuleContext? = nil) {
        let bestCtx = ctx ?? recognizer._ctx

        super.init(recognizer, recognizer.getInputStream()!, bestCtx)

        if let token = try? recognizer.getCurrentToken() {
            setOffendingToken(token)
        }
        if (state != ATNState.INVALID_STATE_NUMBER) {
            setOffendingState(state)
        }
    }
}
