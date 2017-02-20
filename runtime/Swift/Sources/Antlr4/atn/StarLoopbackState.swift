/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.


public final class StarLoopbackState: ATNState {
    public func getLoopEntryState() -> StarLoopEntryState {
        return transition(0).target as! StarLoopEntryState
    }

    override
    public func getStateType() -> Int {
        return ATNState.STAR_LOOP_BACK
    }
}
