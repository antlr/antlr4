package org.antlr.v4.test;

/**
 * Created by jason on 4/1/15.
 */

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;
//Used by generated tests.
@SuppressWarnings("unused")
public class TreeShapeListener implements ParseTreeListener {
    @Override
    public void visitTerminal(TerminalNode node) {
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree parent = ctx.getChild(i).getParent();
            if (!(parent instanceof RuleNode) || ((RuleNode) parent).getRuleContext() != ctx) {
                throw new IllegalStateException("Invalid parse tree shape detected.");
            }
        }
    }
}

