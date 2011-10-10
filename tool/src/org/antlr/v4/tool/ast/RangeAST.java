package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class RangeAST extends GrammarAST implements RuleElementAST {
	public RangeAST(Token t) { super(t); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
