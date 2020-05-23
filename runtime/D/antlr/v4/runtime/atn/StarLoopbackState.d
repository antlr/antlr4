module antlr.v4.runtime.atn.StarLoopbackState;

import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.StateNames;
import antlr.v4.runtime.atn.StarLoopEntryState;

/**
 * TODO add class description
 */
class StarLoopbackState : ATNState
{

    public StarLoopEntryState getLoopEntryState()
    {
        return cast(StarLoopEntryState)transition(0).target;
    }

    /**
     * @uml
     * @override
     */
    public override int getStateType()
    {
        return StateNames.STAR_LOOP_BACK;
    }

}
