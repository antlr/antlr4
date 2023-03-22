import {RecognitionException} from "./RecognitionException";
import {Parser} from "../Parser";

export declare class InputMismatchException extends RecognitionException {
    constructor(recognizer: Parser);
}