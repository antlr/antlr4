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
