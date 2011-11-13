package org.antlr.v4.runtime;

import org.antlr.v4.runtime.tree.AST;

/** This signifies any kind of mismatched input exceptions such as
 *  when the current input does not match the expected token or tree node.
 */
public class InputMismatchException extends RecognitionException {
	public InputMismatchException(BaseRecognizer recognizer) {
		super(recognizer, recognizer.getInputStream(), recognizer._ctx);
		Object la = recognizer.getCurrentInputSymbol();
		if ( la instanceof AST ) {
			this.offendingNode = la;
			this.offendingToken = ((AST)la).getPayload();
		}
		else {
			this.offendingToken = (Token)la;
		}
	}
}
