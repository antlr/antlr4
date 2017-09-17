/// 
/// Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.
/// 



/// 
/// Start of `(A|B|...)+` loop. Technically a decision state, but
/// we don't use for code generation; somebody might need it, so I'm defining
/// it for completeness. In reality, the _org.antlr.v4.runtime.atn.PlusLoopbackState_ node is the
/// real decision-making note for `A+`.
/// 

public final class PlusBlockStartState: BlockStartState {
    public var loopBackState: PlusLoopbackState?

    override
    public func getStateType() -> Int {
        return ATNState.PLUS_BLOCK_START
    }
}
