/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Interlocked = System.Threading.Interlocked;

using Volatile = System.Threading.Volatile;

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
                return Volatile.Read(ref size);
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

                return Volatile.Read(ref arrayData[key - minIndex]);
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

        public override IReadOnlyDictionary<int, T> ToMap()
        {
            if (IsEmpty)
            {
                return Sharpen.Collections.EmptyMap<int, T>();
            }

            IDictionary<int, T> result = new SortedDictionary<int, T>();

            for (int i = 0; i < arrayData.Length; i++)
            {
                T element = arrayData[i];
                if (element == null)
                {
                    continue;
                }
                result[i + minIndex] = element;
            }

            return new ReadOnlyDictionary<int, T>(result);
        }
    }
}
