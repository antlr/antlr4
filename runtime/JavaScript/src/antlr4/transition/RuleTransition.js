/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import Transition from "./Transition.js";

export default class RuleTransition extends Transition {
    constructor(ruleStart, ruleIndex, precedence, followState) {
        super(ruleStart);
        // ptr to the rule definition object for this rule ref
        this.ruleIndex = ruleIndex;
        this.precedence = precedence;
        // what node to begin computations following ref to rule
        this.followState = followState;
        this.serializationType = Transition.RULE;
        this.isEpsilon = true;
    }

    matches(symbol, minVocabSymbol, maxVocabSymbol) {
        return false;
    }
}

