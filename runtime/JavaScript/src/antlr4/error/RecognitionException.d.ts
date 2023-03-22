import {ParserRuleContext, RuleContext} from "../context";
import {TokenStream} from "../TokenStream";
import {Recognizer} from "../Recognizer";
import {CharStream} from "../CharStream";
import {Token} from "../Token";

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
