/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>The Tokens rule start state linking to each lexer rule start state</summary>
    public sealed class TokensStartState : DecisionState
    {
        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.TokenStart;
            }
        }
    }
}
