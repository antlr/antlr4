import {RecognitionException} from "./RecognitionException";
import {Parser} from "../Parser";

export declare class FailedPredicateException extends RecognitionException {

    constructor(recognizer: Parser, predicate: string | undefined, message: string | undefined);
}
