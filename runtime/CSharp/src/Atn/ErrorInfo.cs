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
    /// This class represents profiling event information for a syntax error
    /// identified during prediction.
    /// </summary>
    /// <remarks>
    /// This class represents profiling event information for a syntax error
    /// identified during prediction. Syntax errors occur when the prediction
    /// algorithm is unable to identify an alternative which would lead to a
    /// successful parse.
    /// </remarks>
    /// <seealso cref="Parser.NotifyErrorListeners(IToken, string, RecognitionException)"/>
    /// <seealso cref="IAntlrErrorListener{TSymbol}.SyntaxError"/>
    /// <since>4.3</since>
    public class ErrorInfo : DecisionEventInfo
    {
        /// <summary>
        /// Constructs a new instance of the
        /// <see cref="ErrorInfo"/>
        /// class with the
        /// specified detailed syntax error information.
        /// </summary>
        /// <param name="decision">The decision number</param>
        /// <param name="configs">The final configuration set reached during prediction
        /// prior to reaching the {@link ATNSimulator#ERROR} state
        /// </param>
        /// <param name="input">The input token stream</param>
        /// <param name="startIndex">The start index for the current prediction</param>
        /// <param name="stopIndex">The index at which the syntax error was identified</param>
        /// <param name="fullCtx">{@code true} if the syntax error was identified during LL
        /// prediction; otherwise, {@code false} if the syntax error was identified
        /// during SLL prediction
        /// </param>
        public ErrorInfo(int decision, ATNConfigSet configs, ITokenStream input, int startIndex, int stopIndex, bool fullCtx)
            : base(decision, configs, input, startIndex, stopIndex, fullCtx)
        {
        }
    }
}
