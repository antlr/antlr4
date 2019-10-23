/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { RecognitionException } from "./error"
import { Interval } from "./IntervalSet"
import { RuleContext } from "./RuleContext"
import { Token } from "./Token"
import {
    ErrorNodeImpl,
    INVALID_INTERVAL,
    ParseTree,
    ParseTreeListener,
    TerminalNode,
    TerminalNodeImpl
} from "./tree/Tree"

export class ParserRuleContext extends RuleContext {
    public ruleIndex: number
    public children: Array<ParseTree> | null
    public start: Token | null
    public stop: Token | null
    public exception: RecognitionException | null

    constructor()
    constructor(parent: ParserRuleContext, invokingStateNumber: number)

    copyFrom(ctx: ParserRuleContext): void
    enterRule(listener: ParseTreeListener): void
    exitRule(listener: ParseTreeListener): void
    addChild<T extends ParseTree>(child: T): T
    removeLastChild(): void
    addTokenNode(token: Token): TerminalNodeImpl
    addErrorNode(badToken: Token): ErrorNodeImpl

    getChild(i: number): ParseTree | null
    getChild<T extends typeof ParseTree>(i: number, type: T): T | null

    getToken(ttype: number, i: number): TerminalNode | null
    getTokens(ttype: number): Array<TerminalNode>
    getTypedRuleContext<T extends typeof ParseTree>(ctxType: T, i: number): T | null
    getTypedRuleContexts<T extends typeof ParseTree>(ctxType: T): Array<T>
    getChildCount(): number
    getSourceInterval(): Interval
}
