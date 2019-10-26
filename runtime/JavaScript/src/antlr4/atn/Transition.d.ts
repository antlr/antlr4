/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"

import { ATNState } from "./ATNState"
import { Predicate } from "./SemanticContext"

export declare class Transition {
    static readonly serializationNames: Array<string>

    target: ATNState
    isEpsilon: boolean
    label: IntervalSet | null

    constructor(target: ATNState)
}
export declare namespace Transition {
    export const EPSILON = 1
    export const RANGE = 2
    export const RULE = 3
    export const PREDICATE = 4
    export const ATOM = 5
    export const ACTION = 6
    export const SET = 7
    export const NOT_SET = 8
    export const WILDCARD = 9
    export const PRECEDENCE = 10

    export namespace serializationTypes {
        export const EpsilonTransition: TransitionType
        export const RangeTransition: TransitionType
        export const RuleTransition: TransitionType
        export const PredicateTransition: TransitionType
        export const AtomTransition: TransitionType
        export const ActionTransition: TransitionType
        export const SetTransition: TransitionType
        export const NotSetTransition: TransitionType
        export const WildcardTransition: TransitionType
        export const PrecedencePredicateTransition: TransitionType
    }
}

export declare type TransitionType = 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10

export declare class AtomTransition extends Transition {
    label_: number
    label: IntervalSet
    serializationType: TransitionType

    constructor(target: ATNState, label: number)

    makeLabel(): IntervalSet
    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class RuleTransition extends Transition {
    ruleIndex: number
    precedence: number
    followState: ATNState
    serializationType: TransitionType
    isEpsilon: boolean

    constructor(ruleStart: ATNState, ruleIndex: number, precedence: number, followState: ATNState)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
}

export declare class EpsilonTransition extends Transition {
    serializationType: TransitionType
    isEpsilon: boolean
    outermostPrecedenceReturn: number | undefined

    constructor(target: ATNState, outermostPrecedenceReturn?: number)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class RangeTransition extends Transition {
    serializationType: TransitionType
    start: number
    stop: number
    label: IntervalSet

    constructor(target: ATNState, start: number, stop: number)

    makeLabel(): IntervalSet
    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare abstract class AbstractPredicateTransition extends Transition {
}

export declare class PredicateTransition extends AbstractPredicateTransition {
    serializationType: TransitionType
    ruleIndex: number
    predIndex: number
    isCtxDependent: boolean
    isEpsilon: boolean

    constructor(target: ATNState, ruleIndex: number, predIndex: number, isCtxDependent: boolean)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    getPredicate(): Predicate
    toString(): string
}

export declare class ActionTransition extends Transition {
    serializationType: TransitionType
    ruleIndex: number
    actionIndex: number
    isCtxDependent: boolean
    isEpsilon: boolean

    constructor(target: ATNState, ruleIndex: number, actionIndex?: number, isCtxDependent?: boolean)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class SetTransition extends Transition {
    serializationType: TransitionType
    label: IntervalSet

    constructor(target: ATNState, set?: IntervalSet | null)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class NotSetTransition extends SetTransition {
    serializationType: TransitionType

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class WildcardTransition extends Transition {
    serializationType: TransitionType

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export declare class PrecedencePredicateTransition extends AbstractPredicateTransition {
    serializationType: TransitionType
    precedence: number
    isEpsilon: boolean

    constructor(target: ATNState, precedence: number)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    getPredicate(): Predicate
    toString(): string
}
