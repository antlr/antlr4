import {RecognitionException} from "./RecognitionException.js";
import {Parser} from "../Parser.js";
import {Token} from "../Token.js";

export declare class ErrorStrategy {
    reset(recognizer: Parser): void;
    sync(recognizer: Parser): void;
    recover(recognizer: Parser, e: RecognitionException): void;
    recoverInline(recognizer: Parser): Token;
    reportMatch(recognizer: Parser): void;
    reportError(recognizer: Parser, e: RecognitionException): void;
}
