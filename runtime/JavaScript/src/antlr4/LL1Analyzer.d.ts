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
import { Token } from "./Token"
import { BitSet, Set } from "./Utils"

export class LL1Analyzer {
    public atn: ATN

    constructor(atn: ATN)

    getDecisionLookahead(s: ATNState): Array<IntervalSet>
    LOOK(s: ATNState, stopState: ATNState | null, ctx?: RuleContext | null): IntervalSet

    protected _LOOK(
        s: ATNState,
        stopState: ATNState | null,
        ctx: PredictionContext | null,
        look: IntervalSet,
        lookBusy: Set<ATNConfig>,
        calledRuleStack: BitSet,
        seeThruPreds: boolean,
        addEOF: boolean
    ): void
}
export namespace LL1Analyzer {
    export const HIT_PRED: Token.INVALID_TYPE
    export type HIT_PRED = typeof LL1Analyzer.HIT_PRED
}
