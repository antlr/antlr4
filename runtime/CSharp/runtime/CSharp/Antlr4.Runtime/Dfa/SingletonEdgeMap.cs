/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
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
