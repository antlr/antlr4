/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { PredicateTransition } from "../atn/Transition"
import { TokenStream } from "../BufferedTokenStream"
import { InputStream } from "../InputStream"
import { IntervalSet } from "../IntervalSet"
import { Lexer } from "../Lexer"
import { Parser } from "../Parser"
import { ParserRuleContext } from "../ParserRuleContext"
import { Recognizer } from "../Recognizer"
import { Token } from "../Token"

export class RecognitionException extends Error {
    public message: string
    public recognizer: Recognizer | null
    public input: InputStream | TokenStream | null
    public ctx: ParserRuleContext | null
    public offendingToken: Token | null
    public offendingState: number

    constructor(params: {
        message: string,
        recognizer: Recognizer | null,
        input: InputStream | TokenStream | null,
        ctx: ParserRuleContext | null
    })

    getExpectedTokens(): IntervalSet | null
    toString(): string
}

export class LexerNoViableAltException extends RecognitionException {
    public startIndex: number
    public deadEndConfigs: ATNConfigSet

    // The decision to make `lexer` of type `Lexer` rather than `Lexer | null`
    // was chosen based on existing usage.
    constructor(lexer: Lexer, input: InputStream, startIndex: number, deadEndConfigs: ATNConfigSet)

    // NOTE: If the `lexer` constructor parameter is changed to be to be of
    // type `Lexer | null`, then remove the next line.
    getExpectedTokens(): IntervalSet
}

export class NoViableAltException extends RecognitionException {
    public deadEndConfigs?: ATNConfigSet
    public startToken: Token

    constructor(
        recognizer: Parser,
        input?: InputStream | TokenStream,
        startToken: Token | undefined,
        offendingToken: Token | undefined,
        deadEndConfigs: ATNConfigSet | undefined,
        ctx: ParserRuleContext | undefined
    )

    getExpectedTokens(): IntervalSet
}

export class InputMismatchException extends RecognitionException {
    constructor(recognizer: Parser)

    getExpectedTokens(): IntervalSet
}

export class FailedPredicateException extends RecognitionException {
    public ruleIndex: number
    public predicateIndex: number
    public predicate: string

    constructor(recognizer: Parser, predicate: string, message?: string)

    getExpectedTokens(): IntervalSet
    formatMessage(predicate: string, message: string | null): string
}

export class ParseCancellationException extends Error {
    constructor()
}
