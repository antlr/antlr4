/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;
using System.IO;

namespace Antlr4.Runtime
{
    /// <summary>
    /// Provides an empty default implementation of
    /// <see cref="IAntlrErrorListener{Symbol}"/>
    /// . The
    /// default implementation of each method does nothing, but can be overridden as
    /// necessary.
    /// </summary>
    /// <author>Sam Harwell</author>
    public class BaseErrorListener : IParserErrorListener
    {
        public virtual void SyntaxError(TextWriter output, IRecognizer recognizer, IToken offendingSymbol, int line, int charPositionInLine, string msg, RecognitionException e)
        {
        }

        public virtual void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
        {
        }

        public virtual void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, SimulatorState conflictState)
        {
        }

        public virtual void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, SimulatorState acceptState)
        {
        }
    }
}
