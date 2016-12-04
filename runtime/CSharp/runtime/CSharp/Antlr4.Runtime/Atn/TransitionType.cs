/* Copyright (c) 2012 The ANTLR Project Authors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public enum TransitionType
    {
        Invalid,
        Epsilon,
        Range,
        Rule,
        Predicate,
        Atom,
        Action,
        Set,
        NotSet,
        Wildcard,
        Precedence
    }
}
