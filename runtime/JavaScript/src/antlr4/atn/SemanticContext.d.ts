/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Recognizer } from "../Recognizer"
import { RuleContext } from "../RuleContext"
import { Hash, Set } from "../Utils"

export declare class SemanticContext {
    protected constructor()

    hashCode(): number
    evaluate(parser: Recognizer, outerContext: RuleContext): void
    evalPrecedence(parser: Recognizer, outerContext: RuleContext): SemanticContext | null

    static andContext(a: SemanticContext, b: SemanticContext): SemanticContext
    static orContext(a: SemanticContext, b: SemanticContext): SemanticContext
}
export declare namespace SemanticContext {
    export const NONE: Predicate
}

export declare class Predicate extends SemanticContext {
    ruleIndex: number
    predIndex: number
    isCtxDependent: boolean

    constructor(ruleIndex?: number, predIndex?: number, isCtxDependent?: boolean)

    evaluate(parser: Recognizer, outerContext: RuleContext): boolean
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export declare class PrecedencePredicate extends SemanticContext {
    precedence: number

    constructor(precedence: number)

    evaluate(parser: Recognizer, outerContext: RuleContext): boolean
    evalPrecedence(parser: Recognizer, outerContext: RuleContext): SemanticContext | null
    compareTo(other: { precedence: number }): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string

    static filterPrecedencePredicates(set: Array<SemanticContext>): Array<PrecedencePredicate>
}
