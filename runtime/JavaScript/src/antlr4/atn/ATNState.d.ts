/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"
import { ATN } from "./ATN"
import { Transition } from "./Transition"

type ATNStateNumber =
    ATNState.INVALID_TYPE
    | ATNState.BASIC
    | ATNState.RULE_START
    | ATNState.BLOCK_START
    | ATNState.PLUS_BLOCK_START
    | ATNState.STAR_BLOCK_START
    | ATNState.TOKEN_START
    | ATNState.RULE_STOP
    | ATNState.BLOCK_END
    | ATNState.STAR_LOOP_BACK
    | ATNState.STAR_LOOP_ENTRY
    | ATNState.PLUS_LOOP_BACK
    | ATNState.LOOP_END

export abstract class ATNState {
    public atn: ATN | null
    public stateNumber: ATNState.INVALID_STATE_NUMBER
    public stateType: ATNStateNumber | null
    public ruleIndex: number
    public epsilonOnlyTransitions: false
    public transitions: Array<Transition>
    public nextTokenWithinRule: IntervalSet | null

    constructor()

    toString(): string
    equals(other: any): boolean
    isNonGreedyExitState(): false
    addTransition(trans: Transition, index?: number): void
}
export namespace ATNState {
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
    export type INVALID_TYPE = typeof ATNState.INVALID_TYPE
    export type BASIC = typeof ATNState.BASIC
    export type RULE_START = typeof ATNState.RULE_START
    export type BLOCK_START = typeof ATNState.BLOCK_START
    export type PLUS_BLOCK_START = typeof ATNState.PLUS_BLOCK_START
    export type STAR_BLOCK_START = typeof ATNState.STAR_BLOCK_START
    export type TOKEN_START = typeof ATNState.TOKEN_START
    export type RULE_STOP = typeof ATNState.RULE_STOP
    export type BLOCK_END = typeof ATNState.BLOCK_END
    export type STAR_LOOP_BACK = typeof ATNState.STAR_LOOP_BACK
    export type STAR_LOOP_ENTRY = typeof ATNState.STAR_LOOP_ENTRY
    export type PLUS_LOOP_BACK = typeof ATNState.PLUS_LOOP_BACK
    export type LOOP_END = typeof ATNState.LOOP_END

    export const INVALID_STATE_NUMBER: -1
    export type INVALID_STATE_NUMBER = typeof ATNState.INVALID_STATE_NUMBER

    export const serializationNames: Array<string>
}

export abstract class BasicState extends ATNState {
    public stateType: ATNState.BASIC
}

export abstract class DecisionState extends ATNState {
    public decision: -1
    public nonGreedy: false
}

export abstract class BlockStartState extends DecisionState {
    public endState: BlockEndState | null
}

export class BasicBlockStartState extends BlockStartState {
    public stateType: ATNState.BLOCK_START
}

export class BlockEndState extends ATNState {
    public stateType: ATNState.BLOCK_END
    public startState: BlockStartState | null
}

export class RuleStopState extends ATNState {
    public stateType: ATNState.RULE_STOP
}

export class RuleStartState extends ATNState {
    public stateType: ATNState.RULE_START
    public stopState: RuleStopState | null
    public isPrecedenceRule: false
}

export class PlusLoopbackState extends ATNState {
    public stateType: ATNState.PLUS_LOOP_BACK
}

export class PlusBlockStartState extends ATNState {
    public stateType: ATNState.PLUS_BLOCK_START
    public loopBackState: PlusLoopbackState | null
}

export class StarBlockStartState extends BlockStartState {
    public stateType: ATNState.STAR_BLOCK_START
}

export class StarLoopbackState extends ATNState {
    public stateType: ATNState.STAR_LOOP_BACK
}

export class StarLoopEntryState extends DecisionState {
    public stateType: ATNState.STAR_LOOP_ENTRY
    public loopBackState: StarLoopbackState | null
    public isPrecedenceDecision: boolean | null
}

export class LoopEndState extends ATNState {
    public stateType: ATNState.LOOP_END
    public loopBackState: ATNState | null
}

export class TokensStartState extends DecisionState {
    public stateType: ATNState.TOKEN_START
}
