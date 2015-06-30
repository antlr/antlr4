/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
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
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This implementation of
    /// <see cref="IAntlrErrorListener{Symbol}"/>
    /// can be used to identify
    /// certain potential correctness and performance problems in grammars. "Reports"
    /// are made by calling
    /// <see cref="Parser.NotifyErrorListeners(string)"/>
    /// with the appropriate
    /// message.
    /// <ul>
    /// <li><b>Ambiguities</b>: These are cases where more than one path through the
    /// grammar can match the input.</li>
    /// <li><b>Weak context sensitivity</b>: These are cases where full-context
    /// prediction resolved an SLL conflict to a unique alternative which equaled the
    /// minimum alternative of the SLL conflict.</li>
    /// <li><b>Strong (forced) context sensitivity</b>: These are cases where the
    /// full-context prediction resolved an SLL conflict to a unique alternative,
    /// <em>and</em> the minimum alternative of the SLL conflict was found to not be
    /// a truly viable alternative. Two-stage parsing cannot be used for inputs where
    /// this situation occurs.</li>
    /// </ul>
    /// </summary>
    /// <author>Sam Harwell</author>
    public class DiagnosticErrorListener : BaseErrorListener
    {
        /// <summary>
        /// When
        /// <see langword="true"/>
        /// , only exactly known ambiguities are reported.
        /// </summary>
        protected internal readonly bool exactOnly;

        /// <summary>
        /// Initializes a new instance of
        /// <see cref="DiagnosticErrorListener"/>
        /// which only
        /// reports exact ambiguities.
        /// </summary>
        public DiagnosticErrorListener()
            : this(true)
        {
        }

        /// <summary>
        /// Initializes a new instance of
        /// <see cref="DiagnosticErrorListener"/>
        /// , specifying
        /// whether all ambiguities or only exact ambiguities are reported.
        /// </summary>
        /// <param name="exactOnly">
        /// 
        /// <see langword="true"/>
        /// to report only exact ambiguities, otherwise
        /// <see langword="false"/>
        /// to report all ambiguities.
        /// </param>
        public DiagnosticErrorListener(bool exactOnly)
        {
            this.exactOnly = exactOnly;
        }

        public override void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
        {
            if (exactOnly && !exact)
            {
                return;
            }
            string format = "reportAmbiguity d={0}: ambigAlts={1}, input='{2}'";
            string decision = GetDecisionDescription(recognizer, dfa);
            BitSet conflictingAlts = GetConflictingAlts(ambigAlts, configs);
            string text = ((ITokenStream)recognizer.InputStream).GetText(Interval.Of(startIndex, stopIndex));
            string message = string.Format(format, decision, conflictingAlts, text);
            recognizer.NotifyErrorListeners(message);
        }

        public override void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, SimulatorState conflictState)
        {
            string format = "reportAttemptingFullContext d={0}, input='{1}'";
            string decision = GetDecisionDescription(recognizer, dfa);
            string text = ((ITokenStream)recognizer.InputStream).GetText(Interval.Of(startIndex, stopIndex));
            string message = string.Format(format, decision, text);
            recognizer.NotifyErrorListeners(message);
        }

        public override void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, SimulatorState acceptState)
        {
            string format = "reportContextSensitivity d={0}, input='{1}'";
            string decision = GetDecisionDescription(recognizer, dfa);
            string text = ((ITokenStream)recognizer.InputStream).GetText(Interval.Of(startIndex, stopIndex));
            string message = string.Format(format, decision, text);
            recognizer.NotifyErrorListeners(message);
        }

        protected internal virtual string GetDecisionDescription(Parser recognizer, DFA dfa)
        {
            int decision = dfa.decision;
            int ruleIndex = dfa.atnStartState.ruleIndex;
            string[] ruleNames = recognizer.RuleNames;
            if (ruleIndex < 0 || ruleIndex >= ruleNames.Length)
            {
                return decision.ToString();
            }
            string ruleName = ruleNames[ruleIndex];
            if (string.IsNullOrEmpty(ruleName))
            {
                return decision.ToString();
            }
            return string.Format("{0} ({1})", decision, ruleName);
        }

        /// <summary>
        /// Computes the set of conflicting or ambiguous alternatives from a
        /// configuration set, if that information was not already provided by the
        /// parser.
        /// </summary>
        /// <remarks>
        /// Computes the set of conflicting or ambiguous alternatives from a
        /// configuration set, if that information was not already provided by the
        /// parser.
        /// </remarks>
        /// <param name="reportedAlts">
        /// The set of conflicting or ambiguous alternatives, as
        /// reported by the parser.
        /// </param>
        /// <param name="configs">The conflicting or ambiguous configuration set.</param>
        /// <returns>
        /// Returns
        /// <paramref name="reportedAlts"/>
        /// if it is not
        /// <see langword="null"/>
        /// , otherwise
        /// returns the set of alternatives represented in
        /// <paramref name="configs"/>
        /// .
        /// </returns>
        [return: NotNull]
        protected internal virtual BitSet GetConflictingAlts(BitSet reportedAlts, ATNConfigSet configs)
        {
            if (reportedAlts != null)
            {
                return reportedAlts;
            }
            BitSet result = new BitSet();
            foreach (ATNConfig config in configs)
            {
                result.Set(config.Alt);
            }
            return result;
        }
    }
}
