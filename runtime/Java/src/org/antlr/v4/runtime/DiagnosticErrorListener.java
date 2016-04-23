/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.antlr.v4.runtime;

import org.antlr.v4.runtime.atn.ATNConfig;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.Interval;

import java.util.BitSet;

/**
 * This implementation of {@link ANTLRErrorListener} can be used to identify
 * certain potential correctness and performance problems in grammars. "Reports"
 * are made by calling {@link Parser#notifyErrorListeners} with the appropriate
 * message.
 *
 * <ul>
 * <li><b>Ambiguities</b>: These are cases where more than one path through the
 * grammar can match the input.</li>
 * <li><b>Weak context sensitivity</b>: These are cases where full-context
 * prediction resolved an SLL conflict to a unique alternative which equaled the
 * minimum alternative of the SLL conflict.</li>
 * <li><b>Strong (forced) context sensitivity</b>: These are cases where the
 * full-context prediction resolved an SLL conflict to a unique alternative,
 * <em>and</em> the minimum alternative of the SLL conflict was found to not be
 * a truly viable alternative. Two-stage parsing cannot be used for inputs where
 * this situation occurs.</li>
 * </ul>
 *
 * @author Sam Harwell
 */
public class DiagnosticErrorListener extends BaseErrorListener {
	/**
	 * When {@code true}, only exactly known ambiguities are reported.
	 */
	protected final boolean exactOnly;

	/**
	 * Initializes a new instance of {@link DiagnosticErrorListener} which only
	 * reports exact ambiguities.
	 */
	public DiagnosticErrorListener() {
		this(true);
	}

	/**
	 * Initializes a new instance of {@link DiagnosticErrorListener}, specifying
	 * whether all ambiguities or only exact ambiguities are reported.
	 *
	 * @param exactOnly {@code true} to report only exact ambiguities, otherwise
	 * {@code false} to report all ambiguities.
	 */
	public DiagnosticErrorListener(boolean exactOnly) {
		this.exactOnly = exactOnly;
	}

	@Override
	public void reportAmbiguity(Parser recognizer,
								DFA dfa,
								int startIndex,
								int stopIndex,
								boolean exact,
								BitSet ambigAlts,
								ATNConfigSet configs)
	{
		if (exactOnly && !exact) {
			return;
		}

		String format = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
		String decision = getDecisionDescription(recognizer, dfa);
		BitSet conflictingAlts = getConflictingAlts(ambigAlts, configs);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, conflictingAlts, text);
		recognizer.notifyErrorListeners(message);
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer,
											DFA dfa,
											int startIndex,
											int stopIndex,
											BitSet conflictingAlts,
											ATNConfigSet configs)
	{
		String format = "reportAttemptingFullContext d=%s, input='%s'";
		String decision = getDecisionDescription(recognizer, dfa);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, text);
		recognizer.notifyErrorListeners(message);
	}

	@Override
	public void reportContextSensitivity(Parser recognizer,
										 DFA dfa,
										 int startIndex,
										 int stopIndex,
										 int prediction,
										 ATNConfigSet configs)
	{
		String format = "reportContextSensitivity d=%s, input='%s'";
		String decision = getDecisionDescription(recognizer, dfa);
		String text = recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex));
		String message = String.format(format, decision, text);
		recognizer.notifyErrorListeners(message);
	}

	protected String getDecisionDescription(Parser recognizer, DFA dfa) {
		int decision = dfa.decision;
		int ruleIndex = dfa.atnStartState.ruleIndex;

		String[] ruleNames = recognizer.getRuleNames();
		if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
			return String.valueOf(decision);
		}

		String ruleName = ruleNames[ruleIndex];
		if (ruleName == null || ruleName.isEmpty()) {
			return String.valueOf(decision);
		}

		return String.format("%d (%s)", decision, ruleName);
	}

	/**
	 * Computes the set of conflicting or ambiguous alternatives from a
	 * configuration set, if that information was not already provided by the
	 * parser.
	 *
	 * @param reportedAlts The set of conflicting or ambiguous alternatives, as
	 * reported by the parser.
	 * @param configs The conflicting or ambiguous configuration set.
	 * @return Returns {@code reportedAlts} if it is not {@code null}, otherwise
	 * returns the set of alternatives represented in {@code configs}.
	 */
	protected BitSet getConflictingAlts(BitSet reportedAlts, ATNConfigSet configs) {
		if (reportedAlts != null) {
			return reportedAlts;
		}

		BitSet result = new BitSet();
		for (ATNConfig config : configs) {
			result.set(config.alt);
		}

		return result;
	}
}
