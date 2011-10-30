package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class StarBlockAST extends GrammarAST implements RuleElementAST {
	public StarBlockAST(GrammarAST node) { super(node); }
	public StarBlockAST(int type, Token t) { super(type, t); }

	@Override
	public Tree dupNode() { return new StarBlockAST(this); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
