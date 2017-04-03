using System;
using System.Collections.Generic;

namespace Antlr4.Runtime.Misc
{
	public class DoubleKeyMap<K1, K2, V>
	{
		Dictionary<K1, Dictionary<K2, V>> data = new Dictionary<K1, Dictionary<K2, V>>();

		public V put(K1 k1, K2 k2, V v)
		{
			Dictionary<K2, V> data2 = data.get(k1);
			V prev = null;
			if (data2 == null)
			{
				data2 = new Dict<K2, V>();
				data.put(k1, data2);
			}
			else {
				prev = data2.get(k2);
			}
			data2.put(k2, v);
			return prev;
		}

		public V get(K1 k1, K2 k2)
		{
			Dictionary<K2, V> data2 = data.get(k1);
			if (data2 == null) return null;
			return data2.get(k2);
		}

		public Dictionary<K2, V> get(K1 k1) { return data.get(k1); }

		/** Get all values associated with primary key */
		public ICollection<V> values(K1 k1)
		{
			Dictionary<K2, V> data2 = data.get(k1);
			if (data2 == null) return null;
			return data2.values();
		}

		/** get all primary keys */
		public HashSet<K1> keySet()
		{
			return data.keySet();
		}

		/** get all secondary keys associated with a primary key */
		public HashSet<K2> keySet(K1 k1)
		{
			Dictionary<K2, V> data2 = data.get(k1);
			if (data2 == null) return null;
			return data2.keySet();
		}
	}

}
