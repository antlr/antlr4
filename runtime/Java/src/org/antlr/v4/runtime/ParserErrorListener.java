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
import org.antlr.v4.runtime.atn.SimulatorState;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.IntervalSet;
import org.antlr.v4.runtime.misc.NotNull;

/** How to emit recognition errors */
public interface ParserErrorListener<Symbol extends Token> extends ANTLRErrorListener<Symbol> {
	/** Called when the parser detects a true ambiguity: an input sequence can be matched
	 * literally by two or more pass through the grammar. ANTLR resolves the ambiguity in
	 * favor of the alternative appearing first in the grammar. The start and stop index are
     * zero-based absolute indices into the token stream. ambigAlts is a set of alternative numbers
     * that can match the input sequence. This method is only called when we are parsing with
     * full context.
     */
    void reportAmbiguity(@NotNull Parser<? extends Symbol> recognizer,
						 DFA dfa, int startIndex, int stopIndex, @NotNull IntervalSet ambigAlts,
						 @NotNull ATNConfigSet configs);

	<T extends Symbol> void reportAttemptingFullContext(@NotNull Parser<T> recognizer,
									 @NotNull DFA dfa,
									 int startIndex, int stopIndex,
									 @NotNull SimulatorState<T> initialState);

	/** Called by the parser when it find a conflict that is resolved by retrying the parse
     *  with full context. This is not a warning; it simply notifies you that your grammar
     *  is more complicated than Strong LL can handle. The parser moved up to full context
     *  parsing for that input sequence.
     */
    <T extends Symbol> void reportContextSensitivity(@NotNull Parser<T> recognizer,
                                  @NotNull DFA dfa,
                                  int startIndex, int stopIndex,
                                  @NotNull SimulatorState<T> acceptState);
}
