/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime.Misc
{
    using StringBuilder = System.Text.StringBuilder;

    internal static class EmptyArray<T>
    {
        // net45 doesn't support Array.Empty<T>()
        // ReSharper disable once UseArrayEmptyMethod
        public static readonly T[] Value = new T[0];
    }
    
    internal static class Arrays
    {
        public static bool Equals<T>(T[] left, T[] right)
        {
            if (left == right)
                return true;
            else if (left == null || right == null)
                return false;

            if (left.Length != right.Length)
                return false;

            for (int i = 0; i < left.Length; i++)
            {
                if (!Equals(left[i], right[i]))
                    return false;
            }

            return true;
        }

        public static string ToString<T>(T[] array)
        {
            if (array == null)
                return "null";

            StringBuilder builder = new StringBuilder();
            builder.Append('[');
            for (int i = 0; i < array.Length; i++)
            {
                if (i > 0)
                    builder.Append(", ");

                T o = array[i];
                if (o == null)
                    builder.Append("null");
                else
                    builder.Append(o);
            }

            builder.Append(']');
            return builder.ToString();
        }
    }
}
