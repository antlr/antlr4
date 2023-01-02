import ParserRuleContext from "../context/ParserRuleContext";
import RuleContext from "../context/RuleContext";
import TokenStream from "../TokenStream";
import Recognizer from "../Recognizer";
import CharStream from "../CharStream";

export interface ExceptionParams {
    message: string;
    recognizer?: Recognizer<never>;
    input?: CharStream | TokenStream;
    ctx?: ParserRuleContext;

}

declare class RecognitionException extends Error {
    ctx: RuleContext;

    constructor(params: ExceptionParams);
}

export default RecognitionException;
