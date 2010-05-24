package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class RuleAST extends GrammarASTWithOptions {
	public RuleAST(GrammarAST node) {
		super(node);
	}

	public RuleAST(Token t) { super(t); }
    public RuleAST(int type) { super(type); }

	@Override
	public Tree dupNode() { return new RuleAST(this); }

}
