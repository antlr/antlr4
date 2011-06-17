package org.antlr.v4.tool;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.Tree;

import java.util.List;

public class ActionAST extends GrammarAST {
    // Alt, rule, grammar space
    public AttributeResolver resolver;
	public List<Token> chunks; // useful for ANTLR IDE developers
	/** In which alt does this node live? */
//	public Alternative alt;


	public ActionAST(GrammarAST node) {
		super(node);
		this.resolver = ((ActionAST)node).resolver;
		this.chunks = ((ActionAST)node).chunks;
	}

	public ActionAST(Token t) { super(t); }
    public ActionAST(int type) { super(type); }
    public ActionAST(int type, Token t) { super(type, t); }

	@Override
	public Tree dupNode() { return new ActionAST(this); }

}
