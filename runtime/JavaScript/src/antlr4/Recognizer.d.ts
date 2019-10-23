/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNSimulator } from "./atn/ATNSimulator"
import { ErrorListener, RecognitionException } from "./error"
import { Token } from "./Token"
import { RuleContext } from "./RuleContext"

export abstract class Recognizer {
    protected _listeners: Array<ErrorListener>
    protected _interp: ATNSimulator
    protected _stateNumber: number

    constructor()

    get state(): number
    set state(state: number)

    checkVersion(toolVersion: string): void
    addErrorListener(listener: ErrorListener): void
    removeErrorListeners(): void
    /** FIXME: Replace return type with `Map<string, number>` once fixed. */
    getTokenTypeMap(): never
    /** FIXME: Replace return type with `Map<string, number>` once fixed. */
    getRuleIndexMap(): never
    /** FIXME: Replace return type with `number` once fixed. */
    getTokenType(tokenName: string): never
    getErrorHeader(e: RecognitionException): string
    getTokenErrorDisplay(t: Token): string
    sempred(localctx: RuleContext, ruleIndex: number, actionIndex: number): boolean
    precpred(localctx: RuleContext, precedence: number): boolean
}
export namespace Recognizer {
    export const tokenTypeMapCache: Map<Array<string>, Map<string, number>>
    export const ruleIndexMapCache: Map<Array<string>, Map<string, number>>
}
