import Recognizer from "./Recognizer";
import TokenStream from "./TokenStream";
import ParserRuleContext from './context/ParserRuleContext';
import ParserATNSimulator from "./atn/ParserATNSimulator";
import ErrorStrategy from "./error/ErrorStrategy";
import Token from "./Token";
import Printer from "./utils/Printer";

export default class Parser extends Recognizer {

    _input: TokenStream;
    _ctx: ParserRuleContext;
    _interp: ParserATNSimulator;
    _errHandler: ErrorStrategy;
    matchedEOF: boolean;
    buildParseTrees: boolean;
    printer?: Printer;

    constructor(input: TokenStream);
    consume(): Token;
    enterRule(localctx: ParserRuleContext, state: number, ruleIndex: number): void;
    exitRule() : void;
    enterOuterAlt(localctx: ParserRuleContext, altNum: number): void;
}
