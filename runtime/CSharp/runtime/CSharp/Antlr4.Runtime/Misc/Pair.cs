/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;

namespace Antlr4.Runtime.Misc
{
	public class Pair<A, B>
	{
		public readonly A a;
		public readonly B b;

		public Pair(A a, B b)
		{
			this.a = a;
			this.b = b;
		}

		public override bool Equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			else if (!(obj is Pair<A, B>))
			{
				return false;
			}

			Pair<A, B> other = (Pair<A, B>)obj;
			return (a == null ? other.a == null : a.Equals(other.a)) &&
				   (b == null ? other.b == null : b.Equals(other.b));
		}

		public override int GetHashCode()
		{
			int hash = MurmurHash.Initialize();
			hash = MurmurHash.Update(hash, a);
			hash = MurmurHash.Update(hash, b);
			return MurmurHash.Finish(hash, 2);
		}

		public override String ToString()
		{
			return String.Format("({0}, {1})", a, b);
		}
	}
}