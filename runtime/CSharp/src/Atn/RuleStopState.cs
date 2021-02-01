/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    /// <summary>The last node in the ATN for a rule, unless that rule is the start symbol.</summary>
    /// <remarks>
    /// The last node in the ATN for a rule, unless that rule is the start symbol.
    /// In that case, there is one transition to EOF. Later, we might encode
    /// references to all calls to this rule to compute FOLLOW sets for
    /// error handling.
    /// </remarks>
    public sealed class RuleStopState : ATNState
    {
        public override int NonStopStateNumber
        {
            get
            {
                return -1;
            }
        }

        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.RuleStop;
            }
        }
    }
}
