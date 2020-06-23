module antlr.v4.runtime.tree.xpath.XPathTokenAnywhereElement;

import antlr.v4.runtime.tree.xpath.XPathElement;
import antlr.v4.runtime.tree.Trees;
import antlr.v4.runtime.tree.ParseTree;

/**
 * TODO add class description
 */
class XPathTokenAnywhereElement : XPathElement
{

    protected int tokenType;

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
        return Trees.findAllTokenNodes(t, tokenType);
    }

}
