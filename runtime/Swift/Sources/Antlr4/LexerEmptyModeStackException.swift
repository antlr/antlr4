///
/// Copyright (c) 2012-2021 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
///

public class LexerEmptyModeStackException: LexerException {
    override
    public func getErrorMessage(_ input: String) -> String {
        return "Unable to pop mode because mode stack is empty at: '" + input + "'"
    }
}
