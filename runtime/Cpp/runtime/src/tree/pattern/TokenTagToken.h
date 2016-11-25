/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 * Copyright (c) 2013 Terence Parr
 * Copyright (c) 2013 Dan McLaughlin
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

#include "CommonToken.h"

namespace antlr4 {
namespace tree {
namespace pattern {

  /// <summary>
  /// A <seealso cref="Token"/> object representing a token of a particular type; e.g.,
  /// {@code <ID>}. These tokens are created for <seealso cref="TagChunk"/> chunks where the
  /// tag corresponds to a lexer rule or token type.
  /// </summary>
  class ANTLR4CPP_PUBLIC TokenTagToken : public CommonToken {
    /// <summary>
    /// This is the backing field for <seealso cref="#getTokenName"/>.
    /// </summary>
  private:
    const std::string tokenName;
    /// <summary>
    /// This is the backing field for <seealso cref="#getLabe"/>.
    /// </summary>
    const std::string label;

    /// <summary>
    /// Constructs a new instance of <seealso cref="TokenTagToken"/> for an unlabeled tag
    /// with the specified token name and type.
    /// </summary>
    /// <param name="tokenName"> The token name. </param>
    /// <param name="type"> The token type. </param>
  public:
    TokenTagToken(const std::string &tokenName, int type); //this(tokenName, type, nullptr);

    /// <summary>
    /// Constructs a new instance of <seealso cref="TokenTagToken"/> with the specified
    /// token name, type, and label.
    /// </summary>
    /// <param name="tokenName"> The token name. </param>
    /// <param name="type"> The token type. </param>
    /// <param name="label"> The label associated with the token tag, or {@code null} if
    /// the token tag is unlabeled. </param>
    TokenTagToken(const std::string &tokenName, int type, const std::string &label);

    /// <summary>
    /// Gets the token name. </summary>
    /// <returns> The token name. </returns>
    std::string getTokenName() const;

    /// <summary>
    /// Gets the label associated with the rule tag.
    /// </summary>
    /// <returns> The name of the label associated with the rule tag, or
    /// {@code null} if this is an unlabeled rule tag. </returns>
    std::string getLabel() const;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="TokenTagToken"/> returns the token tag
    /// formatted with {@code <} and {@code >} delimiters.
    /// </summary>
    virtual std::string getText() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="TokenTagToken"/> returns a string of the form
    /// {@code tokenName:type}.
    /// </summary>
    virtual std::string toString() const override;
  };

} // namespace pattern
} // namespace tree
} // namespace antlr4
