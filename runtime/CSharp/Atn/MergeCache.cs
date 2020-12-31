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
		Dictionary<PredictionContext, Dictionary<PredictionContext, PredictionContext>> data = new Dictionary<PredictionContext, Dictionary<PredictionContext, PredictionContext>>();

		public PredictionContext Get(PredictionContext a, PredictionContext b)
		{
			Dictionary<PredictionContext, PredictionContext> first;
			if (!data.TryGetValue(a, out first))
				return null;
			PredictionContext value;
			if (first.TryGetValue(b, out value))
				return value;
			else
				return null;

		}

		public void Put(PredictionContext a, PredictionContext b, PredictionContext value)
		{
			Dictionary<PredictionContext, PredictionContext> first;
			if (!data.TryGetValue(a, out first))
			{
				first = new Dictionary<PredictionContext, PredictionContext>();
				data[a] = first;
			}
			first[b] = value;
		}
	}
}
