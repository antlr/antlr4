/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace Antlr4.Runtime.Misc
{
    internal static class Collections
    {
        public static ReadOnlyDictionary<TKey, TValue> EmptyMap<TKey, TValue>()
        {
            return EmptyMapImpl<TKey, TValue>.Instance;
        }

        public static ReadOnlyDictionary<TKey, TValue> SingletonMap<TKey, TValue>(TKey key, TValue value)
        {
            return new ReadOnlyDictionary<TKey,TValue>(new Dictionary<TKey, TValue> { { key, value } });
        }

        private static class EmptyMapImpl<TKey, TValue>
        {
            public static readonly ReadOnlyDictionary<TKey, TValue> Instance =
                new ReadOnlyDictionary<TKey, TValue>(new Dictionary<TKey, TValue>());
        }
    }

}
