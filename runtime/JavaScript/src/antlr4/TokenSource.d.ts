/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "./InputStream"
import { Token } from "./Token"

export declare interface TokenSource {
    column: number
    inputStream: InputStream
    line: number
    sourceName: string
    // tokenFactory: TokenFactory
    nextToken(): Token
}
