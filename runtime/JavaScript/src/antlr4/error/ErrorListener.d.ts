/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DFA } from "../dfa"
import { Recognizer } from "../Recognizer"

export class ErrorListener {
    constructor()

    syntaxError(
        recognizer: Recognizer,
        offendingSymbol: unknown,
        line: number,
        column: number,
        msg: unknown,
        e: unknown
    ): void

    reportAmbiguity(
        recognizer: Recognizer,
        dfa: DFA,
        startIndex: number,
        stopIndex:number,
        exact: unknown,
        ambigAlts: unknown,
        configs: unknown
    ): void

    reportAttemptingFullContext(
        recognizer: Recognizer,
        dfa: DFA,
        startIndex: number,
        stopIndex:number,
        conflictingAlts: unknown,
        configs: unknown
    ): void

    reportContextSensitivity(
        recognizer: Recognizer,
        dfa: DFA,
        startIndex: number,
        stopIndex:number,
        prediction: unknown,
        configs: unknown
    ): void
}

export class ConsoleErrorListener extends ErrorListener {
    static readonly INSTANCE: ConsoleErrorListener

    constructor()
}

export class ProxyErrorListener extends ErrorListener {
    public delegates: Array<ErrorListener>

    constructor(delegates: Array<ErrorListener>)
}
