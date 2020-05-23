module antlr.v4.runtime.atn.PrecedencePredicateTransition;

import std.conv;
import antlr.v4.runtime.atn.AbstractPredicateTransition;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.atn.SemanticContext;

/**
 * TODO add class description
 */
class PrecedencePredicateTransition : AbstractPredicateTransition
{

    /**
     * @uml
     * @final
     */
    public int precedence;

    public this(ATNState target, int precedence)
    {
        super(target);
        this.precedence = precedence;
    }

    /**
     * @uml
     * @override
     */
    public override int getSerializationType()
    {
        return TransitionStates.PRECEDENCE;
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

    public SemanticContext.PrecedencePredicate getPredicate()
    {
        auto sc = new SemanticContext;
        return sc.new PrecedencePredicate(precedence);
    }

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return to!string(precedence) ~ " >= _p";
    }

}
