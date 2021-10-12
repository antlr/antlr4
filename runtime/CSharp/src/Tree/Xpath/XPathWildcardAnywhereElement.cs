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
    public class XPathWildcardAnywhereElement : XPathElement
    {
        public XPathWildcardAnywhereElement()
            : base(XPath.Wildcard)
        {
        }

        public override ICollection<IParseTree> Evaluate(IParseTree t)
        {
            if (invert)
            {
                return new List<IParseTree>();
            }
            // !* is weird but valid (empty)
            return Trees.Descendants(t);
        }
    }
}
