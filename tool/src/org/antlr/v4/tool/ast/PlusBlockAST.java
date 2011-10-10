package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class PlusBlockAST extends GrammarAST implements RuleElementAST {
	public PlusBlockAST(int type, Token t) { super(type, t); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
