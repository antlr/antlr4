/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime.Atn;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime.Atn
{
    public sealed class ActionTransition : Transition
    {
        public readonly int ruleIndex;

        public readonly int actionIndex;

        public readonly bool isCtxDependent;

        public ActionTransition(ATNState target, int ruleIndex)
            : this(target, ruleIndex, -1, false)
        {
        }

        public ActionTransition(ATNState target, int ruleIndex, int actionIndex, bool isCtxDependent)
            : base(target)
        {
            // e.g., $i ref in action
            this.ruleIndex = ruleIndex;
            this.actionIndex = actionIndex;
            this.isCtxDependent = isCtxDependent;
        }

        public override Antlr4.Runtime.Atn.TransitionType TransitionType
        {
            get
            {
                return Antlr4.Runtime.Atn.TransitionType.ACTION;
            }
        }

        public override bool IsEpsilon
        {
            get
            {
                return true;
            }
        }

        // we are to be ignored by analysis 'cept for predicates
        public override bool Matches(int symbol, int minVocabSymbol, int maxVocabSymbol)
        {
            return false;
        }

        public override string ToString()
        {
            return "action_" + ruleIndex + ":" + actionIndex;
        }
    }
}
