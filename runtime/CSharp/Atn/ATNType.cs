/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>Represents the type of recognizer an ATN applies to.</summary>
    /// <remarks>Represents the type of recognizer an ATN applies to.</remarks>
    /// <author>Sam Harwell</author>
    public enum ATNType
    {
        Lexer,
        Parser
    }
}
