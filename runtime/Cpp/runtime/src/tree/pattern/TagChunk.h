/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Sam Harwell
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

#pragma once

#include "Chunk.h"

namespace antlr4 {
namespace tree {
namespace pattern {

  /// <summary>
  /// Represents a placeholder tag in a tree pattern. A tag can have any of the
  /// following forms.
  ///
  /// <ul>
  /// <li>{@code expr}: An unlabeled placeholder for a parser rule {@code expr}.</li>
  /// <li>{@code ID}: An unlabeled placeholder for a token of type {@code ID}.</li>
  /// <li>{@code e:expr}: A labeled placeholder for a parser rule {@code expr}.</li>
  /// <li>{@code id:ID}: A labeled placeholder for a token of type {@code ID}.</li>
  /// </ul>
  ///
  /// This class does not perform any validation on the tag or label names aside
  /// from ensuring that the tag is a non-null, non-empty string.
  /// </summary>
  class ANTLR4CPP_PUBLIC TagChunk : public Chunk {
  public:
    /// <summary>
    /// Construct a new instance of <seealso cref="TagChunk"/> using the specified tag and
    /// no label.
    /// </summary>
    /// <param name="tag"> The tag, which should be the name of a parser rule or token
    /// type.
    /// </param>
    /// <exception cref="IllegalArgumentException"> if {@code tag} is {@code null} or
    /// empty. </exception>
    TagChunk(const std::string &tag);
    virtual ~TagChunk() {};

    /// <summary>
    /// Construct a new instance of <seealso cref="TagChunk"/> using the specified label
    /// and tag.
    /// </summary>
    /// <param name="label"> The label for the tag. If this is {@code null}, the
    /// <seealso cref="TagChunk"/> represents an unlabeled tag. </param>
    /// <param name="tag"> The tag, which should be the name of a parser rule or token
    /// type.
    /// </param>
    /// <exception cref="IllegalArgumentException"> if {@code tag} is {@code null} or
    /// empty. </exception>
    TagChunk(const std::string &label, const std::string &tag);

    /// <summary>
    /// Get the tag for this chunk.
    /// </summary>
    /// <returns> The tag for the chunk. </returns>
    std::string getTag();

    /// <summary>
    /// Get the label, if any, assigned to this chunk.
    /// </summary>
    /// <returns> The label assigned to this chunk, or {@code null} if no label is
    /// assigned to the chunk. </returns>
    std::string getLabel();

    /// <summary>
    /// This method returns a text representation of the tag chunk. Labeled tags
    /// are returned in the form {@code label:tag}, and unlabeled tags are
    /// returned as just the tag name.
    /// </summary>
    virtual std::string toString();

  private:
    /// This is the backing field for <seealso cref="#getTag"/>.
    const std::string _tag;
    /// <summary>
    /// This is the backing field for <seealso cref="#getLabe"/>.
    /// </summary>
    const std::string _label;
  };

} // namespace pattern
} // namespace tree
} // namespace antlr4
