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
using System.Collections.Generic;
using Antlr4.Runtime.Dfa;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public sealed class SingletonEdgeMap<T> : AbstractEdgeMap<T>
        where T : class
    {
        private readonly int key;

        private readonly T value;

        public SingletonEdgeMap(int minIndex, int maxIndex, int key, T value)
            : base(minIndex, maxIndex)
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

        public int Key
        {
            get
            {
                return key;
            }
        }

        public T Value
        {
            get
            {
                return value;
            }
        }

        public override int Count
        {
            get
            {
                return value != null ? 1 : 0;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return value == null;
            }
        }

        public override bool ContainsKey(int key)
        {
            return key == this.key && value != null;
        }

        public override T this[int key]
        {
            get
            {
                if (key == this.key)
                {
                    return value;
                }
                return null;
            }
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
                return new EmptyEdgeMap<T>(minIndex, maxIndex);
            }
            return this;
        }

        public override AbstractEdgeMap<T> Clear()
        {
            if (this.value != null)
            {
                return new EmptyEdgeMap<T>(minIndex, maxIndex);
            }
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
            return Antlr4.Runtime.Sharpen.Collections.SingletonMap(key, value);
        }
    }
}
