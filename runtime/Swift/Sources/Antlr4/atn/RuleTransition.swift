/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.





public final class RuleTransition: Transition {
    /// Ptr to the rule definition object for this rule ref
    public final var ruleIndex: Int
    // no Rule object at runtime

    public final var precedence: Int

    /// What node to begin computations following ref to rule
    public final var followState: ATNState

    /// -  Use
    /// {@link #RuleTransition(org.antlr.v4.runtime.atn.RuleStartState, int, int, org.antlr.v4.runtime.atn.ATNState)} instead.
    //@Deprecated
    public convenience init(_ ruleStart: RuleStartState,
                            _ ruleIndex: Int,
                            _ followState: ATNState) {
        self.init(ruleStart, ruleIndex, 0, followState)
    }

    public init(_ ruleStart: RuleStartState,
                _ ruleIndex: Int,
                _ precedence: Int,
                _ followState: ATNState) {

        self.ruleIndex = ruleIndex
        self.precedence = precedence
        self.followState = followState

        super.init(ruleStart)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.RULE
    }

    override
    public func isEpsilon() -> Bool {
        return true
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }
}
