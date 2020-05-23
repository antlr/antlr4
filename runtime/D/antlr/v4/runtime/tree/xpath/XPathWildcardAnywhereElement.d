module antlr.v4.runtime.tree.xpath.XPathWildcardAnywhereElement;

import antlr.v4.runtime.tree.xpath.XPath;
import antlr.v4.runtime.tree.xpath.XPathElement;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.Trees;

/**
 * TODO add class description
 */
class XPathWildcardAnywhereElement : XPathElement
{

    public this()
    {
        super(XPath.WILDCARD);
    }

    /**
     * @uml
     * @override
     */
    public override ParseTree[] evaluate(ParseTree t)
    {
        if (invert) {
            ParseTree[] emtyParseTree;
            return emtyParseTree; // !* is weird but valid (empty)
        }
        return Trees.getDescendants(t);
    }

}
