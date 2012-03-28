/*
 [The "BSD license"]
 Copyright (c) 2011 Terence Parr
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:

 1. Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
 3. The name of the author may not be used to endorse or promote products
    derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

/** How to emit recognition errors */
public interface ANTLRErrorListener<Symbol> {
	/** Upon syntax error, notify any interested parties. This is not how to
	 *  recover from errors or compute error messages. The parser
	 *  ANTLRErrorStrategy specifies how to recover from syntax errors
	 *  and how to compute error messages. This listener's job is simply to
	 *  emit a computed message, though it has enough information to
	 *  create its own message in many cases.
	 *
	 *  The RecognitionException is non-null for all syntax errors except
	 *  when we discover mismatched token errors that we can recover from
	 *  in-line, without returning from the surrounding rule (via the
	 *  single token insertion and deletion mechanism).
	 *
	 * @param recognizer
	 * 		  What parser got the error. From this object, you
	 * 		  can access the context as well as the input stream.
	 * @param offendingSymbol
	 * 		  The offending token in the input token stream, unless recognizer
	 * 		  is a lexer (then it's null)
	 * 		  If no viable alternative error, e has token
	 * 		  at which we started production for the decision.
	 * @param line
	 * 		  At what line in input to the error occur? This always refers to
	 * 		  stopTokenIndex
	 * @param charPositionInLine
	 * 		  At what character position within that line did the error occur.
	 * @param msg
	 * 		  The message to emit
	 * @param e
	 *        The exception generated by the parser that led to
	 *        the reporting of an error. It is null in the case where
	 *        the parser was able to recover in line without exiting the
	 *        surrounding rule.
	 */
	public <T extends Symbol> void error(Recognizer<T, ?> recognizer,
										 @Nullable T offendingSymbol,
										 int line,
										 int charPositionInLine,
										 String msg,
										 @Nullable RecognitionException e);

	/** Called when the parser detects a true ambiguity: an input sequence can be matched
	 * literally by two or more pass through the grammar. ANTLR resolves the ambiguity in
	 * favor of the alternative appearing first in the grammar. The start and stop index are
     * zero-based absolute indices into the token stream. ambigAlts is a set of alternative numbers
     * that can match the input sequence. This method is only called when we are parsing with
     * full context.
     */
    void reportAmbiguity(@NotNull Parser recognizer,
						 DFA dfa, int startIndex, int stopIndex, @NotNull IntervalSet ambigAlts,
						 @NotNull ATNConfigSet configs);

	void reportAttemptingFullContext(@NotNull Parser recognizer,
									 @NotNull DFA dfa,
									 int startIndex, int stopIndex,
									 @NotNull ATNConfigSet configs);

	/** Called by the parser when it find a conflict that is resolved by retrying the parse
     *  with full context. This is not a warning; it simply notifies you that your grammar
     *  is more complicated than Strong LL can handle. The parser moved up to full context
     *  parsing for that input sequence.
     */
    void reportContextSensitivity(@NotNull Parser recognizer,
                                  @NotNull DFA dfa,
                                  int startIndex, int stopIndex,
                                  @NotNull ATNConfigSet configs);
}
