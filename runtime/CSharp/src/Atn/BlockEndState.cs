/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// Terminal node of a simple
    /// <c>(a|b|c)</c>
    /// block.
    /// </summary>
    public sealed class BlockEndState : ATNState
    {
        public BlockStartState startState;

        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.BlockEnd;
            }
        }
    }
}
