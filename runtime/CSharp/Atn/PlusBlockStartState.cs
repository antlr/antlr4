/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>
    /// Start of
    /// <c>(A|B|...)+</c>
    /// loop. Technically a decision state, but
    /// we don't use for code generation; somebody might need it, so I'm defining
    /// it for completeness. In reality, the
    /// <see cref="PlusLoopbackState"/>
    /// node is the
    /// real decision-making note for
    /// <c>A+</c>
    /// .
    /// </summary>
    public sealed class PlusBlockStartState : BlockStartState
    {
        public PlusLoopbackState loopBackState;

        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.PlusBlockStart;
            }
        }
    }
}
