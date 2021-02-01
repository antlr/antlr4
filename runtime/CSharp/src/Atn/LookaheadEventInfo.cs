/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// This class represents profiling event information for tracking the lookahead
    /// depth required in order to make a prediction.
    /// </summary>
    /// <remarks>
    /// This class represents profiling event information for tracking the lookahead
    /// depth required in order to make a prediction.
    /// </remarks>
    /// <since>4.3</since>
    public class LookaheadEventInfo : DecisionEventInfo
    {
        /// <summary>The alternative chosen by adaptivePredict(), not necessarily
        ///  the outermost alt shown for a rule; left-recursive rules have
        ///  user-level alts that differ from the rewritten rule with a (...) block
        ///  and a (..)* loop.
        /// </summary>
        public int predictedAlt;

        /// <summary>
        /// Constructs a new instance of the
        /// <see cref="LookaheadEventInfo"/>
        /// class with
        /// the specified detailed lookahead information.
        /// </summary>
        /// <param name="decision">The decision number</param>
        /// <param name="configs">The final configuration set containing the necessary
        /// information to determine the result of a prediction, or {@code null} if
        /// the final configuration set is not available
        /// </param>
        /// <param name="predictedAlt"></param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">The index at which the prediction was finally made</param>
        /// <param name="fullCtx">
        /// <see langword="true"/>
        /// if the current lookahead is part of an LL
        /// prediction; otherwise,
        /// <see langword="false"/>
        /// if the current lookahead is part of
        /// an SLL prediction
        /// </param>
        public LookaheadEventInfo(int decision, ATNConfigSet configs, int predictedAlt, ITokenStream input, int startIndex, int stopIndex, bool fullCtx)
            : base(decision, configs, input, startIndex, stopIndex, fullCtx)
        {
            this.predictedAlt = predictedAlt;
        }
    }
}
