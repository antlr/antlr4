package org.antlr.v4.runtime;

/** This signifies any kind of mismatched input exceptions such as
 *  when the current input does not match the expected token or tree node.
 */
public class InputMismatchException extends RecognitionException {
	public <T extends Token> InputMismatchException(Parser<T> recognizer) {
		super(recognizer, recognizer.getInputStream(), recognizer._ctx);
		T la = recognizer.getCurrentToken();
		this.offendingToken = la;
	}
}
