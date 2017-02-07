package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class DefaultParseTreeFactory implements ParseTreeFactory {
	@Override
	public ParseTree createRuleNode(int ruleIndex, ParserRuleContext parent, int invokingStateNumber) {
		return null;
	}

	@Override
	public ParseTree createRuleNode(int ruleIndex, ParserRuleContext src) {
		return null;
	}

	@Override
	public ErrorNode createErrorNode(Token badToken) {
		return null;
	}

	@Override
	public TerminalNode createLeaf(Token matchedToken) {
		return null;
	}

	@Override
	public void addChild(RuleNode parent, TerminalNode matchedTokenNode) {

	}

	@Override
	public void addChild(RuleNode parent, ParserRuleContext ruleInvocationNode) {

	}

	@Override
	public void replaceLastChild(ParserRuleContext parent, ParserRuleContext newChild) {
		if ( parent!=null )	{
			parent.removeLastChild();
			parent.addChild(newChild);
		}
	}
}
