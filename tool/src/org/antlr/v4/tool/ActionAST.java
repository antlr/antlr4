package org.antlr.v4.tool;

import org.antlr.runtime.Token;

public class ActionAST extends GrammarAST {
    // Alt, rule, grammar space
    public AttributeResolver space;
    public ActionAST(Token t) { super(t); }
    public ActionAST(int type) { super(type); }
    public ActionAST(int type, Token t) { super(type, t); }
}
