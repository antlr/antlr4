/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "../IntervalSet"
import { Token } from "../Token"
import "../Utils"

export const INVALID_INTERVAL: Interval

export class Tree {
    constructor()
}

export class SyntaxTree extends Tree {
    constructor()
}

export class ParseTree extends SyntaxTree {
    constructor()
}

export class RuleNode extends ParseTree {
    constructor()
}

export class TerminalNode extends ParseTree {
    constructor()
}

export class ErrorNode extends TerminalNode {
    constructor()
}

export class ParseTreeVisitor<T> {
    constructor()

    visit(ctx: Array<ParserRuleContext>): Array<T>
    visit(ctx: ParserRuleContext): T
    visitChildren(ctx: ParserRuleContext): T | null
    visitTerminal(node: TerminalNode): void
    visitErrorNode(node: ErrorNode): void
}

export class ParseTreeListener {
    constructor()

    visitTerminal(node: TerminalNode): void
    visitErrorNode(node: ErrorNode): void
    enterEveryRule(ctx: ParserRuleContext): void
    exitEveryRule(ctx: ParserRuleContext): void
}

export class TerminalNodeImpl extends TerminalNode {
    public parentCtx: ParseTree
    public symbol: Token

    constructor(symbol: Token)

    getChild(i: number): null
    getSymbol(): Token
    getParent(): ParseTree
    getPayload(): Token
    getSourceInterval(): Interval
    getChildCount(): 0
    accept<T>(visitor: ParseTreeVisitor): T
    getText(): string
    toString(): string
}

export class ErrorNodeImpl extends TerminalNodeImpl {
    constructor(token: Token)

    isErrorNode(): true
    accept<T>(visitor: ParseTreeVisitor): T
}

export class ParseTreeWalker {
    static readonly DEFAULT: ParseTreeWalker

    constructor()

    walk(listener: ParseTreeListener, t: ParseTree): void
    enterRule(listener: ParseTreeListener, r: RuleNode): void
    exitRule(listener: ParseTreeListener, r: RuleNode): void
}
