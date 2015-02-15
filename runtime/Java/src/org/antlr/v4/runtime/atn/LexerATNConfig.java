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
import org.antlr.v4.runtime.misc.ObjectEqualityComparator;

public class LexerATNConfig extends ATNConfig {
	/**
	 * This is the backing field for {@link #getLexerActionExecutor}.
	 */
	private final LexerActionExecutor lexerActionExecutor;

	private final boolean passedThroughNonGreedyDecision;

	public LexerATNConfig(ATNState state,
						  int alt,
						  PredictionContext context)
	{
		super(state, alt, context, SemanticContext.NONE);
		this.passedThroughNonGreedyDecision = false;
		this.lexerActionExecutor = null;
	}

	public LexerATNConfig(ATNState state,
						  int alt,
						  PredictionContext context,
						  LexerActionExecutor lexerActionExecutor)
	{
		super(state, alt, context, SemanticContext.NONE);
		this.lexerActionExecutor = lexerActionExecutor;
		this.passedThroughNonGreedyDecision = false;
	}

	public LexerATNConfig(LexerATNConfig c, ATNState state) {
		super(c, state, c.context, c.semanticContext);
		this.lexerActionExecutor = c.lexerActionExecutor;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	public LexerATNConfig(LexerATNConfig c, ATNState state,
						  LexerActionExecutor lexerActionExecutor)
	{
		super(c, state, c.context, c.semanticContext);
		this.lexerActionExecutor = lexerActionExecutor;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	public LexerATNConfig(LexerATNConfig c, ATNState state,
						  PredictionContext context) {
		super(c, state, context, c.semanticContext);
		this.lexerActionExecutor = c.lexerActionExecutor;
		this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
	}

	/**
	 * Gets the {@link LexerActionExecutor} capable of executing the embedded
	 * action(s) for the current configuration.
	 */
	public final LexerActionExecutor getLexerActionExecutor() {
		return lexerActionExecutor;
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
		hashCode = MurmurHash.update(hashCode, lexerActionExecutor);
		hashCode = MurmurHash.finish(hashCode, 6);
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

		if (!ObjectEqualityComparator.INSTANCE.equals(lexerActionExecutor, lexerOther.lexerActionExecutor)) {
			return false;
		}

		return super.equals(other);
	}

	private static boolean checkNonGreedyDecision(LexerATNConfig source, ATNState target) {
		return source.passedThroughNonGreedyDecision
			|| target instanceof DecisionState && ((DecisionState)target).nonGreedy;
	}
}
