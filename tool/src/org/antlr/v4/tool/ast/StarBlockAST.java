package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class StarBlockAST extends GrammarAST implements RuleElementAST {
	public StarBlockAST(int type, Token t) { super(type, t); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
