/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections;
using System.Collections.Generic;

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public abstract class AbstractEdgeMap<T> : IEdgeMap<T>
        where T : class
    {
        protected internal readonly int minIndex;

        protected internal readonly int maxIndex;

        protected AbstractEdgeMap(int minIndex, int maxIndex)
        {
            // the allowed range (with minIndex and maxIndex inclusive) should be less than 2^32
            System.Diagnostics.Debug.Assert(maxIndex - minIndex + 1 >= 0);
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Put(int key, T value);

        IEdgeMap<T> IEdgeMap<T>.Put(int key, T value)
        {
            return Put(key, value);
        }

        public virtual Antlr4.Runtime.Dfa.AbstractEdgeMap<T> PutAll(IEdgeMap<T> m)
        {
            Antlr4.Runtime.Dfa.AbstractEdgeMap<T> result = this;
            foreach (KeyValuePair<int, T> entry in m)
            {
                result = result.Put(entry.Key, entry.Value);
            }
            return result;
        }

        IEdgeMap<T> IEdgeMap<T>.PutAll(IEdgeMap<T> m)
        {
            return PutAll(m);
        }

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Clear();

        IEdgeMap<T> IEdgeMap<T>.Clear()
        {
            return Clear();
        }

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Remove(int key);

        IEdgeMap<T> IEdgeMap<T>.Remove(int key)
        {
            return Remove(key);
        }

        public abstract bool ContainsKey(int arg1);

        public abstract T this[int arg1]
        {
            get;
        }

        public abstract bool IsEmpty
        {
            get;
        }

        public abstract int Count
        {
            get;
        }

        public abstract IReadOnlyDictionary<int, T> ToMap();

        public virtual IEnumerator<KeyValuePair<int, T>> GetEnumerator()
        {
            return ToMap().GetEnumerator();
        }

        IEnumerator IEnumerable.GetEnumerator()
        {
            return GetEnumerator();
        }
    }
}
