module antlr.v4.runtime.atn.PredicateTransition;

import std.conv;
import antlr.v4.runtime.atn.AbstractPredicateTransition;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.SemanticContext;
import antlr.v4.runtime.atn.TransitionStates;

/**
 * TODO add class description
 */
class PredicateTransition : AbstractPredicateTransition
{

    /**
     * @uml
     * @final
     */
    public int ruleIndex;

    /**
     * @uml
     * @final
     */
    public int predIndex;

    /**
     * @uml
     * e.g., $i ref in pred
     * @final
     */
    public bool isCtxDependent;

    public this(ATNState target, int ruleIndex, int predIndex, bool isCtxDependent)
    {
        super(target);
        this.ruleIndex = ruleIndex;
        this.predIndex = predIndex;
        this.isCtxDependent = isCtxDependent;
    }

    /**
     * @uml
     * @override
     */
    public override int getSerializationType()
    {
        return TransitionStates.PREDICATE;
    }

    /**
     * @uml
     * @override
     */
    public override bool isEpsilon()
    {
        return true;
    }

    /**
     * @uml
     * @override
     */
    public override bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
    {
        return false;
    }

    public SemanticContext.Predicate getPredicate()
    {
        auto sp = new SemanticContext;
        return sp.new Predicate(ruleIndex, predIndex, isCtxDependent);
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return "pred_" ~ to!string(ruleIndex) ~ ":" ~ to!string(predIndex);
    }

}
