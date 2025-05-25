import {ParserRuleContext, RuleContext} from "../context/index.js";
import {TokenStream} from "../TokenStream.js";
import {Recognizer} from "../Recognizer.js";
import {CharStream} from "../CharStream.js";
import {Token} from "../Token.js";

export interface ExceptionParams {
    message: string;
    recognizer?: Recognizer<never>;
    input?: CharStream | TokenStream;
    ctx?: ParserRuleContext;

}

export declare class RecognitionException extends Error {
    ctx: RuleContext;
    offendingToken: Token | null;
    constructor(params: ExceptionParams);
}
