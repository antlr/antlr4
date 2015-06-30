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
