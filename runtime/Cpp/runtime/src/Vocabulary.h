/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

#pragma once

#include "antlr4-common.h"

namespace antlr4 {

  /// This class provides a default implementation of the <seealso cref="Vocabulary"/>
  /// interface.
  class ANTLR4CPP_PUBLIC Vocabulary {
  public:
    static const Vocabulary& empty();

    virtual ~Vocabulary() = default;

    /// <summary>
    /// Returns the highest token type value. It can be used to iterate from
    /// zero to that number, inclusively, thus querying all stored entries. </summary>
    /// <returns> the highest token type value </returns>
    constexpr size_t getMaxTokenType() const { return _maxTokenType; }

    /// <summary>
    /// Gets the string literal associated with a token type. The string returned
    /// by this method, when not {@code null}, can be used unaltered in a parser
    /// grammar to represent this token type.
    ///
    /// <para>The following table shows examples of lexer rules and the literal
    /// names assigned to the corresponding token types.</para>
    ///
    /// <table>
    ///  <tr>
    ///   <th>Rule</th>
    ///   <th>Literal Name</th>
    ///   <th>Java String Literal</th>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code THIS : 'this';}</td>
    ///   <td>{@code 'this'}</td>
    ///   <td>{@code "'this'"}</td>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code SQUOTE : '\'';}</td>
    ///   <td>{@code '\''}</td>
    ///   <td>{@code "'\\''"}</td>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code ID : [A-Z]+;}</td>
    ///   <td>n/a</td>
    ///   <td>{@code null}</td>
    ///  </tr>
    /// </table>
    /// </summary>
    /// <param name="tokenType"> The token type.
    /// </param>
    /// <returns> The string literal associated with the specified token type, or
    /// {@code null} if no string literal is associated with the type. </returns>
    virtual std::string_view getLiteralName(size_t tokenType) const = 0;

    /// <summary>
    /// Gets the symbolic name associated with a token type. The string returned
    /// by this method, when not {@code null}, can be used unaltered in a parser
    /// grammar to represent this token type.
    ///
    /// <para>This method supports token types defined by any of the following
    /// methods:</para>
    ///
    /// <ul>
    ///  <li>Tokens created by lexer rules.</li>
    ///  <li>Tokens defined in a <code>tokens{}</code> block in a lexer or parser
    ///  grammar.</li>
    ///  <li>The implicitly defined {@code EOF} token, which has the token type
    ///  <seealso cref="Token#EOF"/>.</li>
    /// </ul>
    ///
    /// <para>The following table shows examples of lexer rules and the literal
    /// names assigned to the corresponding token types.</para>
    ///
    /// <table>
    ///  <tr>
    ///   <th>Rule</th>
    ///   <th>Symbolic Name</th>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code THIS : 'this';}</td>
    ///   <td>{@code THIS}</td>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code SQUOTE : '\'';}</td>
    ///   <td>{@code SQUOTE}</td>
    ///  </tr>
    ///  <tr>
    ///   <td>{@code ID : [A-Z]+;}</td>
    ///   <td>{@code ID}</td>
    ///  </tr>
    /// </table>
    /// </summary>
    /// <param name="tokenType"> The token type.
    /// </param>
    /// <returns> The symbolic name associated with the specified token type, or
    /// {@code null} if no symbolic name is associated with the type. </returns>
    virtual std::string_view getSymbolicName(size_t tokenType) const = 0;

    /// <summary>
    /// Gets the display name of a token type.
    ///
    /// <para>ANTLR provides a default implementation of this method, but
    /// applications are free to override the behavior in any manner which makes
    /// sense for the application. The default implementation returns the first
    /// result from the following list which produces a non-{@code null}
    /// result.</para>
    ///
    /// <ol>
    ///  <li>The result of <seealso cref="#getLiteralName"/></li>
    ///  <li>The result of <seealso cref="#getSymbolicName"/></li>
    ///  <li>The result of <seealso cref="Integer#toString"/></li>
    /// </ol>
    /// </summary>
    /// <param name="tokenType"> The token type.
    /// </param>
    /// <returns> The display name of the token type, for use in error reporting or
    /// other user-visible messages which reference specific token types. </returns>
    virtual std::string getDisplayName(size_t tokenType) const = 0;

  protected:
    explicit Vocabulary(size_t maxTokenType) : _maxTokenType(maxTokenType) {}

  private:
    size_t _maxTokenType = 0;
  };

namespace dfa {

  // For some reason this was initially defined in antlr4::dfa, instead of antlr4. That was a
  // mistake, so we moved it to antlr4. The alias is here to keep source compatibility and will
  // be removed in a future version.
  using Vocabulary = antlr4::Vocabulary;

}  // namespace dfa

}  // namespace antlr4
