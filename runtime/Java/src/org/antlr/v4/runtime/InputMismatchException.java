package org.antlr.v4.runtime;

/** This signifies any kind of mismatched input exceptions such as
 *  when the current input does not match the expected token or tree node.
 */
public class InputMismatchException extends RecognitionException {
	private static final long serialVersionUID = 1532568338707443067L;

	public InputMismatchException(Parser recognizer) {
		super(recognizer, recognizer.getInputStream(), recognizer._ctx);
		Token la = recognizer.getCurrentToken();
		this.offendingToken = la;
	}
}
