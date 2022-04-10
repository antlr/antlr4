/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import ATNState from "./ATNState.js";
import BlockStartState from "./BlockStartState.js";

export default class BasicBlockStartState extends BlockStartState {
    constructor() {
        super();
        this.stateType = ATNState.BLOCK_START;
        return this;
    }
}
