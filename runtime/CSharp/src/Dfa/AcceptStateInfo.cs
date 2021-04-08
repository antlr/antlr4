/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <summary>
    /// Stores information about a
    /// <see cref="DFAState"/>
    /// which is an accept state under
    /// some condition. Certain settings, such as
    /// <see cref="Antlr4.Runtime.Atn.ParserATNSimulator.PredictionMode()"/>
    /// , may be used in addition to
    /// this information to determine whether or not a particular state is an accept
    /// state.
    /// </summary>
    /// <author>Sam Harwell</author>
    public class AcceptStateInfo
    {
        private readonly int prediction;

        private readonly Antlr4.Runtime.Atn.LexerActionExecutor lexerActionExecutor;

        public AcceptStateInfo(int prediction)
        {
            this.prediction = prediction;
            this.lexerActionExecutor = null;
        }

        public AcceptStateInfo(int prediction, Antlr4.Runtime.Atn.LexerActionExecutor lexerActionExecutor)
        {
            this.prediction = prediction;
            this.lexerActionExecutor = lexerActionExecutor;
        }

        /// <summary>Gets the prediction made by this accept state.</summary>
        /// <remarks>
        /// Gets the prediction made by this accept state. Note that this value
        /// assumes the predicates, if any, in the
        /// <see cref="DFAState"/>
        /// evaluate to
        /// <see langword="true"/>
        /// . If predicate evaluation is enabled, the final prediction of
        /// the accept state will be determined by the result of predicate
        /// evaluation.
        /// </remarks>
        public virtual int Prediction
        {
            get
            {
                return prediction;
            }
        }

        /// <summary>
        /// Gets the
        /// <see cref="Antlr4.Runtime.Atn.LexerActionExecutor"/>
        /// which can be used to execute actions
        /// and/or commands after the lexer matches a token.
        /// </summary>
        public virtual Antlr4.Runtime.Atn.LexerActionExecutor LexerActionExecutor
        {
            get
            {
                return lexerActionExecutor;
            }
        }
    }
}
