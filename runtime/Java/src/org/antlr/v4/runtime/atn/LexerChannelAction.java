/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Implements the {@code channel} lexer action by calling
 * {@link Lexer#setChannel} with the assigned channel.
 *
 * @author Sam Harwell
 * @since 4.2
 */
public final class LexerChannelAction implements LexerAction {
	private final int channel;

	/**
	 * Constructs a new {@code channel} action with the specified channel value.
	 * @param channel The channel value to pass to {@link Lexer#setChannel}.
	 */
	public LexerChannelAction(int channel) {
		this.channel = channel;
	}

	/**
	 * Gets the channel to use for the {@link Token} created by the lexer.
	 *
	 * @return The channel to use for the {@link Token} created by the lexer.
	 */
	public int getChannel() {
		return channel;
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@link LexerActionType#CHANNEL}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.CHANNEL;
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
	 * <p>This action is implemented by calling {@link Lexer#setChannel} with the
	 * value provided by {@link #getChannel}.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.setChannel(channel);
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, getActionType().ordinal());
		hash = MurmurHash.update(hash, channel);
		return MurmurHash.finish(hash, 2);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof LexerChannelAction)) {
			return false;
		}

		return channel == ((LexerChannelAction)obj).channel;
	}

	@Override
	public String toString() {
		return String.format("channel(%d)", channel);
	}
}
