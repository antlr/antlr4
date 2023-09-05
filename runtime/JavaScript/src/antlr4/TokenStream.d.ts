/* Copyright (c) 2012-2022 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

import Token from "./Token.js";
import TokenSource from "./TokenSource.js";
import Interval from "./misc/Interval.js";

export declare class TokenStream {
    public tokenSource: TokenSource;
    public index: number;
    public get size(): number;

    public LA(i: number): number;
    public LT(k: number): Token;
    public getText(interval?: Interval): string;

    // channelIndex can be retrieved using: lexer.channelNames().findIndex(channelName)

    public getHiddenTokensToLeft(tokenIndex: number, channelIndex?: number): Token[];
    public getHiddenTokensToRight(tokenIndex: number, channelIndex?: number): Token[];
    public get(idx: number): Token;
}

export default TokenStream;
