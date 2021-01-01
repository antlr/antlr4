/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <author>Sam Harwell</author>
    public class SimulatorState
    {
        public readonly ParserRuleContext outerContext;

        public readonly DFAState s0;

        public readonly bool useContext;

        public readonly ParserRuleContext remainingOuterContext;

        public SimulatorState(ParserRuleContext outerContext, DFAState s0, bool useContext, ParserRuleContext remainingOuterContext)
        {
            this.outerContext = outerContext != null ? outerContext : ParserRuleContext.EmptyContext;
            this.s0 = s0;
            this.useContext = useContext;
            this.remainingOuterContext = remainingOuterContext;
        }
    }
}
