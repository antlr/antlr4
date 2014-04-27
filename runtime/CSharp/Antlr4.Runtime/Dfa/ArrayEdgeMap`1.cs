/*
 * [The "BSD license"]
 *  Copyright (c) 2013 Terence Parr
 *  Copyright (c) 2013 Sam Harwell
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 *  IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 *  INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 *  NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 *  DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 *  THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 *  THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <author>sam</author>
    public class ArrayEdgeMap<T> : AbstractEdgeMap<T>
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
                return size;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return size == 0;
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
                return arrayData[key - minIndex];
            }
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (key >= minIndex && key <= maxIndex)
            {
                T existing = arrayData[key - minIndex];
                arrayData[key - minIndex] = value;
                if (existing == null && value != null)
                {
                    size++;
                }
                else
                {
                    if (existing != null && value == null)
                    {
                        size--;
                    }
                }
            }
            return this;
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            return ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)Put(key, null));
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
                for (int i = minOverlap; i <= maxOverlap; i++)
                {
                    T target = other.arrayData[i - other.minIndex];
                    if (target != null)
                    {
                        T current = this.arrayData[i - this.minIndex];
                        this.arrayData[i - this.minIndex] = target;
                        size += (current != null ? 0 : 1);
                    }
                }
                return this;
            }
            else
            {
                if (m is SingletonEdgeMap<T>)
                {
                    SingletonEdgeMap<T> other = (SingletonEdgeMap<T>)m;
                    System.Diagnostics.Debug.Assert(!other.IsEmpty);
                    return ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)Put(other.Key, other.Value));
                }
                else
                {
                    if (m is SparseEdgeMap<T>)
                    {
                        SparseEdgeMap<T> other = (SparseEdgeMap<T>)m;
                        int[] keys = other.Keys;
                        IList<T> values = other.Values;
                        Antlr4.Runtime.Dfa.ArrayEdgeMap<T> result = this;
                        for (int i = 0; i < values.Count; i++)
                        {
                            result = ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)result.Put(keys[i], values[i]));
                        }
                        return result;
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
            Arrays.Fill(arrayData, null);
            return this;
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
                if (arrayData[i] == null)
                {
                    continue;
                }
                result[i + minIndex] = arrayData[i];
            }
#if NET45PLUS
            return new ReadOnlyDictionary<int, T>(result);
#else
            return result;
#endif
        }
    }
}
