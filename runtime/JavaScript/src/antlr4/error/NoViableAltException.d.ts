import RecognitionException from "./RecognitionException";
import Parser from "../Parser";

export default class NoViableAltException extends RecognitionException {

    constructor(recognizer: Parser);
}
