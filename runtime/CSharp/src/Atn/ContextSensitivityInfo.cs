/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>This class represents profiling event information for a context sensitivity.</summary>
    /// <remarks>
    /// This class represents profiling event information for a context sensitivity.
    /// Context sensitivities are decisions where a particular input resulted in an
    /// SLL conflict, but LL prediction produced a single unique alternative.
    /// <p>
    /// In some cases, the unique alternative identified by LL prediction is not
    /// equal to the minimum represented alternative in the conflicting SLL
    /// configuration set. Grammars and inputs which result in this scenario are
    /// unable to use
    /// <see cref="PredictionMode.SLL"/>
    /// , which in turn means they cannot use
    /// the two-stage parsing strategy to improve parsing performance for that
    /// input.</p>
    /// </remarks>
    /// <seealso cref="ParserATNSimulator.ReportContextSensitivity(Dfa.DFA, int, ATNConfigSet, int, int)"/>
    /// <seealso cref="Antlr4.Runtime.IParserErrorListener.ReportContextSensitivity(Antlr4.Runtime.Parser, Antlr4.Runtime.Dfa.DFA, int, int, int, SimulatorState)"/>
    /// <since>4.3</since>
    public class ContextSensitivityInfo : DecisionEventInfo
    {
        /// <summary>
        /// Constructs a new instance of the
        /// <see cref="ContextSensitivityInfo"/>
        /// class
        /// with the specified detailed context sensitivity information.
        /// </summary>
        /// <param name="decision">The decision number</param>
        /// <param name="configs">The final configuration set identifying the ambiguous
        /// alternatives for the current input
        /// </param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">
        /// The index at which the context sensitivity was
        /// identified during full-context prediction
        /// </param>
        public ContextSensitivityInfo(int decision, ATNConfigSet configs, ITokenStream input, int startIndex, int stopIndex)
            : base(decision, configs, input, startIndex, stopIndex, true)
        {
        }
    }
}
