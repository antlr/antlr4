/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

using System.Collections.Generic;

namespace Antlr4.Runtime.Misc
{
	public class ArrayList<T> : List<T>
	{

		public ArrayList()
		{
		}

		public ArrayList(int count)
			: base(count)
		{
		}

		public override int GetHashCode()
		{
			int hash = MurmurHash.Initialize(1);
			foreach (T t in this)
				hash = MurmurHash.Update(hash, t.GetHashCode());
			hash = MurmurHash.Finish(hash, this.Count);
			return hash;
		}

		public override bool Equals(object o)
		{
			return o == this
				|| (o is List<T> && this.Equals((List<T>)o));
		}


		public bool Equals(List<T> o)
		{
			if (this.Count != o.Count)
				return false;
			IEnumerator<T> thisItems = this.GetEnumerator();
			IEnumerator<T> otherItems = o.GetEnumerator();
			while (thisItems.MoveNext() && otherItems.MoveNext())
			{
				if (!thisItems.Current.Equals(otherItems.Current))
					return false;
			}
			return true;

		}

	}
}
