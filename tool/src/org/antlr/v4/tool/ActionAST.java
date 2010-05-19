package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.List;

public class ActionAST extends GrammarAST {
    // Alt, rule, grammar space
    public AttributeResolver resolver;
	public List<Token> chunks; // useful for ANTLR IDE developers

    public ActionAST(Token t) { super(t); }
    public ActionAST(int type) { super(type); }
    public ActionAST(int type, Token t) { super(type, t); }
}
