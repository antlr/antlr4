/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>This class represents profiling event information for an ambiguity.</summary>
    /// <remarks>
    /// This class represents profiling event information for an ambiguity.
    /// Ambiguities are decisions where a particular input resulted in an SLL
    /// conflict, followed by LL prediction also reaching a conflict state
    /// (indicating a true ambiguity in the grammar).
    /// <p>
    /// This event may be reported during SLL prediction in cases where the
    /// conflicting SLL configuration set provides sufficient information to
    /// determine that the SLL conflict is truly an ambiguity. For example, if none
    /// of the ATN configurations in the conflicting SLL configuration set have
    /// traversed a global follow transition (i.e.
    /// <see cref="ATNConfig.reachesIntoOuterContext"/>
    /// is
    /// <see langword="false"/>
    /// for all
    /// configurations), then the result of SLL prediction for that input is known to
    /// be equivalent to the result of LL prediction for that input.</p>
    /// <p>
    /// In some cases, the minimum represented alternative in the conflicting LL
    /// configuration set is not equal to the minimum represented alternative in the
    /// conflicting SLL configuration set. Grammars and inputs which result in this
    /// scenario are unable to use
    /// <see cref="PredictionMode.SLL"/>
    /// , which in turn means
    /// they cannot use the two-stage parsing strategy to improve parsing performance
    /// for that input.</p>
    /// </remarks>
    /// <seealso cref="ParserATNSimulator.ReportAmbiguity(Antlr4.Runtime.Dfa.DFA, Antlr4.Runtime.Dfa.DFAState, int, int, bool, Antlr4.Runtime.Sharpen.BitSet, ATNConfigSet)"/>
    /// <seealso cref="Antlr4.Runtime.IParserErrorListener.ReportAmbiguity(Antlr4.Runtime.Parser, Antlr4.Runtime.Dfa.DFA, int, int, bool, Antlr4.Runtime.Sharpen.BitSet, ATNConfigSet)"/>
    /// <since>4.3</since>
    public class AmbiguityInfo : DecisionEventInfo
    {
        /// <summary>
        /// Constructs a new instance of the
        /// <see cref="AmbiguityInfo"/>
        /// class with the
        /// specified detailed ambiguity information.
        /// </summary>
        /// <param name="decision">The decision number</param>
        /// <param name="state">
        /// The final simulator state identifying the ambiguous
        /// alternatives for the current input
        /// </param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">
        /// The index at which the ambiguity was identified during
        /// prediction
        /// </param>
        public AmbiguityInfo(int decision, SimulatorState state, ITokenStream input, int startIndex, int stopIndex)
            : base(decision, state, input, startIndex, stopIndex, state.useContext)
        {
        }
    }
}
