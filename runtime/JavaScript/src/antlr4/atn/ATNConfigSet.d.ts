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

export class ATNConfigSet {
    public configLookup: Set<ATNConfig>
    public fullCtx: boolean
    public readonly: boolean
    public configs: Array<ATNConfig>
    public uniqueAlt: number
    public conflictingAlts: BitSet | null
    public hasSemanticContext: boolean
    public dipsIntroOuterContext: boolean
    public cachedHashCode: number

    constructor(fullCtx?: boolean)

    get items(): Array<ATNConfig>
    get length(): number

    add(config: ATNConfig, mergeCache?: DoubleDict<PredictionContext, PredictionContext, PredictionContext>): boolean
    getStates(): Set<ATNState>
    getPredicates(): Array<SemanticContext>
    optimizeConfigs(interpreter: ATNSimulator): void
    addAll<T extends typeof ATNConfig>(coll: Iterable<T>): boolean
    equals(other: any): boolean
    hashCode(): number
    updateHashCode(hash: Hash): void
    isEmpty(): boolean
    contains(item: any): boolean
    containsFast(item: ATNConfig): boolean
    clear(): void
    toString(): string
}

export class OrderedATNConfigSet extends ATNConfigSet {
    constructor()
}
