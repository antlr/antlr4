/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "../InputStream"
import { Lexer } from "../Lexer"
import { Hash } from "../Utils"

import { LexerAction } from "./LexerAction"

export declare class LexerActionExecutor {
    lexerActions: Array<LexerAction>
    cachedHashCode: number

    constructor(lexerActions: Array<LexerAction>)

    fixOffsetBeforeMatch(offset: number): LexerActionExecutor
    execute(lexer: Lexer, input: InputStream, startIndex: number): void
    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean

    static append(lexerActionExecutor: LexerActionExecutor, lexerAction: LexerAction): LexerActionExecutor
}
