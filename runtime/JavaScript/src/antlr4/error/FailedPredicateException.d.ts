import {RecognitionException} from "./RecognitionException.js";
import {Parser} from "../Parser.js";

export declare class FailedPredicateException extends RecognitionException {

    constructor(recognizer: Parser, predicate: string | undefined, message: string | undefined);
}
