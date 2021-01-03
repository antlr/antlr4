/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree.Pattern;

namespace Antlr4.Runtime.Tree.Pattern
{
    /// <summary>
    /// Represents a span of raw text (concrete syntax) between tags in a tree
    /// pattern string.
    /// </summary>
    /// <remarks>
    /// Represents a span of raw text (concrete syntax) between tags in a tree
    /// pattern string.
    /// </remarks>
    internal class TextChunk : Chunk
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="Text()"/>
        /// .
        /// </summary>
        [NotNull]
        private readonly string text;

        /// <summary>
        /// Constructs a new instance of
        /// <see cref="TextChunk"/>
        /// with the specified text.
        /// </summary>
        /// <param name="text">The text of this chunk.</param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="text"/>
        /// is
        /// <see langword="null"/>
        /// .
        /// </exception>
        public TextChunk(string text)
        {
            if (text == null)
            {
                throw new ArgumentException("text cannot be null");
            }
            this.text = text;
        }

        /// <summary>Gets the raw text of this chunk.</summary>
        /// <remarks>Gets the raw text of this chunk.</remarks>
        /// <returns>The text of the chunk.</returns>
        [NotNull]
        public string Text
        {
            get
            {
                return text;
            }
        }

        /// <summary>
        /// <inheritDoc/>
        /// <p>The implementation for
        /// <see cref="TextChunk"/>
        /// returns the result of
        /// <see cref="Text()"/>
        /// in single quotes.</p>
        /// </summary>
        public override string ToString()
        {
            return "'" + text + "'";
        }
    }
}
