import RecognitionException from "./RecognitionException.js";

/**
 * This signifies any kind of mismatched input exceptions such as
 * when the current input does not match the expected token.
 */
export default class InputMismatchException extends RecognitionException {
    constructor(recognizer) {
        super({message: "", recognizer: recognizer, input: recognizer.getInputStream(), ctx: recognizer._ctx});
        this.offendingToken = recognizer.getCurrentToken();
    }
}
