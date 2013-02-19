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
using System.Collections;
using System.Collections.Generic;
using Antlr4.Runtime.Dfa;
using Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public abstract class AbstractEdgeMap<T> : IEdgeMap<T>
        where T : class
    {
        protected internal readonly int minIndex;

        protected internal readonly int maxIndex;

        public AbstractEdgeMap(int minIndex, int maxIndex)
        {
            // the allowed range (with minIndex and maxIndex inclusive) should be less than 2^32
            System.Diagnostics.Debug.Assert(maxIndex - minIndex + 1 >= 0);
            this.minIndex = minIndex;
            this.maxIndex = maxIndex;
        }

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Put(int key, T value);

        public virtual Antlr4.Runtime.Dfa.AbstractEdgeMap<T> PutAll<_T0>(IEdgeMap<_T0> m)
             where _T0:T
        {
            Antlr4.Runtime.Dfa.AbstractEdgeMap<T> result = this;
            foreach (KeyValuePair<int, _T0> entry in m.EntrySet())
            {
                result = result.Put(entry.Key, entry.Value);
            }
            return result;
        }

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Clear();

        public abstract Antlr4.Runtime.Dfa.AbstractEdgeMap<T> Remove(int key);

        protected internal abstract class AbstractEntrySet : AbstractSet<KeyValuePair<int
            , T>>
        {
            public override bool Contains(object o)
            {
                if (!(o is DictionaryEntry))
                {
                    return false;
                }
                KeyValuePair<object, object> entry = (KeyValuePair<object, object>)o;
                if (entry.Key is int)
                {
                    int key = (int)entry.Key;
                    object value = entry.Value;
                    T existing = this._enclosing._enclosing.Get(key);
                    return value == existing || (existing != null && existing.Equals(value));
                }
                return false;
            }

            public override int Count
            {
                get
                {
                    return this._enclosing._enclosing.Size();
                }
            }

            internal AbstractEntrySet(AbstractEdgeMap<T> _enclosing)
            {
                this._enclosing = _enclosing;
            }

            private readonly AbstractEdgeMap<T> _enclosing;
        }

        public abstract bool ContainsKey(int arg1);

        public abstract ISet<KeyValuePair<int, T>> EntrySet();

        public abstract T Get(int arg1);

        public abstract bool IsEmpty();

        public abstract int Size();

        public abstract IDictionary<int, T> ToMap();

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
