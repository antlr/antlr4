module antlr.v4.runtime.atn.BlockEndState;

import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.BlockStartState;
import antlr.v4.runtime.atn.StateNames;

/**
 * TODO add class description
 */
class BlockEndState : ATNState
{

    public BlockStartState startState;

    /**
     * @uml
     * @override
     */
    public override int getStateType()
    {
        return StateNames.BLOCK_END;
    }

}
