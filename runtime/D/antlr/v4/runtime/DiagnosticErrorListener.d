/*
 * Copyright (c) 2012-2019 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 *
 */

module antlr.v4.runtime.DiagnosticErrorListener;

import antlr.v4.runtime.BaseErrorListener;
import antlr.v4.runtime.InterfaceParser;
import antlr.v4.runtime.atn.ATNConfig;
import antlr.v4.runtime.atn.ATNConfigSet;
import antlr.v4.runtime.dfa.DFA;
import antlr.v4.runtime.misc.BitSet;
import antlr.v4.runtime.misc.Interval;
import std.conv;
import std.format;

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
class DiagnosticErrorListener(U, V) : BaseErrorListener!(U, V)
{

    /**
     * When {@code true}, only exactly known ambiguities are reported.
     */
    protected bool exactOnly;

    /**
     * Initializes a new instance of {@link DiagnosticErrorListener} which only
     * reports exact ambiguities.
     */
    public this()
    {
        this(true);
    }

    /**
     * Initializes a new instance of {@link DiagnosticErrorListener}, specifying
     * whether all ambiguities or only exact ambiguities are reported.
     *
     * @param exactOnly {@code true} to report only exact ambiguities, otherwise
     * {@code false} to report all ambiguities.
     */
    public this(bool exactOnly)
    {
	this.exactOnly = exactOnly;
    }

    /**
     * @uml
     * @override
     */
    public override void reportAmbiguity(InterfaceParser recognizer, DFA dfa, size_t startIndex, size_t stopIndex,
                                         bool exact, BitSet ambigAlts, ATNConfigSet configs)
    {
	if (exactOnly && !exact) {
            return;
        }

        string format_info = "reportAmbiguity d=%s: ambigAlts=%s, input='%s'";
        string decision = getDecisionDescription(recognizer, dfa);
        BitSet conflictingAlts = getConflictingAlts(ambigAlts, configs);
        string text = to!string(recognizer.getTokenStream.getText(Interval.of(to!int(startIndex), to!int(stopIndex))));
        string message = format(format_info, decision, conflictingAlts, text);
        recognizer.notifyErrorListeners(message);
    }

    /**
     * @uml
     * @override
     */
    public override void reportAttemptingFullContext(InterfaceParser recognizer, DFA dfa, size_t startIndex,
                                                     size_t stopIndex, BitSet conflictingAlts, ATNConfigSet configs)
    {
        string format_info = "reportAttemptingFullContext d=%s, input='%s'";
        string decision = getDecisionDescription(recognizer, dfa);
        string text = to!string(recognizer.getTokenStream().getText(Interval.of(to!int(startIndex), to!int(stopIndex))));
        string message = format(format_info, decision, text);
        recognizer.notifyErrorListeners(message);
    }

    /**
     * @uml
     * @override
     */
    public override void reportContextSensitivity(InterfaceParser recognizer, DFA dfa, size_t startIndex,
                                                  size_t stopIndex, int prediction, ATNConfigSet configs)
    {
        string format_info = "reportContextSensitivity d=%s, input='%s'";
        string decision = getDecisionDescription(recognizer, dfa);
        string text = to!string(recognizer.getTokenStream().getText(Interval.of(to!int(startIndex), to!int(stopIndex))));
        string message = format(format_info, decision, text);
        recognizer.notifyErrorListeners(message);
    }

    protected string getDecisionDescription(InterfaceParser recognizer, DFA dfa)
    {
        int decision = dfa.decision;
        int ruleIndex = dfa.atnStartState.ruleIndex;

        string[] ruleNames = recognizer.getRuleNames();
        if (ruleIndex < 0 || ruleIndex >= ruleNames.length) {
            return to!string(decision);
        }

        string ruleName = ruleNames[ruleIndex];
        if (ruleName is null || ruleName.length == 0) {
            return to!string(decision);
        }
        return format("%d (%s)", decision, ruleName);

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
    protected BitSet getConflictingAlts(BitSet reportedAlts, ATNConfigSet configs)
    {
        if (reportedAlts.length) {
            return reportedAlts;
        }

        BitSet* result = new BitSet();
        foreach (ATNConfig config; configs.configs) {
            result.set(config.alt, true);
        }

        return *result;
    }

}
