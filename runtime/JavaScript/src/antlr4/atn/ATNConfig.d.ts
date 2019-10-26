/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { PredictionContext } from "../PredictionContext"
import { Hash } from "../Utils"

import { ATNState } from "./ATNState"
import { LexerActionExecutor } from "./LexerActionExecutor"
import { SemanticContext } from "./SemanticContext"

export declare class ATNConfig {
    state: ATNState | null
    alt: number | null
    context: PredictionContext | null
    semanticContext: SemanticContext | null
    reachesIntoOuterContext: number
    precedenceFilterSuppressed: boolean

    // Don't use config.
    constructor(params: { state: ATNState, alt: number, context: PredictionContext, semanticContext?: SemanticContext }, config: null)
    constructor(
        // Use config sparingly.
        params: {
            state: ATNState,
            context?: PredictionContext,
            semanticContext?: SemanticContext
        } | {
            semanticContext: SemanticContext
        } | {
            // Only use config.
        },
        config: ATNConfig
    )

    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    hashCodeForConfigSet(): number
    equalsForConfigSet(other: any): boolean
    toString(): string
}
export declare namespace ATNConfig {
    export const SUPPRESS_PRECEDENCE_FILTER: number
}

export declare class LexerATNConfig extends ATNConfig {
    lexerActionExecutor: LexerActionExecutor
    passedThroughNonGreedyDecision: boolean

    // Don't use config.
    constructor(params: { state: ATNState, alt: number, context: PredictionContext, lexerActionExecutor?: LexerActionExecutor }, config: null)
    // Use config sparingly.
    constructor(params: { state: ATNState, context?: PredictionContext, lexerActionExecutor?: LexerActionExecutor }, config: LexerATNConfig)

    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}
