/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"
import { RuleContext } from "../RuleContext"
import { Map } from "../Utils"

import { ATNState, DecisionState, RuleStartState, RuleStopState, TokensStartState } from "./ATNState"
import { ATNType } from "./ATNType"
import { LexerAction } from "./LexerAction"

export declare class ATN {
    grammarType: ATNType
    maxTokenType: number
    states: Array<ATNState>
    decisionToState: Array<DecisionState>
    ruleToStartState: Array<RuleStartState>
    ruleToStopState: Array<RuleStopState> | null
    modeNameToStartState: Map<string, TokensStartState> | object
    ruleToTokenType: Array<number> | null
    lexerActions: Array<LexerAction> | null
    modeToStartState: Array<TokensStartState>

    constructor(grammarType: ATNType, maxTokenType: number)

    nextTokensInContext(s: ATNState, ctx: RuleContext | null): IntervalSet
    nextTokensNoContext(s: ATNState): IntervalSet
    nextTokens(s: ATNState, ctx?: RuleContext): IntervalSet
    addState(state: ATNState): void
    removeState(state: ATNState): void
    defineDecisionState(s: DecisionState): number
    getDecisionState(decision: number): DecisionState
    getExpectedTokens(stateNumber: number, ctx: RuleContext | null): IntervalSet
}
export declare namespace ATN {
    export const INVALID_ALT_NUMBER: number
}
