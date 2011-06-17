package org.antlr.v4.tool;

import org.antlr.runtime.Token;

public class PredAST extends ActionAST {
	public PredAST(GrammarAST node) {
		super(node);
		this.resolver = ((ActionAST)node).resolver;
		this.chunks = ((ActionAST)node).chunks;
	}

	public PredAST(Token t) { super(t); }
    public PredAST(int type) { super(type); }
    public PredAST(int type, Token t) { super(type, t); }
}
