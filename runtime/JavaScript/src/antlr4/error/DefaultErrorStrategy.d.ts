import ErrorStrategy from "./ErrorStrategy";
import Parser from "../Parser";
import RecognitionException from "./RecognitionException";
import Token from "../Token";

export default class DefaultErrorStrategy implements ErrorStrategy {
    recover(recognizer: Parser, e: RecognitionException): void;

    recoverInline(recognizer: Parser): Token;

    reportError(recognizer: Parser, e: RecognitionException): void;

    reportMatch(recognizer: Parser): void;

    reset(recognizer: Parser): void;

    sync(recognizer: Parser): void;

}
