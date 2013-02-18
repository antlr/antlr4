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
using Antlr4.Runtime.Dfa;
using Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <author>sam</author>
    public class ArrayEdgeMap<T> : AbstractEdgeMap<T>
    {
        private readonly T[] arrayData;

        private int size;

        public ArrayEdgeMap(int minIndex, int maxIndex) : base(minIndex, maxIndex)
        {
            arrayData = (T[])new object[maxIndex - minIndex + 1];
        }

        public override int Size()
        {
            return size;
        }

        public override bool IsEmpty()
        {
            return size == 0;
        }

        public override bool ContainsKey(int key)
        {
            return Get(key) != null;
        }

        public override T Get(int key)
        {
            if (key < minIndex || key > maxIndex)
            {
                return null;
            }
            return arrayData[key - minIndex];
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

        public override AbstractEdgeMap<T> PutAll<_T0>(IEdgeMap<_T0> m)
        {
            if (m.IsEmpty())
            {
                return this;
            }
            if (m is Antlr4.Runtime.Dfa.ArrayEdgeMap<object>)
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
                if (m is SingletonEdgeMap<object>)
                {
                    SingletonEdgeMap<T> other = (SingletonEdgeMap<T>)m;
                    System.Diagnostics.Debug.Assert(!other.IsEmpty());
                    return ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)Put(other.GetKey(), other.GetValue())
                        );
                }
                else
                {
                    if (m is SparseEdgeMap<object>)
                    {
                        SparseEdgeMap<T> other = (SparseEdgeMap<T>)m;
                        int[] keys = other.GetKeys();
                        IList<T> values = other.GetValues();
                        Antlr4.Runtime.Dfa.ArrayEdgeMap<T> result = this;
                        for (int i = 0; i < values.Count; i++)
                        {
                            result = ((Antlr4.Runtime.Dfa.ArrayEdgeMap<T>)result.Put(keys[i], values[i]));
                        }
                        return result;
                    }
                    else
                    {
                        throw new NotSupportedException(string.Format("EdgeMap of type %s is supported yet."
                            , m.GetType().FullName));
                    }
                }
            }
        }

        public override AbstractEdgeMap<T> Clear()
        {
            Arrays.Fill(arrayData, null);
            return this;
        }

        public override IDictionary<int, T> ToMap()
        {
            if (IsEmpty())
            {
                return Sharpen.Collections.EmptyMap();
            }
            IDictionary<int, T> result = new LinkedHashMap<int, T>();
            for (int i = 0; i < arrayData.Length; i++)
            {
                if (arrayData[i] == null)
                {
                    continue;
                }
                result.Put(i + minIndex, arrayData[i]);
            }
            return result;
        }

        public override ISet<KeyValuePair<int, T>> EntrySet()
        {
            return new ArrayEdgeMap.EntrySet(this);
        }

        private class EntrySet : AbstractEdgeMap.AbstractEntrySet
        {
            public override IEnumerator<KeyValuePair<int, T>> GetEnumerator()
            {
                return new ArrayEdgeMap.EntryIterator(this);
            }

            internal EntrySet(ArrayEdgeMap<T> _enclosing) : base(_enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly ArrayEdgeMap<T> _enclosing;
        }

        private class EntryIterator : IEnumerator<KeyValuePair<int, T>>
        {
            private int current;

            private int currentIndex;

            public virtual bool HasNext()
            {
                return this.current < this._enclosing.Size();
            }

            public virtual KeyValuePair<int, T> Next()
            {
                if (this.current >= this._enclosing.Size())
                {
                    throw new InvalidOperationException();
                }
                while (this._enclosing.arrayData[this.currentIndex] == null)
                {
                    this.currentIndex++;
                }
                this.current++;
                this.currentIndex++;
                return new _KeyValuePair_193(this);
            }

            private sealed class _KeyValuePair_193 : KeyValuePair<int, T>
            {
                public _KeyValuePair_193()
                {
                    this.key = this._enclosing._enclosing.minIndex + this._enclosing.currentIndex - 1;
                    this.value = this._enclosing._enclosing.arrayData[this._enclosing.currentIndex - 
                        1];
                }

                private readonly int key;

                private readonly T value;

                public int Key
                {
                    get
                    {
                        return this.key;
                    }
                }

                public T Value
                {
                    get
                    {
                        return this.value;
                    }
                }

                public T SetValue(T value)
                {
                    throw new NotSupportedException("Not supported yet.");
                }
            }

            public virtual void Remove()
            {
                throw new NotSupportedException("Not supported yet.");
            }

            internal EntryIterator(ArrayEdgeMap<T> _enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly ArrayEdgeMap<T> _enclosing;
        }
    }
}
