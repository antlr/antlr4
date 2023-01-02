import TokenStream from "./TokenStream";
import Recognizer from "./Recognizer";
import ErrorStrategy from "./error/ErrorStrategy";
import IntervalSet from "./misc/IntervalSet";
import ParserATNSimulator from "./atn/ParserATNSimulator";
import Token from "./Token";
import ParserRuleContext from "./context/ParserRuleContext";
import Printer from "./utils/Printer";

declare class Parser extends Recognizer<Token> {

    static EOF: number;

    _input: TokenStream;
    _ctx: ParserRuleContext;
    _interp: ParserATNSimulator;
    _errHandler: ErrorStrategy;
    _parseListeners?: any[];
    matchedEOF: boolean;
    buildParseTrees: boolean;
    printer?: Printer;

    constructor(input: TokenStream);
    match(ttype: number): Token;
    matchWildcard(): Token;
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
}

export default Parser;
