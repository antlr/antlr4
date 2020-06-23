module antlr.v4.runtime.tree.xpath.XPathRuleAnywhereElement;

import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.Trees;
import antlr.v4.runtime.tree.xpath.XPathElement;

/**
 * TODO add class description
 */
class XPathRuleAnywhereElement : XPathElement
{

    public int ruleIndex;

    public this(string ruleName, int ruleIndex)
    {
        super(ruleName);
        this.ruleIndex = ruleIndex;
    }

    /**
     * @uml
     * @override
     */
    public override ParseTree[] evaluate(ParseTree t)
    {
        return Trees.findAllRuleNodes(t, ruleIndex);
    }

}
