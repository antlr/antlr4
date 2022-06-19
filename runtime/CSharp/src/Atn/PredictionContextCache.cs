/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
	public class PredictionContextCache
	{
		protected readonly Dictionary<PredictionContext, PredictionContext> cache =
			new Dictionary<PredictionContext, PredictionContext>();

		/** Add a context to the cache and return it. If the context already exists,
		 *  return that one instead and do not add a new context to the cache.
		 *  Protect shared cache from unsafe thread access.
		 */
		public PredictionContext Add(PredictionContext ctx)
		{
			if (ctx == EmptyPredictionContext.Instance)
				return EmptyPredictionContext.Instance;
			PredictionContext existing = cache.Get(ctx);
			if (existing != null)
			{
				return existing;
			}
			cache.Put(ctx, ctx);
			return ctx;
		}

		public PredictionContext Get(PredictionContext ctx)
		{
			return cache.Get(ctx);
		}

		public int Count
		{
			get
			{
				return cache.Count;
			}
		}

	}
}
