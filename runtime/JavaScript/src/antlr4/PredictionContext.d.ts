/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATN } from "./atn"
import { RuleContext } from "./RuleContext"
import { DoubleDict, Hash, Map } from "./Utils"

export abstract class PredictionContext {
    public cachedHashCode: number
    public id: number

    constructor(cachedHashCode: number)

    isEmpty(): boolean
    hasEmptyPath(): boolean
    hashCode(): number
    updateHashCode(hash: Hash): void
}
export namespace PredictionContext {
    export const EMPTY: EmptyPredictionContext
    export const EMPTY_RETURN_STATE: number
    export const globalNodeCount: number
    export type EMPTY = typeof PredictionContext.EMPTY
    export type EMPTY_RETURN_STATE = typeof PredictionContext.EMPTY_RETURN_STATE
}

export class PredictionContextCache<PC extends PredictionContext> {
    public cache: Map<PC, PC>

    constructor()

    get length(): number

    add(ctx: PC): PC
    get(ctx: PC): PC | null
}

export class SingletonPredictionContext extends PredictionContext {
    public parentCtx: PredictionContext
    public returnState: number

    constructor(parent: PredictionContext | PredictionContext.EMPTY, returnState: number)

    get length(): number

    getParent(index: number): PredictionContext | PredictionContext.EMPTY
    getReturnState(index: number): number | PredictionContext.EMPTY_RETURN_STATE
    equals(other: any): boolean
    toString(): string
}
export namespace SingletonPredictionContext {
    export function create(
        parent: PredictionContext | PredictionContext.EMPTY,
        returnState: number
    ): SingletonPredictionContext | PredictionContext.EMPTY
}

export class EmptyPredictionContext extends SingletonPredictionContext {
    constructor()

    isEmpty(): true
    getParent(): PredictionContext.EMPTY
    getReturnState(): PredictionContext.EMPTY_RETURN_STATE
}

export class ArrayPredictionContext extends PredictionContext {
    constructor(parents: Array<PredictionContext>, returnStates: Array<number>)

    get length(): number

    getParent(index: number): PredictionContext | PredictionContext.EMPTY
    getReturnState(index: number): number | PredictionContext.EMPTY_RETURN_STATE
    equals(other: any): boolean
    toString(): string
}

export function predictionContextFromRuleContext(
    atn: ATN,
    outerContext: RuleContext
): PredictionContext

export function merge(
    a: PredictionContext,
    b: PredictionContext,
    rootIsWildcard: boolean,
    mergeCache: DoubleDict<PredictionContext, PredictionContext, PredictionContext>
): PredictionContext

export function getCachedPredictionContext(
    context: PredictionContext,
    contextCache: PredictionContextCache<PredictionContext>,
    visited: Map<PredictionContext, PredictionContext>
): PredictionContext
