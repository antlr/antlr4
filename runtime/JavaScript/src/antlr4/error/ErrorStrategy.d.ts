import {RecognitionException} from "./RecognitionException";
import {Parser} from "../Parser";
import {Token} from "../Token";

export declare class ErrorStrategy {
    reset(recognizer: Parser): void;
    sync(recognizer: Parser): void;
    recover(recognizer: Parser, e: RecognitionException): void;
    recoverInline(recognizer: Parser): Token;
    reportMatch(recognizer: Parser): void;
    reportError(recognizer: Parser, e: RecognitionException): void;
}
