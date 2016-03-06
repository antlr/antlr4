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
namespace Antlr4.Runtime.Sharpen
{
    using System;
    using System.Collections.Generic;
    using System.Collections.ObjectModel;

    internal static class Collections
    {
        public static T[] EmptyList<T>()
        {
            return EmptyListImpl<T>.Instance;
        }

#if NET45PLUS
        public static ReadOnlyDictionary<TKey, TValue> EmptyMap<TKey, TValue>()
#else
        public static IDictionary<TKey, TValue> EmptyMap<TKey, TValue>()
#endif
        {
            return EmptyMapImpl<TKey, TValue>.Instance;
        }

        public static ReadOnlyCollection<T> SingletonList<T>(T item)
        {
            return new ReadOnlyCollection<T>(new T[] { item });
        }

#if NET45PLUS
        public static ReadOnlyDictionary<TKey, TValue> SingletonMap<TKey, TValue>(TKey key, TValue value)
#else
        public static IDictionary<TKey, TValue> SingletonMap<TKey, TValue>(TKey key, TValue value)
#endif
        {
#if NET45PLUS
            return new ReadOnlyDictionary<TKey,TValue>(new Dictionary<TKey, TValue> { { key, value } });
#else
            return new Dictionary<TKey, TValue> { { key, value } };
#endif
        }

        private static class EmptyListImpl<T>
        {
            public static readonly T[] Instance = new T[0];
        }

        private static class EmptyMapImpl<TKey, TValue>
        {
#if NET45PLUS
            public static readonly ReadOnlyDictionary<TKey, TValue> Instance =
                new ReadOnlyDictionary<TKey, TValue>(new Dictionary<TKey, TValue>());
#else
            public static IDictionary<TKey, TValue> Instance
            {
                get
                {
                    return new Dictionary<TKey, TValue>();
                }
            }
#endif
        }
    }

}
