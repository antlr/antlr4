/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime.Sharpen
{
    using System.Collections.Generic;
    using System.Collections.ObjectModel;

    internal static class Collections
    {
        public static T[] EmptyList<T>()
        {
            return EmptyListImpl<T>.Instance;
        }

        public static ReadOnlyDictionary<TKey, TValue> EmptyMap<TKey, TValue>()
        {
            return EmptyMapImpl<TKey, TValue>.Instance;
        }

        public static ReadOnlyCollection<T> SingletonList<T>(T item)
        {
            return new ReadOnlyCollection<T>(new T[] { item });
        }

        public static ReadOnlyDictionary<TKey, TValue> SingletonMap<TKey, TValue>(TKey key, TValue value)
        {
            return new ReadOnlyDictionary<TKey,TValue>(new Dictionary<TKey, TValue> { { key, value } });
        }

        private static class EmptyListImpl<T>
        {
            public static readonly T[] Instance = new T[0];
        }

        private static class EmptyMapImpl<TKey, TValue>
        {
            public static readonly ReadOnlyDictionary<TKey, TValue> Instance =
                new ReadOnlyDictionary<TKey, TValue>(new Dictionary<TKey, TValue>());
        }
    }

}
