/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DFA } from "../dfa"
import { DFAState } from "../dfa/DFAState"
import { InputStream } from "../InputStream"
import { Lexer } from "../Lexer"
import { PredictionContextCache } from "../PredictionContext"
import { ATN } from "./ATN"
import { LexerATNConfig } from "./ATNConfig"
import { ATNConfigSet, OrderedATNConfigSet } from "./ATNConfigSet"
import { ATNSimulator } from "./ATNSimulator"
import { ATNState } from "./ATNState"
import { LexerActionExecutor } from "./LexerActionExecutor"
import { Transition } from "./Transition"

export interface SimState {
    index: number
    line: number
    column: number
    dfaState: DFAState | null
}
export class SimState implements SimState {
    constructor()

    reset(): void
}

export class LexerATNSimulator extends ATNSimulator {
    public decisionToDFA: Array<DFA>
    public recog: Lexer
    public startIndex: number
    public line: number
    public column: number
    public mode: number
    public prevAccept: SimState

    constructor(
        recog: Lexer,
        atn: ATN,
        decisionToDFA: Array<DFA>,
        sharedContextCache: PredictionContextCache
    )

    public copyState(simulator: LexerATNSimulator): void
    public match(input: InputStream, mode: number): number
    public reset(): void
    public getDFA(mode: number): DFA
    public getText(input: InputStream): string
    public consume(input: InputStream): void
    public getTokenName(tt: number): string

    protected matchATN(input: InputStream): number
    protected execATN(input: InputStream, ds0: DFAState): number
    protected getExistingTargetState(s: DFAState, t: number): DFAState | null
    protected computeTargetState(input: InputStream, s: DFAState, t: number): DFAState
    protected failOrAccept(prevAccept: SimState, input: InputStream, reach: ATNConfigSet, t: number): number
    protected getReachableConfigSet(
        input: InputStream,
        closure: ATNConfigSet,
        reach: ATNConfigSet,
        t: number
    ): void
    protected accept(
        input: InputStream,
        lexerActionExecutor: LexerActionExecutor,
        startIndex: number,
        index: number,
        line: number,
        charPos: number
    ): void
    protected getReachableTarget(trans: Transition, t: number): ATNState | null
    protected computeStartState(input: InputStream, p: ATNState): OrderedATNConfigSet
    protected closure(
        input: InputStream,
        config: LexerATNConfig,
        configs: ATNConfigSet,
        currentAltReachedAcceptState: boolean,
        speculative: boolean,
        treatEofAsEpsilon: boolean
    ): boolean
    protected getEpsilonTarget(
        input: InputStream,
        config: LexerATNConfig,
        t: Transition,
        configs: ATNConfigSet,
        speculative: boolean,
        treatEofAsEpsilon: boolean
    ): LexerATNConfig
    protected evaluatePredicate(
        input: InputStream,
        ruleIndex: number,
        predIndex: number,
        speculative: boolean
    ): boolean
    protected captureSimState(settings: SimState, input: InputStream, dfaState: DFAState): void
    // If `to` is DFAState, then it isn't modified before being returned.
    protected addDFAEdge(from_: DFAState, tk: number, to: DFAState): DFAState
    // If `to` is undefined, then it is coerced to null.
    // If `to` is null, then it is set to DFAState.
    protected addDFAEdge(from_: DFAState, tk: number, to: undefined | null, cfgs: ATNConfigSet): DFAState
    protected addDFAState(configs: ATNConfigSet): DFAState
}
export namespace LexerATNSimulator {
    export const MIN_DFA_EDGE: number
    export const MAX_DFA_EDGE: number
    export type MIN_DFA_EDGE = typeof LexerATNSimulator.MIN_DFA_EDGE
    export type MAX_DFA_EDGE = typeof LexerATNSimulator.MAX_DFA_EDGE

    export let debug: boolean
    export let dfa_debug: boolean
    export let match_calls: number
}
