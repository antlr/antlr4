/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
package org.antlr.v4.runtime;

/**
 * This interface provides information about the vocabulary used by a
 * recognizer.
 *
 * @see Recognizer#getVocabulary()
 * @author Sam Harwell
 */
public interface Vocabulary {
	/**
	 * Returns the highest token type value. It can be used to iterate from
	 * zero to that number, inclusively, thus querying all stored entries.
	 * @return the highest token type value
	 */
	int getMaxTokenType();

	/**
	 * Gets the string literal associated with a token type. The string returned
	 * by this method, when not {@code null}, can be used unaltered in a parser
	 * grammar to represent this token type.
	 *
	 * <p>The following table shows examples of lexer rules and the literal
	 * names assigned to the corresponding token types.</p>
         * 
         * <pre>
         * Rule                 Literal Name    Java String Literal
         * --------------------+---------------+-----------------------------
         * {@code THIS   : 'this';}     {@code 'this'}          {@code "'this'"}
         * {@code SQUOTE : '\'';}       {@code '\''}            {@code "'\\''"}
         * {@code ID     : [A-Z]+;}     {@code n/a}             {@code null} 
         * </pre>
	 *
	 * @param tokenType The token type.
	 *
	 * @return The string literal associated with the specified token type, or
	 * {@code null} if no string literal is associated with the type.
	 */
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
	 *  <li>Tokens defined in a <code>tokens{}</code> block in a lexer or parser
	 *  grammar.</li>
	 *  <li>The implicitly defined {@code EOF} token, which has the token type
	 *  {@link Token#EOF}.</li>
	 * </ul>
	 *
	 * <p>The following table shows examples of lexer rules and the literal
	 * names assigned to the corresponding token types.</p>
	 *
         * <pre>
         * Rule                 Symbolic Name
         * --------------------+--------------------------
	 * {@code THIS   : 'this';}     {@code THIS}
	 * {@code SQUOTE : '\'';}       {@code SQUOTE}
	 * {@code ID     : [A-Z]+;}     {@code ID} 
	 * </pre>
         *
	 * @param tokenType The token type.
	 *
	 * @return The symbolic name associated with the specified token type, or
	 * {@code null} if no symbolic name is associated with the type.
	 */
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
	String getDisplayName(int tokenType);
}
