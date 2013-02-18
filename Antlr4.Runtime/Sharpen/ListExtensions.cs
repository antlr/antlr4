namespace Sharpen
{
    using System.Collections.Generic;

    public static class ListExtensions
    {
        public static T Set<T>(this IList<T> list, int index, T value)
            where T : class
        {
            T previous = list[index];
            list[index] = value;
            return previous;
        }
    }
}
