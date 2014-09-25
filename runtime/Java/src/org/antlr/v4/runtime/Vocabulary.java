/*
 * [The "BSD license"]
 *  Copyright (c) 2014 Terence Parr
 *  Copyright (c) 2014 Sam Harwell
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
package org.antlr.v4.runtime;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

/**
 * This interface provides information about the vocabulary used by a
 * recognizer.
 *
 * @see Recognizer#getVocabulary()
 * @author Sam Harwell
 */
public interface Vocabulary {

	/**
	 * Gets the string literal associated with a token type. The string returned
	 * by this method, when not {@code null}, can be used unaltered in a parser
	 * grammar to represent this token type.
	 *
	 * <p>The following table shows examples of lexer rules and the literal
	 * names assigned to the corresponding token types.</p>
	 *
	 * <table>
	 *  <tr>
	 *   <th>Rule</th>
	 *   <th>Literal Name</th>
	 *   <th>Java String Literal</th>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code THIS : 'this';}</td>
	 *   <td>{@code 'this'}</td>
	 *   <td>{@code "'this'"}</td>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code SQUOTE : '\'';}</td>
	 *   <td>{@code '\''}</td>
	 *   <td>{@code "'\\''"}</td>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code ID : [A-Z]+;}</td>
	 *   <td>n/a</td>
	 *   <td>{@code null}</td>
	 *  </tr>
	 * </table>
	 *
	 * @param tokenType The token type.
	 *
	 * @return The string literal associated with the specified token type, or
	 * {@code null} if no string literal is associated with the type.
	 */
	@Nullable
	String getLiteralName(int tokenType);

	/**
	 * Gets the symbolic name associated with a token type. The string returned
	 * by this method, when not {@code null}, can be used unaltered in a parser
	 * grammar to represent this token type.
	 *
	 * <p>This method supports token types defined by any of the following
	 * methods:</p>
	 *
	 * <ul>
	 *  <li>Tokens created by lexer rules.</li>
	 *  <li>Tokens defined in a {@code tokens{}} block in a lexer or parser
	 *  grammar.</li>
	 *  <li>The implicitly defined {@code EOF} token, which has the token type
	 *  {@link Token#EOF}.</li>
	 * </ul>
	 *
	 * <p>The following table shows examples of lexer rules and the literal
	 * names assigned to the corresponding token types.</p>
	 *
	 * <table>
	 *  <tr>
	 *   <th>Rule</th>
	 *   <th>Symbolic Name</th>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code THIS : 'this';}</td>
	 *   <td>{@code THIS}</td>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code SQUOTE : '\'';}</td>
	 *   <td>{@code SQUOTE}</td>
	 *  </tr>
	 *  <tr>
	 *   <td>{@code ID : [A-Z]+;}</td>
	 *   <td>{@code ID}</td>
	 *  </tr>
	 * </table>
	 *
	 * @param tokenType The token type.
	 *
	 * @return The symbolic name associated with the specified token type, or
	 * {@code null} if no symbolic name is associated with the type.
	 */
	@Nullable
	String getSymbolicName(int tokenType);

	/**
	 * Gets the display name of a token type.
	 *
	 * <p>ANTLR provides a default implementation of this method, but
	 * applications are free to override the behavior in any manner which makes
	 * sense for the application. The default implementation returns the first
	 * result from the following list which produces a non-{@code null}
	 * result.</p>
	 *
	 * <ol>
	 *  <li>The result of {@link #getLiteralName}</li>
	 *  <li>The result of {@link #getSymbolicName}</li>
	 *  <li>The result of {@link Integer#toString}</li>
	 * </ol>
	 *
	 * @param tokenType The token type.
	 *
	 * @return The display name of the token type, for use in error reporting or
	 * other user-visible messages which reference specific token types.
	 */
	@NotNull
	String getDisplayName(int tokenType);

}
