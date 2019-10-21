/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"
import { Lexer } from "./Lexer"

export type SourcePair = Tuple<Lexer | null, InputStream | null>

export class Token {
    static readonly INVALID_TYPE: number
    static readonly EPSILON: number
    static readonly MIN_USER_TOKEN_TYPE: number
    static readonly EOF: number
    static readonly DEFAULT_CHANNEL: number
    static readonly HIDDEN_CHANNEL: number

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
    set text(text: string): void

    getTokenSource(): Lexer | null
    getInputStream(): InputStream | null
}

export class CommonToken extends Token {
    static readonly EMPTY_SOURCE: Tuple<null, null>

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
