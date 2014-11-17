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
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// A
    /// <see cref="Antlr4.Runtime.IToken"/>
    /// object representing a token of a particular type; e.g.,
    /// <c>&lt;ID&gt;</c>
    /// . These tokens are created for
    /// <see cref="TagChunk"/>
    /// chunks where the
    /// tag corresponds to a lexer rule or token type.
    /// </summary>
    [System.Serializable]
    public class TokenTagToken : CommonToken
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="TokenName()"/>
        /// .
        /// </summary>
        [NotNull]
        private readonly string tokenName;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Label()"/>
        /// .
        /// </summary>
        [Nullable]
        private readonly string label;

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="TokenTagToken"/>
        /// for an unlabeled tag
        /// with the specified token name and type.
        /// </summary>
        /// <param name="tokenName">The token name.</param>
        /// <param name="type">The token type.</param>
        public TokenTagToken(string tokenName, int type)
            : this(tokenName, type, null)
        {
        }

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="TokenTagToken"/>
        /// with the specified
        /// token name, type, and label.
        /// </summary>
        /// <param name="tokenName">The token name.</param>
        /// <param name="type">The token type.</param>
        /// <param name="label">
        /// The label associated with the token tag, or
        /// <see langword="null"/>
        /// if
        /// the token tag is unlabeled.
        /// </param>
        public TokenTagToken(string tokenName, int type, string label)
            : base(type)
        {
            this.tokenName = tokenName;
            this.label = label;
        }

        /// <summary>Gets the token name.</summary>
        /// <remarks>Gets the token name.</remarks>
        /// <returns>The token name.</returns>
        [NotNull]
        public string TokenName
        {
            get
            {
                return tokenName;
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
        /// <p>The implementation for
        /// <see cref="TokenTagToken"/>
        /// returns the token tag
        /// formatted with
        /// <c>&lt;</c>
        /// and
        /// <c>&gt;</c>
        /// delimiters.</p>
        /// </summary>
        public override string Text
        {
            get
            {
                if (label != null)
                {
                    return "<" + label + ":" + tokenName + ">";
                }
                return "<" + tokenName + ">";
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="TokenTagToken"/>
        /// returns a string of the form
        /// <c>tokenName:type</c>
        /// .</p>
        /// </summary>
        public override string ToString()
        {
            return tokenName + ":" + Type;
        }
    }
}
