/*
 * [The "BSD license"]
 *  Copyright (c) 2016 Mike Lischke
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Dan McLaughlin
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

#pragma once

namespace org {
namespace antlr {
namespace v4 {
namespace runtime {
namespace dfa {

  /// This interface provides information about the vocabulary used by a
  /// recognizer.
  ///
  /// <seealso cref= Recognizer#getVocabulary()
  /// @author Sam Harwell </seealso>
  class ANTLR4CPP_PUBLIC Vocabulary {
  public:
    /// <summary>
    /// Returns the highest token type value. It can be used to iterate from
    /// zero to that number, inclusively, thus querying all stored entries. </summary>
    /// <returns> the highest token type value </returns>
    virtual int getMaxTokenType() const = 0;

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
    virtual std::wstring getLiteralName(ssize_t tokenType) const = 0;

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
    virtual std::wstring getSymbolicName(ssize_t tokenType) const = 0;

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
    virtual std::wstring getDisplayName(ssize_t tokenType) const = 0;
  };

} // namespace atn
} // namespace runtime
} // namespace v4
} // namespace antlr
} // namespace org
