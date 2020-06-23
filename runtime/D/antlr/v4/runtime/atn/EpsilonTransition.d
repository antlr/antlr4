/*
 * Copyright (c) 2012-2020 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.EpsilonTransition;

import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.atn.Transition;
import antlr.v4.runtime.atn.TransitionStates;

/**
 * Special Transition
 */
class EpsilonTransition : Transition
{

    /**
     *  @return the rule index of a precedence rule for which this transition is
     *  returning from, where the precedence value is 0; otherwise, -1.
     *
     *  @see ATNConfig#isPrecedenceFilterSuppressed()
     *  @see ParserATNSimulator#applyPrecedenceFilter(ATNConfigSet)
     *  @since 4.4.1
     * @uml
     * @read
     * @final
     */
    private int outermostPrecedenceReturn_;

    public this(ATNState target)
    {
        this(target, -1);
    }

    public this(ATNState target, int outermostPrecedenceReturn)
    {
        super(target);
        this.outermostPrecedenceReturn_ = outermostPrecedenceReturn;
    }

    /**
     * Only for unittest required.
     */
    public this()
    {
    }

    /**
     * @uml
     * @override
     */
    public override int getSerializationType()
    {
        return TransitionStates.EPSILON;
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

    /**
     * @uml
     * @override
     */
    public override string toString()
    {
        return "epsilon";
    }

    public final int outermostPrecedenceReturn()
    {
        return this.outermostPrecedenceReturn_;
    }

}
