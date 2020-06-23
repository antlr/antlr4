/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

module antlr.v4.runtime.atn.Transition;

import std.conv;
import antlr.v4.runtime.atn.TransitionStates;
import antlr.v4.runtime.atn.EpsilonTransition;
import antlr.v4.runtime.atn.RuleTransition;
import antlr.v4.runtime.atn.PredicateTransition;
import antlr.v4.runtime.atn.RangeTransition;
import antlr.v4.runtime.atn.AtomTransition;
import antlr.v4.runtime.atn.ActionTransition;
import antlr.v4.runtime.atn.SetTransition;
import antlr.v4.runtime.atn.NotSetTransition;
import antlr.v4.runtime.atn.WildcardTransition;
import antlr.v4.runtime.atn.PrecedencePredicateTransition;
import antlr.v4.runtime.atn.ATNState;
import antlr.v4.runtime.misc.IntervalSet;

/**
 * An ATN transition between any two ATN states.  Subclasses define
 * atom, set, epsilon, action, predicate, rule transitions.
 *
 * <p>This is a one way link.  It emanates from a state (usually via a list of
 * transitions) and has a target state.</p>
 *
 * <p>Since we never have to change the ATN transitions once we construct it,
 * we can fix these transitions as specific classes. The DFA transitions
 * on the other hand need to update the labels as it adds transitions to
 * the states. We'll use the term Edge for the DFA to distinguish them from
 * ATN transitions.</p>
 */
abstract class Transition
{

    /**
     * The target of this transition.
     */
    public ATNState target;

    public static string[] serializationNames = [
        "INVALID",
        "EPSILON",
        "RANGE",
        "RULE",
        "PREDICATE",
        "ATOM",
        "ACTION",
        "SET",
        "NOT_SET",
        "WILDCARD",
        "PRECEDENCE"
    ];

    public this(ATNState target)
    {
        this.target = target;
    }

    public this()
    {
    }

    abstract public int getSerializationType();

    /**
     * Determines if the transition is an "epsilon" transition.
     *
     * <p>The default implementation returns {@code false}.</p>
     *
     *  @return {@code true} if traversing this transition in the ATN does not
     *  consume an input symbol; otherwise, {@code false} if traversing this
     *  transition consumes (matches) an input symbol.
     */
    public bool isEpsilon()
    {
        return false;
    }

    public IntervalSet label()
    {
        return null;
    }

    abstract public bool matches(int symbol, int minVocabSymbol, int maxVocabSymbol);

}
