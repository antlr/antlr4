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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// This is the base class for gathering detailed information about prediction
    /// events which occur during parsing.
    /// </summary>
    /// <remarks>
    /// This is the base class for gathering detailed information about prediction
    /// events which occur during parsing.
    /// </remarks>
    /// <since>4.3</since>
    public class DecisionEventInfo
    {
        /// <summary>The invoked decision number which this event is related to.</summary>
        /// <remarks>The invoked decision number which this event is related to.</remarks>
        /// <seealso cref="ATN.decisionToState"/>
        public readonly int decision;

        /// <summary>
        /// The simulator state containing additional information relevant to the
        /// prediction state when the current event occurred, or
        /// <see langword="null"/>
        /// if no
        /// additional information is relevant or available.
        /// </summary>
        [Nullable]
        public readonly SimulatorState state;

        /// <summary>The input token stream which is being parsed.</summary>
        /// <remarks>The input token stream which is being parsed.</remarks>
        [NotNull]
        public readonly ITokenStream input;

        /// <summary>
        /// The token index in the input stream at which the current prediction was
        /// originally invoked.
        /// </summary>
        /// <remarks>
        /// The token index in the input stream at which the current prediction was
        /// originally invoked.
        /// </remarks>
        public readonly int startIndex;

        /// <summary>The token index in the input stream at which the current event occurred.</summary>
        /// <remarks>The token index in the input stream at which the current event occurred.</remarks>
        public readonly int stopIndex;

        /// <summary>
        /// <see langword="true"/>
        /// if the current event occurred during LL prediction;
        /// otherwise,
        /// <see langword="false"/>
        /// if the input occurred during SLL prediction.
        /// </summary>
        public readonly bool fullCtx;

        public DecisionEventInfo(int decision, SimulatorState state, ITokenStream input, int startIndex, int stopIndex, bool fullCtx)
        {
            this.decision = decision;
            this.fullCtx = fullCtx;
            this.stopIndex = stopIndex;
            this.input = input;
            this.startIndex = startIndex;
            this.state = state;
        }
    }
}
