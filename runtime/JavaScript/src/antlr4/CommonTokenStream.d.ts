/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { BufferedTokenStream } from "./BufferedTokenStream"
import { Lexer } from "./Lexer"
import { Token } from "./Token"

export class CommonTokenStream extends BufferedTokenStream {
    public channel: number | Token.DEFAULT_CHANNEL

    constructor(lexer: Lexer, channel?: number)

    adjustSeekIndex(i: number): number
    LB(k: number): Token | null
    LT(k: number): Token | null
    getNumberOfOnChannelTokens(): number
}
