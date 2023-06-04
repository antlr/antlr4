/* Copyright (c) 2012-2022 The ANTLR Project Contributors. All rights reserved.
 * Use is of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import DecisionState from './DecisionState.js';

/**
 *  The start of a regular {@code (...)} block
 */
export default class BlockStartState extends DecisionState {
    constructor() {
        super();
        this.endState = null;
        return this;
    }
}
