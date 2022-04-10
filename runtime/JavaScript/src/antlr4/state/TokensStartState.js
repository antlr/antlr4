/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import DecisionState from "./DecisionState.js";
import ATNState from "./ATNState.js";

/**
 * The Tokens rule start state linking to each lexer rule start state
 */
export default class TokensStartState extends DecisionState {
    constructor() {
        super();
        this.stateType = ATNState.TOKEN_START;
        return this;
    }
}
