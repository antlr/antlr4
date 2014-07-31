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

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// A
    /// <see cref="Antlr4.Runtime.IToken"/>
    /// object representing an entire subtree matched by a parser
    /// rule; e.g.,
    /// <c>&lt;expr&gt;</c>
    /// . These tokens are created for
    /// <see cref="TagChunk"/>
    /// chunks where the tag corresponds to a parser rule.
    /// </summary>
    public class RuleTagToken : IToken
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="RuleName()"/>
        /// .
        /// </summary>
        private readonly string ruleName;

        /// <summary>The token type for the current token.</summary>
        /// <remarks>
        /// The token type for the current token. This is the token type assigned to
        /// the bypass alternative for the rule during ATN deserialization.
        /// </remarks>
        private readonly int bypassTokenType;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Label()"/>
        /// .
        /// </summary>
        private readonly string label;

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="RuleTagToken"/>
        /// with the specified rule
        /// name and bypass token type and no label.
        /// </summary>
        /// <param name="ruleName">The name of the parser rule this rule tag matches.</param>
        /// <param name="bypassTokenType">The bypass token type assigned to the parser rule.</param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="ruleName"/>
        /// is
        /// <see langword="null"/>
        /// or empty.
        /// </exception>
        public RuleTagToken(string ruleName, int bypassTokenType)
            : this(ruleName, bypassTokenType, null)
        {
        }

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="RuleTagToken"/>
        /// with the specified rule
        /// name, bypass token type, and label.
        /// </summary>
        /// <param name="ruleName">The name of the parser rule this rule tag matches.</param>
        /// <param name="bypassTokenType">The bypass token type assigned to the parser rule.</param>
        /// <param name="label">
        /// The label associated with the rule tag, or
        /// <see langword="null"/>
        /// if
        /// the rule tag is unlabeled.
        /// </param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="ruleName"/>
        /// is
        /// <see langword="null"/>
        /// or empty.
        /// </exception>
        public RuleTagToken(string ruleName, int bypassTokenType, string label)
        {
            if (string.IsNullOrEmpty(ruleName))
            {
                throw new ArgumentException("ruleName cannot be null or empty.");
            }
            this.ruleName = ruleName;
            this.bypassTokenType = bypassTokenType;
            this.label = label;
        }

        /// <summary>Gets the name of the rule associated with this rule tag.</summary>
        /// <remarks>Gets the name of the rule associated with this rule tag.</remarks>
        /// <returns>The name of the parser rule associated with this rule tag.</returns>
        [NotNull]
        public string RuleName
        {
            get
            {
                return ruleName;
            }
        }

        /// <summary>Gets the label associated with the rule tag.</summary>
        /// <remarks>Gets the label associated with the rule tag.</remarks>
        /// <returns>
        /// The name of the label associated with the rule tag, or
        /// <see langword="null"/>
        /// if this is an unlabeled rule tag.
        /// </returns>
        [Nullable]
        public string Label
        {
            get
            {
                return label;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>Rule tag tokens are always placed on the
        /// <see cref="TokenConstants.DefaultChannel"/>
        /// .</p>
        /// </summary>
        public virtual int Channel
        {
            get
            {
                return TokenConstants.DefaultChannel;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>This method returns the rule tag formatted with
        /// <c>&lt;</c>
        /// and
        /// <c>&gt;</c>
        /// delimiters.</p>
        /// </summary>
        public virtual string Text
        {
            get
            {
                if (label != null)
                {
                    return "<" + label + ":" + ruleName + ">";
                }
                return "<" + ruleName + ">";
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>Rule tag tokens have types assigned according to the rule bypass
        /// transitions created during ATN deserialization.</p>
        /// </summary>
        public virtual int Type
        {
            get
            {
                return bypassTokenType;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns 0.</p>
        /// </summary>
        public virtual int Line
        {
            get
            {
                return 0;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns -1.</p>
        /// </summary>
        public virtual int Column
        {
            get
            {
                return -1;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns -1.</p>
        /// </summary>
        public virtual int TokenIndex
        {
            get
            {
                return -1;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns -1.</p>
        /// </summary>
        public virtual int StartIndex
        {
            get
            {
                return -1;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns -1.</p>
        /// </summary>
        public virtual int StopIndex
        {
            get
            {
                return -1;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns
        /// <see langword="null"/>
        /// .</p>
        /// </summary>
        public virtual ITokenSource TokenSource
        {
            get
            {
                return null;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// always returns
        /// <see langword="null"/>
        /// .</p>
        /// </summary>
        public virtual ICharStream InputStream
        {
            get
            {
                return null;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="RuleTagToken"/>
        /// returns a string of the form
        /// <c>ruleName:bypassTokenType</c>
        /// .</p>
        /// </summary>
        public override string ToString()
        {
            return ruleName + ":" + bypassTokenType;
        }
    }
}
