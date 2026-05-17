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
		/// <summary>
		/// Explicit struct key to avoid ValueTuple which is unavailable on net45.
		/// </summary>
		private readonly struct PredictionContextPair : IEquatable<PredictionContextPair>
		{
			public readonly PredictionContext A;
			public readonly PredictionContext B;

			public PredictionContextPair(PredictionContext a, PredictionContext b)
			{
				A = a;
				B = b;
			}

			public bool Equals(PredictionContextPair other)
				=> Equals(A, other.A) && Equals(B, other.B);

			public override bool Equals(object obj)
				=> obj is PredictionContextPair other && Equals(other);

			public override int GetHashCode()
			{
				int h = 17;
				h = h * 31 + (A != null ? A.GetHashCode() : 0);
				h = h * 31 + (B != null ? B.GetHashCode() : 0);
				return h;
			}
		}

		private readonly Dictionary<PredictionContextPair, PredictionContext> data =
			new Dictionary<PredictionContextPair, PredictionContext>();

		public PredictionContext Get(PredictionContext a, PredictionContext b)
		{
			PredictionContext value;
			if (data.TryGetValue(new PredictionContextPair(a, b), out value))
				return value;
			return null;
		}

		public void Put(PredictionContext a, PredictionContext b, PredictionContext value)
		{
			data[new PredictionContextPair(a, b)] = value;
		}

		public void Clear()
		{
			data.Clear();
		}
	}
}
