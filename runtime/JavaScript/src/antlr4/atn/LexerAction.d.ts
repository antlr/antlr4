/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Lexer } from "../Lexer"
import { Hash } from "../Utils"

export declare enum LexerActionType {
    CHANNEL = 0,
    CUSTOM = 1,
    MODE = 2,
    MORE = 3,
    POP_MODE = 4,
    PUSH_MODE = 5,
    SKIP = 6,
    TYPE = 7
}

export declare class LexerAction {
    actionType: LexerActionType
    isPositionDependent: boolean

    constructor(action: LexerActionType)

    hashCode(): number
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}

export declare class LexerSkipAction {
    static readonly INSTANCE: LexerSkipAction

    private constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export declare class LexerTypeAction {
    type: LexerActionType

    constructor(type: LexerActionType)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export declare class LexerPushModeAction {
    mode: number

    constructor(mode: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export declare class LexerPopModeAction {
    static readonly INSTANCE: LexerPopModeAction

    private constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export declare class LexerMoreAction {
    static readonly INSTANCE: LexerMoreAction

    private constructor()

    execute(lexer: Lexer): void
    toString(): string
}

export declare class LexerModeAction {
    mode: number

    constructor(mode: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export declare class LexerCustomAction extends LexerAction {
    ruleIndex: number
    actionIndex: number

    constructor(ruleIndex: number, actionIndex: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}

export declare class LexerChannelAction extends LexerAction {
    channel: number

    constructor(channel: number)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
    toString(): string
}

export declare class LexerIndexedCustomAction extends LexerAction {
    offset: number
    action: LexerAction

    constructor(offset: number, action: LexerAction)

    execute(lexer: Lexer): void
    updateHashCode(hash: Hash): void
    equals(other: any): boolean
}
