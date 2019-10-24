/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"

import { ATN } from "./ATN"
import { ATNDeserializationOptions } from "./ATNDeserializationOptions"
import { ATNState, ATNStateType } from "./ATNState"
import { LexerAction, LexerActionType } from "./LexerAction"
import { Transition, TransitionType } from "./Transition"

export declare class ATNDeserializer {
    deserializationOptions: ATNDeserializationOptions
    actionFactories: Array<<A extends LexerAction>(data1: number, data2: number) => A> | null
    stateFactories: Array<<S extends ATNState>() => S | null> | null
    data: Array<number> | undefined
    pos: number

    constructor(options?: ATNDeserializationOptions)

    deserialize(data: string): ATN
    reset(data: string): void

    protected isFeatureSupported(feature: string, actualUuid: string): boolean
    protected checkVersion(): void
    protected checkUUID(): void
    protected readATN(): ATN
    protected readStates(atn: ATN): void
    protected readRules(atn: ATN): void
    protected readModes(atn: ATN): void
    protected readSets(atn: ATN, sets: Array<IntervalSet>, readUnicode: () => number): void
    protected readEdges(atn: ATN, sets: Array<IntervalSet>): void
    protected readDecisions(atn: ATN): void
    protected readLexerActions(atn: ATN): void
    protected generateRuleBypassTransitions(atn: ATN): void
    protected generateRuleBypassTransition(atn: ATN, idx: number): void
    protected stateIsEndStateFor<S extends ATNState>(state: S, idx: number): S | null
    protected markPrecedenceDecisions(atn: ATN): void
    protected verifyATN(atn: ATN): void
    protected checkCondition(condition: boolean, message?: string | null): void
    protected readInt(): number
    protected readInt32(): number
    protected readLong(): number
    protected readUUID(): string
    protected edgeFactory(atn: ATN, type: TransitionType, src: number, trg: number, arg1: number, arg2: number, arg3: number, sets: Array<IntervalSet>): Transition
    protected stateFactory(type: ATNStateType, ruleIndex: number): ATNState
    protected lexerActionFactory(type: LexerActionType, data1: number, data2: number): LexerAction
}
