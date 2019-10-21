/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { InputStream } from "../InputStream"
import { Lexer } from "../Lexer"
import { Hash, hashStuff } from "../Utils"

import { LexerAction, LexerIndexedCustomAction } from "./LexerAction"

export class LexerActionExecutor {
    public lexerActions: Array<LexerAction>
    public cachedHashCode: number

    constructor(lexerActions: Array<LexerAction>)

    fixOffsetBeforeMatch(offset: number): LexerActionExecutor
    execute(lexer: Lexer, input: InputStream, startIndex: number): void
    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}
export namespace LexerActionExecutor {
    export function append(
        LexerActionExecutor: LexerActionExecutor,
        LexerAction: LexerAction
    ): LexerActionExecutor
}
