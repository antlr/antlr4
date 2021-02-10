/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */

namespace Antlr4.Runtime.Atn
{
    public sealed class StarLoopbackState : ATNState
    {
        public StarLoopEntryState LoopEntryState
        {
            get
            {
                return (StarLoopEntryState)Transition(0).target;
            }
        }

        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.StarLoopBack;
            }
        }
    }
}
