/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

namespace Antlr4.Runtime
{
    using System;

    [AttributeUsage(AttributeTargets.Method, Inherited = false, AllowMultiple = false)]
    public sealed class RuleVersionAttribute : Attribute
    {
        private readonly int _version;

        public RuleVersionAttribute(int version)
        {
            _version = version;
        }

        public int Version
        {
            get
            {
                return _version;
            }
        }
    }
}
