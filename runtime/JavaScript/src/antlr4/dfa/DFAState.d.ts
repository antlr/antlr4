/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { LexerActionExecutor } from "../atn/LexerActionExecutor"
import { SemanticContext } from "../atn/SemanticContext"
import { Hash, Set } from "../Utils"

export class PredPrediction {
    public alt: number
    public pred: SemanticContext

    constructor(pred: SemanticContext, alt: number)

    toString(): string
}

export class DFAState {
    public stateNumber: number
    public configs: ATNConfigSet
    public edges: Array<DFAState> | null
    public isAcceptState: boolean
    public prediction: number
    public lexerActionExecutor: LexerActionExecutor | null
    public predicates: Array<PredPrediction> | null

    constructor(stateNumber: number | null, configs: ATNConfigSet | null)

    getAltSet(): Set<number>
    equals(other: any): boolean
    toString(): string
    hashCode(): number
}
