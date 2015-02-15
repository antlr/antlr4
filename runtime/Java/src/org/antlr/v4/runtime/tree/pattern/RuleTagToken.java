/*
 * [The "BSD license"]
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

package org.antlr.v4.runtime.tree.pattern;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;

/**
 * A {@link Token} object representing an entire subtree matched by a parser
 * rule; e.g., {@code <expr>}. These tokens are created for {@link TagChunk}
 * chunks where the tag corresponds to a parser rule.
 */
public class RuleTagToken implements Token {
	/**
	 * This is the backing field for {@link #getRuleName}.
	 */
	private final String ruleName;
	/**
	 * The token type for the current token. This is the token type assigned to
	 * the bypass alternative for the rule during ATN deserialization.
	 */
	private final int bypassTokenType;
	/**
	 * This is the backing field for {@link #getLabel}.
	 */
	private final String label;

	/**
	 * Constructs a new instance of {@link RuleTagToken} with the specified rule
	 * name and bypass token type and no label.
	 *
	 * @param ruleName The name of the parser rule this rule tag matches.
	 * @param bypassTokenType The bypass token type assigned to the parser rule.
	 *
	 * @exception IllegalArgumentException if {@code ruleName} is {@code null}
	 * or empty.
	 */
	public RuleTagToken(String ruleName, int bypassTokenType) {
		this(ruleName, bypassTokenType, null);
	}

	/**
	 * Constructs a new instance of {@link RuleTagToken} with the specified rule
	 * name, bypass token type, and label.
	 *
	 * @param ruleName The name of the parser rule this rule tag matches.
	 * @param bypassTokenType The bypass token type assigned to the parser rule.
	 * @param label The label associated with the rule tag, or {@code null} if
	 * the rule tag is unlabeled.
	 *
	 * @exception IllegalArgumentException if {@code ruleName} is {@code null}
	 * or empty.
	 */
	public RuleTagToken(String ruleName, int bypassTokenType, String label) {
		if (ruleName == null || ruleName.isEmpty()) {
			throw new IllegalArgumentException("ruleName cannot be null or empty.");
		}

		this.ruleName = ruleName;
		this.bypassTokenType = bypassTokenType;
		this.label = label;
	}

	/**
	 * Gets the name of the rule associated with this rule tag.
	 *
	 * @return The name of the parser rule associated with this rule tag.
	 */

	public final String getRuleName() {
		return ruleName;
	}

	/**
	 * Gets the label associated with the rule tag.
	 *
	 * @return The name of the label associated with the rule tag, or
	 * {@code null} if this is an unlabeled rule tag.
	 */

	public final String getLabel() {
		return label;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Rule tag tokens are always placed on the {@link #DEFAULT_CHANNEL}.</p>
	 */
	@Override
	public int getChannel() {
		return DEFAULT_CHANNEL;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This method returns the rule tag formatted with {@code <} and {@code >}
	 * delimiters.</p>
	 */
	@Override
	public String getText() {
		if (label != null) {
			return "<" + label + ":" + ruleName + ">";
		}

		return "<" + ruleName + ">";
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Rule tag tokens have types assigned according to the rule bypass
	 * transitions created during ATN deserialization.</p>
	 */
	@Override
	public int getType() {
		return bypassTokenType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns 0.</p>
	 */
	@Override
	public int getLine() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns -1.</p>
	 */
	@Override
	public int getCharPositionInLine() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns -1.</p>
	 */
	@Override
	public int getTokenIndex() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns -1.</p>
	 */
	@Override
	public int getStartIndex() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns -1.</p>
	 */
	@Override
	public int getStopIndex() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns {@code null}.</p>
	 */
	@Override
	public TokenSource getTokenSource() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} always returns {@code null}.</p>
	 */
	@Override
	public CharStream getInputStream() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>The implementation for {@link RuleTagToken} returns a string of the form
	 * {@code ruleName:bypassTokenType}.</p>
	 */
	@Override
	public String toString() {
		return ruleName + ":" + bypassTokenType;
	}
}
