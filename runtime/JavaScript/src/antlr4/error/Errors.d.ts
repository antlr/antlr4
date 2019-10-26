/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { InputStream } from "../InputStream"
import { IntervalSet } from "../IntervalSet"
import { Lexer } from "../Lexer"
import { Parser } from "../Parser"
import { ParserRuleContext } from "../ParserRuleContext"
import { Recognizer } from "../Recognizer"
import { Token } from "../Token"
import { TokenStream } from "../TokenStream"

export declare class RecognitionException extends Error {
    message: string
    recognizer: Recognizer | null
    input: InputStream | TokenStream | null
    ctx: ParserRuleContext | null
    offendingToken: Token | null
    offendingState: number

    constructor(params: { message: string, recognizer: Recognizer | null, input: InputStream | TokenStream | null, ctx: ParserRuleContext | null })

    getExpectedTokens(): IntervalSet | null
    toString(): string
}

export declare class LexerNoViableAltException extends RecognitionException {
    startIndex: number
    deadEndConfigs: ATNConfigSet

    constructor(lexer: Lexer, input: InputStream, startIndex: number, deadEndConfigs: ATNConfigSet)

    getExpectedTokens(): IntervalSet
}

export declare class NoViableAltException extends RecognitionException {
    deadEndConfigs: ATNConfigSet
    startToken: Token

    constructor(recognizer: Parser, input?: TokenStream, startToken?: Token, offendingToken?: Token, deadEndConfigs?: ATNConfigSet, ctx?: ParserRuleContext)

    getExpectedTokens(): IntervalSet
}

export declare class InputMismatchException extends RecognitionException {
    constructor(recognizer: Parser)

    getExpectedTokens(): IntervalSet
}

export declare class FailedPredicateException extends RecognitionException {
    ruleIndex: number
    predicateIndex: number
    predicate: string

    constructor(recognizer: Parser, predicate: string, message?: string)

    getExpectedTokens(): IntervalSet

    protected formatMessage(predicate: string, message: string | null): string
}

export declare class ParseCancellationException extends Error {
    constructor(message?: string, cause?: Error)
}
