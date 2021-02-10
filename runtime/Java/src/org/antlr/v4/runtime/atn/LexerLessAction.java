/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Implements the {@code less} lexer action by calling {@link Lexer#less}.
 *
 * <p>The {@code less} command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */
public final class LexerLessAction implements LexerAction {
	/**
	 * Provides a singleton instance of this parameterless lexer action.
	 */
	public static final LexerLessAction INSTANCE = new LexerLessAction();

	/**
	 * Constructs the singleton instance of the lexer {@code less} command.
	 */
	private LexerLessAction() {
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@link LexerActionType#LESS}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.LESS;
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
	 * <p>This action is implemented by calling {@link Lexer#less}.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.less();
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, getActionType().ordinal());
		return MurmurHash.finish(hash, 1);
	}

	@Override
	@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public String toString() {
		return "less";
	}
}
