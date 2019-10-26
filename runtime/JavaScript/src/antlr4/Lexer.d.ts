/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { LexerNoViableAltException } from "./error"
import { Recognizer } from "./Recognizer"
import { SourcePair, Token } from "./Token"
import { TokenFactory } from "./TokenFactory"
import { TokenSource } from "./TokenSource"
import { InputStream } from "./InputStream"

export declare class Lexer extends Recognizer implements TokenSource {
    _token: Token | null
    _tokenStartCharIndex: number
    _tokenStartLine: number
    _tokenStartColumn: number
    _hitEOF: boolean
    _channel: number
    _modeStack: Array<number>
    _mode: number
    _factory: TokenFactory

    protected _input: InputStream
    protected _type: number
    protected _text: string | null
    protected _tokenFactorySourcePair: SourcePair

    constructor(input: InputStream)

    get inputStream(): InputStream
    set inputStream(input: InputStream)

    get sourceName(): string

    get type(): number
    set type(type: number)

    get line(): number
    set line(line: number)

    get column(): number
    set column(column: number)

    get text(): string | null
    set text(text: string | null)

    reset(): void
    nextToken(): Token
    skip(): void
    more(): void
    mode(m: number): void
    pushMode(m: number): void
    popMode(): number
    emitToken(token: Token): void
    emit(): Token
    emitEOF(): Token
    getCharIndex(): number
    getAllTokens(): Array<Token>
    notifyListeners(e: LexerNoViableAltException): void
    getErrorDisplay(s: string): string
    getCharErrorDisplay(c: string): string
    recover(re: LexerNoViableAltException): void

    protected getErrorDisplayForChar(c: string): string
}
export declare namespace Lexer {
    export const DEFAULT_MODE: number
    export const MORE: number
    export const SKIP: number
    export const DEFAULT_TOKEN_CHANNEL: number
    export const HIDDEN: number
    export const MIN_CHAR_VALUE: number
    export const MAX_CHAR_VALUE: number
}
