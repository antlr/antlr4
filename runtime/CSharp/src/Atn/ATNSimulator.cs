/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Misc;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
	public abstract class ATNSimulator
	{


		/** Must distinguish between missing edge and edge we know leads nowhere */

		public static readonly DFAState ERROR = InitERROR();

		static DFAState InitERROR()
		{
			DFAState state = new DFAState(new ATNConfigSet());
			state.stateNumber = Int32.MaxValue;
			return state;
		}

		public readonly ATN atn;

		/** The context cache maps all PredictionContext objects that are equals()
		 *  to a single cached copy. This cache is shared across all contexts
		 *  in all ATNConfigs in all DFA states.  We rebuild each ATNConfigSet
		 *  to use only cached nodes/graphs in addDFAState(). We don't want to
		 *  fill this during closure() since there are lots of contexts that
		 *  pop up but are not used ever again. It also greatly slows down closure().
		 *
		 *  <p>This cache makes a huge difference in memory and a little bit in speed.
		 *  For the Java grammar on java.*, it dropped the memory requirements
		 *  at the end from 25M to 16M. We don't store any of the full context
		 *  graphs in the DFA because they are limited to local context only,
		 *  but apparently there's a lot of repetition there as well. We optimize
		 *  the config contexts before storing the config set in the DFA states
		 *  by literally rebuilding them with cached subgraphs only.</p>
		 *
		 *  <p>I tried a cache for use during closure operations, that was
		 *  whacked after each adaptivePredict(). It cost a little bit
		 *  more time I think and doesn't save on the overall footprint
		 *  so it's not worth the complexity.</p>
		 */
		protected readonly PredictionContextCache sharedContextCache;


		public ATNSimulator(ATN atn, PredictionContextCache sharedContextCache)
		{
			this.atn = atn;
			this.sharedContextCache = sharedContextCache;
		}

		public abstract void Reset();

		/**
		 * Clear the DFA cache used by the current instance. Since the DFA cache may
		 * be shared by multiple ATN simulators, this method may affect the
		 * performance (but not accuracy) of other parsers which are being used
		 * concurrently.
		 *
		 * @throws UnsupportedOperationException if the current instance does not
		 * support clearing the DFA.
		 *
		 * @since 4.3
		 */
		public virtual void ClearDFA()
		{
			throw new Exception("This ATN simulator does not support clearing the DFA.");
		}

        public PredictionContextCache getSharedContextCache()
		{
			return sharedContextCache;
		}

		public PredictionContext getCachedContext(PredictionContext context)
		{
			if (sharedContextCache == null) return context;

			lock (sharedContextCache)
			{
				PredictionContext.IdentityHashMap visited =
					new PredictionContext.IdentityHashMap();
				return PredictionContext.GetCachedContext(context,
														  sharedContextCache,
														  visited);
			}
		}

	}
}
