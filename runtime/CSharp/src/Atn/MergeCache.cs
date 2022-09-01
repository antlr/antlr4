/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

using System.Collections.Generic;

namespace Antlr4.Runtime.Atn
{
	public class MergeCache
	{
		Dictionary<PredictionContext, Dictionary<PredictionContext, PredictionContext>> data = new Dictionary<PredictionContext, Dictionary<PredictionContext, PredictionContext>>();

		public PredictionContext Get(PredictionContext a, PredictionContext b)
		{
			if (!data.TryGetValue(a, out var first))
				return null;
			if (first.TryGetValue(b, out var value))
				return value;
			else
				return null;

		}

		public void Put(PredictionContext a, PredictionContext b, PredictionContext value)
		{
			if (!data.TryGetValue(a, out var first))
			{
				first = new Dictionary<PredictionContext, PredictionContext>();
				data[a] = first;
			}
			first[b] = value;
		}
	}
}
