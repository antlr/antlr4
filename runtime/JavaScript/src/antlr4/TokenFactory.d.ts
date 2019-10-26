/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Lexer } from "./Lexer"
import { CommonToken, NullableSourcePair } from "./Token"

export declare interface TokenFactory {
    create(source: NullableSourcePair, type: number, text: string | null, channel: number, start: number | undefined, stop: number | undefined, line: number, column: number): CommonToken
    createThin(type: number, text: string | null): CommonToken
}
