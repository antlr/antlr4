/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime.Sharpen
{
    using System.Collections.Generic;

    internal static class DictionaryExtensions
    {
        public static TValue Get<TKey, TValue>(this IDictionary<TKey, TValue> dictionary, TKey key)
            where TValue : class
        {
            TValue value;
            if (!dictionary.TryGetValue(key, out value))
                return null;

            return value;
        }

        public static TValue Put<TKey, TValue>(this IDictionary<TKey, TValue> dictionary, TKey key, TValue value)
            where TValue : class
        {
            TValue previous;
            if (!dictionary.TryGetValue(key, out previous))
                previous = null;

            dictionary[key] = value;
            return previous;
        }
    }
}
