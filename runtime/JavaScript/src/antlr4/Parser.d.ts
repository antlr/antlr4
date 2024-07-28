import {TokenStream} from "./TokenStream.js";
import {Recognizer} from "./Recognizer.js";
import {ErrorStrategy, RecognitionException} from "./error/index.js";
import {IntervalSet} from "./misc/index.js";
import {ParserATNSimulator} from "./atn/index.js";
import {Token} from "./Token.js";
import {ParserRuleContext} from "./context/index.js";
import {Printer} from "./utils/index.js";
import {ParseTreeListener} from "./tree/index.js";

export declare class Parser extends Recognizer<Token> {

    static EOF: number;

    _input: TokenStream;
    _ctx: ParserRuleContext;
    _interp: ParserATNSimulator;
    _errHandler: ErrorStrategy;
    _parseListeners?: any[];
    matchedEOF: boolean;
    buildParseTrees: boolean;
    printer?: Printer;
    syntaxErrorsCount: number;

    constructor(input: TokenStream);
    match(ttype: number): Token;
    matchWildcard(): Token;
    getParseListeners(): ParseTreeListener[];
    addParseListener(listener: ParseTreeListener): void;
    removeParseListener(listener: ParseTreeListener): void;
    removeParseListeners(): void;
    consume(): Token;
    enterRule(localctx: ParserRuleContext, state: number, ruleIndex: number): void;
    exitRule() : void;
    triggerExitRuleEvent() : void;
    enterOuterAlt(localctx: ParserRuleContext, altNum: number): void;
    enterRecursionRule(localctx: ParserRuleContext, state: number, ruleIndex: number, precedence: number): void;
    pushNewRecursionContext(localctx: ParserRuleContext, state: number, ruleIndex: number): void;
    unrollRecursionContexts(parentCtx: ParserRuleContext): void;
    precpred(localctx: ParserRuleContext, precedence: number): boolean;
    getRuleInvocationStack(): string[];
    dumpDFA(): void;
    getExpectedTokens(): IntervalSet;
    getTokenStream(): TokenStream;
    reset(): void;
    setTokenStream(input: TokenStream): void;
    notifyErrorListeners(msg: string, offendingToken: Token, err: RecognitionException | undefined): void;
    getCurrentToken(): Token;
    
}
