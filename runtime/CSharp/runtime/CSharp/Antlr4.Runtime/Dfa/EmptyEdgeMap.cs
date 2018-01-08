/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Sharpen;

#if NET45PLUS
using System.Collections.ObjectModel;
#endif

namespace Antlr4.Runtime.Dfa
{
    /// <summary>
    /// This implementation of
    /// <see cref="AbstractEdgeMap{T}"/>
    /// represents an empty edge map.
    /// </summary>
    /// <author>Sam Harwell</author>
    public sealed class EmptyEdgeMap<T> : AbstractEdgeMap<T>
        where T : class
    {
        public EmptyEdgeMap(int minIndex, int maxIndex)
            : base(minIndex, maxIndex)
        {
        }

        public override AbstractEdgeMap<T> Put(int key, T value)
        {
            if (value == null || key < minIndex || key > maxIndex)
            {
                // remains empty
                return this;
            }
            return new SingletonEdgeMap<T>(minIndex, maxIndex, key, value);
        }

        public override AbstractEdgeMap<T> Clear()
        {
            return this;
        }

        public override AbstractEdgeMap<T> Remove(int key)
        {
            return this;
        }

        public override int Count
        {
            get
            {
                return 0;
            }
        }

        public override bool IsEmpty
        {
            get
            {
                return true;
            }
        }

        public override bool ContainsKey(int key)
        {
            return false;
        }

        public override T this[int key]
        {
            get
            {
                return null;
            }
        }

#if NET45PLUS
        public override IReadOnlyDictionary<int, T> ToMap()
#else
        public override IDictionary<int, T> ToMap()
#endif
        {
            Dictionary<int, T> result = new Dictionary<int, T>();
#if NET45PLUS
            return new ReadOnlyDictionary<int, T>(result);
#else
            return result;
#endif
        }
    }
}
