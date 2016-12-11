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
