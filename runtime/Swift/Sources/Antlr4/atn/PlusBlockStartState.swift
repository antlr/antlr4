/// Copyright (c) 2012-2016 The ANTLR Project. All rights reserved.
/// Use of this file is governed by the BSD 3-clause license that
/// can be found in the LICENSE.txt file in the project root.



/// Start of {@code (A|B|...)+} loop. Technically a decision state, but
/// we don't use for code generation; somebody might need it, so I'm defining
/// it for completeness. In reality, the {@link org.antlr.v4.runtime.atn.PlusLoopbackState} node is the
/// real decision-making note for {@code A+}.

public final class PlusBlockStartState: BlockStartState {
    public var loopBackState: PlusLoopbackState?

    override
    public func getStateType() -> Int {
        return ATNState.PLUS_BLOCK_START
    }
}
