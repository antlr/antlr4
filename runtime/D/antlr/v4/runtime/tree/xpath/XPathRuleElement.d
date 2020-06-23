module antlr.v4.runtime.tree.xpath.XPathRuleElement;

import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.tree.ParseTree;
import antlr.v4.runtime.tree.Tree;
import antlr.v4.runtime.tree.Trees;
import antlr.v4.runtime.tree.xpath.XPathElement;

/**
 * TODO add class description
 */
class XPathRuleElement : XPathElement
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
        ParseTree[] nodes;
        foreach (Tree c; Trees.getChildren(t)) {
            if (c.classinfo == ParserRuleContext.classinfo) {
                ParserRuleContext ctx = cast(ParserRuleContext)c;
                if ((ctx.getRuleIndex() == ruleIndex && !invert) ||
                     (ctx.getRuleIndex() != ruleIndex && invert))
                    {
                        nodes ~= ctx;
                    }
            }
        }
        return nodes;
    }

}
