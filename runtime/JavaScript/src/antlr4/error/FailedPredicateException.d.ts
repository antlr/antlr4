import RecognitionException from "./RecognitionException";
import Parser from "../Parser";

declare class FailedPredicateException extends RecognitionException {

    constructor(recognizer: Parser, predicate: string | undefined, message: string | undefined);
}

export default FailedPredicateException;
