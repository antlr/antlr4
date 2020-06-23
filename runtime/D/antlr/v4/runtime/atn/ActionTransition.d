module antlr.v4.runtime.atn.ActionTransition;

import std.conv;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.atn.ATNState;

/**
 * TODO add class description
 */
class ActionTransition : Transition
{

    public int ruleIndex;

    /**
     * @uml
     * @read
     * @write
     */
    private int actionIndex_;

    /**
     * @uml
     * e.g., $i ref in action
     */
    public bool isCtxDependent;

    public this(ATNState target, int ruleIndex)
    {
        this(target, ruleIndex, -1, false);
    }

    public this(ATNState target, int ruleIndex, int actionIndex, bool isCtxDependent)
    {
        super(target);
        this.ruleIndex = ruleIndex;
        this.actionIndex = actionIndex;
        this.isCtxDependent = isCtxDependent;
    }

    /**
     * @uml
     * @override
     */
    public override int getSerializationType()
    {
        return TransitionStates.ACTION;
    }

    /**
     * @uml
     * @override
     */
    public override bool isEpsilon()
    {
        return true; // we are to be ignored by analysis 'cept for predicates
    }

    /**
     * @uml
     * @override
     */
    public override bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
    {
        return false;
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return "action_" ~ to!string(ruleIndex) ~ ":" ~ to!string(actionIndex);
    }

    public final int actionIndex()
    {
        return this.actionIndex_;
    }

    public final void actionIndex(int actionIndex)
    {
        this.actionIndex_ = actionIndex;
    }

}
