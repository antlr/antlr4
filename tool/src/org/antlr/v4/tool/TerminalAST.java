package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

public class TerminalAST extends GrammarASTWithOptions {
    public static final String defaultTokenOption = "node";

	public TerminalAST(GrammarAST node) {
		super(node);
	}

	public TerminalAST(Token t) { super(t); }
    public TerminalAST(int type) { super(type); }
    public TerminalAST(int type, Token t) { super(type, t); }

	@Override
	public Tree dupNode() { return new TerminalAST(this); }	
}
