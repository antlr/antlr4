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
