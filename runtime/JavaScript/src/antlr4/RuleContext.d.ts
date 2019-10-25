/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "./IntervalSet"
import { Parser } from "./Parser"
import { ParserRuleContext } from "./ParserRuleContext"
import { ParseTreeVisitor, RuleNode } from "./tree"
import { ParseTree } from "./tree/Tree"

export declare class RuleContext extends RuleNode {
    parentCtx: RuleContext | null
    invokingState: number

    constructor()
    constructor(parent: RuleContext, invokingState: number)

    depth(): number
    isEmpty(): boolean
    getSourceInterval(): Interval
    getRuleContext(): RuleContext
    getPayload(): RuleContext
    getText(): string
    getAltNumber(): number
    setAltNumber(altNumber: number): void
    getChild(i: number): ParseTree | null
    getChildCount(): number
    accept(visitor: ParseTreeVisitor): any
    toStringTree(ruleNames?: Array<string> | null, recog?: Parser | null): string
    toString(ruleNames?: Array<string> | null, stop?: number | null): string
}
export declare namespace RuleContext {
    export const EMPTY: ParserRuleContext
}
