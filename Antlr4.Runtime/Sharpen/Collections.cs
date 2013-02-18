namespace Sharpen
{
    using System;
    using System.Collections.Generic;
    using IEnumerable = System.Collections.IEnumerable;
    using IEnumerator = System.Collections.IEnumerator;

    internal static class Collections
    {
        public static IList<T> EmptyList<T>()
        {
            return EmptyListImpl<T>.Instance;
        }

        public static IDictionary<TKey, TValue> EmptyMap<TKey, TValue>()
        {
            return EmptyMapImpl<TKey, TValue>.Instance;
        }

        private static class EmptyListImpl<T>
        {
            public static readonly IList<T> Instance = new T[0];
        }

        private class EmptyMapImpl<TKey, TValue> : IDictionary<TKey, TValue>
        {
            public static readonly EmptyMapImpl<TKey, TValue> Instance =
                new EmptyMapImpl<TKey, TValue>();

            public void Add(TKey key, TValue value)
            {
                throw new InvalidOperationException("This collection is read-only.");
            }

            public bool ContainsKey(TKey key)
            {
                return false;
            }

            public ICollection<TKey> Keys
            {
                get
                {
                    return EmptyList<TKey>();
                }
            }

            public bool Remove(TKey key)
            {
                throw new InvalidOperationException("This collection is read-only.");
            }

            public bool TryGetValue(TKey key, out TValue value)
            {
                value = default(TValue);
                return false;
            }

            public ICollection<TValue> Values
            {
                get
                {
                    return EmptyList<TValue>();
                }
            }

            public TValue this[TKey key]
            {
                get
                {
                    throw new System.NotImplementedException();
                }
                set
                {
                    throw new InvalidOperationException("This collection is read-only.");
                }
            }

            public void Add(KeyValuePair<TKey, TValue> item)
            {
                throw new InvalidOperationException("This collection is read-only.");
            }

            public void Clear()
            {
                throw new InvalidOperationException("This collection is read-only.");
            }

            public bool Contains(KeyValuePair<TKey, TValue> item)
            {
                return false;
            }

            public void CopyTo(KeyValuePair<TKey, TValue>[] array, int arrayIndex)
            {
            }

            public int Count
            {
                get
                {
                    return 0;
                }
            }

            public bool IsReadOnly
            {
                get
                {
                    return true;
                }
            }

            public bool Remove(KeyValuePair<TKey, TValue> item)
            {
                throw new InvalidOperationException("This collection is read-only.");
            }

            public IEnumerator<KeyValuePair<TKey, TValue>> GetEnumerator()
            {
                yield break;
            }

            IEnumerator IEnumerable.GetEnumerator()
            {
                return GetEnumerator();
            }
        }
    }
}
