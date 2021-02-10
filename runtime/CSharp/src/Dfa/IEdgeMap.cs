/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Dfa
{
    /// <author>Sam Harwell</author>
    public interface IEdgeMap<T> : IEnumerable<KeyValuePair<int, T>>
    {
        int Count
        {
            get;
        }

        bool IsEmpty
        {
            get;
        }

        bool ContainsKey(int key);

        T this[int key]
        {
            get;
        }

        [return: NotNull]
        IEdgeMap<T> Put(int key, T value);

        [return: NotNull]
        IEdgeMap<T> Remove(int key);

        [return: NotNull]
        IEdgeMap<T> PutAll(IEdgeMap<T> m);

        [return: NotNull]
        IEdgeMap<T> Clear();

        [return: NotNull]
        IReadOnlyDictionary<int, T> ToMap();
    }
}
