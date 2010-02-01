package org.antlr.v4.tool;

import org.antlr.runtime.Token;

import java.util.Map;

public class TerminalAST extends GrammarAST {
    public static final String defaultTokenOption = "node";

    public Map<String, String> options;

    public TerminalAST(Token t) { super(t); }

}
