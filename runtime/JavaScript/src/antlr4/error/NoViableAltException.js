import RecognitionException from "./RecognitionException.js";

/**
 * Indicates that the parser could not decide which of two or more paths
 * to take based upon the remaining input. It tracks the starting token
 * of the offending input and also knows where the parser was
 * in the various paths when the error. Reported by reportNoViableAlternative()
 */

export default class NoViableAltException extends RecognitionException {
    constructor(recognizer, input, startToken, offendingToken, deadEndConfigs, ctx) {
        ctx = ctx || recognizer._ctx;
        offendingToken = offendingToken || recognizer.getCurrentToken();
        startToken = startToken || recognizer.getCurrentToken();
        input = input || recognizer.getInputStream();
        super({message: "", recognizer: recognizer, input: input, ctx: ctx});
        // Which configurations did we try at input.index() that couldn't match
        // input.LT(1)?//
        this.deadEndConfigs = deadEndConfigs;
        // The token object at the start index; the input stream might
        // not be buffering tokens so get a reference to it. (At the
        // time the error occurred, of course the stream needs to keep a
        // buffer all of the tokens but later we might not have access to those.)
        this.startToken = startToken;
        this.offendingToken = offendingToken;
    }
}
