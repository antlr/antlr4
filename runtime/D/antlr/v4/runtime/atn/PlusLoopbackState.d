module antlr.v4.runtime.atn.PlusLoopbackState;

import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.StateNames;

/**
 * TODO add class description
 */
class PlusLoopbackState : DecisionState
{

    /**
     * @uml
     * @override
     */
    public override int getStateType()
    {
        return StateNames.PLUS_LOOP_BACK;
    }

}
