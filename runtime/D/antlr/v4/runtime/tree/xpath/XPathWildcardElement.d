module antlr.v4.runtime.tree.xpath.XPathWildcardElement;

import antlr.v4.runtime.tree.Tree;
import antlr.v4.runtime.tree.Trees;
import antlr.v4.runtime.tree.xpath.XPath;
import antlr.v4.runtime.tree.xpath.XPathElement;
import antlr.v4.runtime.tree.ParseTree;

/**
 * TODO add class description
 */
class XPathWildcardElement : XPathElement
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
        ParseTree[] pt;
        if (invert) return pt; // !* is weird but valid (empty)
        ParseTree[] kids;
        foreach (Tree c; Trees.getChildren(t)) {
            kids ~= cast(ParseTree)c;
        }
        return kids;
    }

}
