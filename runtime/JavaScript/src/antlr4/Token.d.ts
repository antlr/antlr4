/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"
import { Lexer } from "./Lexer"

export type SourcePair = [Lexer | null, InputStream | null]

export class Token {
    public source: SourcePair | null
    public type: number | null
    public channel: number | null
    public start: number | null
    public stop: number | null
    public tokenIndex: number | null
    public line: number | null
    public column: number | null

    protected _text: string | null

    constructor()

    get text(): string
    set text(text: string)

    getTokenSource(): Lexer | null
    getInputStream(): InputStream | null
}
export namespace Token {
    export const INVALID_TYPE: number
    export const EPSILON: number
    export const MIN_USER_TOKEN_TYPE: number
    export const EOF: number
    export const DEFAULT_CHANNEL: number
    export const HIDDEN_CHANNEL: number
    export type INVALID_TYPE = typeof Token.INVALID_TYPE
    export type EPSILON = typeof Token.EPSILON
    export type MIN_USER_TOKEN_TYPE = typeof Token.MIN_USER_TOKEN_TYPE
    export type EOF = typeof Token.EOF
    export type DEFAULT_CHANNEL = typeof Token.DEFAULT_CHANNEL
    export type HIDDEN_CHANNEL = typeof Token.HIDDEN_CHANNEL
}

export class CommonToken extends Token {
    constructor(
        source?: SourcePair,
        type?: number,
        channel?: number,
        start?: number,
        stop?: number
    )

    clone(): CommonToken
    toString(): string
}
export namespace CommonToken {
    export const EMPTY_SOURCE: [null, null]
    export type EMPTY_SOURCE = typeof CommonToken.EMPTY_SOURCE
}
