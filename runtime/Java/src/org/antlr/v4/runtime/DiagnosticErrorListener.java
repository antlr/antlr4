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
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.util.BitSet;

public class DiagnosticErrorListener extends BaseErrorListener {
    @Override
    public void reportAmbiguity(@NotNull Parser recognizer,
								DFA dfa, int startIndex, int stopIndex,
								boolean exact,
								@Nullable BitSet ambigAlts,
								@NotNull ATNConfigSet configs)
    {
		if (!exact) {
			return;
		}

		recognizer.notifyErrorListeners("reportAmbiguity d=" + dfa.decision +
			": ambigAlts=" + getConflictingAlts(ambigAlts, configs) + ", input='" +
			recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)) + "'");
    }

	@Override
	public void reportAttemptingFullContext(@NotNull Parser recognizer,
											@NotNull DFA dfa,
											int startIndex, int stopIndex,
											@Nullable BitSet conflictingAlts,
											@NotNull ATNConfigSet configs)
	{
		recognizer.notifyErrorListeners("reportAttemptingFullContext d=" +
			dfa.decision + ", input='" +
			recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)) + "'");
	}

	@Override
	public void reportContextSensitivity(@NotNull Parser recognizer,
										 @NotNull DFA dfa,
                                         int startIndex, int stopIndex,
										 int prediction,
										 @NotNull ATNConfigSet configs)
    {
        recognizer.notifyErrorListeners("reportContextSensitivity d=" +
			dfa.decision + ", input='" +
			recognizer.getTokenStream().getText(Interval.of(startIndex, stopIndex)) + "'");
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
	@NotNull
	protected BitSet getConflictingAlts(@Nullable BitSet reportedAlts, @NotNull ATNConfigSet configs) {
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
