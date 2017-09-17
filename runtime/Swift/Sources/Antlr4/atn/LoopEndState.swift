/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Mark the end of a * or + loop.
/// 

public final class LoopEndState: ATNState {
    public var loopBackState: ATNState?

    override
    public func getStateType() -> Int {
        return ATNState.LOOP_END
    }
}
