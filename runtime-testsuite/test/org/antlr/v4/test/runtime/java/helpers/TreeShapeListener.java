package org.antlr.v4.test.runtime.java.helpers;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.*;

public class TreeShapeListener implements ParseTreeListener {
	public static final TreeShapeListener INSTANCE = new TreeShapeListener();

	@Override public void visitTerminal(TerminalNode node) { }
	@Override public void visitErrorNode(ErrorNode node) { }
	@Override public void exitEveryRule(ParserRuleContext ctx) { }

	@Override
	public void enterEveryRule(ParserRuleContext ctx) {
		for (int i = 0; i < ctx.getChildCount(); i++) {
			ParseTree parent = ctx.getChild(i).getParent();
			if (!(parent instanceof RuleNode) || ((RuleNode)parent).getRuleContext() != ctx) {
				throw new IllegalStateException("Invalid parse tree shape detected.");
			}
		}
	}
}
