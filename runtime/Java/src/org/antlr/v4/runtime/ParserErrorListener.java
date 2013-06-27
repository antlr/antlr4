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
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionMode;
import org.antlr.v4.runtime.atn.SimulatorState;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;

/** How to emit recognition errors for parsers.
 */
public interface ParserErrorListener extends ANTLRErrorListener<Token> {
	/**
	 * This method is called by the parser when a full-context prediction
	 * results in an ambiguity.
	 * <p/>
	 * When {@code exact} is {@code true}, <em>all</em> of the alternatives in
	 * {@code ambigAlts} are viable, i.e. this is reporting an exact ambiguity.
	 * When {@code exact} is {@code false}, <em>at least two</em> of the
	 * alternatives in {@code ambigAlts} are viable for the current input, but
	 * the prediction algorithm terminated as soon as it determined that at
	 * least the <em>minimum</em> alternative in {@code ambigAlts} is viable.
	 * <p/>
	 * When the {@link PredictionMode#LL_EXACT_AMBIG_DETECTION} prediction mode
	 * is used, the parser is required to identify exact ambiguities so
	 * {@code exact} will always be {@code true}.
	 *
	 * @param recognizer the parser instance
	 * @param dfa the DFA for the current decision
	 * @param startIndex the input index where the decision started
	 * @param stopIndex the input input where the ambiguity is reported
	 * @param exact {@code true} if the ambiguity is exactly known, otherwise
	 * {@code false}. This is always {@code true} when
	 * {@link PredictionMode#LL_EXACT_AMBIG_DETECTION} is used.
	 * @param ambigAlts the potentially ambiguous alternatives
	 * @param configs the ATN configuration set where the ambiguity was
	 * determined
	 */
	void reportAmbiguity(@NotNull Parser recognizer,
						 @NotNull DFA dfa,
						 int startIndex,
						 int stopIndex,
						 boolean exact,
						 @NotNull BitSet ambigAlts,
						 @NotNull ATNConfigSet configs);

	/**
	 * This method is called when an SLL conflict occurs and the parser is about
	 * to use the full context information to make an LL decision.
	 * <p/>
	 * If one or more configurations in {@code configs} contains a semantic
	 * predicate, the predicates are evaluated before this method is called. The
	 * subset of alternatives which are still viable after predicates are
	 * evaluated is reported in {@code conflictingAlts}.
	 *
	 * @param recognizer the parser instance
	 * @param dfa the DFA for the current decision
	 * @param startIndex the input index where the decision started
	 * @param stopIndex the input index where the SLL conflict occurred
	 * @param conflictingAlts The specific conflicting alternatives. If this is
	 * {@code null}, the conflicting alternatives are all alternatives
	 * represented in {@code configs}.
	 * @param conflictState the simulator state when the SLL conflict was
	 * detected
	 */
	void reportAttemptingFullContext(@NotNull Parser recognizer,
									 @NotNull DFA dfa,
									 int startIndex,
									 int stopIndex,
									 @Nullable BitSet conflictingAlts,
									 @NotNull SimulatorState conflictState);

	/**
	 * This method is called by the parser when a full-context prediction has a
	 * unique result.
	 * <p/>
	 * For prediction implementations that only evaluate full-context
	 * predictions when an SLL conflict is found (including the default
	 * {@link ParserATNSimulator} implementation), this method reports cases
	 * where SLL conflicts were resolved to unique full-context predictions,
	 * i.e. the decision was context-sensitive. This report does not necessarily
	 * indicate a problem, and it may appear even in completely unambiguous
	 * grammars.
	 * <p/>
	 * {@code configs} may have more than one represented alternative if the
	 * full-context prediction algorithm does not evaluate predicates before
	 * beginning the full-context prediction. In all cases, the final prediction
	 * is passed as the {@code prediction} argument.
	 *
	 * @param recognizer the parser instance
	 * @param dfa the DFA for the current decision
	 * @param startIndex the input index where the decision started
	 * @param stopIndex the input index where the context sensitivity was
	 * finally determined
	 * @param prediction the unambiguous result of the full-context prediction
	 * @param acceptState the simulator state when the unambiguous prediction
	 * was determined
	 */
	void reportContextSensitivity(@NotNull Parser recognizer,
								  @NotNull DFA dfa,
								  int startIndex,
								  int stopIndex,
								  int prediction,
								  @NotNull SimulatorState acceptState);
}
