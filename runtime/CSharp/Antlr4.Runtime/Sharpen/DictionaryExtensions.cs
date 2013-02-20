namespace Sharpen
{
    using System.Collections.Generic;

    public static class DictionaryExtensions
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
