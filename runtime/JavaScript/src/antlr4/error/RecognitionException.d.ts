import {ParserRuleContext, RuleContext} from "../context";
import Recognizer from "../Recognizer";
import CharStream from "../CharStream";
import TokenStream from "../TokenStream";

export interface ExceptionParams {
    message: string;
    recognizer?: Recognizer<never>;
    input?: CharStream | TokenStream;
    ctx?: ParserRuleContext;

}

export default class RecognitionException extends Error {
    ctx: RuleContext;

    constructor(params: ExceptionParams);
}
