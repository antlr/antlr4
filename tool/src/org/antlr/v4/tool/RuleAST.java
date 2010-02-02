package org.antlr.v4.tool;

import org.antlr.runtime.Token;

public class RuleAST extends GrammarASTWithOptions {
    public RuleAST(Token t) { super(t); }
    public RuleAST(int type) { super(type); }
}
