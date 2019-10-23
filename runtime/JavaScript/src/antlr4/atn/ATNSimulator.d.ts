/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DFAState } from "../dfa/DFAState"
import { PredictionContext, PredictionContextCache } from "../PredictionContext"
import { Map } from "../Utils"
import { ATN } from "./ATN"
import {} from "./ATNConfigSet"

export class ATNSimulator {
    public atn: ATN
    public sharedContextCache: PredictionContextCache

    constructor(atn: ATN, sharedContextCache: PredictionContextCache)

    getCachedContext(context: PredictionContext): PredictionContext
}
export namespace ATNSimulator {
    export const ERROR: DFAState
}
