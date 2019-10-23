/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { DecisionState } from "../atn/ATNState"
import { Map, Set } from "../Utils"
import { DFAState } from "./DFAState"

export class DFA {
    public atnStartState: DecisionState
    public decision: number
    public s0: DFAState | null
    public precedenceDfa: boolean

    protected _states: Set<DFAState>

    constructor(atnStartState: DecisionState, decision?: number)

    get states(): Set<DFAState>

    getPrecedenceStartState(precedence: number): DFAState | null
    setPrecedenceStartState(precedence: number, startState: DFAState): void
    setPrecedenceDfa(precedenceDfa: boolean): void
    sortedStates(): Array<DFAState>
    toString(): string
    toLexerString(): string
}
