package org.antlr.v4.tool.ast;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class OptionalBlockAST extends GrammarAST implements RuleElementAST {
	public OptionalBlockAST(GrammarAST node) { super(node); }
	public OptionalBlockAST(int type, Token t) { super(type, t); }

	@Override
	public Tree dupNode() { return new OptionalBlockAST(this); }

	@Override
	public Object visit(GrammarASTVisitor v) { return v.visit(this); }

}
