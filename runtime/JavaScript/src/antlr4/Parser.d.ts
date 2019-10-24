/* Copyright (c) The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
import { ParseTreeListener } from "./tree"
import { Recognizer } from "./Recognizer"
import { TokenStream } from "./TokenStream"
import { ErrorStrategy } from "./error/ErrorStrategy"
import { ParserRuleContext } from "./ParserRuleContext"
import { TerminalNode } from "./tree/Tree"
import { Token } from "./Token"
import { TokenFactory } from "./TokenFactory"
import { ATN } from "./atn"
import { Lexer } from "./Lexer"
import { TokenSource } from "./TokenSource"
import { RecognitionException } from "./error"
import { RuleContext } from "./RuleContext"
import { IntervalSet } from "./IntervalSet"

export class TraceListener extends ParseTreeListener {
    public parser: Parser

    constructor(parser: Parser)

    enterEveryRule(ctx: ParserRuleContext): void
    visitTerminal(node: TerminalNode): void
    exitEveryRule(ctx: ParserRuleContext): void
}

export class Parser extends Recognizer {
    public _input: TokenStream | null
    public _errHandler: ErrorStrategy
    public _precedenceStack: Array<number>
    public _ctx: ParserRuleContext | null
    public buildParseTrees: boolean
    public _tracer: TraceListener | null
    public _parseListeners: Array<ParseTreeListener> | null
    public _syntaxErrors: number

    constructor(input: TokenStream)

    public reset(): void
    public match(ttype: number): Token | null
    public matchWildcard(): Token | null
    public getParseListeners(): ReadonlyArray<ParseTreeListener>
    public addParseListener(listener: ParseTreeListener): void
    public removeParseListener(listener: ParseTreeListener): void
    public removeParseListeners(): void
    public getTokenFactory(): TokenFactory
    public setTokenFactory<F extends TokenFactory>(factory: F): void
    public getATNWithBypassAlts(): ATN
    /** TODO: not implemented.
     *
     * Replace with `ParseTreePattern` once complete.
     */
    public compileParseTreePattern(pattern: string, patternRuleIndex: number, lexer?: Lexer): never
    public getInputStream(): TokenStream
    public setInputStream<S extends TokenStream>(input: S): void
    public getTokenStream(): TokenSource
    public setTokenStream<S extends TokenSource>(input: S): void
    public getCurrentToken(): Token | null
    public notifyErrorListeners(msg: string, offendingToken: Token | undefined | null, err: RecognitionException): void
    public consume(): Token | null
    public enterRule(): void
    public exitRule(): void
    public enterOuterAlt(localctx: ParserRuleContext, altNum: number): void
    public getPrecedence(): number
    public enterRecursionRule(localctx: ParserRuleContext, state: number, ruleIndex: number, precedence: number): void
    public pushNewRecursionContext(localctx: ParserRuleContext, state: number, ruleIndex: number): void
    public unrollRecursionContexts(parentCtx: ParserRuleContext): void
    public getInvokingContext(ruleIndex?: number): ParserRuleContext | null
    public precpred(localctx: RuleContext, precedence: number): boolean
    public inContext(context: string): boolean
    public isExpectedToken(symbol: number): boolean
    public getExpectedTokens(): IntervalSet
    public getExpectedTokensWithinCurrentRule(): IntervalSet
    /** TODO: `getRuleIndexMap` method not implemeted in generated parser.
     *
     * Replace with `number` once complete.
     */
    public getRuleIndex(ruleName: string): never
    public getRuleInvocationStack(p?: RuleContext): Array<string>
    public getDFAStrings(): Array<string>
    public dumpDFA(): void
    /** TODO: `sourceName` property not implemented in any extending classes of TokenStream.
     *
     * Replace with `string` once complete.
     *
     * If `this._input` is non-null, then this method returns `undefined`.
     * If `this._input` is null, then an error will be thrown.
     */
    public getSourceName(): never
    public setTrace(trace: boolean): void

    protected triggerEnterRuleEvent(): void
    protected triggerExitRuleEvent(): void
    protected addContextToParseTree(): void
}
