namespace Sharpen
{
    using System;

    internal static class Arrays
    {
        public static T[] CopyOf<T>(T[] array, int newSize)
        {
            Array.Resize(ref array, newSize);
            return array;
        }
    }
}
