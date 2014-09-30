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
