module antlr.v4.runtime.tree.xpath.XPathTokenElement;

import antlr.v4.runtime.tree.xpath.XPathElement;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.TerminalNode;
import antlr.v4.runtime.tree.Tree;
import antlr.v4.runtime.tree.Trees;

/**
 * TODO add class description
 */
class XPathTokenElement : XPathElement
{

    public int tokenType;

    public this(string tokenName, int tokenType)
    {
        super(tokenName);
        this.tokenType = tokenType;
    }

    /**
     * @uml
     * @override
     */
    public override ParseTree[] evaluate(ParseTree t)
    {
	// return all children of t that match nodeName
        ParseTree[] nodes;
        foreach (Tree c; Trees.getChildren(t)) {
            if (c.classinfo == TerminalNode.classinfo) {
                TerminalNode tnode = cast(TerminalNode)c;
                if ((tnode.getSymbol().getType() == tokenType && !invert) ||
                    (tnode.getSymbol().getType() != tokenType && invert))
                    {
                        nodes ~= tnode;
                    }
            }
        }
        return nodes;
    }

}
