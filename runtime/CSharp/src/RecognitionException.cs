/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>The root of the ANTLR exception hierarchy.</summary>
    /// <remarks>
    /// The root of the ANTLR exception hierarchy. In general, ANTLR tracks just
    /// 3 kinds of errors: prediction errors, failed predicate errors, and
    /// mismatched input errors. In each case, the parser knows where it is
    /// in the input, where it is in the ATN, the rule invocation stack,
    /// and what kind of problem occurred.
    /// </remarks>
    [System.Serializable]
    public class RecognitionException : Exception
    {
        private const long serialVersionUID = -3861826954750022374L;

        /// <summary>
        /// The
        /// <see cref="IRecognizer"/>
        /// where this exception originated.
        /// </summary>
        [Nullable]
        private readonly IRecognizer recognizer;

        [Nullable]
        private readonly RuleContext ctx;

        [Nullable]
        private readonly IIntStream input;

        /// <summary>
        /// The current
        /// <see cref="IToken"/>
        /// when an error occurred. Since not all streams
        /// support accessing symbols by index, we have to track the
        /// <see cref="IToken"/>
        /// instance itself.
        /// </summary>
        private IToken offendingToken;

        private int offendingState = -1;

        public RecognitionException(Lexer lexer, ICharStream input)
        {
            this.recognizer = lexer;
            this.input = input;
            this.ctx = null;
        }

        public RecognitionException(IRecognizer recognizer, IIntStream input, ParserRuleContext ctx)
        {
            this.recognizer = recognizer;
            this.input = input;
            this.ctx = ctx;
            if (recognizer != null)
            {
                this.offendingState = recognizer.State;
            }
        }

        public RecognitionException(string message, IRecognizer recognizer, IIntStream input, ParserRuleContext ctx)
            : base(message)
        {
            this.recognizer = recognizer;
            this.input = input;
            this.ctx = ctx;
            if (recognizer != null)
            {
                this.offendingState = recognizer.State;
            }
        }

        /// <summary>
        /// Get the ATN state number the parser was in at the time the error
        /// occurred.
        /// </summary>
        /// <remarks>
        /// Get the ATN state number the parser was in at the time the error
        /// occurred. For
        /// <see cref="NoViableAltException"/>
        /// and
        /// <see cref="LexerNoViableAltException"/>
        /// exceptions, this is the
        /// <see cref="Antlr4.Runtime.Atn.DecisionState"/>
        /// number. For others, it is the state whose outgoing
        /// edge we couldn't match.
        /// <p>If the state number is not known, this method returns -1.</p>
        /// </remarks>
        public int OffendingState
        {
            get
            {
                return offendingState;
            }
            protected set
            {
                int offendingState = value;
                this.offendingState = offendingState;
            }
        }

        /// <summary>
        /// Gets the set of input symbols which could potentially follow the
        /// previously matched symbol at the time this exception was thrown.
        /// </summary>
        /// <remarks>
        /// Gets the set of input symbols which could potentially follow the
        /// previously matched symbol at the time this exception was thrown.
        /// <p>If the set of expected tokens is not known and could not be computed,
        /// this method returns
        /// <see langword="null"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// The set of token types that could potentially follow the current
        /// state in the ATN, or
        /// <see langword="null"/>
        /// if the information is not available.
        /// </returns>
        [return: Nullable]
        public virtual IntervalSet GetExpectedTokens()
        {
            if (recognizer != null)
            {
                return recognizer.Atn.GetExpectedTokens(offendingState, ctx);
            }
            return null;
        }

        /// <summary>
        /// Gets the
        /// <see cref="RuleContext"/>
        /// at the time this exception was thrown.
        /// <p>If the context is not available, this method returns
        /// <see langword="null"/>
        /// .</p>
        /// </summary>
        /// <returns>
        /// The
        /// <see cref="RuleContext"/>
        /// at the time this exception was thrown.
        /// If the context is not available, this method returns
        /// <see langword="null"/>
        /// .
        /// </returns>
        public virtual RuleContext Context
        {
            get
            {
                return ctx;
            }
        }

        /// <summary>
        /// Gets the input stream which is the symbol source for the recognizer where
        /// this exception was thrown.
        /// </summary>
        /// <remarks>
        /// Gets the input stream which is the symbol source for the recognizer where
        /// this exception was thrown.
        /// <p>If the input stream is not available, this method returns
        /// <see langword="null"/>
        /// .</p>
        /// </remarks>
        /// <returns>
        /// The input stream which is the symbol source for the recognizer
        /// where this exception was thrown, or
        /// <see langword="null"/>
        /// if the stream is not
        /// available.
        /// </returns>
        public virtual IIntStream InputStream
        {
            get
            {
                return input;
            }
        }

        public IToken OffendingToken
        {
            get
            {
                return offendingToken;
            }
            protected set
            {
                IToken offendingToken = value;
                this.offendingToken = offendingToken;
            }
        }

        /// <summary>
        /// Gets the
        /// <see cref="IRecognizer"/>
        /// where this exception occurred.
        /// <p>If the recognizer is not available, this method returns
        /// <see langword="null"/>
        /// .</p>
        /// </summary>
        /// <returns>
        /// The recognizer where this exception occurred, or
        /// <see langword="null"/>
        /// if
        /// the recognizer is not available.
        /// </returns>
        public virtual IRecognizer Recognizer
        {
            get
            {
                return recognizer;
            }
        }
    }
}
