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

export declare class ErrorListener {
    constructor()

    // If the recognizer is a Lexer, then the offendingSymbol not used.
    syntaxError<R extends Recognizer>(recognizer: R, offendingSymbol: R extends Lexer ? null : Token, line: number, column: number, msg: string, e: RecognitionException): void
    reportAmbiguity(recognizer: Parser, dfa: DFA, startIndex: number, stopIndex: number, exact: boolean, ambigAlts: BitSet | null, configs: ATNConfigSet): void
    reportAttemptingFullContext(recognizer: Parser, dfa: DFA, startIndex: number, stopIndex: number, conflictingAlts: BitSet | null, configs: ATNConfigSet): void
    reportContextSensitivity(recognizer: Parser, dfa: DFA, startIndex: number, stopIndex: number, prediction: number, configs: ATNConfigSet): void
}

export declare class ConsoleErrorListener extends ErrorListener {
    static readonly INSTANCE: ConsoleErrorListener

    constructor()
}

export declare class ProxyErrorListener extends ErrorListener {
    protected delegates: Array<ErrorListener>

    constructor(delegates: Array<ErrorListener>)
}
