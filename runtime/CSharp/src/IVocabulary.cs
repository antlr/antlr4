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
    /// This interface provides information about the vocabulary used by a
    /// recognizer.
    /// </summary>
    /// <remarks>
    /// This interface provides information about the vocabulary used by a
    /// recognizer.
    /// </remarks>
    /// <seealso cref="Recognizer{Symbol, ATNInterpreter}.Vocabulary()"/>
    /// <author>Sam Harwell</author>
    public interface IVocabulary
    {
        /// <summary>Gets the string literal associated with a token type.</summary>
        /// <remarks>
        /// Gets the string literal associated with a token type. The string returned
        /// by this method, when not
        /// <see langword="null"/>
        /// , can be used unaltered in a parser
        /// grammar to represent this token type.
        /// <p>The following table shows examples of lexer rules and the literal
        /// names assigned to the corresponding token types.</p>
        /// <table>
        /// <tr>
        /// <th>Rule</th>
        /// <th>Literal Name</th>
        /// <th>Java String Literal</th>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>THIS : 'this';</c>
        /// </td>
        /// <td>
        /// <c>'this'</c>
        /// </td>
        /// <td>
        /// <c>"'this'"</c>
        /// </td>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>SQUOTE : '\'';</c>
        /// </td>
        /// <td>
        /// <c>'\''</c>
        /// </td>
        /// <td>
        /// <c>"'\\''"</c>
        /// </td>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>ID : [A-Z]+;</c>
        /// </td>
        /// <td>n/a</td>
        /// <td>
        /// <see langword="null"/>
        /// </td>
        /// </tr>
        /// </table>
        /// </remarks>
        /// <param name="tokenType">The token type.</param>
        /// <returns>
        /// The string literal associated with the specified token type, or
        /// <see langword="null"/>
        /// if no string literal is associated with the type.
        /// </returns>
        [return: Nullable]
        string GetLiteralName(int tokenType);

        /// <summary>Gets the symbolic name associated with a token type.</summary>
        /// <remarks>
        /// Gets the symbolic name associated with a token type. The string returned
        /// by this method, when not
        /// <see langword="null"/>
        /// , can be used unaltered in a parser
        /// grammar to represent this token type.
        /// <p>This method supports token types defined by any of the following
        /// methods:</p>
        /// <ul>
        /// <li>Tokens created by lexer rules.</li>
        /// <li>Tokens defined in a
        /// <c/>
        /// tokens
        /// block in a lexer or parser
        /// grammar.</li>
        /// <li>The implicitly defined
        /// <c>EOF</c>
        /// token, which has the token type
        /// <see cref="TokenConstants.EOF"/>
        /// .</li>
        /// </ul>
        /// <p>The following table shows examples of lexer rules and the literal
        /// names assigned to the corresponding token types.</p>
        /// <table>
        /// <tr>
        /// <th>Rule</th>
        /// <th>Symbolic Name</th>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>THIS : 'this';</c>
        /// </td>
        /// <td>
        /// <c>THIS</c>
        /// </td>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>SQUOTE : '\'';</c>
        /// </td>
        /// <td>
        /// <c>SQUOTE</c>
        /// </td>
        /// </tr>
        /// <tr>
        /// <td>
        /// <c>ID : [A-Z]+;</c>
        /// </td>
        /// <td>
        /// <c>ID</c>
        /// </td>
        /// </tr>
        /// </table>
        /// </remarks>
        /// <param name="tokenType">The token type.</param>
        /// <returns>
        /// The symbolic name associated with the specified token type, or
        /// <see langword="null"/>
        /// if no symbolic name is associated with the type.
        /// </returns>
        [return: Nullable]
        string GetSymbolicName(int tokenType);

        /// <summary>Gets the display name of a token type.</summary>
        /// <remarks>
        /// Gets the display name of a token type.
        /// <p>ANTLR provides a default implementation of this method, but
        /// applications are free to override the behavior in any manner which makes
        /// sense for the application. The default implementation returns the first
        /// result from the following list which produces a non-
        /// <see langword="null"/>
        /// result.</p>
        /// <ol>
        /// <li>The result of
        /// <see cref="GetLiteralName(int)"/>
        /// </li>
        /// <li>The result of
        /// <see cref="GetSymbolicName(int)"/>
        /// </li>
        /// <li>The result of
        /// <see cref="int.ToString()"/>
        /// </li>
        /// </ol>
        /// </remarks>
        /// <param name="tokenType">The token type.</param>
        /// <returns>
        /// The display name of the token type, for use in error reporting or
        /// other user-visible messages which reference specific token types.
        /// </returns>
        [return: NotNull]
        string GetDisplayName(int tokenType);
    }
}
