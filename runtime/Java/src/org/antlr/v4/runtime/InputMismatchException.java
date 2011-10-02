package org.antlr.v4.runtime;

/** This signifies any kind of mismatched input exceptions such as
 *  when the current input does not match the expected token or tree node.
 */
public class InputMismatchException extends RecognitionException {
	public InputMismatchException(BaseRecognizer recognizer) {
		super(recognizer, recognizer.getInputStream(), recognizer._ctx);
		this.offendingToken = (Token)recognizer.getCurrentInputSymbol();
	}
}
