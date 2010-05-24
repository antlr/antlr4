package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

/** An ALT or ALT_REWRITE node (left of ->) */
public class AltAST extends GrammarAST {
	public Alternative alt;

	public AltAST(GrammarAST node) {
		super(node);
		this.alt = ((AltAST)node).alt;
	}

	public AltAST(Token t) { super(t); }
	public AltAST(int type) { super(type); }
	public AltAST(int type, Token t) { super(type, t); }	

	@Override
	public Tree dupNode() { return new AltAST(this); }
}
