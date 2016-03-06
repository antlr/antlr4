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

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;

/**
 * Represents a single action which can be executed following the successful
 * match of a lexer rule. Lexer actions are used for both embedded action syntax
 * and ANTLR 4's new lexer command syntax.
 *
 * @author Sam Harwell
 * @since 4.2
 */
public interface LexerAction {
	/**
	 * Gets the serialization type of the lexer action.
	 *
	 * @return The serialization type of the lexer action.
	 */
	LexerActionType getActionType();

	/**
	 * Gets whether the lexer action is position-dependent. Position-dependent
	 * actions may have different semantics depending on the {@link CharStream}
	 * index at the time the action is executed.
	 *
	 * <p>Many lexer commands, including {@code type}, {@code skip}, and
	 * {@code more}, do not check the input index during their execution.
	 * Actions like this are position-independent, and may be stored more
	 * efficiently as part of the {@link LexerATNConfig#lexerActionExecutor}.</p>
	 *
	 * @return {@code true} if the lexer action semantics can be affected by the
	 * position of the input {@link CharStream} at the time it is executed;
	 * otherwise, {@code false}.
	 */
	boolean isPositionDependent();

	/**
	 * Execute the lexer action in the context of the specified {@link Lexer}.
	 *
	 * <p>For position-dependent actions, the input stream must already be
	 * positioned correctly prior to calling this method.</p>
	 *
	 * @param lexer The lexer instance.
	 */
	void execute(Lexer lexer);
}
