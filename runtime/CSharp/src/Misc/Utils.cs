/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using System.Collections.Generic;
using System.Text;

namespace Antlr4.Runtime.Misc
{
    public static class StaticUtils
    {
        public static string ToString<T>(this IEnumerable<T> list)
        {
            return "[" + Utils.Join(", ", list) + "]";
        }
    }

    public static class Utils
    {
        public static string Join<T>(string separator, IEnumerable<T> items)
        {
            return string.Join(separator, items);
        }

        public static string EscapeWhitespace(string s, bool escapeSpaces)
        {
            StringBuilder buf = new StringBuilder();
            foreach (char c in s)
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

        /// <summary>Convert array of strings to string&#x2192;index map.</summary>
        /// <remarks>
        /// Convert array of strings to string&#x2192;index map. Useful for
        /// converting rulenames to name&#x2192;ruleindex map.
        /// </remarks>
        public static IDictionary<string, int> ToMap(string[] keys)
        {
            IDictionary<string, int> m = new Dictionary<string, int>();
            for (int i = 0; i < keys.Length; i++)
            {
                m[keys[i]] = i;
            }
            return m;
        }
    }
}
