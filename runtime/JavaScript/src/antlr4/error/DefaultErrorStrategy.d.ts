import {ErrorStrategy} from "./ErrorStrategy.js";
import {RecognitionException} from "./RecognitionException.js";
import {Parser} from "../Parser.js";
import {Token} from "../Token.js";

export declare class DefaultErrorStrategy implements ErrorStrategy {
    recover(recognizer: Parser, e: RecognitionException): void;

    recoverInline(recognizer: Parser): Token;

    reportError(recognizer: Parser, e: RecognitionException): void;

    reportMatch(recognizer: Parser): void;

    reset(recognizer: Parser): void;

    sync(recognizer: Parser): void;

    inErrorRecoveryMode(recognizer: Parser): boolean;

    beginErrorCondition(recognizer: Parser): void;

    getMissingSymbol(recognizer: Parser): Token;
}
