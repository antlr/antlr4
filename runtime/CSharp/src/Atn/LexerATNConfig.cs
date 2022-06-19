/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Atn
{
	public class LexerATNConfig : ATNConfig
	{

		/**
		 * This is the backing field for {@link #getLexerActionExecutor}.
		 */
		private readonly LexerActionExecutor lexerActionExecutor;

		private readonly bool passedThroughNonGreedyDecision;

		public LexerATNConfig(ATNState state,
							  int alt,
							  PredictionContext context)
				: base(state, alt, context/*, SemanticContext.Empty.Instance*/) // TODO
		{
			this.passedThroughNonGreedyDecision = false;
			this.lexerActionExecutor = null;
		}

		public LexerATNConfig(ATNState state,
							  int alt,
							  PredictionContext context,
							  LexerActionExecutor lexerActionExecutor)
			: base(state, alt, context, SemanticContext.Empty.Instance)
		{
			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = false;
		}

		public LexerATNConfig(LexerATNConfig c, ATNState state)
			: base(c, state, c.context, c.semanticContext)
		{
			this.lexerActionExecutor = c.lexerActionExecutor;
			this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
		}

		public LexerATNConfig(LexerATNConfig c, ATNState state,
							  LexerActionExecutor lexerActionExecutor)
			: base(c, state, c.context, c.semanticContext)
		{
			this.lexerActionExecutor = lexerActionExecutor;
			this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
		}

		public LexerATNConfig(LexerATNConfig c, ATNState state,
							  PredictionContext context)
			: base(c, state, context, c.semanticContext)
		{
			this.lexerActionExecutor = c.lexerActionExecutor;
			this.passedThroughNonGreedyDecision = checkNonGreedyDecision(c, state);
		}

		/**
		 * Gets the {@link LexerActionExecutor} capable of executing the embedded
		 * action(s) for the current configuration.
		 */
		public LexerActionExecutor getLexerActionExecutor()
		{
			return lexerActionExecutor;
		}

		public bool hasPassedThroughNonGreedyDecision()
		{
			return passedThroughNonGreedyDecision;
		}

		public override int GetHashCode()
		{
			int hashCode = MurmurHash.Initialize(7);
			hashCode = MurmurHash.Update(hashCode, state.stateNumber);
			hashCode = MurmurHash.Update(hashCode, alt);
			hashCode = MurmurHash.Update(hashCode, context);
			hashCode = MurmurHash.Update(hashCode, semanticContext);
			hashCode = MurmurHash.Update(hashCode, passedThroughNonGreedyDecision ? 1 : 0);
			hashCode = MurmurHash.Update(hashCode, lexerActionExecutor);
			hashCode = MurmurHash.Finish(hashCode, 6);
			return hashCode;
		}

		public override bool Equals(ATNConfig other)
		{
			if (this == other)
			{
				return true;
			}
			else if (!(other is LexerATNConfig))
			{
				return false;
			}

			LexerATNConfig lexerOther = (LexerATNConfig)other;
			if (passedThroughNonGreedyDecision != lexerOther.passedThroughNonGreedyDecision)
			{
				return false;
			}

			if (!(lexerActionExecutor==null ? lexerOther.lexerActionExecutor==null : lexerActionExecutor.Equals(lexerOther.lexerActionExecutor)))
			{
				return false;
			}

			return base.Equals(other);
		}

		private static bool checkNonGreedyDecision(LexerATNConfig source, ATNState target)
		{
			return source.passedThroughNonGreedyDecision
				|| target is DecisionState && ((DecisionState)target).nonGreedy;
		}
	}
}
