/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;
using Antlr4.Runtime.Tree.Xpath;

namespace Antlr4.Runtime.Tree.Xpath
{
    public class XPathRuleElement : XPathElement
    {
        protected internal int ruleIndex;

        public XPathRuleElement(string ruleName, int ruleIndex)
            : base(ruleName)
        {
            this.ruleIndex = ruleIndex;
        }

        public override ICollection<IParseTree> Evaluate(IParseTree t)
        {
            // return all children of t that match nodeName
            IList<IParseTree> nodes = new List<IParseTree>();
            foreach (ITree c in Trees.GetChildren(t))
            {
                if (c is ParserRuleContext)
                {
                    ParserRuleContext ctx = (ParserRuleContext)c;
                    if ((ctx.RuleIndex == ruleIndex && !invert) || (ctx.RuleIndex != ruleIndex && invert))
                    {
                        nodes.Add(ctx);
                    }
                }
            }
            return nodes;
        }
    }
}
