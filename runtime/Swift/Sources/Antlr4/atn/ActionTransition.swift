/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public final class ActionTransition: Transition, CustomStringConvertible {
    public let ruleIndex: Int
    public let actionIndex: Int
    public let isCtxDependent: Bool
    // e.g., $i ref in action


    public convenience init(_ target: ATNState, _ ruleIndex: Int) {
        self.init(target, ruleIndex, -1, false)
    }

    public init(_ target: ATNState, _ ruleIndex: Int, _ actionIndex: Int, _ isCtxDependent: Bool) {

        self.ruleIndex = ruleIndex
        self.actionIndex = actionIndex
        self.isCtxDependent = isCtxDependent
        super.init(target)
    }

    override
    public func getSerializationType() -> Int {
        return Transition.ACTION
    }

    override
    public func isEpsilon() -> Bool {
        return true // we are to be ignored by analysis 'cept for predicates
    }

    override
    public func matches(_ symbol: Int, _ minVocabSymbol: Int, _ maxVocabSymbol: Int) -> Bool {
        return false
    }

    public var description: String {
        return "action_\(ruleIndex):\(actionIndex)"
    }

}
