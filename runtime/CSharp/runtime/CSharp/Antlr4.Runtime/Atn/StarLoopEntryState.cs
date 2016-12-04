/* Copyright (c) 2012 The ANTLR Project Contributors. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public sealed class StarLoopEntryState : DecisionState
    {
        public StarLoopbackState loopBackState;

        /// <summary>
        /// Indicates whether this state can benefit from a precedence DFA during SLL
        /// decision making.
        /// </summary>
        /// <remarks>
        /// Indicates whether this state can benefit from a precedence DFA during SLL
        /// decision making.
        /// <p>This is a computed property that is calculated during ATN deserialization
        /// and stored for use in
        /// <see cref="ParserATNSimulator"/>
        /// and
        /// <see cref="Antlr4.Runtime.ParserInterpreter"/>
        /// .</p>
        /// </remarks>
        /// <seealso cref="Antlr4.Runtime.Dfa.DFA.IsPrecedenceDfa()"/>
        public bool precedenceRuleDecision;

        public override Antlr4.Runtime.Atn.StateType StateType
        {
            get
            {
                return Antlr4.Runtime.Atn.StateType.StarLoopEntry;
            }
        }
    }
}
