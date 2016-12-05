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
        /// <see cref="TokenConstants.Eof"/>
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

		/** Gets the list of literals ('this' from rules like {@code THIS : 'this';})
		 *  found in the associated recognizer. Literal at index i is
		 *  associated with token type i.  Token types without literals
		 *  (such as {@code ID : [A-Z]+;}) have null entries
		 *  in the returned array. The first possibly valid entry is
		 *  {@link Token#MIN_USER_TOKEN_TYPE}.
		 *
		 * @return The non-null array of literal names assigned to tokens; size is not guaranteed to be max tokentype + 1.
		 *
		 * @since 4.6
		 */
        [return: NotNull]
		string[] GetLiteralNames();

		/** Gets the list of symbolic names (ID from rules like {@code ID : [A-Z]+;})
		 *  found in the associated recognizer. Name at index i is
		 *  associated with token type i. All tokens have a symbol name and so
		 *  all entries in array at {@link Token#MIN_USER_TOKEN_TYPE} and above
		 *  have a non-null entry.
		 *
		 *  EOF has no entry in the return array as its token type is -1.
		 *
		 * return The non-null array of symbol names assigned to tokens; size is not guaranteed to be max tokentype + 1.
		 *
		 * since 4.6
		 */
		[return: NotNull]
		string[] GetSymbolicNames();

		/** Gets the list of display names as computed by {@link #getDisplayName(int)}
		 *  found in the associated recognizer. Name at index i is
		 *  associated with token type i. All tokens have a display name and so
		 *  all entries in array at {@link Token#MIN_USER_TOKEN_TYPE} and above
		 *  have a non-null entry.
		 *
		 *  EOF has no entry in the return array as its token type is -1.
		 *
		 * return The non-null array of display names assigned to tokens; size is not guaranteed to be max tokentype + 1.
		 *
		 * since 4.6
		 */
		[return: NotNull]
		string[] GetDisplayNames();
    }
}
