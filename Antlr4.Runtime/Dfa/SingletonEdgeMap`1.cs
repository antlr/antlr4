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
    /// <author>Sam Harwell</author>
    public class SingletonEdgeMap<T> : AbstractEdgeMap<T>
    {
        private readonly int key;

        private readonly T value;

        public SingletonEdgeMap(int minIndex, int maxIndex) : base(minIndex, maxIndex)
        {
            this.key = 0;
            this.value = null;
        }

        public SingletonEdgeMap(int minIndex, int maxIndex, int key, T value) : base(minIndex
            , maxIndex)
        {
            if (key >= minIndex && key <= maxIndex)
            {
                this.key = key;
                this.value = value;
            }
            else
            {
                this.key = 0;
                this.value = null;
            }
        }

        public virtual int GetKey()
        {
            return key;
        }

        public virtual T GetValue()
        {
            return value;
        }

        public override int Size()
        {
            return value != null ? 1 : 0;
        }

        public override bool IsEmpty()
        {
            return value == null;
        }

        public override bool ContainsKey(int key)
        {
            return key == this.key && value != null;
        }

        public override T Get(int key)
        {
            if (key == this.key)
            {
                return value;
            }
            return null;
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (key < minIndex || key > maxIndex)
            {
                return this;
            }
            if (key == this.key || this.value == null)
            {
                return new Antlr4.Runtime.Dfa.SingletonEdgeMap<T>(minIndex, maxIndex, key, value);
            }
            else
            {
                if (value != null)
                {
                    AbstractEdgeMap<T> result = new SparseEdgeMap<T>(minIndex, maxIndex);
                    result = result.Put(this.key, this.value);
                    result = result.Put(key, value);
                    return result;
                }
                else
                {
                    return this;
                }
            }
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            if (key == this.key && this.value != null)
            {
                return new Antlr4.Runtime.Dfa.SingletonEdgeMap<T>(minIndex, maxIndex);
            }
            return this;
        }

        public override AbstractEdgeMap<T> Clear()
        {
            if (this.value != null)
            {
                return new Antlr4.Runtime.Dfa.SingletonEdgeMap<T>(minIndex, maxIndex);
            }
            return this;
        }

        public override IDictionary<int, T> ToMap()
        {
            if (IsEmpty())
            {
                return Sharpen.Collections.EmptyMap();
            }
            return Sharpen.Collections.SingletonMap(key, value);
        }

        public override ISet<KeyValuePair<int, T>> EntrySet()
        {
            return new SingletonEdgeMap.EntrySet(this);
        }

        private class EntrySet : AbstractEdgeMap.AbstractEntrySet
        {
            public override IEnumerator<KeyValuePair<int, T>> GetEnumerator()
            {
                return new SingletonEdgeMap.EntryIterator(this);
            }

            internal EntrySet(SingletonEdgeMap<T> _enclosing) : base(_enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly SingletonEdgeMap<T> _enclosing;
        }

        private class EntryIterator : IEnumerator<KeyValuePair<int, T>>
        {
            private int current;

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
                this.current++;
                return new _KeyValuePair_166(this);
            }

            private sealed class _KeyValuePair_166 : KeyValuePair<int, T>
            {
                public _KeyValuePair_166()
                {
                    this.key = this._enclosing._enclosing._enclosing.key;
                    this.value = this._enclosing._enclosing._enclosing.value;
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

            internal EntryIterator(SingletonEdgeMap<T> _enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly SingletonEdgeMap<T> _enclosing;
        }
    }
}
