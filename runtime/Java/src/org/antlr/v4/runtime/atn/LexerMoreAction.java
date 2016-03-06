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

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Implements the {@code more} lexer action by calling {@link Lexer#more}.
 *
 * <p>The {@code more} command does not have any parameters, so this action is
 * implemented as a singleton instance exposed by {@link #INSTANCE}.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */
public final class LexerMoreAction implements LexerAction {
	/**
	 * Provides a singleton instance of this parameterless lexer action.
	 */
	public static final LexerMoreAction INSTANCE = new LexerMoreAction();

	/**
	 * Constructs the singleton instance of the lexer {@code more} command.
	 */
	private LexerMoreAction() {
	}

	/**
	 * {@inheritDoc}
	 * @return This method returns {@link LexerActionType#MORE}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.MORE;
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
	 * <p>This action is implemented by calling {@link Lexer#more}.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.more();
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
		return "more";
	}
}
