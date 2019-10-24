/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "./IntervalSet"
import { Token } from "./Token"
import { TokenSource } from "./TokenSource"
import { TokenStream } from "./TokenStream"

export class BufferedTokenStream implements TokenStream {
    public tokenSource: TokenSource
    public tokens: Array<Token>
    public index: number
    public fetchedEOF: boolean

    constructor(tokenSource: TokenSource)

    public mark(): number
    public release(marker: number): void
    public reset(): void
    public seek(index: number): void
    public get(index: number): Token | undefined
    public consume(): void
    public getTokens(start: number, stop: number, types?: number | null): Array<Token> | null
    public LA(i: number): number
    public LT(k: number): Token | null
    public setTokenSource(tokenSource: TokenSource): void
    public getHiddenTokensToRight(tokenIndex: number, channel: number): Array<Token>
    public getHiddenTokensToLeft(tokenIndex: number, channel: number): Array<Token>
    /** FIXME: Replace with `string` once fixed.
     *
     * Calls `this.tokenSource.getSourceName()`, which isn't implemented.
    */
    public getSourceName(): never
    public getText(interval: Interval): string
    public fill(): void

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
