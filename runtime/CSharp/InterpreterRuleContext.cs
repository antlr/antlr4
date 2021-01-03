/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;

namespace Antlr4.Runtime
{
    /// <summary>
    /// This class extends
    /// <see cref="ParserRuleContext"/>
    /// by allowing the value of
    /// <see cref="RuleIndex()"/>
    /// to be explicitly set for the context.
    /// <p>
    /// <see cref="ParserRuleContext"/>
    /// does not include field storage for the rule index
    /// since the context classes created by the code generator override the
    /// <see cref="RuleIndex()"/>
    /// method to return the correct value for that context.
    /// Since the parser interpreter does not use the context classes generated for a
    /// parser, this class (with slightly more memory overhead per node) is used to
    /// provide equivalent functionality.</p>
    /// </summary>
    public class InterpreterRuleContext : ParserRuleContext
    {
        /// <summary>
        /// This is the backing field for
        /// <see cref="RuleIndex()"/>
        /// .
        /// </summary>
        private readonly int ruleIndex;

        /// <summary>
        /// Constructs a new
        /// <see cref="InterpreterRuleContext"/>
        /// with the specified
        /// parent, invoking state, and rule index.
        /// </summary>
        /// <param name="parent">The parent context.</param>
        /// <param name="invokingStateNumber">The invoking state number.</param>
        /// <param name="ruleIndex">The rule index for the current context.</param>
        public InterpreterRuleContext(ParserRuleContext parent, int invokingStateNumber, int ruleIndex)
            : base(parent, invokingStateNumber)
        {
            this.ruleIndex = ruleIndex;
        }

        public override int RuleIndex
        {
            get
            {
                return ruleIndex;
            }
        }
    }
}
