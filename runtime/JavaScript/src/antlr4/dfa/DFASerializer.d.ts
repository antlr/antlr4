/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DFA } from "./DFA"
import { DFAState } from "./DFAState"

export class DFASerializer {
    public dfa: DFA
    public literalNames: Array<string>
    public symbolicNames: Array<string>

    constructor(dfa: DFA, literalNames?: Array<string>, symbolicNames?: Array<string>)

    toString(): string
    getEdgeLabel(i: number): string
    getStateString(s: DFAState): string
}

export class LexerDFASerializer extends DFASerializer {
    constructor(dfa: DFA)
}
