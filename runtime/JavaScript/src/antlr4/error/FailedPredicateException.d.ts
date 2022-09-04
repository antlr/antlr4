import RecognitionException from "./RecognitionException";
import Recognizer from "../Recognizer";

export default class FailedPredicateException extends RecognitionException {

    constructor(recognizer: Recognizer, predicate: string | undefined, message: string | undefined);
}
