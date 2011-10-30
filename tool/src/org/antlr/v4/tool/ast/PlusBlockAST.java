package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class PlusBlockAST extends GrammarAST implements RuleElementAST {
	public PlusBlockAST(GrammarAST node) { super(node); }
	public PlusBlockAST(int type, Token t) { super(type, t); }

	@Override
	public Tree dupNode() { return new PlusBlockAST(this); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }
}
