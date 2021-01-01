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
