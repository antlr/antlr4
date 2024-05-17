import {ErrorStrategy} from "./ErrorStrategy";
import {RecognitionException} from "./RecognitionException";
import {Parser} from "../Parser";
import {Token} from "../Token";

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
