/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// The last node in the ATN for a rule, unless that rule is the start symbol.
/// In that case, there is one transition to EOF. Later, we might encode
/// references to all calls to this rule to compute FOLLOW sets for
/// error handling.

public final class RuleStopState: ATNState {

    override
    public func getStateType() -> Int {
        return ATNState.RULE_STOP
    }

}
