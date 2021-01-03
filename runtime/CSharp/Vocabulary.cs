/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
        /// <see cref="TokenConstants.EOF"/>
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

        private readonly int maxTokenType;

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
			this.maxTokenType =
				System.Math.Max(this.displayNames.Length,
						 System.Math.Max(this.literalNames.Length, this.symbolicNames.Length)) - 1;

        }

		/// <summary>
		/// Returns the highest token type value. It can be used to iterate from
		/// zero to that number, inclusively, thus querying all stored entries.
		/// </summary>
        public virtual int getMaxTokenType()
        {
        	return maxTokenType;
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
            if (tokenType == TokenConstants.EOF)
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
