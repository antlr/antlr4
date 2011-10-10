package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;

public class SetAST extends GrammarAST implements RuleElementAST {
	public SetAST(int type, Token t, String text) { super(type,t,text); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
