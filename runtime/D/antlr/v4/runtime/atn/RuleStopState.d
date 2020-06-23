module antlr.v4.runtime.atn.RuleStopState;

import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.StateNames : StateNames;

/**
 * TODO add class description
 */
class RuleStopState : ATNState
{

    /**
     * @uml
     * @override
     */
    public override int getStateType()
    {
        return StateNames.RULE_STOP;
    }

}
