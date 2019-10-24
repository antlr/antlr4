/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"

import { ATN } from "./ATN"
import { Transition } from "./Transition"

export declare abstract class ATNState {
    static serializationNames: ReadonlyArray<string>

    atn: ATN | null
    stateNumber: number
    stateType: ATNStateType | null
    ruleIndex: number
    epsilonOnlyTransitions: boolean
    transitions: Array<Transition>
    nextTokenWithinRule: IntervalSet | null

    constructor()

    toString(): string
    equals(other: any): boolean
    isNonGreedyExitState(): boolean
    addTransition(trans: Transition, index?: number): void
}
export declare namespace ATNState {
    export const INVALID_TYPE: 0
    export const BASIC: 1
    export const RULE_START: 2
    export const BLOCK_START: 3
    export const PLUS_BLOCK_START: 4
    export const STAR_BLOCK_START: 5
    export const TOKEN_START: 6
    export const RULE_STOP: 7
    export const BLOCK_END: 8
    export const STAR_LOOP_BACK: 9
    export const STAR_LOOP_ENTRY: 10
    export const PLUS_LOOP_BACK: 11
    export const LOOP_END: 12

    export const INVALID_STATE_NUMBER: number
}

export declare type ATNStateType = 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12

export declare class BasicState extends ATNState {
    stateType: ATNStateType
}

export declare abstract class DecisionState extends ATNState {
    decision: -1
    nonGreedy: boolean
}

export abstract class BlockStartState extends DecisionState {
    endState: BlockEndState | null
}

export declare class BasicBlockStartState extends BlockStartState {
    stateType: ATNStateType
}

export declare class BlockEndState extends ATNState {
    stateType: ATNStateType
    startState: BlockStartState | null
}

export declare class RuleStopState extends ATNState {
    stateType: ATNStateType
}

export declare class RuleStartState extends ATNState {
    stateType: ATNStateType
    stopState: RuleStopState | null
    isPrecedenceRule: boolean
}

export declare class PlusLoopbackState extends ATNState {
    stateType: ATNStateType
}

export declare class PlusBlockStartState extends ATNState {
    stateType: ATNStateType
    loopBackState: PlusLoopbackState | null
}

export declare class StarBlockStartState extends BlockStartState {
    stateType: ATNStateType
}

export declare class StarLoopbackState extends ATNState {
    stateType: ATNStateType
}

export declare class StarLoopEntryState extends DecisionState {
    stateType: ATNStateType
    loopBackState: StarLoopbackState | null
    isPrecedenceDecision: boolean | null
}

export declare class LoopEndState extends ATNState {
    stateType: ATNStateType
    loopBackState: ATNState | null
}

export declare class TokensStartState extends DecisionState {
    stateType: ATNStateType
}
