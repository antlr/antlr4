/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;
using Interlocked = System.Threading.Interlocked;

#if NET45PLUS
using Volatile = System.Threading.Volatile;
#elif !PORTABLE && !COMPACT
using Thread = System.Threading.Thread;
#endif

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public sealed class ArrayEdgeMap<T> : AbstractEdgeMap<T>
        where T : class
    {
        private readonly T[] arrayData;

        private int size;

        public ArrayEdgeMap(int minIndex, int maxIndex)
            : base(minIndex, maxIndex)
        {
            arrayData = new T[maxIndex - minIndex + 1];
        }

        public override int Count
        {
            get
            {
#if NET45PLUS
                return Volatile.Read(ref size);
#elif !PORTABLE && !COMPACT
                return Thread.VolatileRead(ref size);
#else
                return Interlocked.CompareExchange(ref size, 0, 0);
#endif
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return Count == 0;
            }
        }

        public override bool ContainsKey(int key)
        {
            return this[key] != null;
        }

        public override T this[int key]
        {
            get
            {
                if (key < minIndex || key > maxIndex)
                {
                    return null;
                }

#if NET45PLUS
                return Volatile.Read(ref arrayData[key - minIndex]);
#else
                return Interlocked.CompareExchange(ref arrayData[key - minIndex], null, null);
#endif
            }
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (key >= minIndex && key <= maxIndex)
            {
                T existing = Interlocked.Exchange(ref arrayData[key - minIndex], value);
                if (existing == null && value != null)
                {
                    Interlocked.Increment(ref size);
                }
                else
                {
                    if (existing != null && value == null)
                    {
                        Interlocked.Decrement(ref size);
                    }
                }
            }
            return this;
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            return Put(key, null);
        }

        public override AbstractEdgeMap<T> PutAll(IEdgeMap<T> m)
        {
            if (m.IsEmpty)
            {
                return this;
            }
            if (m is Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)
            {
                Antlr4.Runtime.Dfa.ArrayEdgeMap<T> other = (Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)m;
                int minOverlap = Math.Max(minIndex, other.minIndex);
                int maxOverlap = Math.Min(maxIndex, other.maxIndex);
                Antlr4.Runtime.Dfa.ArrayEdgeMap<T> result = this;
                for (int i = minOverlap; i <= maxOverlap; i++)
                {
                    result = ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)result.Put(i, m[i]));
                }
                return result;
            }
            else
            {
                if (m is SingletonEdgeMap<T>)
                {
                    SingletonEdgeMap<T> other = (SingletonEdgeMap<T>)m;
                    System.Diagnostics.Debug.Assert(!other.IsEmpty);
                    return Put(other.Key, other.Value);
                }
                else
                {
                    if (m is SparseEdgeMap<T>)
                    {
                        SparseEdgeMap<T> other = (SparseEdgeMap<T>)m;
                        lock (other)
                        {
                            int[] keys = other.Keys;
                            IList<T> values = other.Values;
                            Antlr4.Runtime.Dfa.ArrayEdgeMap<T> result = this;
                            for (int i = 0; i < values.Count; i++)
                            {
                                result = ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)result.Put(keys[i], values[i]));
                            }
                            return result;
                        }
                    }
                    else
                    {
                        throw new NotSupportedException(string.Format("EdgeMap of type {0} is supported yet.", m.GetType().FullName));
                    }
                }
            }
        }

        public override AbstractEdgeMap<T> Clear()
        {
            return new EmptyEdgeMap<T>(minIndex, maxIndex);
        }

#if NET45PLUS
        public override IReadOnlyDictionary<int, T> ToMap()
#else
        public override IDictionary<int, T> ToMap()
#endif
        {
            if (IsEmpty)
            {
                return Sharpen.Collections.EmptyMap<int, T>();
            }

#if COMPACT
            IDictionary<int, T> result = new SortedList<int, T>();
#elif PORTABLE && !NET45PLUS
            IDictionary<int, T> result = new Dictionary<int, T>();
#else
            IDictionary<int, T> result = new SortedDictionary<int, T>();
#endif
            for (int i = 0; i < arrayData.Length; i++)
            {
                T element = arrayData[i];
                if (element == null)
                {
                    continue;
                }
                result[i + minIndex] = element;
            }
#if NET45PLUS
            return new ReadOnlyDictionary<int, T>(result);
#else
            return result;
#endif
        }
    }
}
