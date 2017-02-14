/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// The block that begins a closure loop.

public final class StarBlockStartState: BlockStartState {

    override
    public func getStateType() -> Int {
        return ATNState.STAR_BLOCK_START
    }
}
