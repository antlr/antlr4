/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <author>Sam Harwell</author>
    public class ProxyParserErrorListener : ProxyErrorListener<IToken>, IParserErrorListener
    {
        public ProxyParserErrorListener(ICollection<IAntlrErrorListener<IToken>> delegates)
            : base(delegates)
        {
        }

        public virtual void ReportAmbiguity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, bool exact, BitSet ambigAlts, ATNConfigSet configs)
        {
            foreach (IAntlrErrorListener<IToken> listener in Delegates)
            {
                if (!(listener is IParserErrorListener))
                {
                    continue;
                }
                IParserErrorListener parserErrorListener = (IParserErrorListener)listener;
                parserErrorListener.ReportAmbiguity(recognizer, dfa, startIndex, stopIndex, exact, ambigAlts, configs);
            }
        }

        public virtual void ReportAttemptingFullContext(Parser recognizer, DFA dfa, int startIndex, int stopIndex, BitSet conflictingAlts, SimulatorState conflictState)
        {
            foreach (IAntlrErrorListener<IToken> listener in Delegates)
            {
                if (!(listener is IParserErrorListener))
                {
                    continue;
                }
                IParserErrorListener parserErrorListener = (IParserErrorListener)listener;
                parserErrorListener.ReportAttemptingFullContext(recognizer, dfa, startIndex, stopIndex, conflictingAlts, conflictState);
            }
        }

        public virtual void ReportContextSensitivity(Parser recognizer, DFA dfa, int startIndex, int stopIndex, int prediction, SimulatorState acceptState)
        {
            foreach (IAntlrErrorListener<IToken> listener in Delegates)
            {
                if (!(listener is IParserErrorListener))
                {
                    continue;
                }
                IParserErrorListener parserErrorListener = (IParserErrorListener)listener;
                parserErrorListener.ReportContextSensitivity(recognizer, dfa, startIndex, stopIndex, prediction, acceptState);
            }
        }
    }
}
