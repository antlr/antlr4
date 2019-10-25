/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATN } from "./atn"
import { RuleContext } from "./RuleContext"
import { DoubleDict, Hash, Map } from "./Utils"

export declare abstract class PredictionContext {
    static globalNodeCount: number

    cachedHashCode: number
    id: number

    constructor(cachedHashCode: number)

    isEmpty(): boolean
    hasEmptyPath(): boolean
    hashCode(): number
    updateHashCode(hash: Hash): void
}
export declare namespace PredictionContext {
    export const EMPTY: EmptyPredictionContext
    export const EMPTY_RETURN_STATE: number
}

export declare class PredictionContextCache {
    cache: Map<PredictionContext, PredictionContext>

    constructor()

    get length(): number

    add(ctx: PredictionContext): PredictionContext
    get(ctx: PredictionContext): PredictionContext | null
}

export declare class SingletonPredictionContext extends PredictionContext {
    parentCtx: PredictionContext
    returnState: number

    constructor(parent: PredictionContext, returnState: number)

    get length(): number

    getParent(index: number): PredictionContext
    getReturnState(index: number): number
    equals(other: any): boolean
    toString(): string

    static create(parent: PredictionContext, returnState: number): SingletonPredictionContext
}

export declare class EmptyPredictionContext extends SingletonPredictionContext {
    constructor()

    getParent(): typeof PredictionContext.EMPTY
    getReturnState(): typeof PredictionContext.EMPTY_RETURN_STATE
}

export declare class ArrayPredictionContext extends PredictionContext {
    constructor(parents: Array<PredictionContext>, returnStates: Array<number>)

    get length(): number

    getParent(index: number): PredictionContext
    getReturnState(index: number): number
    equals(other: any): boolean
    toString(): string
}

export declare function predictionContextFromRuleContext(atn: ATN, outerContext: RuleContext): PredictionContext

export declare function merge(a: PredictionContext, b: PredictionContext, rootIsWildcard: boolean, mergeCache: DoubleDict<PredictionContext, PredictionContext, PredictionContext>): PredictionContext

export declare function getCachedPredictionContext(context: PredictionContext, contextCache: PredictionContextCache, visited: Map<PredictionContext, PredictionContext>): PredictionContext
