/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;

namespace Antlr4.Runtime.Atn
{
	public class MergeCache
	{
		private readonly Dictionary<(PredictionContext, PredictionContext), PredictionContext> data =
			new Dictionary<(PredictionContext, PredictionContext), PredictionContext>();

		public PredictionContext Get(PredictionContext a, PredictionContext b)
		{
			PredictionContext value;
			if (data.TryGetValue((a, b), out value))
				return value;
			return null;
		}

		public void Put(PredictionContext a, PredictionContext b, PredictionContext value)
		{
			data[(a, b)] = value;
		}

		public void Clear()
		{
			data.Clear();
		}
	}
}
