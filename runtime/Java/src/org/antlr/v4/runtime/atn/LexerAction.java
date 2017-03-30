/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
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
