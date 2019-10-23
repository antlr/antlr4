/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { PredictionContext } from "../PredictionContext"
import { ATNState } from "./ATNState"
import { LexerActionExecutor } from "./LexerActionExecutor"
import { SemanticContext } from "./SemanticContext"

interface ATNConfigParams {
    state?: ATNState
    alt?: number
    context?: PredictionContext
    semanticContext?: SemanticContext
}
interface ATNConfigLike extends ATNConfigParams {
    reachesIntoOuterContext?: number
    precedenceFilterSuppressed?: boolean
}
export class ATNConfig {
    public state: ATNState | null
    public alt: number | null
    public context: PredictionContext | null
    public semanticContext: SemanticContext | null
    public reachesIntoOuterContext: number
    public precedenceFilterSuppressed: boolean

    constructor(params: ATNConfigParams, config: ATNConfigLike | null)

    checkContext(params: { context?: object | null }, config: { context?: object | null } | null): void
    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    hashCodeForConfigSet(): number
    equalsForConfigSet(other: any): boolean
    toString(): string
}
export namespace ATNConfig {
    export const SUPPRESS_PRECEDENCE_FILTER: number
    export type SUPPRESS_PRECEDENCE_FILTER = typeof ATNConfig.SUPPRESS_PRECEDENCE_FILTER
}

interface LexerATNConfigParams extends ATNConfigParams {
    lexerActionExecutor?: LexerActionExecutor
}
interface LexerATNConfigLike extends LexerATNConfigParams {
    lexerActionExecutor: LexerActionExecutor
}
export class LexerATNConfig extends ATNConfig {
    public lexerActionExecutor: LexerActionExecutor
    public passedThroughNonGreedyDecision: boolean

    constructor(params: LexerATNConfigParams, config: LexerATNConfigLike | null)

    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}
