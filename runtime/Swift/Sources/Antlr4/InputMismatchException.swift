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
    public init(_ recognizer: Parser) {
        super.init(recognizer, recognizer.getInputStream()!, recognizer._ctx)
        if let token = try? recognizer.getCurrentToken() {
            setOffendingToken(token)
        }
    }
}
