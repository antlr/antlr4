/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATN } from "./atn"
import { ATNConfig } from "./atn/ATNConfig"
import { ATNState } from "./atn/ATNState"
import { IntervalSet } from "./IntervalSet"
import { PredictionContext } from "./PredictionContext"
import { RuleContext } from "./RuleContext"
import { BitSet, Set } from "./Utils"

export declare class LL1Analyzer {
    atn: ATN

    constructor(atn: ATN)

    getDecisionLookahead(s: ATNState): Array<IntervalSet>
    LOOK(s: ATNState, stopState: ATNState | null, ctx?: RuleContext | null): IntervalSet

    protected _LOOK(s: ATNState, stopState: ATNState | null, ctx: PredictionContext | null, look: IntervalSet, lookBusy: Set<ATNConfig>, calledRuleStack: BitSet, seeThruPreds: boolean, addEOF: boolean): void
}
export declare namespace LL1Analyzer {
    export const HIT_PRED: number
}
