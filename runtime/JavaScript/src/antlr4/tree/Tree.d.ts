/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { Interval } from "../IntervalSet"
import { ParserRuleContext } from "../ParserRuleContext"
import { RuleContext } from "../RuleContext"
import { Token } from "../Token"
import "../Utils"

export declare const INVALID_INTERVAL: Interval

export declare interface Tree {
    // getChild(i: number): Tree | null
    // getChildCount(): number
    // getPayload(): object
    // getParent(): Tree | null
    // toString(): string
}

export declare interface SyntaxTree extends Tree {
    // getSourceInterval(): Interval
}

export declare interface ParseTree extends SyntaxTree {
    // accept(visitor: ParseTreeVisitor): any
    // getParent(): ParseTree | null
}

export declare abstract class RuleNode implements ParseTree {
    protected constructor()

    abstract accept(visitor: ParseTreeVisitor): any
    abstract getChild(i: number): Tree | null
    abstract getChildCount(): number
    // abstract getParent(): ParseTree | null
    abstract getPayload(): object
    abstract getSourceInterval(): Interval
    abstract toString(): string
}

export declare abstract class TerminalNode implements ParseTree {
    protected constructor()

    abstract accept(visitor: ParseTreeVisitor): any
    abstract getChild(i: number): Tree | null
    abstract getChildCount(): number
    abstract getParent(): ParseTree | null
    abstract getPayload(): object
    abstract getSourceInterval(): Interval
    abstract getSymbol(): Token
    abstract toString(): string
}

export declare abstract class ErrorNode extends TerminalNode {
    protected constructor()
}

export declare class ParseTreeVisitor {
    constructor()

    visit(ctx: Array<ParseTree>): Array<any>
    visit(ctx: RuleContext): any
    visitChildren(ctx: RuleContext): any | null
    visitErrorNode(node: ErrorNode): any
    visitTerminal(node: TerminalNode): any
}

export declare class ParseTreeListener {
    constructor()

    visitErrorNode(node: ErrorNode): void
    visitTerminal(node: TerminalNode): void
    enterEveryRule(ctx: ParserRuleContext): void
    exitEveryRule(ctx: ParserRuleContext): void
}

export declare class TerminalNodeImpl implements TerminalNode {
    parentCtx: ParseTree
    symbol: Token

    constructor(symbol: Token)

    getChild(i: number): null
    getSymbol(): Token
    getParent(): ParseTree
    getPayload(): Token
    getSourceInterval(): Interval
    getChildCount(): number
    accept(visitor: ParseTreeVisitor): any
    getText(): string
    toString(): string
}

export declare class ErrorNodeImpl extends TerminalNodeImpl implements ErrorNode {
    constructor(token: Token)

    isErrorNode(): boolean
    accept(visitor: ParseTreeVisitor): any
}

export declare class ParseTreeWalker {
    static readonly DEFAULT: ParseTreeWalker

    constructor()

    walk(listener: ParseTreeListener, t: ParseTree): void

    protected enterRule(listener: ParseTreeListener, r: RuleNode): void
    protected exitRule(listener: ParseTreeListener, r: RuleNode): void
}
