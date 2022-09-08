import RecognitionException from "./RecognitionException";
import Parser from "../Parser";

export default class FailedPredicateException extends RecognitionException {

    constructor(recognizer: Parser, predicate: string | undefined, message: string | undefined);
}
