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
    /// <summary>Represents a placeholder tag in a tree pattern.</summary>
    /// <remarks>
    /// Represents a placeholder tag in a tree pattern. A tag can have any of the
    /// following forms.
    /// <ul>
    /// <li>
    /// <c>expr</c>
    /// : An unlabeled placeholder for a parser rule
    /// <c>expr</c>
    /// .</li>
    /// <li>
    /// <c>ID</c>
    /// : An unlabeled placeholder for a token of type
    /// <c>ID</c>
    /// .</li>
    /// <li>
    /// <c>e:expr</c>
    /// : A labeled placeholder for a parser rule
    /// <c>expr</c>
    /// .</li>
    /// <li>
    /// <c>id:ID</c>
    /// : A labeled placeholder for a token of type
    /// <c>ID</c>
    /// .</li>
    /// </ul>
    /// This class does not perform any validation on the tag or label names aside
    /// from ensuring that the tag is a non-null, non-empty string.
    /// </remarks>
    internal class TagChunk : Chunk
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="Tag()"/>
        /// .
        /// </summary>
        private readonly string tag;

        /// <summary>
        /// This is the backing field for
        /// <see cref="Label()"/>
        /// .
        /// </summary>
        private readonly string label;

        /// <summary>
        /// Construct a new instance of
        /// <see cref="TagChunk"/>
        /// using the specified tag and
        /// no label.
        /// </summary>
        /// <param name="tag">
        /// The tag, which should be the name of a parser rule or token
        /// type.
        /// </param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="tag"/>
        /// is
        /// <see langword="null"/>
        /// or
        /// empty.
        /// </exception>
        public TagChunk(string tag)
            : this(null, tag)
        {
        }

        /// <summary>
        /// Construct a new instance of
        /// <see cref="TagChunk"/>
        /// using the specified label
        /// and tag.
        /// </summary>
        /// <param name="label">
        /// The label for the tag. If this is
        /// <see langword="null"/>
        /// , the
        /// <see cref="TagChunk"/>
        /// represents an unlabeled tag.
        /// </param>
        /// <param name="tag">
        /// The tag, which should be the name of a parser rule or token
        /// type.
        /// </param>
        /// <exception>
        /// IllegalArgumentException
        /// if
        /// <paramref name="tag"/>
        /// is
        /// <see langword="null"/>
        /// or
        /// empty.
        /// </exception>
        public TagChunk(string label, string tag)
        {
            if (string.IsNullOrEmpty(tag))
            {
                throw new ArgumentException("tag cannot be null or empty");
            }
            this.label = label;
            this.tag = tag;
        }

        /// <summary>Get the tag for this chunk.</summary>
        /// <remarks>Get the tag for this chunk.</remarks>
        /// <returns>The tag for the chunk.</returns>
        [NotNull]
        public string Tag
        {
            get
            {
                return tag;
            }
        }

        /// <summary>Get the label, if any, assigned to this chunk.</summary>
        /// <remarks>Get the label, if any, assigned to this chunk.</remarks>
        /// <returns>
        /// The label assigned to this chunk, or
        /// <see langword="null"/>
        /// if no label is
        /// assigned to the chunk.
        /// </returns>
        [Nullable]
        public string Label
        {
            get
            {
                return label;
            }
        }

        /// <summary>This method returns a text representation of the tag chunk.</summary>
        /// <remarks>
        /// This method returns a text representation of the tag chunk. Labeled tags
        /// are returned in the form
        /// <c>label:tag</c>
        /// , and unlabeled tags are
        /// returned as just the tag name.
        /// </remarks>
        public override string ToString()
        {
            if (label != null)
            {
                return label + ":" + tag;
            }
            return tag;
        }
    }
}
