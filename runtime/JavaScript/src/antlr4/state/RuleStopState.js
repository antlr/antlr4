/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import ATNState from "./ATNState.js";

/**
 * The last node in the ATN for a rule, unless that rule is the start symbol.
 * In that case, there is one transition to EOF. Later, we might encode
 * references to all calls to this rule to compute FOLLOW sets for
 * error handling
 */
export default class RuleStopState extends ATNState {
    constructor() {
        super();
        this.stateType = ATNState.RULE_STOP;
        return this;
    }
}
