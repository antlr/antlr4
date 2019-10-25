/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ATNConfigSet } from "../atn/ATNConfigSet"
import { DFA } from "../dfa"
import { Parser } from "../Parser"
import { BitSet } from "../Utils"

import { ErrorListener } from "./ErrorListener"

export declare class DiagnosticErrorListener extends ErrorListener {
    exactOnly: boolean

    constructor(exactOnly?: boolean | null)

    protected getDecisionDescription(recognizer: Parser, dfa: DFA): string
    protected getConflictingAlts(reportedAlts: BitSet | null, configs: ATNConfigSet): BitSet
}
