module antlr.v4.runtime.atn.BlockStartState;

import antlr.v4.runtime.atn.DecisionState;
import antlr.v4.runtime.atn.BlockEndState;

/**
 * TODO add class description
 */
abstract class BlockStartState : DecisionState
{

    public BlockEndState endState;

}
