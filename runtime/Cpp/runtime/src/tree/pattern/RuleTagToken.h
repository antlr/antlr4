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

#include "Token.h"

namespace antlr4 {
namespace tree {
namespace pattern {

  /// <summary>
  /// A <seealso cref="Token"/> object representing an entire subtree matched by a parser
  /// rule; e.g., {@code <expr>}. These tokens are created for <seealso cref="TagChunk"/>
  /// chunks where the tag corresponds to a parser rule.
  /// </summary>
  class ANTLR4CPP_PUBLIC RuleTagToken : public Token {
    /// <summary>
    /// This is the backing field for <seealso cref="#getRuleName"/>.
    /// </summary>
  private:
    const std::string ruleName;
    /// <summary>
    /// The token type for the current token. This is the token type assigned to
    /// the bypass alternative for the rule during ATN deserialization.
    /// </summary>
    const int bypassTokenType;
    /// <summary>
    /// This is the backing field for <seealso cref="#getLabe"/>.
    /// </summary>
    const std::string label;

    /// <summary>
    /// Constructs a new instance of <seealso cref="RuleTagToken"/> with the specified rule
    /// name and bypass token type and no label.
    /// </summary>
    /// <param name="ruleName"> The name of the parser rule this rule tag matches. </param>
    /// <param name="bypassTokenType"> The bypass token type assigned to the parser rule.
    /// </param>
    /// <exception cref="IllegalArgumentException"> if {@code ruleName} is {@code null}
    /// or empty. </exception>
  public:

    RuleTagToken(const std::string &ruleName, int bypassTokenType); //this(ruleName, bypassTokenType, nullptr);

    /// <summary>
    /// Constructs a new instance of <seealso cref="RuleTagToken"/> with the specified rule
    /// name, bypass token type, and label.
    /// </summary>
    /// <param name="ruleName"> The name of the parser rule this rule tag matches. </param>
    /// <param name="bypassTokenType"> The bypass token type assigned to the parser rule. </param>
    /// <param name="label"> The label associated with the rule tag, or {@code null} if
    /// the rule tag is unlabeled.
    /// </param>
    /// <exception cref="IllegalArgumentException"> if {@code ruleName} is {@code null}
    /// or empty. </exception>
    RuleTagToken(const std::string &ruleName, int bypassTokenType, const std::string &label);

    /// <summary>
    /// Gets the name of the rule associated with this rule tag.
    /// </summary>
    /// <returns> The name of the parser rule associated with this rule tag. </returns>
    std::string getRuleName() const;

    /// <summary>
    /// Gets the label associated with the rule tag.
    /// </summary>
    /// <returns> The name of the label associated with the rule tag, or
    /// {@code null} if this is an unlabeled rule tag. </returns>
    std::string getLabel() const;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// Rule tag tokens are always placed on the <seealso cref="#DEFAULT_CHANNE"/>.
    /// </summary>
    virtual size_t getChannel() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// This method returns the rule tag formatted with {@code <} and {@code >}
    /// delimiters.
    /// </summary>
    virtual std::string getText() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// Rule tag tokens have types assigned according to the rule bypass
    /// transitions created during ATN deserialization.
    /// </summary>
    virtual int getType() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns 0.
    /// </summary>
    virtual int getLine() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns -1.
    /// </summary>
    virtual int getCharPositionInLine() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns -1.
    /// </summary>
    virtual int getTokenIndex() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns -1.
    /// </summary>
    virtual int getStartIndex() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns -1.
    /// </summary>
    virtual int getStopIndex() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns {@code null}.
    /// </summary>
    virtual TokenSource *getTokenSource() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> always returns {@code null}.
    /// </summary>
    virtual CharStream *getInputStream() const override;

    /// <summary>
    /// {@inheritDoc}
    /// <p/>
    /// The implementation for <seealso cref="RuleTagToken"/> returns a string of the form
    /// {@code ruleName:bypassTokenType}.
    /// </summary>
    virtual std::string toString() const override;
  };

} // namespace pattern
} // namespace tree
} // namespace antlr4
