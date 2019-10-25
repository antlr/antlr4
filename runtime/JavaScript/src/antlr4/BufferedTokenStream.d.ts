/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "./IntervalSet"
import { Token } from "./Token"
import { TokenSource } from "./TokenSource"
import { TokenStream } from "./TokenStream"

export declare class BufferedTokenStream implements TokenStream {
    tokenSource: TokenSource
    tokens: Array<Token>
    index: number
    fetchedEOF: boolean

    constructor(tokenSource: TokenSource)

    mark(): number
    release(marker: number): void
    reset(): void
    seek(index: number): void
    get(index: number): Token | undefined
    consume(): void
    getTokens(start: number, stop: number, types?: number | null): Array<Token> | null
    LA(i: number): number
    LT(k: number): Token | null
    setTokenSource(tokenSource: TokenSource): void
    getHiddenTokensToRight(tokenIndex: number, channel: number): Array<Token>
    getHiddenTokensToLeft(tokenIndex: number, channel: number): Array<Token>
    /** FIXME: Replace with `string` once fixed.
     *
     * Calls `this.tokenSource.getSourceName()`, which isn't implemented.
    */
    getSourceName(): never
    getText(interval: Interval): string
    fill(): void

    protected sync(i: number): boolean
    protected fetch(n: number): number
    protected LB(k: number): Token | null
    protected adjustSeekIndex(i: number): number
    protected lazyInit(): void
    protected setup(): void
    protected nextTokenOnChannel(i: number, channel: number): number
    protected previousTokenOnChannel(i: number, channel: number): number
    protected filterForChannel(left: number, right: number, channel: number): Array<Token>
}
