/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Misc
{
    [System.Serializable]
    public class MultiMap<K, V> : Dictionary<K, IList<V>>
    {
        private const long serialVersionUID = -4956746660057462312L;

        public virtual void Map(K key, V value)
        {
            IList<V> elementsForKey;
            if (!TryGetValue(key, out elementsForKey))
            {
                elementsForKey = new ArrayList<V>();
                this[key] = elementsForKey;
            }
            elementsForKey.Add(value);
        }

        public virtual IList<Tuple<K, V>> GetPairs()
        {
            IList<Tuple<K, V>> pairs = new ArrayList<Tuple<K, V>>();
            foreach (KeyValuePair<K, IList<V>> pair in this)
            {
                foreach (V value in pair.Value)
                {
                    pairs.Add(Tuple.Create(pair.Key, value));
                }
            }

            return pairs;
        }
    }
}
