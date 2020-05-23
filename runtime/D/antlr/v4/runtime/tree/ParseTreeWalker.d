module antlr.v4.runtime.tree.ParseTreeWalker;

import antlr.v4.runtime.ParserRuleContext;
import antlr.v4.runtime.tree.ErrorNode;
import antlr.v4.runtime.tree.RuleNode;
import antlr.v4.runtime.tree.TerminalNode;
import antlr.v4.runtime.tree.ParseTreeWalker;
import antlr.v4.runtime.tree.ParseTreeListener;
import antlr.v4.runtime.tree.ParseTree;

/**
 * TODO add class description
 */
class ParseTreeWalker
{

    public static immutable ParseTreeWalker DEFAULT;

    public static this()
    {
    }

    public void walk(ParseTreeListener listener, ParseTree t)
    {
        if (cast(ErrorNode)t) {
            listener.visitErrorNode(cast(ErrorNode)t);
            return;
        }
        else if (cast(TerminalNode)t) {
            listener.visitTerminal(cast(TerminalNode)t);
            return;
        }
        RuleNode r = cast(RuleNode)t;
        enterRule(listener, r);
        int n = r.getChildCount();
        for (int i = 0; i<n; i++) {
            walk(listener, r.getChild(i));
        }
        exitRule(listener, r);
    }

    protected void enterRule(ParseTreeListener listener, RuleNode r)
    {
        ParserRuleContext ctx = cast(ParserRuleContext)r.getRuleContext();
        listener.enterEveryRule(ctx);
        ctx.enterRule(listener);
    }

    public void exitRule(ParseTreeListener listener, RuleNode r)
    {
        ParserRuleContext ctx = cast(ParserRuleContext)r.getRuleContext();
        ctx.exitRule(listener);
        listener.exitEveryRule(ctx);
    }

}
