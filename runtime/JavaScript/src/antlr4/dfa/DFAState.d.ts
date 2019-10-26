/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { LexerActionExecutor } from "../atn/LexerActionExecutor"
import { SemanticContext } from "../atn/SemanticContext"
import { Set } from "../Utils"

export declare class PredPrediction {
    alt: number
    pred: SemanticContext

    constructor(pred: SemanticContext, alt: number)

    toString(): string
}

export declare class DFAState {
    stateNumber: number
    configs: ATNConfigSet
    edges: Array<DFAState> | null
    isAcceptState: boolean
    prediction: number
    lexerActionExecutor: LexerActionExecutor | null
    requiresFullContext: boolean
    predicates: Array<PredPrediction> | null

    constructor(stateNumber: number | null, configs: ATNConfigSet | null)

    getAltSet(): Set<number>
    equals(other: any): boolean
    toString(): string
    hashCode(): number
}
