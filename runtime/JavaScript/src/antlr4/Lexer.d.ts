/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { LexerNoViableAltException } from "./error"
import { InputStream } from "./InputStream"
import { Recognizer } from "./Recognizer"
import { SourcePair, Token } from "./Token"
import { TokenFactory } from "./TokenFactory"
import { TokenSource } from "./TokenSource"

export class Lexer extends Recognizer implements TokenSource {
    public _token: Token | null
    public _tokenStartCharIndex: number
    public _tokenStartLine: number
    public _tokenStartColumn: number
    public _hitEOF: boolean
    public _channel: number
    public _modeStack: Array<number>
    public _mode: number
    protected _input: InputStream
    protected _type: number
    protected _text: string | null
    protected _factory: TokenFactory
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

    get text(): string
    set text(text: string | null)

    public reset(): void
    public nextToken(): Token
    public skip(): void
    public more(): void
    public mode(m: number): void
    public pushMode(m: number): void
    public popMode(): number
    public emitToken(token: Token): void
    public emit(): Token
    public emitEOF(): Token
    public getCharIndex(): number
    public getAllTokens(): Array<Token>
    public notifyListeners(e: LexerNoViableAltException): void
    public getErrorDisplay(s: string): string
    public getCharErrorDisplay(c: string): string
    public recover(re: LexerNoViableAltException): void

    protected getErrorDisplayForChar(c: string): string
}
export namespace Lexer {
    export const DEFAULT_MODE: number
    export const MORE: number
    export const SKIP: number
    export const DEFAULT_TOKEN_CHANNEL: number
    export const HIDDEN: number
    export const MIN_CHAR_VALUE: number
    export const MAX_CHAR_VALUE: number
}
