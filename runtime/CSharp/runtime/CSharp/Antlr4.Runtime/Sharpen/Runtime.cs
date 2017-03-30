/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
namespace Antlr4.Runtime.Sharpen
{
    using System;

    internal static class Runtime
    {
        public static string Substring(string str, int beginOffset, int endOffset)
        {
            if (str == null)
                throw new ArgumentNullException("str");

            return str.Substring(beginOffset, endOffset - beginOffset);
        }
    }
}
