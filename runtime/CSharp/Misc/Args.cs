/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;

namespace Antlr4.Runtime.Misc
{
    /// <author>Sam Harwell</author>
    public static class Args
    {
        /// <exception cref="System.ArgumentNullException">
        /// if
        /// <paramref name="value"/>
        /// is
        /// <see langword="null"/>
        /// .
        /// </exception>
        public static void NotNull(string parameterName, object value)
        {
            if (value == null)
            {
                throw new ArgumentNullException(parameterName);
            }
        }
    }
}
