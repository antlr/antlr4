/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Implements the {@code type} lexer action by calling {@link Lexer#setType}
 * with the assigned type.
 *
 * @author Sam Harwell
 * @since 4.2
 */
public class LexerTypeAction implements LexerAction {
	private final int type;

	/**
	 * Constructs a new {@code type} action with the specified token type value.
	 * @param type The type to assign to the token using {@link Lexer#setType}.
	 */
	public LexerTypeAction(int type) {
		this.type = type;
	}

	/**
	 * Gets the type to assign to a token created by the lexer.
	 * @return The type to assign to a token created by the lexer.
	 */
	public int getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@link LexerActionType#TYPE}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.TYPE;
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@code false}.
	 */
	@Override
	public boolean isPositionDependent() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>This action is implemented by calling {@link Lexer#setType} with the
	 * value provided by {@link #getType}.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.setType(type);
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, getActionType().ordinal());
		hash = MurmurHash.update(hash, type);
		return MurmurHash.finish(hash, 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof LexerTypeAction)) {
			return false;
		}

		return type == ((LexerTypeAction)obj).type;
	}

	@Override
	public String toString() {
		return String.format("type(%d)", type);
	}
}
