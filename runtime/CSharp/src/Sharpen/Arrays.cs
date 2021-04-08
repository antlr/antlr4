/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime.Sharpen
{
    using System;
    using System.Collections.Generic;
    using StringBuilder = System.Text.StringBuilder;

    internal static class Arrays
    {
        public static T[] CopyOf<T>(T[] array, int newSize)
        {
            if (array.Length == newSize)
                return (T[])array.Clone();

            Array.Resize(ref array, newSize);
            return array;
        }

        public static IList<T> AsList<T>(params T[] array)
        {
            return array;
        }

        public static void Fill<T>(T[] array, T value)
        {
            for (int i = 0; i < array.Length; i++)
                array[i] = value;
        }

        public static int HashCode<T>(T[] array)
        {
            if (array == null)
                return 0;

            int result = 1;
            foreach (object o in array)
                result = 31 * result + (o == null ? 0 : o.GetHashCode());

            return result;
        }

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
                if (!object.Equals(left[i], right[i]))
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
