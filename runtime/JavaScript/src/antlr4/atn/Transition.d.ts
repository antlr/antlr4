/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Token } from "../Token"
import { IntervalSet } from "../IntervalSet"

import { ATNState } from "./ATNState"
import { Predicate, PrecedencePredicate } from "./SemanticContext"

export class Transition {
    public target: ATNState
    public isEpsilon: false
    public label: IntervalSet | null

    constructor(target: ATNState)
}
export namespace Transition {
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
    export type EPSILON = typeof Transition.EPSILON
    export type RANGE = typeof Transition.RANGE
    export type RULE = typeof Transition.RULE
    export type PREDICATE = typeof Transition.PREDICATE
    export type ATOM = typeof Transition.ATOM
    export type ACTION = typeof Transition.ACTION
    export type SET = typeof Transition.SET
    export type NOT_SET = typeof Transition.NOT_SET
    export type WILDCARD = typeof Transition.WILDCARD
    export type PRECEDENCE = typeof Transition.PRECEDENCE

    export const serializationNames: Array<string>
    export namespace serializationTypes {
        export const EpsilonTransition: Transition.EPSILON
        export const RangeTransition: Transition.RANGE
        export const RuleTransition: Transition.RULE
        export const PredicateTransition: Transition.PREDICATE
        export const AtomTransition: Transition.ATOM
        export const ActionTransition: Transition.ACTION
        export const SetTransition: Transition.SET
        export const NotSetTransition: Transition.NOT_SET
        export const WildcardTransition: Transition.WILDCARD
        export const PrecedencePredicateTransition: Transition.PRECEDENCE
    }
}

export class AtomTransition extends Transition {
    public label_: number
    public label: IntervalSet
    public serializationType: Transition.ATOM

    constructor(target: ATNState, label: number)

    makeLabel(): IntervalSet
    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export class RuleTransition extends Transition {
    public ruleIndex: number
    public precedence: number
    public followState: ATNState
    public serializationType: Transition.RULE
    public isEpsilon: true

    constructor(ruleStart: ATNState, ruleIndex: number, precedence: number, followState: ATNState)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): false
}

export class EpsilonTransition extends Transition {
    public serializationType: Transition.EPSILON
    public isEpsilon: true
    public outermostPrecedenceReturn: number

    constructor(target: ATNState, outermostPrecedenceReturn: number)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): false
    toString(): string
}

export class RangeTransition extends Transition {
    public serializationType: Transition.RANGE
    public start: number
    public stop: number
    public label: IntervalSet

    constructor(target: ATNState, start: number, stop: number)

    makeLabel(): IntervalSet
    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export abstract class AbstractPredicateTransition extends Transition {
}

export class PredicateTransition extends AbstractPredicateTransition {
    public serializationType: Transition.PREDICATE
    public ruleIndex: number
    public predIndex: number
    public isCtxDependent: boolean
    public isEpsilon: true

    constructor(target: ATNState, ruleIndex: number, predIndex: number, isCtxDependent: boolean)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): false
    getPredicate(): Predicate
    toString(): string
}

export class ActionTransition extends Transition {
    public serializationType: Transition.ACTION
    public ruleIndex: number
    public actionIndex: number
    public isCtxDependent: boolean
    public isEpsilon: true

    constructor(target: ATNState, ruleIndex: number, actionIndex?: number, isCtxDependent?: boolean)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): false
    toString(): string
}

export class SetTransition extends Transition {
    public serializationType: Transition.SET
    public label: IntervalSet

    constructor(target: ATNState, set?: IntervalSet | null)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export class NotSetTransition extends SetTransition {
    public serializationType: Transition.NOT_SET

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export class WildcardTransition extends Transition {
    public serializationType: Transition.WILDCARD

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): boolean
    toString(): string
}

export class PrecedencePredicateTransition extends AbstractPredicateTransition {
    public serializationType: Transition.PRECEDENCE
    public precedence: number
    public isEpsilon: true

    constructor(target: ATNState, precedence: number)

    matches(symbol: number, minVocabSymbol: number, maxVocabSymbol: number): false
    getPredicate(): Predicate
    toString(): string
}
