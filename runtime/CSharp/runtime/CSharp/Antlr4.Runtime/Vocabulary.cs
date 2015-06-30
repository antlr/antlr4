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

namespace Antlr4.Runtime
{
    /// <summary>
    /// This class provides a default implementation of the
    /// <see cref="IVocabulary"/>
    /// interface.
    /// </summary>
    /// <author>Sam Harwell</author>
    public class Vocabulary : IVocabulary
    {
        private static readonly string[] EmptyNames = new string[0];

        /// <summary>
        /// Gets an empty
        /// <see cref="IVocabulary"/>
        /// instance.
        /// <p>
        /// No literal or symbol names are assigned to token types, so
        /// <see cref="GetDisplayName(int)"/>
        /// returns the numeric value for all tokens
        /// except
        /// <see cref="TokenConstants.Eof"/>
        /// .</p>
        /// </summary>
        [NotNull]
        public static readonly Vocabulary EmptyVocabulary = new Vocabulary(EmptyNames, EmptyNames, EmptyNames);

        [NotNull]
        private readonly string[] literalNames;

        [NotNull]
        private readonly string[] symbolicNames;

        [NotNull]
        private readonly string[] displayNames;

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="Vocabulary"/>
        /// from the specified
        /// literal and symbolic token names.
        /// </summary>
        /// <param name="literalNames">
        /// The literal names assigned to tokens, or
        /// <see langword="null"/>
        /// if no literal names are assigned.
        /// </param>
        /// <param name="symbolicNames">
        /// The symbolic names assigned to tokens, or
        /// <see langword="null"/>
        /// if no symbolic names are assigned.
        /// </param>
        /// <seealso cref="GetLiteralName(int)"/>
        /// <seealso cref="GetSymbolicName(int)"/>
        public Vocabulary(string[] literalNames, string[] symbolicNames)
            : this(literalNames, symbolicNames, null)
        {
        }

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="Vocabulary"/>
        /// from the specified
        /// literal, symbolic, and display token names.
        /// </summary>
        /// <param name="literalNames">
        /// The literal names assigned to tokens, or
        /// <see langword="null"/>
        /// if no literal names are assigned.
        /// </param>
        /// <param name="symbolicNames">
        /// The symbolic names assigned to tokens, or
        /// <see langword="null"/>
        /// if no symbolic names are assigned.
        /// </param>
        /// <param name="displayNames">
        /// The display names assigned to tokens, or
        /// <see langword="null"/>
        /// to use the values in
        /// <paramref name="literalNames"/>
        /// and
        /// <paramref name="symbolicNames"/>
        /// as
        /// the source of display names, as described in
        /// <see cref="GetDisplayName(int)"/>
        /// .
        /// </param>
        /// <seealso cref="GetLiteralName(int)"/>
        /// <seealso cref="GetSymbolicName(int)"/>
        /// <seealso cref="GetDisplayName(int)"/>
        public Vocabulary(string[] literalNames, string[] symbolicNames, string[] displayNames)
        {
            this.literalNames = literalNames != null ? literalNames : EmptyNames;
            this.symbolicNames = symbolicNames != null ? symbolicNames : EmptyNames;
            this.displayNames = displayNames != null ? displayNames : EmptyNames;
        }

        [return: Nullable]
        public virtual string GetLiteralName(int tokenType)
        {
            if (tokenType >= 0 && tokenType < literalNames.Length)
            {
                return literalNames[tokenType];
            }
            return null;
        }

        [return: Nullable]
        public virtual string GetSymbolicName(int tokenType)
        {
            if (tokenType >= 0 && tokenType < symbolicNames.Length)
            {
                return symbolicNames[tokenType];
            }
            if (tokenType == TokenConstants.Eof)
            {
                return "EOF";
            }
            return null;
        }

        [return: NotNull]
        public virtual string GetDisplayName(int tokenType)
        {
            if (tokenType >= 0 && tokenType < displayNames.Length)
            {
                string displayName = displayNames[tokenType];
                if (displayName != null)
                {
                    return displayName;
                }
            }
            string literalName = GetLiteralName(tokenType);
            if (literalName != null)
            {
                return literalName;
            }
            string symbolicName = GetSymbolicName(tokenType);
            if (symbolicName != null)
            {
                return symbolicName;
            }
            return tokenType.ToString();
        }
    }
}
