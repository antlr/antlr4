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
using System.Collections.Generic;
using System.Text;
using Antlr4.Runtime.Misc;
using Sharpen;

namespace Antlr4.Runtime.Misc
{
    public class Utils
    {
        public static int NumNonnull(object[] data)
        {
            int n = 0;
            if (data == null)
            {
                return n;
            }
            foreach (object o in data)
            {
                if (o != null)
                {
                    n++;
                }
            }
            return n;
        }

        public static void RemoveAllElements<T>(ICollection<T> data, T value)
        {
            if (data == null)
            {
                return;
            }
            while (data.Contains(value))
            {
                data.Remove(value);
            }
        }

        public static string EscapeWhitespace(string s, bool escapeSpaces)
        {
            StringBuilder buf = new StringBuilder();
            foreach (char c in s.ToCharArray())
            {
                if (c == ' ' && escapeSpaces)
                {
                    buf.Append('\u00B7');
                }
                else
                {
                    if (c == '\t')
                    {
                        buf.Append("\\t");
                    }
                    else
                    {
                        if (c == '\n')
                        {
                            buf.Append("\\n");
                        }
                        else
                        {
                            if (c == '\r')
                            {
                                buf.Append("\\r");
                            }
                            else
                            {
                                buf.Append(c);
                            }
                        }
                    }
                }
            }
            return buf.ToString();
        }

        public static void RemoveAll<T, _T1>(IList<T> list, IPredicate<_T1> predicate)
        {
            int j = 0;
            for (int i = 0; i < list.Count; i++)
            {
                T item = list[i];
                if (!predicate.Eval(item))
                {
                    if (j != i)
                    {
                        list.Set(j, item);
                    }
                    j++;
                }
            }
            if (j < list.Count)
            {
                list.SubList(j, list.Count).Clear();
            }
        }

        public static void RemoveAll<T, _T1>(IEnumerable<T> iterable, IPredicate<_T1> predicate
            )
        {
            if (iterable is IList<object>)
            {
                RemoveAll((IList<T>)iterable, predicate);
                return;
            }
            for (IEnumerator<T> iterator = iterable.GetEnumerator(); iterator.HasNext(); )
            {
                T item = iterator.Next();
                if (predicate.Eval(item))
                {
                    iterator.Remove();
                }
            }
        }
    }
}
