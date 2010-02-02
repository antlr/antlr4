package org.antlr.v4.tool;

import org.antlr.runtime.Token;

public class TerminalAST extends GrammarASTWithOptions {
    public static final String defaultTokenOption = "node";

    public TerminalAST(Token t) { super(t); }
    public TerminalAST(int type) { super(type); }
    public TerminalAST(int type, Token t) { super(type, t); }
}
