/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "./IntervalSet"
import { Parser } from "./Parser"
import { ParseTreeVisitor, RuleNode } from "./tree"

export class RuleContext extends RuleNode {
    public parentCtx: unknown | null
    public invokingState: number

    constructor()
    constructor(parent: unknown, invokingState: number)

    depth(): number
    isEmpty(): boolean
    getSourceInterval(): Interval
    getRuleContext(): RuleContext
    getPayload(): RuleContext
    getText(): string
    getAltNumber(): number
    setAltNumber(altNumber: number): void
    getChild(i: number): null
    getChildCount(): 0
    accept<T>(visitor: ParseTreeVisitor): T
    toStringTree(ruleNames?: Array<string>, recog: Parser): string
    toString(ruleNames?: Array<string>, stop?: number): string
}
