/* Copyright (c) 2012-2017 The ANTLR Project. All rights reserved.
 * Use of this file is governed by the BSD 3-clause license that
 * can be found in the LICENSE.txt file in the project root.
 */
using System.Collections.Generic;
using Antlr4.Runtime.Sharpen;
using Antlr4.Runtime.Tree;

namespace Antlr4.Runtime.Tree.Xpath
{
    public abstract class XPathElement
    {
        protected internal string nodeName;

        protected internal bool invert;

        /// <summary>
        /// Construct element like
        /// <c>/ID</c>
        /// or
        /// <c>ID</c>
        /// or
        /// <c>/*</c>
        /// etc...
        /// op is null if just node
        /// </summary>
        public XPathElement(string nodeName)
        {
            this.nodeName = nodeName;
        }

        /// <summary>
        /// Given tree rooted at
        /// <paramref name="t"/>
        /// return all nodes matched by this path
        /// element.
        /// </summary>
        public abstract ICollection<IParseTree> Evaluate(IParseTree t);

        public override string ToString()
        {
            string inv = invert ? "!" : string.Empty;
            return GetType().Name + "[" + inv + nodeName + "]";
        }
    }
}
