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
    /// <see cref="ATNConfig.ReachesIntoOuterContext()"/>
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
    /// <see cref="PredictionMode.Sll"/>
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
