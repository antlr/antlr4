package org.antlr.v4.runtime;

/** The interface for defining strategies to deal with syntax errors
 *  encountered during a parse by ANTLR-generated parsers and tree parsers.
 *  We distinguish between three different kinds of errors:
 *
 *  	o The parser could not figure out which path to take in the ATN
 *        (none of the available alternatives could possibly match)
 *      o The current input does not match what we were looking for.
 *      o A predicate evaluated to false.
 *
 *  The default implementation of this interface reports errors to any
 *  error listeners of the parser. It also handles single token insertion
 *  and deletion for mismatched elements.
 *
 *  We pass in the parser to each function so that the same strategy
 *  can be shared between multiple parsers running at the same time.
 *  This is just for flexibility, not that we need it for the default system.
 *
 *  TODO: what to do about lexers
 */
public interface ANTLRErrorStrategy {
	/** Report any kind of RecognitionException. */
	void reportError(BaseRecognizer recognizer,
					 RecognitionException e)
		throws RecognitionException;

	/** When matching elements within alternative, use this method
	 *  to recover. The default implementation uses single token
	 *  insertion and deletion. If you want to change the way ANTLR
	 *  response to mismatched element errors within an alternative,
	 *  implement this method.
	 *
	 *  From the recognizer, we can get the input stream to get
	 *  the current input symbol and we can get the current context.
	 *  That context gives us the current state within the ATN.
	 *  From that state, we can look at its transition to figure out
	 *  what was expected.
	 *
	 *  Because we can recover from a single token deletions by
	 *  "inserting" tokens, we need to specify what that implicitly created
	 *  token is. We use object, because it could be a tree node.
	 *
	 *  To bail out upon first error, simply rethrow e.
	 */
	Object recoverInline(BaseRecognizer recognizer)
		throws RecognitionException;

	/** Resynchronize the parser by consuming tokens until we find one
	 *  in the resynchronization set--loosely the set of tokens that can follow
	 *  the current rule.
	 *
	 *  To bail out upon first error, simply rethrow e.
	 */
	void recover(BaseRecognizer recognizer);

	/** Reset the error handler. The parser invokes this
	 *  from its own reset method.
	 */
	void reset();
}
