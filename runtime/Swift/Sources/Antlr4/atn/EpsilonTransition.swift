/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public final class EpsilonTransition: Transition, CustomStringConvertible {

    private let outermostPrecedenceReturnInside: Int

    public convenience override init(_ target: ATNState) {
        self.init(target, -1)
    }

    public init(_ target: ATNState, _ outermostPrecedenceReturn: Int) {

        self.outermostPrecedenceReturnInside = outermostPrecedenceReturn
        super.init(target)
    }

    /// - returns: the rule index of a precedence rule for which this transition is
    /// returning from, where the precedence value is 0; otherwise, -1.
    /// 
    /// - seealso: org.antlr.v4.runtime.atn.ATNConfig#isPrecedenceFilterSuppressed()
    /// - seealso: org.antlr.v4.runtime.atn.ParserATNSimulator#applyPrecedenceFilter(org.antlr.v4.runtime.atn.ATNConfigSet)
    /// -  4.4.1
    public func outermostPrecedenceReturn() -> Int {
        return outermostPrecedenceReturnInside
    }

    override
    public func getSerializationType() -> Int {
        return Transition.EPSILON
    }

    override
    public func isEpsilon() -> Bool {
        return true
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }


    public var description: String {
        return "epsilon"
    }
}
