/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public class LexerDFASerializer: DFASerializer {
    public init(_ dfa: DFA) {
        super.init(dfa, Vocabulary.EMPTY_VOCABULARY)
    }

    override

    internal func getEdgeLabel(_ i: Int) -> String {
        return "'\(Character(integerLiteral: i))'"
    }
}
