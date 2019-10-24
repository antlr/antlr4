/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { AltDict, BitSet } from "../Utils"

import { ATNConfigSet } from "./ATNConfigSet"
import { ATNState } from "./ATNState"

export declare enum PredictionMode {
    SLL = 0,
    LL,
    LL_EXACT_AMBIG_DETECTION
}
export declare namespace PredictionMode {
    export function hasSLLConflictTerminatingPrediction(mode: PredictionMode, configs: ATNConfigSet): boolean
    export function hasConfigInRuleStopState(configs: ATNConfigSet): boolean
    export function allConfigsInRuleStopStates(configs: ATNConfigSet): boolean
    export function resolvesToJustOneViableAlt(altsets: Array<BitSet>): number
    export function allSubsetsConflict(altsets: Array<BitSet>): boolean
    export function hasNonConflictingAltSet(altsets: Array<BitSet>): boolean
    export function hasConflictingAltSet(altsets: Array<BitSet>): boolean
    export function allSubsetsEqual(altsets: Array<BitSet>): boolean
    export function getUniqueAlt(altsets: Array<BitSet>): number
    export function getAlts(altsets: Array<BitSet>): BitSet
    export function getConflictingAltSubsets(configs: ATNConfigSet): Array<BitSet>
    export function getStateToAltMap(configs: ATNConfigSet): AltDict<ATNState, BitSet>
    export function hasStateAssociatedWithOneAlt(configs: ATNConfigSet): boolean
    export function getSingleViableAlt(altsets: Array<BitSet>): number | null
}
