module antlr.v4.runtime.atn.PlusBlockStartState;

import antlr.v4.runtime.atn.BlockStartState;
import antlr.v4.runtime.atn.PlusLoopbackState;
import antlr.v4.runtime.atn.StateNames;

/**
 * @uml
 * Start of {@code (A|B|...)+} loop. Technically a decision state, but
 * we don't use for code generation; somebody might need it, so I'm defining
 * it for completeness. In reality, the {@link PlusLoopbackState} node is the
 * real decision-making note for {@code A+}.
 */
class PlusBlockStartState : BlockStartState
{

    public PlusLoopbackState loopBackState;

    /**
     * @uml
     * @override
     */
    public override int getStateType()
    {
        return StateNames.PLUS_BLOCK_START;
    }

}
