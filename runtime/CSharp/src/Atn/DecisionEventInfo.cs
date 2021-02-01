/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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

        /// <summary>The configuration set containing additional information relevant to the
        /// prediction state when the current event occurred, or {@code null} if no
        /// additional information is relevant or available.</summary>
        /// <remarks>The configuration set containing additional information relevant to the
        /// prediction state when the current event occurred, or {@code null} if no
        /// additional information is relevant or available.</remarks>
        public readonly ATNConfigSet configs;

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

        public DecisionEventInfo(int decision,
            ATNConfigSet configs,
            ITokenStream input, int startIndex, int stopIndex,
            bool fullCtx)
        {
            this.decision = decision;
            this.fullCtx = fullCtx;
            this.stopIndex = stopIndex;
            this.input = input;
            this.startIndex = startIndex;
            this.configs = configs;
        }
    }
}
