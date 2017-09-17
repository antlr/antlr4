/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public final class StarLoopEntryState: DecisionState {
    public var loopBackState: StarLoopbackState?

    /// 
    /// Indicates whether this state can benefit from a precedence DFA during SLL
    /// decision making.
    /// 
    /// This is a computed property that is calculated during ATN deserialization
    /// and stored for use in _org.antlr.v4.runtime.atn.ParserATNSimulator_ and
    /// _org.antlr.v4.runtime.ParserInterpreter_.
    /// 
    /// - seealso: org.antlr.v4.runtime.dfa.DFA#isPrecedenceDfa()
    /// 
    public var precedenceRuleDecision: Bool = false

    override
    public func getStateType() -> Int {
        return ATNState.STAR_LOOP_ENTRY
    }
}
