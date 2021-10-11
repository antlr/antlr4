/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System;
using Antlr4.Runtime.Misc;

namespace Antlr4.Runtime.Atn
{
    public sealed class RuleTransition : Transition
    {
        /// <summary>Ptr to the rule definition object for this rule ref</summary>
        public readonly int ruleIndex;

        public readonly int precedence;

        /// <summary>What node to begin computations following ref to rule</summary>
        [NotNull]
        public ATNState followState;

        public bool tailCall;

        public bool optimizedTailCall;

        [Obsolete(@"UseRuleTransition(RuleStartState, int, int, ATNState) instead.")]
        public RuleTransition(RuleStartState ruleStart, int ruleIndex, ATNState followState)
            : this(ruleStart, ruleIndex, 0, followState)
        {
        }

        public RuleTransition(RuleStartState ruleStart, int ruleIndex, int precedence, ATNState followState)
            : base(ruleStart)
        {
            // no Rule object at runtime
            this.ruleIndex = ruleIndex;
            this.precedence = precedence;
            this.followState = followState;
        }

        public override Antlr4.Runtime.Atn.TransitionType TransitionType
        {
            get
            {
                return Antlr4.Runtime.Atn.TransitionType.RULE;
            }
        }

        public override bool IsEpsilon
        {
            get
            {
                return true;
            }
        }

        public override bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
        {
            return false;
        }
    }
}
