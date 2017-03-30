/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Implements the {@code pushMode} lexer action by calling
 * {@link Lexer#pushMode} with the assigned mode.
 *
 * @author Sam Harwell
 * @since 4.2
 */
public final class LexerPushModeAction implements LexerAction {
	private final int mode;

	/**
	 * Constructs a new {@code pushMode} action with the specified mode value.
	 * @param mode The mode value to pass to {@link Lexer#pushMode}.
	 */
	public LexerPushModeAction(int mode) {
		this.mode = mode;
	}

	/**
	 * Get the lexer mode this action should transition the lexer to.
	 *
	 * @return The lexer mode for this {@code pushMode} command.
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@link LexerActionType#PUSH_MODE}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.PUSH_MODE;
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
	 * <p>This action is implemented by calling {@link Lexer#pushMode} with the
	 * value provided by {@link #getMode}.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.pushMode(mode);
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, getActionType().ordinal());
		hash = MurmurHash.update(hash, mode);
		return MurmurHash.finish(hash, 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof LexerPushModeAction)) {
			return false;
		}

		return mode == ((LexerPushModeAction)obj).mode;
	}

	@Override
	public String toString() {
		return String.format("pushMode(%d)", mode);
	}
}
