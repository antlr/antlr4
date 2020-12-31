/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <author>Sam Harwell</author>
    [Flags]
    public enum Dependents
    {
        None = 0,
        Self = 1 << 0,
        Parents = 1 << 1,
        Children = 1 << 2,
        Ancestors = 1 << 3,
        Descendants = 1 << 4,
        Siblings = 1 << 5,
        PreceedingSiblings = 1 << 6,
        FollowingSiblings = 1 << 7,
        Preceeding = 1 << 8,
        Following = 1 << 9
    }
}
