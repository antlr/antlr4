/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 


public final class RuleStartState: ATNState {
    public var stopState: RuleStopState?
    public var isPrecedenceRule: Bool = false
    //Synonymous with rule being left recursive; consider renaming.

    override
    public func getStateType() -> Int {
        return ATNState.RULE_START
    }
}
