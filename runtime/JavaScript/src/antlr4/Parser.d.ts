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

export declare class TraceListener extends ParseTreeListener {
    parser: Parser

    constructor(parser: Parser)

    enterEveryRule(ctx: ParserRuleContext): void
    visitTerminal(node: TerminalNode): void
    exitEveryRule(ctx: ParserRuleContext): void
}

export declare class Parser extends Recognizer {
    _input: TokenStream | null
    _errHandler: ErrorStrategy
    _precedenceStack: Array<number>
    _ctx: ParserRuleContext | null
    buildParseTrees: boolean
    _tracer: TraceListener | null
    _parseListeners: Array<ParseTreeListener> | null
    _syntaxErrors: number

    constructor(input: TokenStream)

    reset(): void
    match(ttype: number): Token | null
    matchWildcard(): Token | null
    getParseListeners(): ReadonlyArray<ParseTreeListener>
    addParseListener(listener: ParseTreeListener): void
    removeParseListener(listener: ParseTreeListener): void
    removeParseListeners(): void
    getTokenFactory(): TokenFactory
    setTokenFactory<F extends TokenFactory>(factory: F): void
    getATNWithBypassAlts(): ATN
    /** TODO: not implemented.
     *
     * Replace with `ParseTreePattern` once complete.
     */
    compileParseTreePattern(pattern: string, patternRuleIndex: number, lexer?: Lexer): never
    getInputStream(): TokenStream
    setInputStream(input: TokenStream): void
    getTokenStream(): TokenSource
    setTokenStream(input: TokenSource): void
    getCurrentToken(): Token | null
    notifyErrorListeners(msg: string, offendingToken: Token | undefined | null, err: RecognitionException): void
    consume(): Token | null
    enterRule(): void
    exitRule(): void
    enterOuterAlt(localctx: ParserRuleContext, altNum: number): void
    getPrecedence(): number
    enterRecursionRule(localctx: ParserRuleContext, state: number, ruleIndex: number, precedence: number): void
    pushNewRecursionContext(localctx: ParserRuleContext, state: number, ruleIndex: number): void
    unrollRecursionContexts(parentCtx: ParserRuleContext): void
    getInvokingContext(ruleIndex?: number): ParserRuleContext | null
    precpred(localctx: RuleContext, precedence: number): boolean
    inContext(context: string): boolean
    isExpectedToken(symbol: number): boolean
    getExpectedTokens(): IntervalSet
    getExpectedTokensWithinCurrentRule(): IntervalSet
    /** TODO: `getRuleIndexMap` method not implemeted in generated parser.
     *
     * Replace with `number` once complete.
     */
    getRuleIndex(ruleName: string): never
    getRuleInvocationStack(p?: RuleContext): Array<string>
    getDFAStrings(): Array<string>
    dumpDFA(): void
    /** TODO: `sourceName` property not implemented in any extending classes of TokenStream.
     *
     * Replace with `string` once complete.
     *
     * If `this._input` is non-null, then this method returns `undefined`.
     * If `this._input` is null, then an error will be thrown.
     */
    getSourceName(): never
    setTrace(trace: boolean): void

    protected triggerEnterRuleEvent(): void
    protected triggerExitRuleEvent(): void
    protected addContextToParseTree(): void
}
