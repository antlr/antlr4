/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
