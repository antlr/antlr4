/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

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
export default class Transition {
    constructor(target) {
        // The target of this transition.
        if (target===undefined || target===null) {
            throw "target cannot be null.";
        }
        this.target = target;
        // Are we epsilon, action, sempred?
        this.isEpsilon = false;
        this.label = null;
    }
}

// constants for serialization

Transition.EPSILON = 1;
Transition.RANGE = 2;
Transition.RULE = 3;
// e.g., {isType(input.LT(1))}?
Transition.PREDICATE = 4;
Transition.ATOM = 5;
Transition.ACTION = 6;
// ~(A|B) or ~atom, wildcard, which convert to next 2
Transition.SET = 7;
Transition.NOT_SET = 8;
Transition.WILDCARD = 9;
Transition.PRECEDENCE = 10;

Transition.serializationNames = [
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

Transition.serializationTypes = {
        EpsilonTransition: Transition.EPSILON,
        RangeTransition: Transition.RANGE,
        RuleTransition: Transition.RULE,
        PredicateTransition: Transition.PREDICATE,
        AtomTransition: Transition.ATOM,
        ActionTransition: Transition.ACTION,
        SetTransition: Transition.SET,
        NotSetTransition: Transition.NOT_SET,
        WildcardTransition: Transition.WILDCARD,
        PrecedencePredicateTransition: Transition.PRECEDENCE
    };

