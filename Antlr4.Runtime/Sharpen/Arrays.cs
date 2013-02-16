namespace Sharpen
{
    using System;
    using System.Collections.Generic;

    internal static class Arrays
    {
        public static T[] CopyOf<T>(T[] array, int newSize)
        {
            Array.Resize(ref array, newSize);
            return array;
        }

        public static IList<T> AsList<T>(params T[] array)
        {
            return array;
        }
    }
}
