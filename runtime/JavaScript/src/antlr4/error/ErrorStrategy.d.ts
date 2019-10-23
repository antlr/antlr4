/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { IntervalSet } from "../IntervalSet"
import { Parser } from "../Parser"
import { Token } from "../Token"
import {
    FailedPredicateException,
    InputMismatchException,
    NoViableAltException,
    RecognitionException
} from "./Errors"

export interface ErrorStrategy {
    reset(recognizer: Parser): void
    recoverInline(recognizer: Parser): void
    recover(recognizer: Parser, e: RecognitionException): void
    sync(recognizer: Parser): void
    inErrorRecoveryMode(recognizer: Parser): void
    reportMatch(recognizer: Parser)
    reportError(recognizer: Parser, e: RecognitionException): void
}

export class DefaultErrorStrategy implements ErrorStrategy {
    public errorRecoveryMode: boolean
    public lastErrorIndex: number
    public lastErrorStates: IntervalSet | null

    constructor()

    reset(recognizer: Parser): void
    recoverInline(recognizer: Parser): void
    recover(recognizer: Parser, e: RecognitionException): void
    sync(recognizer: Parser): void
    inErrorRecoveryMode(recognizer: Parser): void
    reportMatch(recognizer: Parser): void
    reportError(recognizer: Parser, e: RecognitionException): void

    protected beginErrorCondition(recognizer: Parser): void
    protected endErrorCondition(recognizer: Parser): void
    protected reportNoViableAlternative(recognizer: Parser, e: NoViableAltException): void
    protected reportInputMismatch(recognizer: Parser, e: InputMismatchException): void
    protected reportFailedPredicate(recognizer: Parser, e: FailedPredicateException): void
    protected reportUnwantedToken(recognizer: Parser): void
    protected reportMissingToken(recognizer: Parser): void
    protected singleTokenInsertion(recognizer: Parser): boolean
    protected singleTokenDeletion(recognizer: Parser): Token
    protected getMissingSymbol(recognizer: Parser): Token
    protected getExpectedTokens(recognizer: Parser): IntervalSet
    protected getTokenErrorDisplay(t: Token): string
    protected escapeWSAndQuote(s: string): string
    protected getErrorRecoverySet(recognizer: Parser): IntervalSet
    protected consumeUntil(recognizer: Parser, set: IntervalSet): void
}

export class BailErrorStrategy extends DefaultErrorStrategy {
    constructor()

    recoverInline(recognizer: Parser): never
    recover(recognizer: Parser, e: RecognitionException): never
}
