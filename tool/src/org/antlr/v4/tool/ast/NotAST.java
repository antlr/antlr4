package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class NotAST extends GrammarAST implements RuleElementAST {
	public NotAST(int type, Token t) { super(type, t); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
