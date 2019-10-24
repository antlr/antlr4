/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { PredictionContext } from "../PredictionContext"
import { BitSet, DoubleDict, Hash, Set } from "../Utils"

import { ATNConfig } from "./ATNConfig"
import { ATNSimulator } from "./ATNSimulator"
import { ATNState } from "./ATNState"
import { SemanticContext } from "./SemanticContext"

declare interface Indexable<T> {
    [index: number]: T
}

export declare class ATNConfigSet {
    configLookup: Set<ATNConfig> | null
    fullCtx: boolean
    readOnly: boolean
    configs: Array<ATNConfig>
    uniqueAlt: number
    conflictingAlts: BitSet | null
    hasSemanticContext: boolean
    dipsIntroOuterContext: boolean
    cachedHashCode: number

    constructor(fullCtx?: boolean)

    get items(): Array<ATNConfig>

    get length(): number

    add(config: ATNConfig, mergeCache?: DoubleDict<PredictionContext, PredictionContext, PredictionContext> | null): boolean
    getStates(): Set<ATNState>
    getPredicates(): Array<SemanticContext>
    optimizeConfigs(interpreter: ATNSimulator): void
    addAll<T extends ATNConfig>(coll: Array<T>): boolean
    equals(other: any): boolean
    hashCode(): number
    updateHashCode(hash: Hash): void
    isEmpty(): boolean
    contains(item: any): boolean
    /** FIXME: Replace return type with `boolean` once fixed.
     *
     * (PR #2674)
    */
    containsFast(item: ATNConfig): never
    clear(): void
    toString(): string
}

export declare class OrderedATNConfigSet extends ATNConfigSet {
    constructor()
}
