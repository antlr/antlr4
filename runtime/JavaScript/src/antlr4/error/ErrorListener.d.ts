/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { DFA } from "../dfa"
import { Lexer } from "../Lexer"
import { Parser } from "../Parser"
import { Recognizer } from "../Recognizer"
import { Token } from "../Token"
import { BitSet } from "../Utils"
import { RecognitionException } from "./Errors"

export class ErrorListener {
    constructor()

    syntaxError<R extends typeof Recognizer>(
        recognizer: R,
        offendingSymbol: Token,
        line: number,
        column: number,
        msg: string,
        e: RecognitionException
    ): void
    syntaxError<L extends typeof Lexer>(
        recognizer: L,
        offendingSymbol: null,
        line: number,
        column: number,
        msg: string,
        e: RecognitionException
    ): void

    reportAmbiguity<P extends typeof Parser>(
        recognizer: P,
        dfa: DFA,
        startIndex: number,
        stopIndex: number,
        exact: boolean,
        ambigAlts: BitSet | null,
        configs: ATNConfigSet
    ): void

    reportAttemptingFullContext<P extends typeof Parser>(
        recognizer: P,
        dfa: DFA,
        startIndex: number,
        stopIndex: number,
        conflictingAlts: BitSet | null,
        configs: ATNConfigSet
    ): void

    reportContextSensitivity<P extends typeof Parser>(
        recognizer: P,
        dfa: DFA,
        startIndex: number,
        stopIndex: number,
        prediction: number,
        configs: ATNConfigSet
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
