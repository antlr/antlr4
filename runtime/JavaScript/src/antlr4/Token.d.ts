/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"
import { Lexer } from "./Lexer"

export declare type SourcePair = [Lexer, InputStream]
export declare type NullableSourcePair = [Lexer | null, InputStream | null]

export declare class Token {
    source: NullableSourcePair | null
    type: number | null
    channel: number | null
    start: number | null
    stop: number | null
    tokenIndex: number | null
    line: number | null
    column: number | null

    protected _text: string | null

    constructor()

    get text(): string | null
    set text(text: string | null)

    getTokenSource(): Lexer | null
    getInputStream(): InputStream | null
}
export declare namespace Token {
    export const INVALID_TYPE: number
    export const EPSILON: number
    export const MIN_USER_TOKEN_TYPE: number
    export const EOF: number
    export const DEFAULT_CHANNEL: number
    export const HIDDEN_CHANNEL: number
}

export declare class CommonToken extends Token {
    constructor(source?: NullableSourcePair, type?: number, channel?: number, start?: number, stop?: number)

    clone(): CommonToken
    toString(): string
}
export declare namespace CommonToken {
    export const EMPTY_SOURCE: [null, null]
}
