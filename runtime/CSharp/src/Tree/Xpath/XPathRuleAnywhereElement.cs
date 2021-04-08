/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Xpath;

namespace Antlr4.Runtime.Tree.Xpath
{
    /// <summary>
    /// Either
    /// <c>ID</c>
    /// at start of path or
    /// <c>...//ID</c>
    /// in middle of path.
    /// </summary>
    public class XPathRuleAnywhereElement : XPathElement
    {
        protected internal int ruleIndex;

        public XPathRuleAnywhereElement(string ruleName, int ruleIndex)
            : base(ruleName)
        {
            this.ruleIndex = ruleIndex;
        }

        public override ICollection<IParseTree> Evaluate(IParseTree t)
        {
            return Trees.FindAllRuleNodes(t, ruleIndex);
        }
    }
}
