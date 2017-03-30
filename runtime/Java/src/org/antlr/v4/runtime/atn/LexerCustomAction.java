/*
 * Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

package org.antlr.v4.runtime.atn;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.MurmurHash;

/**
 * Executes a custom lexer action by calling {@link Recognizer#action} with the
 * rule and action indexes assigned to the custom action. The implementation of
 * a custom action is added to the generated code for the lexer in an override
 * of {@link Recognizer#action} when the grammar is compiled.
 *
 * <p>This class may represent embedded actions created with the <code>{...}</code>
 * syntax in ANTLR 4, as well as actions created for lexer commands where the
 * command argument could not be evaluated when the grammar was compiled.</p>
 *
 * @author Sam Harwell
 * @since 4.2
 */
public final class LexerCustomAction implements LexerAction {
	private final int ruleIndex;
	private final int actionIndex;

	/**
	 * Constructs a custom lexer action with the specified rule and action
	 * indexes.
	 *
	 * @param ruleIndex The rule index to use for calls to
	 * {@link Recognizer#action}.
	 * @param actionIndex The action index to use for calls to
	 * {@link Recognizer#action}.
	 */
	public LexerCustomAction(int ruleIndex, int actionIndex) {
		this.ruleIndex = ruleIndex;
		this.actionIndex = actionIndex;
	}

	/**
	 * Gets the rule index to use for calls to {@link Recognizer#action}.
	 *
	 * @return The rule index for the custom action.
	 */
	public int getRuleIndex() {
		return ruleIndex;
	}

	/**
	 * Gets the action index to use for calls to {@link Recognizer#action}.
	 *
	 * @return The action index for the custom action.
	 */
	public int getActionIndex() {
		return actionIndex;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @return This method returns {@link LexerActionType#CUSTOM}.
	 */
	@Override
	public LexerActionType getActionType() {
		return LexerActionType.CUSTOM;
	}

	/**
	 * Gets whether the lexer action is position-dependent. Position-dependent
	 * actions may have different semantics depending on the {@link CharStream}
	 * index at the time the action is executed.
	 *
	 * <p>Custom actions are position-dependent since they may represent a
	 * user-defined embedded action which makes calls to methods like
	 * {@link Lexer#getText}.</p>
	 *
	 * @return This method returns {@code true}.
	 */
	@Override
	public boolean isPositionDependent() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>Custom actions are implemented by calling {@link Lexer#action} with the
	 * appropriate rule and action indexes.</p>
	 */
	@Override
	public void execute(Lexer lexer) {
		lexer.action(null, ruleIndex, actionIndex);
	}

	@Override
	public int hashCode() {
		int hash = MurmurHash.initialize();
		hash = MurmurHash.update(hash, getActionType().ordinal());
		hash = MurmurHash.update(hash, ruleIndex);
		hash = MurmurHash.update(hash, actionIndex);
		return MurmurHash.finish(hash, 3);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		else if (!(obj instanceof LexerCustomAction)) {
			return false;
		}

		LexerCustomAction other = (LexerCustomAction)obj;
		return ruleIndex == other.ruleIndex
			&& actionIndex == other.actionIndex;
	}
}
