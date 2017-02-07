package org.antlr.v4.runtime.tree;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class DefaultParseTreeFactory implements ParseTreeFactory {
	protected Parser parser;

	public DefaultParseTreeFactory(Parser parser) {
		this.parser = parser;
	}

	@Override
	public ParseTree createRuleNode(int ruleIndex, ParserRuleContext parent, int invokingStateNumber) {
		return parser.createRuleNode(ruleIndex, parent, invokingStateNumber);
	}

	@Override
	public ParseTree createAltLabelRuleNode(int ruleIndex, int altIndex, ParserRuleContext src) {
		return parser.createAltLabelRuleNode(ruleIndex, altIndex, src);
	}

	@Override
	public ErrorNode createErrorNode(Token badToken) {
		return new ErrorNodeImpl(badToken);
	}

	@Override
	public TerminalNode createLeaf(Token matchedToken) {
		return new TerminalNodeImpl(matchedToken);
	}

	@Override
	public void addChild(ParserRuleContext parent, TerminalNode matchedTokenNode) {
		parent.addChild(matchedTokenNode);
	}

	@Override
	public void addChild(ParserRuleContext parent, ParserRuleContext ruleInvocationNode) {
		parent.addChild(ruleInvocationNode);
	}

	@Override
	public void replaceLastChild(ParserRuleContext parent, ParserRuleContext newChild) {
		if ( parent!=null )	{
			parent.removeLastChild();
			parent.addChild(newChild);
		}
	}
}
