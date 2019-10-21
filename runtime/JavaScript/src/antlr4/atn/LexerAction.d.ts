/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Lexer } from "../Lexer"
import { Hash } from "../Utils"

export enum LexerActionType {
    CHANNEL = 0,
    CUSTOM = 1,
    MODE = 2,
    MORE = 3,
    POP_MODE = 4,
    PUSH_MODE = 5,
    SKIP = 6,
    TYPE = 7
}

export class LexerAction {
    public actionType: LexerActionType
    public isPositionDependent: boolean

    constructor(action: LexerActionType)

    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}

export class LexerSkipAction {
    static readonly INSTANCE: LexerSkipAction

    constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export class LexerTypeAction {
    public type: LexerActionType

    constructor(type: LexerActionType)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export class LexerPushModeAction {
    public mode: number

    constructor(mode: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export class LexerPopModeAction {
    static readonly INSTANCE: LexerPopModeAction

    constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export class LexerMoreAction {
    static readonly INSTANCE: LexerMoreAction

    constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export class LexerModeAction {
    public mode: number

    constructor(mode: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export class LexerCustomAction extends LexerAction {
    public ruleIndex: number
    public actionIndex: number

    constructor(ruleIndex: number, actionIndex: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}

export class LexerChannelAction extends LexerAction {
    public channel: number

    constructor(channel: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export class LexerIndexedCustomAction extends LexerAction {
    public offset: number
    public action: LexerAction

    constructor(offset: number, action: LexerAction)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}
