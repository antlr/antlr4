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
    /// <see cref="PredictionMode.Sll"/>
    /// , which in turn means they cannot use
    /// the two-stage parsing strategy to improve parsing performance for that
    /// input.</p>
    /// </remarks>
    /// <seealso cref="ParserATNSimulator.ReportContextSensitivity(Antlr4.Runtime.Dfa.DFA, int, SimulatorState, int, int)"/>
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
        /// <param name="state">
        /// The final simulator state containing the unique
        /// alternative identified by full-context prediction
        /// </param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">
        /// The index at which the context sensitivity was
        /// identified during full-context prediction
        /// </param>
        public ContextSensitivityInfo(int decision, SimulatorState state, ITokenStream input, int startIndex, int stopIndex)
            : base(decision, state, input, startIndex, stopIndex, true)
        {
        }
    }
}
