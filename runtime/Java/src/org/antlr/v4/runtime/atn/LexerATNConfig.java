/*
 * [The "BSD license"]
 *  Copyright (c) 2012 Terence Parr
 *  Copyright (c) 2012 Sam Harwell
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

import org.antlr.v4.runtime.misc.MurmurHash;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

public class LexerATNConfig extends ATNConfig {
	/** Capture lexer action we traverse */
	public int lexerActionIndex = -1;

	private final boolean passedThroughNonGreedyDecision;

	public LexerATNConfig(@NotNull ATNState state,
						  int alt,
						  @Nullable PredictionContext context)
	{
		super(state, alt, context, SemanticContext.NONE);
		this.passedThroughNonGreedyDecision = false;
	}

	public LexerATNConfig(@NotNull ATNState state,
						  int alt,
						  @Nullable PredictionContext context,
						  int actionIndex)
	{
		super(state, alt, context, SemanticContext.NONE);
		this.lexerActionIndex = actionIndex;
		this.passedThroughNonGreedyDecision = false;
	}

	public LexerATNConfig(@NotNull LexerATNConfig c, @NotNull ATNState state) {
		super(c, state, c.context, c.semanticContext);
		this.lexerActionIndex = c.lexerActionIndex;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	public LexerATNConfig(@NotNull LexerATNConfig c, @NotNull ATNState state,
						  int actionIndex)
	{
		super(c, state, c.context, c.semanticContext);
		this.lexerActionIndex = actionIndex;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	public LexerATNConfig(@NotNull LexerATNConfig c, @NotNull ATNState state,
						  @Nullable PredictionContext context) {
		super(c, state, context, c.semanticContext);
		this.lexerActionIndex = c.lexerActionIndex;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	public final boolean hasPassedThroughNonGreedyDecision() {
		return passedThroughNonGreedyDecision;
	}

	@Override
	public int hashCode() {
		int hashCode = MurmurHash.initialize(7);
		hashCode = MurmurHash.update(hashCode, state.stateNumber);
		hashCode = MurmurHash.update(hashCode, alt);
		hashCode = MurmurHash.update(hashCode, context);
		hashCode = MurmurHash.update(hashCode, semanticContext);
		hashCode = MurmurHash.update(hashCode, passedThroughNonGreedyDecision ? 1 : 0);
		hashCode = MurmurHash.finish(hashCode, 5);
		return hashCode;
	}

	@Override
	public boolean equals(ATNConfig other) {
		if (this == other) {
			return true;
		}
		else if (!(other instanceof LexerATNConfig)) {
			return false;
		}

		LexerATNConfig lexerOther = (LexerATNConfig)other;
		if (passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision) {
			return false;
		}

		return super.equals(other);
	}

	private static boolean checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
		return source.passedThroughNonGreedyDecision
			|| target instanceof DecisionState && ((DecisionState)target).nonGreedy;
	}
}
